package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/series")
@CrossOrigin("*")
public class SeriesApi {
    @Autowired
    private SeriesService seriesService;
}
