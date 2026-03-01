package com.erp.comercializacion.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.erp.config.AESUtil;
import com.erp.interfaces.CVClientes;
import com.erp.interfaces.ClienteDuplicadoGrupoView;
import com.erp.interfaces.ClienteDuplicadoView;
import com.erp.interfaces.mobile.ClientesMobile;
import com.erp.comercializacion.modelo.Clientes;
import com.erp.comercializacion.repositorio.ClientesR;

@Service
public class ClienteServicio {

    @Autowired
    private ClientesR dao;

    public List<Map<String, Object>> obtenerCampos() {
        return dao.findAllClientsFields();
    }

    public List<Clientes> findByNombreIdentifi(String nombreIdentifi) {
        return dao.findByNombreIdentifi(nombreIdentifi);
    }

    public List<Clientes> findByIdentificacion(String identificacion) {
        return dao.findByIdentificacion(identificacion);
    }

    public List<Clientes> findByNombre(String nombre) {
        return dao.findByNombre(nombre);
    }

    public boolean valIdentificacion(String nombre) {
        return dao.valIdentificacion(nombre);
    }

    public boolean valNombre(String nombre) {
        return dao.valNombre(nombre);
    }

    public <S extends Clientes> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Clientes> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteByIdQ(id);
    }

    public List<Clientes> used(Long id) {
        return dao.used(id);
    }

    public Long totalclientes() {
        return dao.totalClientes();
    }

    public List<CVClientes> getCVByCliente(LocalDate fecha) {
        return dao.getCVByCliente(fecha);
    }

    public Page<CVClientes> getCVOfClientes(LocalDate fecha, String name, int page, int size) {
        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, size);

        if (name == null || name.isEmpty()) {
            return dao.getCVOfClientes(fecha, pageable);
        } else {
            return dao.getCVOfNCliente(fecha, name, pageable);
        }
    }

    public void actualizarCredenciales(Long idcliente, String username, String password) throws Exception {
        Clientes c = dao.findById(idcliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        c.setUsername(username);
        c.setPassword(AESUtil.cifrar(password));

        dao.save(c);
    }

    public Page<ClienteDuplicadoView> listarDuplicados(Pageable pageable) {
        return dao.findDuplicados(pageable);
    }

    public Page<ClienteDuplicadoGrupoView> findDuplicadosAgrupados(Pageable pageable) {
        return dao.findDuplicadosAgrupados(pageable);
    }

    public Page<ClienteDuplicadoGrupoView> listarDuplicadosFiltrados(String q, int page, int size) {

        String filtro = (q == null) ? "" : q.trim();

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.ASC, "cedula"));

        return dao.findDuplicadosAgrupadosFiltrado(filtro, pageable);
    }

    public List<Clientes> findAll() {
        return dao.findAll();
    }

    public List<ClientesMobile> getAllClientesMobile() {
        return dao.findAllBy();
    }
}


