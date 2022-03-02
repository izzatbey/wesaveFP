package com.wesavefp.wesaveFP.helper.proxy;

import java.util.List;

import org.zaproxy.clientapi.core.Alert;

public interface ScanningProxy extends LoggingProxy {
    List<Alert> getAlerts() throws ProxyException;

    List<Alert> getAlerts(int var1, int var2) throws ProxyException;

    int getAlertsCount() throws ProxyException;

    void deleteAlerts() throws ProxyException;

    void scan(String var1) throws ProxyException;

    int getScanProgress(int var1) throws ProxyException;

    int getLastScannerScanId() throws ProxyException;

    byte[] getXmlReport() throws ProxyException;

    byte[] getHtmlReport() throws ProxyException;

    void setScannerAttackStrength(String var1, String var2) throws ProxyException;

    void setScannerAlertThreshold(String var1, String var2) throws ProxyException;

    void setEnableScanners(String var1, boolean var2) throws ProxyException;

    void disableAllScanners() throws ProxyException;

    void enableAllScanners() throws ProxyException;

    void setEnablePassiveScan(boolean var1) throws ProxyException;

    void excludeFromScanner(String var1) throws ProxyException;

    void shutdown() throws ProxyException;
}
