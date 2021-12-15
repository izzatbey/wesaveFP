package com.wesavefp.wesaveFP.repository;

import com.wesavefp.wesaveFP.model.database.Scan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScanRepository extends MongoRepository<Scan, String> {
}
