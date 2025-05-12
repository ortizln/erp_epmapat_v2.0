package com.erp.sri.service;

import com.erp.sri.interfaces.LastConection_int;
import com.erp.sri.repository.Cajas_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Cajas_ser {
    @Autowired
    private Cajas_rep c_dao;
    @Autowired
    private Usuario_ser s_usuario;
    @Autowired
    private Recaudaxcaja_ser s_recaudaxcaja;
    @Autowired
    private Factura_ser s_factura;

    public Object cajasSingIn(String username, String password){
        System.out.println(password);
        String pass = s_usuario.convertPassword(password);
        System.out.println(pass);
        LastConection_int l_conecction = c_dao.getLConectionByUserPass(username, pass);
            Map<String, Object> respuesta = new HashMap<>();
        if (l_conecction == null){
            respuesta.put("mensaje", "NOMBRE DE USUARIO INCORRECTO, CAJA NO REGISTRADA");
        }else {

            if (l_conecction.getEstado() == 1) {
                Long usuario = s_usuario.userLogin(username, password);
                  if (usuario != null){
                      respuesta.put("mensaje", "exito");
                  }else{
                      respuesta.put("mensaje", "no hay exito");
                  }
                // s_recaudaxcaja.cerrarCaja(l_conecction.getIdrecaudaxcaja());
                respuesta.put("mensaje", "LA CAJA ESTA ABIERTA");
                respuesta.put("Link: ", "/cajas/singout?username=" + username);
                respuesta.put("usuario", Usuario_ser.codificar(l_conecction.getUsuario().toString()));
                respuesta.put("n",l_conecction.getUsuario());
                //return "redirect:/cajas/singout?username="+username;
            }
            if (l_conecction.getEstado() == 0) {
                try {
                    Long usuario = s_usuario.userLogin(username, password);
                    s_recaudaxcaja.abrirCaja(l_conecction.getIdrecaudaxcaja(), usuario);
                    respuesta.put("mensaje", "Acceso aprobado");
                    respuesta.put("autentification", s_usuario.codificar(usuario.toString()));
                    respuesta.put("autentification", usuario);

                } catch (Exception e) {
                    System.out.println("Error al iniciar caja " + e);
                    respuesta.put("mensaje", "Acceso denegado " +e );
                    respuesta.put("autentification", false);
                }
            }
            // Long usuario = s_usuario.userLogin(username, password);
        }
        return respuesta;
    }
    public Object cajasSingOut(String username){
        LastConection_int l_conecction = c_dao.getLConectionByUsername(username);
        Map<String, Object> respuesta = new HashMap<>();
        if(l_conecction.getEstado() ==1){
            s_recaudaxcaja.cerrarCaja(l_conecction.getIdrecaudaxcaja());
            respuesta.put("mensaje", "Cerrando la ultima conexión");
        }
        return respuesta;
    }
    public LastConection_int getLastConectionByUduario (Long idusuario){
        return c_dao.getLastConection(idusuario);
    }
    public Object testIfLogin(Long idusuario){
        LastConection_int lconection = c_dao.getLastConection(idusuario);
        Map<String, Object> respuesta = new HashMap<>();

        if(lconection != null && lconection.getEstado() ==1){
            respuesta.put("estado", lconection.getEstado());
            respuesta.put("username", lconection.getNomusu());
            respuesta.put("codigo", Factura_ser.fPemiCaja( lconection.getCodigo()));
            respuesta.put("establecimiento", Factura_ser.fPemiCaja(lconection.getEstablecimiento()));
            respuesta.put("secuencial",Factura_ser.fSecuencial(lconection.getSecuencial()));
            respuesta.put("iniciolabor", lconection.getFechainiciolabor());
            respuesta.put("finlabor", lconection.getFechafinlabor());
        }else{
            respuesta.put("estado", 0);
            respuesta.put("mensaje", "Caja no iniciada");
        }
        return respuesta;

    }

}
