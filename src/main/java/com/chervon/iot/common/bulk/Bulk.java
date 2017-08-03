package com.chervon.iot.common.bulk;

import com.chervon.iot.common.mq.jdbcutils.JDBCUtils;
import com.sforce.async.*;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * Created by ZAC on 2017-7-3.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Component
public class Bulk {
    public List<Long> ids;  //bulk操作的每条记录对应的数据库中的id的集合
    private String authEndpoint = "https://test.salesforce.com/services/Soap/u/40.0";

    @Autowired
    private JDBCUtils jdbcUtils;

    /**
     * 创建一个批量api作业并以csv文件的方式上传批量作业
     * @param sobjectType
     * @param username
     * @param password
     * @param sampleFileName
     */
    public void run(String sobjectType, String username, String password, String sampleFileName)
            throws ConnectionException, AsyncApiException, IOException, InterruptedException {
        BulkConnection connection = getBulkConnection(username, password);
        JobInfo job = createJob(sobjectType, connection);
        List<BatchInfo> batchInfoList = createBatchesFromCSVFile(connection, job,
                sampleFileName);
        closeJob(connection, job.getId());
        awaitCompletion(connection, job, batchInfoList);
        checkResults(connection, job, batchInfoList);
    }

    /**
     * 创建一个Bulk连接，用于调用Bulk API操作
     * @param username
     * @param password
     * @return
     */
    private BulkConnection getBulkConnection(String username, String password)
            throws ConnectionException, AsyncApiException, InterruptedException {
        ConnectorConfig partnerConfig = new ConnectorConfig();
        partnerConfig.setUsername(username);
        partnerConfig.setPassword(password);
        partnerConfig.setAuthEndpoint(authEndpoint);
        // Creating the connection automatically handles login and stores
        // the session in partnerConfig
        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                throw new ConnectionException("---------------------PartnerConnection连接失败----");
            }
            try {
                new PartnerConnection(partnerConfig);
                break;
            } catch (Exception e) {
                System.out.println("---------------------PartnerConnection连接异常，重新连接,第"+(i+1)+"次----");
                Thread.sleep(5000L);
            }
        }
        // When PartnerConnection is instantiated, a login is implicitly
        // executed and, if successful,
        // a valid session is stored in the ConnectorConfig instance.
        // Use this key to initialize a BulkConnection:
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(partnerConfig.getSessionId());
        // The endpoint for the Bulk API service is the same as for the normal
        // SOAP uri until the /Soap/ part. From here it's '/async/versionNumber'
        String soapEndpoint = partnerConfig.getServiceEndpoint();
        String apiVersion = "40.0";
        String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))
                + "async/" + apiVersion;
        config.setRestEndpoint(restEndpoint);
        // This should only be false when doing debugging.
        config.setCompression(true);
        // Set this to true to see HTTP requests and responses on stdout
        config.setTraceMessage(false);
        BulkConnection connection = new BulkConnection(config);
        return connection;
    }

    /**
     * 用Bulk API创建一个Job
     * @param sobjectType
     * @param connection
     * @return
     */
    private JobInfo createJob(String sobjectType, BulkConnection connection)
            throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setObject(sobjectType);
        job.setOperation(OperationEnum.insert);
        job.setContentType(ContentType.CSV);
        job = connection.createJob(job);
        System.out.println(job + "---------------------创建了Job----");
        return job;
    }

    /**
     * 用CSV文件创建一个上传批处理
     * @param connection
     * @param jobInfo
     * @param csvFileName
     * @return
     * @throws FileNotFoundException
     */
    private List<BatchInfo> createBatchesFromCSVFile(BulkConnection connection,
                                                     JobInfo jobInfo, String csvFileName)
            throws IOException, AsyncApiException {
        List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
        BufferedReader rdr = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFileName))
        );
        // read the CSV header row
        byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
        int headerBytesLength = headerBytes.length;
        File tmpFile = File.createTempFile("bulkAPIInsert", ".csv");

        // Split the CSV file into multiple batches
        try {
            FileOutputStream tmpOut = new FileOutputStream(tmpFile);
            int maxBytesPerBatch = 10000000; // 10 million bytes per batch
            int maxRowsPerBatch = 10000; // 10 thousand rows per batch
            int currentBytes = 0;
            int currentLines = 0;
            String nextLine;
            while ((nextLine = rdr.readLine()) != null) {
                byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
                // Create a new batch when our batch size limit is reached
                if (currentBytes + bytes.length > maxBytesPerBatch
                        || currentLines > maxRowsPerBatch) {
                    createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
                    currentBytes = 0;
                    currentLines = 0;
                }
                if (currentBytes == 0) {
                    tmpOut = new FileOutputStream(tmpFile);
                    tmpOut.write(headerBytes);
                    currentBytes = headerBytesLength;
                    currentLines = 1;
                }
                tmpOut.write(bytes);
                currentBytes += bytes.length;
                currentLines++;
            }
            // Finished processing all rows
            // Create a final batch for any remaining data
            if (currentLines > 1) {
                createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
            }
        } finally {
            tmpFile.delete();
        }
        return batchInfos;
    }

    /**
     * 通过上传文件的内容创建一个批处理。这个方法关闭了输出流。
     * @param tmpOut
     * @param tmpFile
     * @param batchInfos
     * @param connection
     * @param jobInfo
     * @throws IOException
     */
    private void createBatch(FileOutputStream tmpOut, File tmpFile,
                             List<BatchInfo> batchInfos, BulkConnection connection, JobInfo jobInfo)
            throws IOException, AsyncApiException {
        tmpOut.flush();
        tmpOut.close();
        FileInputStream tmpInputStream = new FileInputStream(tmpFile);
        try {
            BatchInfo batchInfo =
                    connection.createBatchFromStream(jobInfo, tmpInputStream);
            System.out.println(batchInfo + "---------------------创建了Batch----");
            batchInfos.add(batchInfo);
        } finally {
            tmpInputStream.close();
        }
    }

    /**
     * 关闭Job
     * @param connection
     * @param jobId
     * @throws AsyncApiException
     */
    private void closeJob(BulkConnection connection, String jobId)
            throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setId(jobId);
        job.setState(JobStateEnum.Closed);
        connection.updateJob(job);
    }

    /**
     * 通过查询Bulk API等待Job完成
     * @param connection
     * @param job
     * @param batchInfoList
     */
    private void awaitCompletion(BulkConnection connection, JobInfo job,
                                 List<BatchInfo> batchInfoList)
            throws AsyncApiException {
        long sleepTime = 0L;
        Set<String> incomplete = new HashSet<String>();
        for (BatchInfo bi : batchInfoList) {
            incomplete.add(bi.getId());
        }
        while (!incomplete.isEmpty()) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}
            System.out.println("---------------------Awaiting results..." + incomplete.size());
            sleepTime = 10000L;
            BatchInfo[] statusList =
                    connection.getBatchInfoList(job.getId()).getBatchInfo();
            for (BatchInfo b : statusList) {
                if (b.getState() == BatchStateEnum.Completed
                        || b.getState() == BatchStateEnum.Failed) {
                    if (incomplete.remove(b.getId())) {
                        System.out.println("---------------------BATCH STATUS:\n" + b);
                    }
                }
            }
        }
    }

    /**
     * 得到操作的结果并检查错误
     * @param bulkConnection
     * @param jobInfo
     * @param batchInfoList
     * @throws AsyncApiException
     * @throws IOException
     */
    private void checkResults(BulkConnection bulkConnection, JobInfo jobInfo,
                              List<BatchInfo> batchInfoList) throws AsyncApiException, IOException, InterruptedException {
        Integer count = 0; //用来记录返回日志的条数，及每条记录的操作结果

        System.out.println("---------------------开始执行结果日志----");
        
        // batchInfoList was populated when batches were created and submitted
        for (BatchInfo batchInfo : batchInfoList) {
            CSVReader cr = new CSVReader(bulkConnection.getBatchResultStream(jobInfo.getId(),
                    batchInfo.getId()));
            List<String> resultHeader = cr.nextRecord();
            int resultConls = resultHeader.size();

            List<String> row;
            List<Long> successAndCreatedIds = new ArrayList<Long>();
            List<Long> errorIds = new ArrayList<Long>();
            Long msgtableId = null;
            while ((row = cr.nextRecord()) != null) {
                count++;
                Map<String, String> resultInfo = new HashMap<String, String>();
                for (int i = 0; i < resultConls; i++) {
                    resultInfo.put(resultHeader.get(i), row.get(i));
                }
                boolean success = Boolean.valueOf(resultInfo.get("Success"));
                boolean created = Boolean.valueOf(resultInfo.get("Created"));
                String id = resultInfo.get("Id");
                String error = resultInfo.get("Error");
                if (success && created) {
//                    System.out.println("Created row with id " + id);
                    msgtableId = ids.get(count-1);
                    successAndCreatedIds.add(msgtableId);
                } else if (!success) {
//                    System.out.println("Failed with error: " + error);
                    msgtableId = ids.get(count-1);
                    errorIds.add(msgtableId);
                }
            }

            updateById(successAndCreatedIds, "success");
            updateById(errorIds, "error");
        }
    }

    /**
     * 修改数据库中每行记录执行结果
     * @param ids
     * @param isSuccess
     */
    private void updateById(List<Long> ids, String isSuccess){
        jdbcUtils.updateBatch(ids,isSuccess);
    }
}
