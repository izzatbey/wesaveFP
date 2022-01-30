package com.wesavefp.wesaveFP.helper;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.gen.Reports;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class GenerateReport {
    Logger log = Logger.getLogger(GenerateReport.class.getName());
    private final ClientApi clientApi;
    private final Reports reports;
    private final String title = "output";
    private final String template = "traditional-json";
    private final String theme = "Original";
    private final String contexts = "Default Context";
    private final String sites;
    private final String confidences = "Confirmed|High|Medium|Low";
    private final String risks = "High|Medium|Low|Informational";
    private final String reportFileName = "output.json";
    private final String fileNamePattern = "output";
    private final String directory = "/home/hduser/Documents/final-project-izzat/wesaveFP/output";
    private final String display = "false";

    public GenerateReport(String host, int port, String sites) throws ClientApiException {
        validateHost(host);
        validatePort(port);
        this.clientApi = new ClientApi(host, port);
        log.info("Client API Checked");
        this.sites = sites;
        this.reports = new Reports(clientApi);

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

    public ApiResponse generateJson() throws ClientApiException {
        log.info("Report Generated");
        return clientApi.reports.generate(title, template, null, null, null, sites, null, confidences, risks, reportFileName, null, directory, display);
    }

}
