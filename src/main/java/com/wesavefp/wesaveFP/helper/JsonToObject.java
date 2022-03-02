package com.wesavefp.wesaveFP.helper;

import com.wesavefp.wesaveFP.model.database.Alert;
import com.wesavefp.wesaveFP.model.database.Scan;
import com.wesavefp.wesaveFP.model.database.Site;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonToObject {


    public static Scan convertFromJson() {
        JSONParser jsonParser = new JSONParser();
        File f = new File("./output/output.json");
        try (FileReader reader = new FileReader("./output/output.json")){
            Object obj = jsonParser.parse(reader);
            JSONObject list = (JSONObject) obj;
            Scan scan = parseListObject(list);
            f.delete();
            return scan;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Scan parseListObject(JSONObject list) {
        Scan scan = new Scan();
        Site site = new Site();

        String date = (String) list.get("@generated");
//        System.out.println(date);

        JSONArray siteObject = (JSONArray) list.get("site");
//        System.out.println(siteObject);

        JSONObject domainIdx = (JSONObject) siteObject.get(0);
//        System.out.println(domainIdx);

        List<Map<String, String>> alertObj = (List<Map<String, String>>) domainIdx.get("alerts");
        List<Alert> alerts = new ArrayList<Alert>();

        String domain = (String) domainIdx.get("@name");
//        System.out.println(domain);

        String host = (String) domainIdx.get("@host");
//        System.out.println(host);

        String port = (String) domainIdx.get("@port");
//        System.out.println(port);

        String ssl = (String) domainIdx.get("@ssl");
//        System.out.println(ssl);

        scan.setDomain(domain);
        scan.setSite(site);

        site.setDate(date);
        site.setHost(host);
        site.setPort(port);
        site.setSsl(ssl);
        site.setAlerts(alerts);

        //List Alert
        alertObj.forEach(arr -> alerts.add(parseArrayToAlerts(arr)));
//        System.out.println(alerts.toString());

        return scan;
    }

    private static Alert parseArrayToAlerts(Map<String, String> list) {
        Alert alert = new Alert();
        alert.setPluginId(list.get("pluginid"));
        alert.setName(list.get("name"));
        alert.setRisk(list.get("riskdesc"));
        alert.setDesc(list.get("desc"));
        alert.setCount(list.get("count"));
        alert.setSolution(list.get("solution"));
        alert.setReference(list.get("reference"));
        alert.setCweId(list.get("cweid"));
        return alert;
    }


}
