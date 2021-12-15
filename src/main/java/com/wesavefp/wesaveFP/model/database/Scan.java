package com.wesavefp.wesaveFP.model.database;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Scan {
    @Id
    private String id;
    private String domain;
    private Site site;

}
