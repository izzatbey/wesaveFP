package com.wesavefp.wesaveFP.helper.proxy.model;

import org.zaproxy.clientapi.core.ApiResponseSet;

public class ScannerInfo {
    boolean enabled;
    int wascId;
    int cweId;

    public ScannerInfo(ApiResponseSet responseSet) {
        this.enabled = Boolean.parseBoolean(responseSet.getAttribute("enabled"));
        this.wascId = Integer.parseInt(responseSet.getAttribute("wascid"));
        this.cweId = Integer.parseInt(responseSet.getAttribute("cweid"));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getWascId() {
        return this.wascId;
    }

    public int getCweId() {
        return this.cweId;
    }
}
