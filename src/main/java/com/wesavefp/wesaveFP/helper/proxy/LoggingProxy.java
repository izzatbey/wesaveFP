package com.wesavefp.wesaveFP.helper.proxy;

import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import java.net.UnknownHostException;
import java.util.List;
import org.openqa.selenium.Proxy;

public interface LoggingProxy {
    void clear() throws ProxyException;

    List<HarEntry> getHistory() throws ProxyException;

    List<HarEntry> getHistory(int var1, int var2) throws ProxyException;

    int getHistoryCount() throws ProxyException;

    List<HarEntry> findInRequestHistory(String var1) throws ProxyException;

    List<HarEntry> findInResponseHistory(String var1) throws ProxyException;

    List<HarEntry> findInResponseHistory(String var1, List<HarEntry> var2);

    List<HarEntry> makeRequest(HarRequest var1, boolean var2) throws ProxyException;

    Proxy getSeleniumProxy() throws UnknownHostException;
}
