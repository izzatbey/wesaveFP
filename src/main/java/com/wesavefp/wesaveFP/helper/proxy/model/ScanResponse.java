package com.wesavefp.wesaveFP.helper.proxy.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;

public class ScanResponse {
    List<ScanInfo> scans = new ArrayList();

    public ScanResponse(ApiResponseList responseList) {
        Iterator var2 = responseList.getItems().iterator();

        while(var2.hasNext()) {
            ApiResponse rawResponse = (ApiResponse)var2.next();
            this.scans.add(new ScanInfo((ApiResponseSet)rawResponse));
        }

    }

    public List<ScanInfo> getScans() {
        return this.scans;
    }

    public ScanInfo getScanById(int scanId) {
        Iterator var2 = this.scans.iterator();

        ScanInfo scan;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            scan = (ScanInfo)var2.next();
        } while(scan.getId() != scanId);

        return scan;
    }

    public ScanInfo getLastScan() {
        return (ScanInfo)this.scans.get(this.scans.size() - 1);
    }
}
