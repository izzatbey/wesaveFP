package com.wesavefp.wesaveFP.helper;

import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import java.util.logging.Logger;

public class GenerateReport {
    Logger log = Logger.getLogger(GenerateReport.class.getName());
    private final ClientApi clientApi;
    private final String sites;

    public GenerateReport(String host, int port, String sites) {
        validateHost(host);
        validatePort(port);
        this.clientApi = new ClientApi(host, port);
        log.info("Client API Checked");
        this.sites = sites;
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

    public void generateJson() throws ClientApiException {
        log.info("Report Generated");
        String title = "output";
        String template = "traditional-json";
        String confidences = "Confirmed|High|Medium|Low";
        String risks = "High|Medium|Low|Informational";
        String reportFileName = "output.json";
        String directory = "/home/hduser/Documents/final-project-izzat/wesaveFP/output";
        String display = "false";
        clientApi.reports.generate(title, template, null, null, null, sites, null, confidences, risks, reportFileName, null, directory, display);
    }

}
