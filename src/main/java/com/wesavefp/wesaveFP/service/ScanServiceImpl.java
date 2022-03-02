package com.wesavefp.wesaveFP.service;

import com.wesavefp.wesaveFP.helper.JsonToObject;
import com.wesavefp.wesaveFP.helper.autoscanner.AutomatedScanner;
import com.wesavefp.wesaveFP.model.database.Scan;
import com.wesavefp.wesaveFP.model.request.CreateScanRequest;
import com.wesavefp.wesaveFP.repository.ScanRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
public class ScanServiceImpl implements ScanService {

    @Autowired
    private ScanRepository scanRepository;

    @Override
    public Scan start(CreateScanRequest request) throws ClientApiException {
        AutomatedScanner automatedScanner = new AutomatedScanner(request);
        automatedScanner.start();
        Scan scan = JsonToObject.convertFromJson();
        return scanRepository.save(scan);
    }

    @Override
    public void update(String id) {
    }

    @Override
    public List<Scan> findAll() {
        return scanRepository.findAll();
    }

    @Override
    public Scan findById(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
