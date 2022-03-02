package com.wesavefp.wesaveFP.helper.autoscanner;

import com.wesavefp.wesaveFP.helper.GenerateReport;
import com.wesavefp.wesaveFP.helper.proxy.ScanningProxy;
import com.wesavefp.wesaveFP.helper.proxy.Spider;
import com.wesavefp.wesaveFP.helper.proxy.ZAProxyScanner;
import com.wesavefp.wesaveFP.model.request.CreateScanRequest;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ClientApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class AutomatedScanner {
     Logger log = Logger.getLogger(AutomatedScanner.class.getName());
    private final String ZAP_HOST = "localhost";
    private final int ZAP_PORT = 8098;
    private final String ZAP_APIKEY = null;
    private ScanningProxy scanner;
    private Spider spider;
    private WebDriver driver;
//    private  String[] policyNames = {"directory-browsing","cross-site-scripting","sql-injection","path-traversal","remote-file-inclusion","server-side-include","script-active-scan-rules","server-side-code-injection","external-redirect","crlf-injection"};
     int currentScanID;
    private CreateScanRequest request;

    public AutomatedScanner(CreateScanRequest request) {
        this.request = request;
    }

    private void reportGenerate() throws ClientApiException {
        GenerateReport generateReport = new GenerateReport(ZAP_HOST, ZAP_PORT, this.request.getUrl());
        generateReport.generateJson();
        log.info("Report JSON Created");
    }

    public void start() throws ClientApiException {
        setup();
        WebSpider();
        reportGenerate();
    }

    public  void setup() {
        scanner = new ZAProxyScanner(ZAP_HOST, ZAP_PORT, ZAP_APIKEY);
        scanner.clear();
        spider = (Spider) scanner;
        log.info("Created client to ZAP API");
        String DRIVER_PATH = "/home/hduser/Documents/final-project-izzat/wesaveFP/drivers/chromedriver";
        driver = DriverFactory.createProxyDriver("chrome",createZapProxyConfigurationForWebDriver(), DRIVER_PATH);
        log.info("Driver Created");
    }

    private  Proxy createZapProxyConfigurationForWebDriver() {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(ZAP_HOST + ":" + ZAP_PORT);
        proxy.setSslProxy(ZAP_HOST + ":" + ZAP_PORT);
        return proxy;
    }

    public void WebSpider() {
        log.info("Spidering...");
        spiderWithZap();
        log.info("Spider done.");

        setAlertAndAttackStrength();
        scanner.setEnablePassiveScan(true);
        scanWithZap();

        List<Alert> alerts = filterAlerts(scanner.getAlerts());
        logAlerts(alerts);
        assertThat(alerts.size(), equalTo(0));
    }

    private void logAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            log.info("Alert: "+alert.getAlert()+" at URL: "+alert.getUrl()+" Parameter: "+alert.getParam()+" CWE ID: "+alert.getCweId());
        }
    }
    /*
        Remove false positives, filter based on risk and reliability
     */
    private  List<Alert> filterAlerts(List<Alert> alerts) {
        List<Alert> filtered = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getRisk().equals(Alert.Risk.High) && alert.getConfidence() != Alert.Confidence.Low) filtered.add(alert);
        }
        return filtered;
    }

    private void scanWithZap() {
        log.info("Scanning...");
        scanner.scan(this.request.getUrl());
        currentScanID = scanner.getLastScannerScanId();
        int complete = 0;
        while (complete < 100) {
            complete = scanner.getScanProgress(currentScanID);
            log.info("Scan is " + complete + "% complete.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Scanning done.");
    }

    public void setAlertAndAttackStrength() {
        for (String policyName : this.request.getPolicyNames()) {
            String ids = enableZapPolicy(policyName);
            for (String id : ids.split(",")) {
                scanner.setScannerAlertThreshold(id, this.request.getThreshold());
                scanner.setScannerAttackStrength(id, this.request.getStrength());
            }
        }
    }

    private  void spiderWithZap() {
        spider.setThreadCount(5);
        spider.setMaxDepth(5);
        spider.setPostForms(false);
        spider.spider(this.request.getUrl());
        int spiderID = spider.getLastSpiderScanId();
        int complete  = 0;
        while (complete < 100) {
            complete = spider.getSpiderProgress(spiderID);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (String url : spider.getSpiderResults(spiderID)) {
            log.info("Found URL: "+url);
        }
    }

    private  String enableZapPolicy(String policyName) {
        String scannerIds = null;
        switch (policyName.toLowerCase()) {
            case "directory-browsing":
                scannerIds = "0";
                break;
            case "cross-site-scripting":
                scannerIds = "40012,40014,40016,40017";
                break;
            case "sql-injection":
                scannerIds = "40018";
                break;
            case "path-traversal":
                scannerIds = "6";
                break;
            case "remote-file-inclusion":
                scannerIds = "7";
                break;
            case "server-side-include":
                scannerIds = "40009";
                break;
            case "script-active-scan-rules":
                scannerIds = "50000";
                break;
            case "server-side-code-injection":
                scannerIds = "90019";
                break;
            case "remote-os-command-injection":
                scannerIds = "90020";
                break;
            case "external-redirect":
                scannerIds = "20019";
                break;
            case "crlf-injection":
                scannerIds = "40003";
                break;
            case "source-code-disclosure":
                scannerIds = "42,10045,20017";
                break;
            case "shell-shock":
                scannerIds = "10048";
                break;
            case "remote-code-execution":
                scannerIds = "20018";
                break;
            case "ldap-injection":
                scannerIds = "40015";
                break;
            case "xpath-injection":
                scannerIds = "90021";
                break;
            case "xml-external-entity":
                scannerIds = "90023";
                break;
            case "padding-oracle":
                scannerIds = "90024";
                break;
            case "el-injection":
                scannerIds = "90025";
                break;
            case "insecure-http-methods":
                scannerIds = "90028";
                break;
            case "parameter-pollution":
                scannerIds = "20014";
                break;
            default : throw new RuntimeException("No policy found for: "+policyName);
        }
        if (scannerIds == null) throw new RuntimeException("No matching policy found for: " + policyName);
        scanner.setEnableScanners(scannerIds, true);
        return scannerIds;
    }
}

