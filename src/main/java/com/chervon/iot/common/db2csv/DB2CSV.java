package com.chervon.iot.common.db2csv;

import com.chervon.iot.common.bulk.Bulk;
import com.chervon.iot.common.mq.jdbcutils.JDBCUtils;
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
//    private static List<String> filenames = new ArrayList<>();
    @Autowired
    private JDBCUtils jdbcUtils;
    @Autowired
    private PriorityMapper priorityMapper;
    @Autowired
    private Bulk bulk;
    private Integer exportNum = 10000;     //导出记录的条数，此数据与AmqpConfig配置文件中doExportNum相同

    /**
     * 从数据库生成csv文件
     * @throws Exception
     */
    public void startTableToCSV() throws AsyncApiException, InterruptedException, ConnectionException, IOException {
        System.out.println("-------------开始执行导出数据库数据操作----");
        List<String> msgTypes = priorityMapper.selectMsgType();
        for (String msgtype : msgTypes) {
            String filename = generateFilename();
            Long beforeMsgtable = System.currentTimeMillis();
            System.out.println("-------------开始查询msgtable数据----"+beforeMsgtable);
            JSONArray jsonArray = jdbcUtils.selectMsgtable(msgtype, exportNum);
            Long afterMsgtable = System.currentTimeMillis();
            System.out.println("-------------结束查询msgtable数据----"+afterMsgtable+
                    "__"+(afterMsgtable-beforeMsgtable));

            if (jsonArray != null && jsonArray.length() > 0) {
                Long beforeIds = System.currentTimeMillis();
                System.out.println("-------------开始查询msgtable id----"+beforeIds);
                List<Long> ids = jdbcUtils.selectMsgtableIds(msgtype, exportNum);
                Long afterIds = System.currentTimeMillis();
                System.out.println("-------------结束查询msgtable id----"+afterIds+
                        "__"+(afterIds-beforeIds));

                Long beforeCsv =  System.currentTimeMillis();
                System.out.println("-------------开始将数据写出为csv文件----"+beforeCsv);
                Json2CSV.jsonArray2csv(jsonArray, filename);
                Long afterCsv = System.currentTimeMillis();
                System.out.println("-------------成功将数据写出为csv文件----"+afterCsv+
                        "__"+(afterCsv-beforeCsv));

//                filenames.add(filename);

                File file = new File(filename);
                while(true){
                    if (!file.exists()) {
                        Thread.sleep(100L);
                    } else {
                        bulk.ids = ids;
                        Long beforeBulk = System.currentTimeMillis();
                        System.out.println("-----------------开始执行bulk api----"+beforeBulk);
                        bulk.run("BulkTest__c", "muke.meng@chervon.com.cn.pa",
                                "muke4321", filename);
                        Long afterBulk = System.currentTimeMillis();
                        System.out.println("-----------------Bulk API执行成功----"+afterBulk+
                                "__"+(afterBulk-beforeBulk));
                        file.delete();
                        break;
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void tableToCSV() throws ConnectionException, AsyncApiException, InterruptedException, IOException {
        System.out.println("-------------Scheduled 启动----");

        List<String> msgTypes = priorityMapper.selectMsgType();
        for (String msgtype : msgTypes) {
            String filename = generateFilename();
            Long beforeMsgtable = System.currentTimeMillis();
            System.out.println("-------------Scheduled 开始查询msgtable数据----"+beforeMsgtable);
            JSONArray jsonArray = jdbcUtils.selectMsgtableAll(msgtype);
            Long afterMsgtable = System.currentTimeMillis();
            System.out.println("-------------Scheduled 结束查询msgtable数据----"+afterMsgtable+
                    "__"+(afterMsgtable-beforeMsgtable));

            if (jsonArray != null && jsonArray.length() > 0) {
                Long beforeIds = System.currentTimeMillis();
                System.out.println("-------------Scheduled 开始查询msgtable id----"+beforeIds);
                List<Long> ids = jdbcUtils.selectMsgtableAllIds(msgtype);
                Long afterIds = System.currentTimeMillis();
                System.out.println("-------------Scheduled 结束查询msgtable id----"+afterIds+
                        "__"+(afterIds-beforeIds));

                Long beforeCsv =  System.currentTimeMillis();
                System.out.println("-------------Scheduled 开始将数据写出为csv文件----"+beforeCsv);
                Json2CSV.jsonArray2csv(jsonArray, filename);
                Long afterCsv = System.currentTimeMillis();
                System.out.println("-------------Scheduled 成功将数据写出为csv文件----"+afterCsv+
                        "__"+(afterCsv-beforeCsv));

//                filenames.add(filename);

                File file = new File(filename);
                while(true){
                    if (!file.exists()) {
                        Thread.sleep(100L);
                    } else {
                        bulk.ids = ids;
                        Long beforeBulk = System.currentTimeMillis();
                        System.out.println("-----------------Scheduled 开始执行Bulk Api----"+beforeBulk);
                        bulk.run("BulkTest__c", "muke.meng@chervon.com.cn.pa",
                                "muke4321", filename);
                        Long afterBulk = System.currentTimeMillis();
                        System.out.println("-----------------Scheduled Bulk Api执行成功----"+afterBulk+
                                "__"+(afterBulk-beforeBulk));
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

//    @Scheduled(fixedRate = 35 * 60 * 1000)
//    public void deleteCsv() {
//        System.out.println("--------每隔35分执行一次的方法启动了--------");
//        if (filenames != null && filenames.size() > 0) {
//            String filename = filenames.get(0);
//            File file = new File(filename);
//            file.delete();
//        }
//    }
}
