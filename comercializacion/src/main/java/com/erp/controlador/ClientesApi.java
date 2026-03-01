package com.erp.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.DTO.ClienteMergeRequest;
import com.erp.DTO.CredencialesRequest;
import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.interfaces.CVClientes;
import com.erp.interfaces.ClienteDuplicadoGrupoView;
import com.erp.interfaces.ClienteDuplicadoView;
import com.erp.interfaces.mobile.ClientesMobile;
import com.erp.modelo.Clientes;
import com.erp.servicio.ClienteMergeService;
import com.erp.servicio.ClienteServicio;

@RestController
@RequestMapping("/api/clientes")
public class ClientesApi {

    @Autowired
    private ClienteServicio cliServicio;

    @Autowired
    private ClienteMergeService cliMergeServicio;

    @GetMapping
    public List<Clientes> getAllClientes(@Param(value = "identificacion") String identificacion,
            @Param(value = "nombre") String nombre, @Param(value = "nombreIdentifi") String nombreIdentifi,
            @Param(value = "idused") Long idused) {
        if (nombreIdentifi != null) {
            return cliServicio.findByNombreIdentifi(nombreIdentifi.toLowerCase());
        }
        if (identificacion != null) {
            return cliServicio.findByIdentificacion(identificacion);
        }
        if (nombre != null) {
            return cliServicio.findByNombre(nombre.toLowerCase());
        }
        if (idused != null) {
            return cliServicio.used(idused);
        }
        return null;
    }

    @GetMapping("/one")
    public ResponseEntity<Clientes> getByIdCliente(@RequestParam Long idcliente) {
        Clientes clienteM = cliServicio.findById(idcliente)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe ese cliente con ese Id: " + idcliente)));
        return ResponseEntity.ok(clienteM);
    }

    @GetMapping("/valIdentificacion")
    public ResponseEntity<Boolean> valIdentificacion(@Param(value = "identificacion") String identificacion) {
        boolean rtn = cliServicio.valIdentificacion(identificacion);
        return ResponseEntity.ok(rtn);
    }

    @GetMapping("/valNombre")
    public ResponseEntity<Boolean> valNombre(@Param(value = "nombre") String nombre) {
        boolean rtn = cliServicio.valNombre(nombre.toLowerCase());
        return ResponseEntity.ok(rtn);
    }

    @GetMapping("/campos")
    public List<Map<String, Object>> obtenerTodosLosCampos() {
        return cliServicio.obtenerCampos();
    }

    @PostMapping
    public ResponseEntity<Clientes> saveClientes(@RequestBody Clientes clienteM) {
        boolean resp = validadorDeCedula(clienteM.getCedula());
        if (resp) {
            return ResponseEntity.ok(cliServicio.save(clienteM));
        } else {
            if (clienteM.getCedula().length() <= 13) {
                return ResponseEntity.ok(cliServicio.save(clienteM));
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PutMapping(value = "/{idcliente}")
    public ResponseEntity<Clientes> updateCliente(@PathVariable Long idcliente, @RequestBody Clientes clientem) {
        Clientes clienteM = cliServicio.findById(idcliente)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No Existe ese cliente con ese Id: " + idcliente));
        clienteM.setCedula(clientem.getCedula());
        clienteM.setNombre(clientem.getNombre());
        clienteM.setDireccion(clientem.getDireccion());
        clienteM.setTelefono(clientem.getTelefono());
        clienteM.setFechanacimiento(clientem.getFechanacimiento());
        clienteM.setDiscapacitado(clientem.getDiscapacitado());
        clienteM.setIdtpidentifica_tpidentifica(clientem.getIdtpidentifica_tpidentifica());
        clienteM.setPorcexonera(clientem.getPorcexonera());
        clienteM.setEstado(clientem.getEstado());
        clienteM.setEmail(clientem.getEmail());
        clienteM.setUsucrea(clientem.getUsucrea());
        clienteM.setFeccrea(clientem.getFeccrea());
        clienteM.setUsumodi(clientem.getUsumodi());
        clienteM.setFecmodi(clientem.getFecmodi());
        clienteM.setIdpjuridica_personeriajuridica(clientem.getIdpjuridica_personeriajuridica());
        Clientes updateCliente = cliServicio.save(clienteM);
        return ResponseEntity.ok(updateCliente);
    }

    @DeleteMapping(value = "/{idcliente}")
    public ResponseEntity<Object> deleteCliente(@PathVariable("idcliente") Long idcliente) {
        cliServicio.deleteById(idcliente);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalClientes() {
        return ResponseEntity.ok(cliServicio.totalclientes());
    }

    public boolean validadorDeCedula(String cedula) {
        boolean cedulaCorrecta = false;
        try {
            if (cedula.length() == 10) {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
                if (tercerDigito < 6) {
                    int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
                    int verificador = Integer.parseInt(cedula.substring(9, 10));
                    int suma = 0;
                    int digito = 0;
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = (10 - (suma % 10)) == verificador;
                    }
                }
            }
        } catch (Exception e) {
            cedulaCorrecta = false;
        }
        return cedulaCorrecta;
    }

    @GetMapping("/reportes/carteravencida")
    ResponseEntity<List<CVClientes>> getCVByCliente(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        return ResponseEntity.ok(cliServicio.getCVByCliente(fecha));
    }

    @GetMapping("/cartera/clientes")
    public ResponseEntity<Page<CVClientes>> getCVOfClientes(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0)
            page = 0;
        Page<CVClientes> result = cliServicio.getCVOfClientes(fecha, name.toLowerCase(), page, size);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/credenciales")
    public ResponseEntity<Void> actualizarCredenciales(@PathVariable("id") Long id, @RequestBody CredencialesRequest req)
            throws Exception {
        cliServicio.actualizarCredenciales(id, req.getUsername(), req.getPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/duplicados")
    public Page<ClienteDuplicadoView> listarDuplicados(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return cliServicio.listarDuplicados(PageRequest.of(page, size));
    }

    @GetMapping("/duplicados-agrupado")
    public ResponseEntity<Page<ClienteDuplicadoGrupoView>> duplicadosFiltrados(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (q.isEmpty()) {
            return ResponseEntity.ok(cliServicio.findDuplicadosAgrupados(PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(cliServicio.listarDuplicadosFiltrados(q, page, size));
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeClientes(@RequestBody ClienteMergeRequest req) {
        cliMergeServicio.merge(req.getMasterId(), req.getDuplicateIds(), req.getUsuario());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Clientes>> getAllClientes() {
        return ResponseEntity.ok(cliServicio.findAll());
    }

    @GetMapping("/all-mobile")
    public ResponseEntity<List<ClientesMobile>> getAllClientesMobile() {
        return ResponseEntity.ok(cliServicio.getAllClientesMobile());
    }
}
