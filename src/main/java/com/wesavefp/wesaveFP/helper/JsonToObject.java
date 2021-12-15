package com.wesavefp.wesaveFP.helper;


import com.google.gson.Gson;
import com.wesavefp.wesaveFP.model.database.Scan;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class JsonToObject {


    public static Scan convertFromJson() {
        Gson gson = new Gson();

        try (Reader reader = new FileReader("./output/output.json")) {
            Scan scan = gson.fromJson(reader, Scan.class);
            System.out.println(scan);
            return scan;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static Scan convertFromJson() throws Exception {
//        Object obj = new JSONParser().parse(new FileReader("./output/output.json"));
//        JSONObject jo = (JSONObject) obj;
//
//
//        return null;
//    }


}
