package com.erp.comercializacion.servicio;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.excepciones.RutasOcupadasException;
import com.erp.interfaces.UsrxrutasService;
import com.erp.comercializacion.modelo.Emisiones;
import com.erp.comercializacion.modelo.Rutas;
import com.erp.comercializacion.modelo.Usrxrutas;
import com.erp.comercializacion.modelo.administracion.Usuarios;
import com.erp.comercializacion.repositorio.EmisionesR;
import com.erp.comercializacion.repositorio.UsrxrutasR;
import com.erp.comercializacion.repositorio.administracion.UsuariosR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsrxrutaService implements UsrxrutasService {

    private final UsrxrutasR usrxrutasRepository;
    private final UsuariosR usuariosRepository;
    private final EmisionesR emisionesRepository;

    @Override
    public Usrxrutas crear(Usrxrutas entity) {

        Long idusuario = entity.getIdusuario_usuarios() != null ? entity.getIdusuario_usuarios().getIdusuario() : null;
        Long idemision = entity.getIdemision_emisiones() != null ? entity.getIdemision_emisiones().getIdemision() : null;

        if (idusuario == null || idemision == null) {
            throw new IllegalArgumentException("Debe enviar idusuario_usuarios e idemision_emisiones.");
        }

        Usuarios usuario = usuariosRepository.findById(idusuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no existe: " + idusuario));

        Emisiones emision = emisionesRepository.findById(idemision)
                .orElseThrow(() -> new EntityNotFoundException("Emisión no existe: " + idemision));

        List<Rutas> rutas = entity.getRutas() != null ? entity.getRutas() : List.of();

        List<Long> idrutas = rutas.stream().map(Rutas::getIdruta).toList();

        if (!idrutas.isEmpty()) {
            List<Long> ocupadas = usrxrutasRepository
                    .findRutasOcupadasEnEmisionPorOtros(idemision, idusuario, idrutas);

            if (!ocupadas.isEmpty()) {
                throw new RutasOcupadasException("Existen rutas ya asignadas a otro lector.", ocupadas);
            }
        }

        Optional<Usrxrutas> existente = usrxrutasRepository.findByUsuarioAndEmision(idusuario, idemision);
        if (existente.isPresent()) {
            Usrxrutas reg = existente.get();
            reg.setRutas(entity.getRutas());
            return usrxrutasRepository.save(reg);
        }

        entity.setIdusuario_usuarios(usuario);
        entity.setIdemision_emisiones(emision);
        return usrxrutasRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> rutasOcupadasEnEmision(Long idemision) {
        return usrxrutasRepository.findRutasOcupadasEnEmision(idemision);
    }

    @Override
    public Usrxrutas actualizar(Long id, Usrxrutas entity) {

        Usrxrutas actual = usrxrutasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no existe: " + id));

        if (entity.getIdusuario_usuarios() != null && entity.getIdusuario_usuarios().getIdusuario() != null) {
            Long idusuario = entity.getIdusuario_usuarios().getIdusuario();
            Usuarios usuario = usuariosRepository.findById(idusuario)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no existe: " + idusuario));
            actual.setIdusuario_usuarios(usuario);
        }

        if (entity.getIdemision_emisiones() != null && entity.getIdemision_emisiones().getIdemision() != null) {
            Long idemision = entity.getIdemision_emisiones().getIdemision();
            Emisiones emision = emisionesRepository.findById(idemision)
                    .orElseThrow(() -> new EntityNotFoundException("Emisión no existe: " + idemision));
            actual.setIdemision_emisiones(emision);
        }

        if (entity.getRutas() != null) {
            Long idusuarioActual = actual.getIdusuario_usuarios().getIdusuario();
            Long idemisionActual = actual.getIdemision_emisiones().getIdemision();

            List<Long> idrutas = entity.getRutas().stream().map(Rutas::getIdruta).toList();

            if (!idrutas.isEmpty()) {
                List<Long> ocupadas = usrxrutasRepository
                        .findRutasOcupadasEnEmisionPorOtros(idemisionActual, idusuarioActual, idrutas);

                if (!ocupadas.isEmpty()) {
                    throw new RutasOcupadasException("Existen rutas ya asignadas a otro lector.", ocupadas);
                }
            }

            actual.setRutas(entity.getRutas());
        }

        return usrxrutasRepository.save(actual);
    }

    @Override
    @Transactional(readOnly = true)
    public Usrxrutas obtenerPorId(Long id) {
        return usrxrutasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no existe: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listar() {
        return usrxrutasRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!usrxrutasRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro no existe: " + id);
        }
        usrxrutasRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listarPorUsuario(Long idusuario) {
        return usrxrutasRepository.findByUsuario(idusuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listarPorEmision(Long idemision) {
        return usrxrutasRepository.findByEmision(idemision);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usrxrutas> findByUsuarioAndEmision(Long idusuario, Long idemision) {
        return usrxrutasRepository.findByUsuarioAndEmision(idusuario, idemision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> findByEmision(Long idemision) {
        return usrxrutasRepository.findByEmision(idemision);
    }
}


