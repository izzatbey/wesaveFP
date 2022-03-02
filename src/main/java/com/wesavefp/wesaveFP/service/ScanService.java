package com.wesavefp.wesaveFP.service;

import com.wesavefp.wesaveFP.model.database.Scan;
import com.wesavefp.wesaveFP.model.request.CreateScanRequest;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.IOException;
import java.util.List;

public interface ScanService {

    Scan start(CreateScanRequest request) throws IOException, ClientApiException;

    void update(String id);

    List<Scan> findAll();

    Scan findById(String id);

    void delete(String id);
}
