package com.wesavefp.wesaveFP.model.database;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Site {
    private String date;
    private String host;
    private String port;
    private Boolean ssl;
    private List<Alert> alerts;
}
