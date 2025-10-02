package com.erp.sri.controller;

import java.util.List;
import java.util.Optional;

import com.erp.sri.interfaces.Cliente_int;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri.model.Clientes;
import com.erp.sri.service.Cliente_ser;

@RestController
@RequestMapping("/api/rec/cliente")
@CrossOrigin("*")
public class Cliente_api {
	@Autowired
	private Cliente_ser s_cliente;

	@GetMapping
	public ResponseEntity<Optional<Clientes>> getByIdCliente(@RequestParam Long idcliente) {
		return ResponseEntity.ok(s_cliente.findByIdCliente(idcliente));
	}
	@GetMapping("/datos")
	public ResponseEntity<List<Cliente_int>> clienteDatos(@RequestParam String dato){
		return ResponseEntity.ok(s_cliente.clienteDatos(dato));
	}
}
