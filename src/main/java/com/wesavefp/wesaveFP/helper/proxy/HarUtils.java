package com.wesavefp.wesaveFP.helper.proxy;

import edu.umass.cs.benchlab.har.HarHeader;
import edu.umass.cs.benchlab.har.HarRequest;
import java.util.Iterator;

public class HarUtils {
    public HarUtils() {
    }

    public static HarRequest changeCookieValue(HarRequest request, String name, String value) {
        String patternMulti = "([; ]" + name + ")=[^;]*(.*)";
        String patternStart = "^(" + name + ")=[^;]*(.*)";
        Iterator var5 = request.getHeaders().getHeaders().iterator();

        while(var5.hasNext()) {
            HarHeader header = (HarHeader)var5.next();
            if (header.getName().equalsIgnoreCase("COOKIE") && header.getValue() != null) {
                String updated = header.getValue().replaceAll(patternMulti, "$1=" + value + "$2");
                if (updated.equals(header.getValue())) {
                    updated = header.getValue().replaceAll(patternStart, "$1=" + value + "$2");
                }

                header.setValue(updated);
            }
        }

        return request;
    }
}
