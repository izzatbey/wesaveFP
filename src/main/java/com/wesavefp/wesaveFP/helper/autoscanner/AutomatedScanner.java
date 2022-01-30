package com.wesavefp.wesaveFP.helper.autoscanner;

import com.google.gson.Gson;
import com.wesavefp.wesaveFP.helper.GenerateReport;
import com.wesavefp.wesaveFP.helper.JsonToObject;
import com.wesavefp.wesaveFP.helper.proxy.ScanningProxy;
import com.wesavefp.wesaveFP.helper.proxy.Spider;
import com.wesavefp.wesaveFP.helper.proxy.ZAProxyScanner;
import com.wesavefp.wesaveFP.model.database.Scan;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AutomatedScanner {
    static Logger log = Logger.getLogger(AutomatedScanner.class.getName());
    private final static String ZAP_HOST = "localhost";
    private final static int ZAP_PORT = 8098;
    private final static String ZAP_APIKEY = null;
    private ClientApi clientApi;
    private final static String DRIVER_PATH = "/home/hduser/Documents/final-project-izzat/wesaveFP/drivers/chromedriver";
    private static String THRESHOLD = "MEDIUM";
    private static String STRENGTH = "HIGH";
    private static ScanningProxy scanner;
    private static Spider spider;
    private static WebDriver driver;
    private static AppNavigation appNav;
    public static String BASE_URL = "http://localhost:3000";
    private final static String[] policyNames = {"directory-browsing","cross-site-scripting","sql-injection","path-traversal","remote-file-inclusion","server-side-include",
            "script-active-scan-rules","server-side-code-injection","external-redirect","crlf-injection"};
    static int currentScanID;

    public static void reportGenerate() throws ClientApiException, IOException, ClassNotFoundException {
        GenerateReport generateReport = new GenerateReport(ZAP_HOST, ZAP_PORT, BASE_URL);
        generateReport.generateJson();
        log.info("Report JSON Created");
    }

    public static void start() throws IOException {
        setup();
        WebSpider();
    }

    public static void setup() {
        scanner = new ZAProxyScanner(ZAP_HOST, ZAP_PORT, ZAP_APIKEY);
        scanner.clear();
        spider = (Spider) scanner;
        log.info("Created client to ZAP API");
        driver = DriverFactory.createProxyDriver("chrome",createZapProxyConfigurationForWebDriver(), DRIVER_PATH);
        log.info("Driver Created");
        appNav = new AppNavigation(driver, BASE_URL);
    }

//    public void setupDriver() throws ClientApiException {
//        this.clientApi = new ClientApi(ZAP_HOST, ZAP_PORT);
//        clientApi.selenium.setOptionChromeDriverPath(DRIVER_PATH);
//    }

    private static Proxy createZapProxyConfigurationForWebDriver() {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(ZAP_HOST + ":" + ZAP_PORT);
        proxy.setSslProxy(ZAP_HOST + ":" + ZAP_PORT);
        return proxy;
    }

    public static void WebSpider() throws IOException{
        log.info("Spidering...");
        spiderWithZap();
        log.info("Spider done.");

        setAlertAndAttackStrength();
        scanner.setEnablePassiveScan(true);
        scanWithZap();
    }

    private static void logAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            log.info("Alert: "+alert.getAlert()+" at URL: "+alert.getUrl()+" Parameter: "+alert.getParam()+" CWE ID: "+alert.getCweId());
        }
    }
    /*
        Remove false positives, filter based on risk and reliability
     */
    private static List<Alert> filterAlerts(List<Alert> alerts) {
        List<Alert> filtered = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getRisk().equals(Alert.Risk.High) && alert.getConfidence() != Alert.Confidence.Low) filtered.add(alert);
        }
        return filtered;
    }

    private static void scanWithZap() {
        log.info("Scanning...");
        scanner.scan(BASE_URL);
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

    public static void setAlertAndAttackStrength() {
        for (String policyName : policyNames) {
            String ids = enableZapPolicy(policyName);
            for (String id : ids.split(",")) {
                scanner.setScannerAlertThreshold(id,THRESHOLD);
                scanner.setScannerAttackStrength(id,STRENGTH);
            }
        }
    }

    private static void spiderWithZap() {
        spider.setThreadCount(5);
        spider.setMaxDepth(5);
        spider.setPostForms(false);
        spider.spider(BASE_URL);
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

    private static String enableZapPolicy(String policyName) {
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

