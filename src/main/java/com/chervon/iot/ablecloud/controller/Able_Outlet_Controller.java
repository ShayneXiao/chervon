package com.chervon.iot.ablecloud.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 喷水君 on 2017/7/26.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class Able_Outlet_Controller {
    @RequestMapping(value = "/devices/{device_id}/outlets",method = RequestMethod.GET)
    public ResponseEntity<?> outletsList(@RequestHeader String Authorization, @PathVariable String device_id , Pageable pageable){
        return null;
    }
    @RequestMapping(value = "/outlets/{outlet_id}",method = RequestMethod.GET)
    public ResponseEntity<?> outletRead(@RequestHeader String Authorization,@PathVariable String outlet_id){
        return null;
    }
    @RequestMapping(value = "/outlets/{outlet_id}",method = RequestMethod.PATCH)
    public ResponseEntity<?> outletUpdate(@RequestHeader String Authorization,@PathVariable String outlet_id){
        return null;
    }
}
