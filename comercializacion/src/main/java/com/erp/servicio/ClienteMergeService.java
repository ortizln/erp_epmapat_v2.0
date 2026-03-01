package com.erp.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.modelo.Abonados;
import com.erp.modelo.ClienteMerge;
import com.erp.modelo.ClienteMergeAbonado;
import com.erp.modelo.ClienteMergeCliente;
import com.erp.modelo.ClienteMergeFactura;
import com.erp.modelo.ClienteMergeLectura;
import com.erp.modelo.Clientes;
import com.erp.modelo.Facturas;
import com.erp.modelo.Lecturas;
import com.erp.repositorio.AbonadosR;
import com.erp.repositorio.ClienteMergeAbonadoR;
import com.erp.repositorio.ClienteMergeClienteR;
import com.erp.repositorio.ClienteMergeFacturaR;
import com.erp.repositorio.ClienteMergeLecturaR;
import com.erp.repositorio.ClienteMergeR;
import com.erp.repositorio.ClientesR;
import com.erp.repositorio.FacturasR;
import com.erp.repositorio.LecturasR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteMergeService {

    private final ClientesR clienteRepo;
    private final AbonadosR abonadoRepo;
    private final FacturasR facturaRepo;
    private final LecturasR lecturasRepo;

    private final ClienteMergeR mergeRepo;
    private final ClienteMergeClienteR mergeClienteRepo;
    private final ClienteMergeAbonadoR mergeAbonadoRepo;
    private final ClienteMergeFacturaR mergeFacturaRepo;
    private final ClienteMergeLecturaR mergeLecturaRepo;

    @Transactional
    public void merge(Long masterId, List<Long> duplicateIds, Long usuario) {

        if (duplicateIds == null || duplicateIds.isEmpty()) {
            throw new IllegalArgumentException("No existen clientes duplicados para merge");
        }

        if (duplicateIds.contains(masterId)) {
            throw new IllegalArgumentException("El cliente master no puede estar en duplicados");
        }

        Clientes master = clienteRepo.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Cliente master no existe"));

        ClienteMerge merge = new ClienteMerge();
        merge.setMasterId(masterId);
        merge.setUsuarioMerge(usuario);
        merge.setFechaMerge(LocalDateTime.now());
        mergeRepo.save(merge);

        for (Long dupId : duplicateIds) {

            Clientes duplicado = clienteRepo.findById(dupId)
                    .orElseThrow(() -> new RuntimeException("Cliente duplicado no existe"));

            mergeClienteRepo.save(new ClienteMergeCliente(merge.getIdMerge(), dupId));

            List<Abonados> abonados = abonadoRepo.findByIdcliente(dupId);
            for (Abonados a : abonados) {
                mergeAbonadoRepo.save(new ClienteMergeAbonado(merge.getIdMerge(), a.getIdabonado(), dupId));
                a.setIdcliente_clientes(master);
                abonadoRepo.save(a);
            }

            List<Facturas> facturas = facturaRepo.findSincobroToMerge(dupId);
            for (Facturas f : facturas) {
                mergeFacturaRepo.save(new ClienteMergeFactura(merge.getIdMerge(), f.getIdfactura(), dupId));
                f.setIdcliente(master);
                facturaRepo.save(f);
            }

            List<Lecturas> lecturas = lecturasRepo.findPendientesByCliente(dupId);
            for (Lecturas l : lecturas) {
                ClienteMergeLectura ml = new ClienteMergeLectura();
                ml.setIdMerge(merge.getIdMerge());
                ml.setIdLectura(l.getIdlectura());
                ml.setIdClienteOrigen(dupId);
                mergeLecturaRepo.save(ml);
                lecturasRepo.save(l);
            }

            lecturasRepo.reasignarCliente(dupId, masterId);
            LocalDate now = LocalDate.now();
            duplicado.setUsucrea(usuario);
            duplicado.setFecmodi(now);
            duplicado.setActivo(false);
            clienteRepo.save(duplicado);
        }
    }
}
