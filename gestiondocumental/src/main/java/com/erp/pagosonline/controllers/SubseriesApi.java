package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.SubseriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subseries")
@CrossOrigin("*")
public class SubseriesApi {
    @Autowired
    private SubseriesService subseriesService;
}
