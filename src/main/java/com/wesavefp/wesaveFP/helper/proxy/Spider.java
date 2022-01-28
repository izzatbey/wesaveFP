package com.wesavefp.wesaveFP.helper.proxy;

import java.util.List;

public interface Spider {
    void spider(String var1);

    int getSpiderProgress(int var1);

    int getLastSpiderScanId();

    List<String> getSpiderResults(int var1);

    void excludeFromSpider(String var1);

    void setMaxDepth(int var1);

    void setPostForms(boolean var1);

    void setThreadCount(int var1);
}
