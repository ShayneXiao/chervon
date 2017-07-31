package com.chervon.iot.common.db2csv.writer;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVWriter {

    public void writeAsCSV(List<Map<String, String>> flatJson, String fileName) throws FileNotFoundException {
//        String output = "FirstName__c,LastName__c" + "\n";

        Set<String> headers = collectHeaders(flatJson);
        String output = StringUtils.join(headers.toArray(), ",") + "\n";
        for (Map<String, String> map : flatJson) {
            output = output + getCommaSeperatedRow(headers, map) + "\n";
        }

        writeToFile(output, fileName);
    }

    private void writeToFile(String output, String fileName) throws FileNotFoundException {
        synchronized("write") {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileName,true));
                writer.write(output);
                System.out.println("-----------------写出了csv文件----"+System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("-----------------写出csv文件：失败----"+System.currentTimeMillis());
            } finally {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                close(writer);
            }
        }
    }

    private void close(BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCommaSeperatedRow(Set<String> headers, Map<String, String> map) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replace(",", "");
            items.add(value);
        }
        return StringUtils.join(items.toArray(), ",");
    }

    private Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new TreeSet<String>();
        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }
}
