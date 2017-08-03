package com.chervon.iot.common.db2csv;


import com.chervon.iot.common.db2csv.parser.JsonFlattener;
import com.chervon.iot.common.db2csv.writer.CSVWriter;
import net.sf.json.JSONArray;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Json2CSV {
    public static void json2csv(String jsonValue,String filename) throws Exception {
        JsonFlattener parser = new JsonFlattener();
        CSVWriter writer = new CSVWriter();

        List<Map<String, String>> flatJson = parser.parseJson(jsonValue);
        writer.writeAsCSV(flatJson, filename);
    }
    public static void jsonArray2csv(JSONArray jsonArray, String filename) throws FileNotFoundException {
        JsonFlattener parser = new JsonFlattener();
        CSVWriter writer = new CSVWriter();

        List<Map<String, String>> flatJson = parser.parse(jsonArray);
        writer.writeAsCSV(flatJson, filename);
    }

    private static String jsonValue() {
        return  "       [" +
                "           {" +
                "               \"id\": \"1\"," +
                "               \"name\": \"Julie Sherman\"," +
                "               \"gender\" : \"female\"," +
                "               \"latitude\" : \"37.33774833333334\"," +
                "               \"longitude\" : \"-121.88670166666667\"" +
                "           }," +
                "           {" +
                "               \"id\": \"2\"," +
                "               \"name\": \"Johnny Depp\"," +
                "               \"gender\" : \"male\"," +
                "               \"latitude\" : \"37.336453\"," +
                "               \"longitude\" : \"-121.884985\"" +
                "           }" +
                "       ]";
    }
}
