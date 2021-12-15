package com.wesavefp.wesaveFP.model.controller;

import com.wesavefp.wesaveFP.helper.JsonToObject;
import com.wesavefp.wesaveFP.model.database.Scan;
import com.wesavefp.wesaveFP.model.request.CreateScanRequest;
import com.wesavefp.wesaveFP.service.ScanService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/scan")
@AllArgsConstructor
public class ScanController {

    private ScanService scanService;

    @GetMapping
    public List<Scan> fetchAllScans(){
        return scanService.findAll();
    }

    @PostMapping(value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Scan startScan(@RequestBody(required = false) CreateScanRequest request){
        try {
            return scanService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
