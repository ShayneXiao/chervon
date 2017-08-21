package com.chervon.iot.common.db2csv;

import com.chervon.iot.common.bulk.Bulk;
import com.chervon.iot.mobile.mapper.PriorityMapper;
import com.sforce.async.AsyncApiException;
import com.sforce.ws.ConnectionException;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ZAC on 2017-7-6.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Component
public class DB2CSV{
    @Autowired
    private PriorityMapper priorityMapper;
    @Autowired
    private Bulk bulk;

    private static List<String> filenames = new ArrayList<>();

    @Scheduled(fixedRateString = "${cronString}")
    public void tableToCSV() throws ConnectionException, AsyncApiException, InterruptedException, IOException {
        System.out.println("-------------Scheduled 启动----");

        List<String> msgTypes = priorityMapper.selectMsgType();
        for (String msgtype : msgTypes) {
            String filename = generateFilename();
            Long beforeMsgtable = System.currentTimeMillis();
            JSONArray jsonArray = null;

            if (jsonArray != null && jsonArray.length() > 0) {
                List<Long> ids = null;

                filenames.add(filename);

                File file = new File(filename);
                while(true){
                    if (!file.exists()) {
                        Thread.sleep(100L);
                    } else {
                        bulk.ids = ids;
                        bulk.run("BulkTest__c", "muke.meng@chervon.com.cn.pa",
                                "muke4321", filename);
                        file.delete();
                        break;
                    }
                }
            }
        }
    }

    private String generateFilename() {
        String filename = "";
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = filename + "msgtable";
        filename += "_";
        filename += sdf.format(new Date());
        filename += ".csv";
        return filename;
    }
}
