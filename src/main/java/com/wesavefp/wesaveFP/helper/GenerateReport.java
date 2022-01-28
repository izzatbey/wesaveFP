package com.wesavefp.wesaveFP.helper;

import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.gen.Reports;

import java.util.logging.Logger;

public class GenerateReport {
    Logger log = Logger.getLogger(GenerateReport.class.getName());
    private final ClientApi clientApi;
    private final Reports reports;
    private final String template;

    public GenerateReport(String host, int port, String template) throws ClientApiException {
        validateHost(host);
        validatePort(port);
        this.clientApi = new ClientApi(host, port);
        log.info("Client API Checked");
        this.template = template;
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

    public byte[] generate() throws ClientApiException {
        log.info("Report Generated");
        return clientApi.core.jsonreport();
    }

}
