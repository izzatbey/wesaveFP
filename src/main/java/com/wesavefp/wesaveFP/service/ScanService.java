package com.wesavefp.wesaveFP.service;

import com.wesavefp.wesaveFP.model.database.Scan;

import java.io.IOException;
import java.util.List;

public interface ScanService {

    Scan start() throws IOException;

    void update(String id);

    List<Scan> findAll();

    Scan findById(String id);

    void delete(String id);
}
