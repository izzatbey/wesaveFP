package com.wesavefp.wesaveFP.helper.proxy;

import com.wesavefp.wesaveFP.helper.proxy.model.ScanResponse;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarLog;
import edu.umass.cs.benchlab.har.HarRequest;
import edu.umass.cs.benchlab.har.tools.HarFileReader;
import org.openqa.selenium.Proxy;
import org.zaproxy.clientapi.core.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ZAProxyScanner implements ScanningProxy, Spider {
    Logger log = Logger.getLogger(ZAProxyScanner.class.getName());
    private static final String MINIMUM_ZAP_DAILY_VERSION = "D-2022-01-24";
    private static final String MINIMUM_ZAP_VERSION = "2.11.1";
    private final ClientApi clientApi;
    private final Proxy seleniumProxy;
    private final String apiKey;

    public ZAProxyScanner(String host, int port, String apiKey) throws IllegalArgumentException, ProxyException {
        validateHost(host);
        validatePort(port);
        this.apiKey = apiKey;
        this.clientApi = new ClientApi(host, port);
        this.validateMinimumRequiredZapVersion();
        this.seleniumProxy = new Proxy();
        this.seleniumProxy.setProxyType(Proxy.ProxyType.PAC);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("http://").append(host).append(":").append(port).append("/proxy.pac");
        this.seleniumProxy.setProxyAutoconfigUrl(strBuilder.toString());
        log.info("ZAP Proxy Created");
    }

    private static void validateHost(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Parameter host must not be null.");
        } else if (host.isEmpty()) {
            throw new IllegalArgumentException("Parameter host must not be empty.");
        }
    }

    private static void validatePort(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Parameter port must be between 1 and 65535.");
        }
    }

    private void validateMinimumRequiredZapVersion() throws ProxyException {
        try {
            String zapVersion = ((ApiResponseElement)this.clientApi.core.version()).getValue();
            boolean minimumRequiredZapVersion = false;
            if (zapVersion.startsWith("D-")) {
                minimumRequiredZapVersion = zapVersion.compareTo("D-2013-11-17") >= 0;
            } else {
                minimumRequiredZapVersion = compareZapVersions(zapVersion, "2.3") >= 0;
            }

            if (!minimumRequiredZapVersion) {
                throw new IllegalStateException("Minimum required ZAP version not met, expected >= \"D-2013-11-17\" or >= \"2.3\" but got: " + zapVersion);
            }
        } catch (ClientApiException var3) {
            var3.printStackTrace();
            throw new ProxyException(var3);
        }
    }

    private static int compareZapVersions(String version, String otherVersion) {
        String[] v1 = version.split("\\.");
        String[] v2 = otherVersion.split("\\.");

        for(int i = 0; i < v1.length; ++i) {
            if (i >= v2.length) {
                return 1;
            }
            if (!v1[i].equals(v2[i])) {
                return Integer.parseInt(v1[i]) - Integer.parseInt(v2[i]);
            }
        }
        return -1;
    }

    private static class ClientApiUtils {
        private ClientApiUtils() {
        }

        public static int getInteger(ApiResponse response) throws ClientApiException {
            try {
                return Integer.parseInt(((ApiResponseElement)response).getValue());
            } catch (Exception var2) {
                throw new ClientApiException("Unable to get integer from response.");
            }
        }

        public static String convertHarRequestToString(HarRequest request) throws ClientApiException {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JsonGenerator g = (new JsonFactory()).createJsonGenerator(os);
                g.writeStartObject();
                request.writeHar(g);
                g.close();
                return os.toString("UTF-8");
            } catch (IOException var3) {
                throw new ClientApiException(var3);
            }
        }

        public static HarLog createHarLog(byte[] bytesHarLog) throws ClientApiException {
            try {
                if (bytesHarLog.length == 0) {
                    throw new ClientApiException("Unexpected ZAP response.");
                } else {
                    HarFileReader reader = new HarFileReader();
                    return reader.readHarFile(new ByteArrayInputStream(bytesHarLog), (List)null);
                }
            } catch (IOException var2) {
                throw new ClientApiException(var2);
            }
        }

        public static List<HarEntry> getHarEntries(byte[] bytesHarLog) throws ClientApiException {
            return createHarLog(bytesHarLog).getEntries().getEntries();
        }
    }

    @Override
    public void clear() throws ProxyException {
        try {
            this.clientApi.ascan.removeAllScans(this.apiKey);
            this.clientApi.core.newSession(this.apiKey, "", "");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<HarEntry> getHistory() throws ProxyException {
        return this.getHistory(-1, -1);
    }

    @Override
    public List<HarEntry> getHistory(int start, int count) throws ProxyException {
        try {
            return ZAProxyScanner.ClientApiUtils.getHarEntries(this.clientApi.core.messagesHar(this.apiKey, "", Integer.toString(start), Integer.toString(count)));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getHistoryCount() throws ProxyException {
        try {
            return ZAProxyScanner.ClientApiUtils.getInteger(this.clientApi.core.numberOfMessages(""));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<HarEntry> findInRequestHistory(String regex) throws ProxyException {
        try {
            return ZAProxyScanner.ClientApiUtils.getHarEntries(this.clientApi.search.harByRequestRegex(this.apiKey, regex, "", "-1", "-1"));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<HarEntry> findInResponseHistory(String regex) throws ProxyException {
        try {
            return ZAProxyScanner.ClientApiUtils.getHarEntries(this.clientApi.search.harByResponseRegex(this.apiKey, regex, "", "-1", "-1"));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<HarEntry> findInResponseHistory(String regex, List<HarEntry> entries) {
        List<HarEntry> found = new ArrayList();
        Iterator var4 = entries.iterator();

        while(var4.hasNext()) {
            HarEntry entry = (HarEntry)var4.next();
            if (entry.getResponse().getContent() != null) {
                String content = entry.getResponse().getContent().getText();
                if ("base64".equalsIgnoreCase(entry.getResponse().getContent().getEncoding())) {
                    content = new String(Base64.decodeBase64(content));
                }

                if (content.contains(regex)) {
                    found.add(entry);
                }
            }
        }

        return found;
    }

    @Override
    public List<HarEntry> makeRequest(HarRequest request, boolean followRedirect) throws ProxyException {
        try {
            String harRequestStr = ZAProxyScanner.ClientApiUtils.convertHarRequestToString(request);
            return ZAProxyScanner.ClientApiUtils.getHarEntries(this.clientApi.core.sendHarRequest(this.apiKey, harRequestStr, Boolean.toString(followRedirect)));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public Proxy getSeleniumProxy() throws UnknownHostException {
        return this.seleniumProxy;
    }

    @Override
    public List<Alert> getAlerts() throws ProxyException {
        return this.getAlerts(-1, -1);
    }

    @Override
    public List<Alert> getAlerts(int start, int count) throws ProxyException {
        try {
            return this.clientApi.getAlerts("", start, count);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getAlertsCount() throws ProxyException {
        try {
            return ZAProxyScanner.ClientApiUtils.getInteger(this.clientApi.core.numberOfAlerts(""));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void deleteAlerts() throws ProxyException {
        try {
            this.clientApi.core.deleteAllAlerts(this.apiKey);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void scan(String url) throws ProxyException {
        try {
            this.clientApi.ascan.scan(this.apiKey, url, "true", "false", (String)null, (String)null, (String)null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getScanProgress(int id) throws ProxyException {
        try {
            ApiResponseList response = (ApiResponseList)this.clientApi.ascan.scans();
            return (new ScanResponse(response)).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getLastScannerScanId() throws ProxyException {
        try {
            ApiResponseList response = (ApiResponseList)this.clientApi.ascan.scans();
            return (new ScanResponse(response)).getLastScan().getId();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public byte[] getXmlReport() throws ProxyException {
        try {
            return this.clientApi.core.xmlreport(this.apiKey);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public byte[] getHtmlReport() throws ProxyException {
        try {
            return this.clientApi.core.htmlreport(this.apiKey);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setScannerAttackStrength(String scannerId, String strength) throws ProxyException {
        try {
            this.clientApi.ascan.setScannerAttackStrength(this.apiKey, scannerId, strength, (String)null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException("Error occured for setScannerAttackStrength", e);
        }
    }

    @Override
    public void setScannerAlertThreshold(String scannerId, String threshold) throws ProxyException {
        try {
            this.clientApi.ascan.setScannerAlertThreshold(this.apiKey, scannerId, threshold, (String)null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setEnableScanners(String ids, boolean enabled) throws ProxyException {
        try {
            if (enabled) {
                this.clientApi.ascan.enableScanners(ids, "Default Policy");
            } else {
                this.clientApi.ascan.disableScanners(ids, "Default Policy");
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disableAllScanners() throws ProxyException {
        try {
            ApiResponse response = this.clientApi.pscan.setEnabled(this.apiKey, "false");
            response = this.clientApi.ascan.disableAllScanners(this.apiKey, (String)null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void enableAllScanners() throws ProxyException {
        try {
            this.clientApi.pscan.setEnabled(this.apiKey, "true");
            this.clientApi.ascan.enableAllScanners(this.apiKey, (String)null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setEnablePassiveScan(boolean enabled) throws ProxyException {
        try {
            this.clientApi.pscan.setEnabled(this.apiKey, Boolean.toString(enabled));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void excludeFromScanner(String regex) throws ProxyException {
        try {
            this.clientApi.ascan.excludeFromScan(this.apiKey, regex);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void shutdown() throws ProxyException {
        try {
            this.clientApi.core.shutdown(this.apiKey);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void spider(String url) {
        try {
            this.clientApi.spider.scan(url, "0", "false" ,(String)null, "true");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getSpiderProgress(int id) {
        try {
            ApiResponseList response = (ApiResponseList)this.clientApi.spider.scans();
            return (new ScanResponse(response)).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getLastSpiderScanId() {
        try {
            ApiResponseList response = (ApiResponseList)this.clientApi.spider.scans();
            return (new ScanResponse(response)).getLastScan().getId();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<String> getSpiderResults(int id) {
        ArrayList results = new ArrayList();

        try {
            ApiResponseList responseList = (ApiResponseList)this.clientApi.spider.results(Integer.toString(id));
            Iterator itr = responseList.getItems().iterator();

            while(itr.hasNext()) {
                ApiResponse response = (ApiResponse)itr.next();
                results.add(((ApiResponseElement)response).getValue());
            }

            return results;
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void excludeFromSpider(String regex) {
        try {
            this.clientApi.spider.excludeFromScan(this.apiKey, regex);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setMaxDepth(int depth) {
        try {
            this.clientApi.spider.setOptionMaxDepth(this.apiKey, depth);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setPostForms(boolean post) {
        try {
            this.clientApi.spider.setOptionPostForm(this.apiKey, post);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setThreadCount(int threads) {
        try {
            this.clientApi.spider.setOptionThreadCount(this.apiKey, threads);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }
}
