package com.wesavefp.wesaveFP.model.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Site {
    private String date;
    private String host;
    private String port;
    private String ssl;
    private List<Alert> alerts;
}
