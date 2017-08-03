package com.chervon.iot.common.mq.jdbcutils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by ZAC on 2017-7-12.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Component
public class JDBCUtils {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 批量插入
     * @param sqls
     */
    public void insertBatch(List<String> sqls){
        String[] strs = new String[sqls.size()];
        for (int i = 0; i < sqls.size(); i++) {
            strs[i] = sqls.get(i);
        }
        jdbcTemplate.batchUpdate(strs);
    }

    /**
     * 查询msgtable表得到json数据
     * @param msgtype
     * @return
     */
    public JSONArray selectMsgtable(@NotNull String msgtype, Integer exportNum) {
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String sql = null;
        if (exportNum != null) {
            // 编写SQL:
            sql = "SELECT json FROM msgtable where msgtype = '" + msgtype +
                    "' AND recallstatus = 'null' ORDER BY id LIMIT " + exportNum;
        } else {
            // 编写SQL:
            sql = "SELECT json FROM msgtable where msgtype = '" + msgtype +
                    "' AND recallstatus = 'null' ORDER BY id";
        }
        List<String> strings = jdbcTemplate.queryForList(sql, String.class);

        jsonArray = new JSONArray();
        // 获得结果集的数据
        for(String json:strings){
            jsonObject = new JSONObject(json);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 根据msgtype查询msgtable表得到json数据的同时，查询id并返回
     * @param msgtype
     * @return
     */
    public List<Long> selectMsgtableIds(@NotNull String msgtype,Integer exportNum) {
        // 编写SQL:
        String sql = null;
        if (exportNum != null) {
            sql = "SELECT id FROM msgtable where msgtype = '" + msgtype +
                    "' AND recallstatus = 'null' ORDER BY id LIMIT " + exportNum;
        } else {
            sql = "SELECT id FROM msgtable where msgtype = '" + msgtype +
                    "' AND recallstatus = 'null' ORDER BY id";
        }
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class);
        return ids;
    }

    /**
     * 修改数据库中每行记录执行结果
     * @param ids
     * @param isSuccess
     */
    public void updateBatch(List<Long> ids, String isSuccess) {
        if (ids != null && ids.size() > 0) {
            String[] sqls = new String[ids.size()];
            String sql = "";
            if ("success".equals(isSuccess.toLowerCase())) {
                for (int i = 0;i < ids.size(); i++) {
                    sql += "UPDATE msgtable SET finalstatus = 'success'," +
                            " recallstatus = 'success'," +
                            " recalltimes = recalltimes + 1 WHERE id = " + ids.get(i);
                    sqls[i] = sql;
                    sql = "";
                }
                jdbcTemplate.batchUpdate(sqls);
                System.out.println("-------------------------更新数据库中成功记录成功----");
            } else if ("error".equals(isSuccess.toLowerCase())){
                for (int i = 0;i < ids.size(); i++) {
                    sql += "UPDATE msgtable SET recallstatus = 'error' , " +
                            "recalltimes = recalltimes + 1" +
                            "WHERE id = " + ids.get(i);
                    sqls[i] = sql;
                    sql = "";
                }
                jdbcTemplate.batchUpdate(sqls);

                String updateSql = "UPDATE msgtable SET finalstatus = 'error'" +
                        " WHERE recalltimes = 3";
                jdbcTemplate.update(updateSql);

                System.out.println("-------------------------更新数据库中失败记录成功----");
            }
        }
    }

    /**
     * 将msgtable中符合所需类型的数据全部查找出来
     * @param msgtype
     * @return
     */
    public JSONArray selectMsgtableAll(String msgtype) {
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        // 编写SQL:
        String sql = "SELECT json FROM msgtable where msgtype = '" + msgtype +
                "' AND finalstatus = 'null' ORDER BY id";
        List<String> strings = jdbcTemplate.queryForList(sql, String.class);

        jsonArray = new JSONArray();
        // 获得结果集的数据
        for(String json:strings){
            jsonObject = new JSONObject(json);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 根据msgtype查询msgtable表得到所有json数据的同时，查询所有id并返回
     * @param msgtype
     * @return
     */
    public List<Long> selectMsgtableAllIds(String msgtype) {
        // 编写SQL:
        String sql = "SELECT id FROM msgtable where msgtype = '" + msgtype +
                "' AND finalstatus = 'null' ORDER BY id";
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class);
        return ids;
    }
}

