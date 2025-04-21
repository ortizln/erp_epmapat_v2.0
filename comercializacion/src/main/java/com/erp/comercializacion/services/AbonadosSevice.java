package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.AbonadoI;
import com.erp.comercializacion.models.Abonados;
import com.erp.comercializacion.repositories.AbonadosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AbonadosSevice {
    @Autowired
    private AbonadosR dao;

    public List<Abonados> findAll(String c, Sort sort) {
        if (c != null) {
            return dao.findAll(c);
        } else {
            return dao.findAll(sort);
        }
    }

    //Todos los Abonados, Campos específicos
    public List<Map<String, Object>> allAbonadosCampos() {
        return dao.allAbonadosCampos();
    }

    // Temporal Todos
    public List<Abonados> tmpTodos() {
        return dao.tmpTodos();
    }

    // Abonados por Cliente (Cuentas de un Cliente)
    public List<Abonados> findByIdcliente(Long idcliente) {
        return dao.findByIdcliente(idcliente);
    }

    public List<Abonados> findByNombreCliente(String nombreCliente) {
        return dao.findByNombreCliente(nombreCliente);
    }

    public List<Abonados> findByidentIficacionCliente(String identificacionCliente) {
        return dao.findByidentIficacionCliente(identificacionCliente);
    }
    public List<AbonadoI> getAbonadoInterfaceIdCliente(Long idcliente) {
        return dao.getAbonadoInterfaceIdCliente(idcliente);
    }

    public List<Abonados> getAbonadoByid(Long idabonado) {
        return dao.getAbonadoByid(idabonado);
    }

    // Busca Abonado por idabonado con parametro (para recaudacion)
    public List<Abonados> getByIdabonado(Long idabonado) {
        return dao.getByIdabonado(idabonado);
    }

    // Abonados por Ruta
    public List<Abonados> findByIdruta(Long idruta) {
        return dao.findByIdruta(idruta);
    }

    public List<Abonados> findByIdCliente(Long idcliente){
        return dao.findByIdCliente(idcliente);
    }

    public <S extends Abonados> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Abonados> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void delete(Abonados entity) {
        dao.delete(entity);
    }

    // Verifica si un Cliente tiene Abonados
    public boolean clienteTieneAbonados(Long idcliente) {
        return dao.existsByIdcliente_clientes(idcliente);
    }
    public Abonados findOne(Long idabonado) {
        return dao.findOne(idabonado);
    }

    public List<AbonadoI> getAbonadoInterface(Long idabonado){
        return dao.getAbonadoInterface(idabonado);
    }
    public List<AbonadoI> getAbonadoInterfaceNombre(String nombre){
        return dao.getAbonadoInterfaceNombre(nombre);
    }
    public List<AbonadoI> getAbonadoInterfaceIdentificacion(String identificacion){
        return dao.getAbonadoInterfaceIdentificacion(identificacion);
    }
    public List<AbonadoI> getAbonadoInterfaceCliente(Long idcliente){
        return dao.getAbonadoInterfaceCliente(idcliente);
    }
    public List<AbonadoI> getAbonadoInterfaceRespPago(Long idresp){
        return dao.getAbonadoInterfaceRespPago(idresp);
    }
    //Un Abonado
    public Abonados unAbonado(Long idabonado) {
        return dao.findByIdabonado(idabonado);
    }
}
