package com.chervon.iot.ablecloud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

/**
 * Created by 喷水君 on 2017/7/26.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class Able_DiagnosticReports_Controller {

    @RequestMapping(value = "/diagnostic_report")
    public ResponseEntity<?> diagnosticList(@RequestHeader String Authorization, Pageable pageable){
    return null;
    }
}
