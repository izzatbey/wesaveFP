package com.wesavefp.wesaveFP.model.request;

import com.wesavefp.wesaveFP.model.database.Site;

public class CreateScanRequest {

    private String domain;

    private Site site;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
