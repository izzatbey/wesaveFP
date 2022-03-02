package com.wesavefp.wesaveFP.model.request;

import com.wesavefp.wesaveFP.model.database.Site;

import java.util.ArrayList;

public class CreateScanRequest {

    private String threshold;

    private String strength;

    private String url;

    private ArrayList<String> policyNames = new ArrayList<>();

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getPolicyNames() {
        return policyNames;
    }

    public void setPolicyNames(ArrayList<String> policyNames) {
        this.policyNames = policyNames;
    }
}
