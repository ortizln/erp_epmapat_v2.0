package com.erp.rrhh.controlador.v1;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.rrhh.dto.BenefitRequest;
import com.erp.rrhh.dto.IncentiveRequest;
import com.erp.rrhh.dto.PageResponse;
import com.erp.rrhh.dto.PayrollRequest;
import com.erp.rrhh.servicio.RrhhCompensationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rrhh")
public class RrhhCompensationController {

    private final RrhhCompensationService service;

    @GetMapping("/payrolls")
    public ResponseEntity<PageResponse<?>> listPayrolls(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listPayrolls(status, date, page, size)));
    }

    @GetMapping("/payrolls/{id}")
    public ResponseEntity<?> getPayroll(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getPayroll(id));
    }

    @PostMapping("/payrolls")
    public ResponseEntity<?> createPayroll(@Valid @RequestBody PayrollRequest request) {
        return ResponseEntity.ok(service.createPayroll(request));
    }

    @GetMapping("/benefits")
    public ResponseEntity<PageResponse<?>> listBenefits(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listBenefits(status, date, page, size)));
    }

    @PostMapping("/benefits")
    public ResponseEntity<?> createBenefit(@Valid @RequestBody BenefitRequest request) {
        return ResponseEntity.ok(service.createBenefit(request));
    }

    @GetMapping("/incentives")
    public ResponseEntity<PageResponse<?>> listIncentives(@RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(PageResponse.from(service.listIncentives(status, date, page, size)));
    }

    @PostMapping("/incentives")
    public ResponseEntity<?> createIncentive(@Valid @RequestBody IncentiveRequest request) {
        return ResponseEntity.ok(service.createIncentive(request));
    }
}

