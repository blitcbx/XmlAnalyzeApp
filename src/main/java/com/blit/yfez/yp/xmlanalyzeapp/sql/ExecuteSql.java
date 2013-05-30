/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.sql;

import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.exception.TooManyRowsException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.sql.conn.DBConn;
import com.blit.yfez.yp.xmlanalyzeapp.sql.conn.DBConnFactory;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import com.blit.yfez.yp.xmlanalyzeapp.utils.SqlStrUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class ExecuteSql {
    
    private String sqlStr;
    private JSONObject tablesInfoJson;
    private JSONObject inputJson;
    private JSONObject eachKeyJson;
    private JSONObject taskVarJson;
    private JSONObject executeCycleJson; 
    private JSONObject executeVarJson;

    public ExecuteSql(String sqlStr, JSONObject tablesInfoJson, JSONObject inputJson, JSONObject eachKeyJson, JSONObject taskVarJson) {
        this.sqlStr = sqlStr;
        this.tablesInfoJson = tablesInfoJson;
        this.inputJson = inputJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
    }
    
    public ExecuteSql(String sqlStr, JSONObject tablesInfoJson, JSONObject inputJson, JSONObject eachKeyJson, JSONObject taskVarJson, JSONObject executeVarJson) {
        this.sqlStr = sqlStr;
        this.tablesInfoJson = tablesInfoJson;
        this.inputJson = inputJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
        this.executeVarJson = executeVarJson;
    }
    
    public ExecuteSql(String sqlStr, JSONObject tablesInfoJson, JSONObject inputJson, JSONObject eachKeyJson, JSONObject taskVarJson, JSONObject executeCycleJson, JSONObject executeVarJson) {
        this.sqlStr = sqlStr;
        this.tablesInfoJson = tablesInfoJson;
        this.inputJson = inputJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
        this.executeCycleJson = executeCycleJson;
        this.executeVarJson = executeVarJson;
    }

    private Connection getDBConn(JSONObject tableInfoJson) throws ClassNotFoundException, SQLException {

        DBConn dbConn = null;

        String dbType = (String) tableInfoJson.get("type");
        switch (dbType) {
            case "ORACLE":
                dbConn = new DBConnFactory().getConn(DBConnFactory.DB_TYPE.ORACLE);
                break;
            case "MSSQL":
                dbConn = new DBConnFactory().getConn(DBConnFactory.DB_TYPE.MSSQL);
                break;
            case "SYBASE":
                dbConn = new DBConnFactory().getConn(DBConnFactory.DB_TYPE.SYBASE);
                break;
            default:
                break;
        }

        String dbIp = tableInfoJson.getString("ip");
        String dbPort = tableInfoJson.getString("port");
        String dbSid = tableInfoJson.getString("sid");
        String dbUsername = tableInfoJson.getString("username");
        String dbPassword = tableInfoJson.getString("password");
        String dbRacConnStr = tableInfoJson.getString("racConnStr");

        return dbConn.conn(dbIp, dbPort, dbSid, dbUsername, dbPassword, dbRacConnStr);
    }

    public JSONArray getJsonSelect() throws Exception {

        JSONArray selectResultArray = new JSONArray();
        ArrayList<String> paramList = SqlStrUtils.getInstance().getParam(sqlStr);

        ArrayList<String> valueList = new ArrayList();
        String tableName = null;

        for (String tmp : paramList) {
            if (StringUtils.startsWith(tmp, "T#")) {
                tableName = StringUtils.substringBetween(tmp, "T#", "#");
                try {
                    JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name");
                } catch (Exception ex) {
                    throw new NullParamException("SQL语句（" + sqlStr + "）中的表名：" + tableName + "找不到对应的变量值。\r\n" + ex.getMessage());
                }
                sqlStr = StringUtils.replace(sqlStr, tmp, JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name"));
            } else if (StringUtils.startsWith(tmp, "TK#")) {
                valueList.add(JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "I#")) {
                valueList.add(JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "TC#")) {
                valueList.add(JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            }
        }

        String displaySqlStr = sqlStr;
        
        try (Connection conn = getDBConn(JsonUtils.getInstance().getJsonObject(tablesInfoJson, tableName))) {
            try (PreparedStatement ps = conn.prepareStatement(sqlStr)) {
                if (!valueList.isEmpty()) {
                    for (int i = 0; i < valueList.size(); i++) {
                        ps.setString(i + 1, valueList.get(i));
                        displaySqlStr = StringUtils.replaceOnce(displaySqlStr, "?",  "'" + valueList.get(i) + "'");
                    }
                }

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    while (rs.next()) {
                        HashMap<String, String> hm = new HashMap<>();
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnName(i + 1);
                            hm.put(columnName, StringUtils.isBlank(rs.getString(columnName)) ? "" : rs.getString(columnName));
                        }
                        selectResultArray.add(hm);
                    }
                }
            }
        } finally {
            LogHelp.getInstance().info("执行 Tasks -> Task -> UserVar 查询SQL：\r\n" + displaySqlStr);
        }

        return selectResultArray;
    }

    public HashMap<String, String> getExecuteVarSelect() throws Exception {

        HashMap<String, String> hmReturn = new HashMap<>();

        ArrayList<String> paramList = SqlStrUtils.getInstance().getParam(sqlStr);
        ArrayList<String> valueList = new ArrayList();
        String tableName = null;

        for (String tmp : paramList) {
            if (StringUtils.startsWith(tmp, "T#")) {
                tableName = StringUtils.substringBetween(tmp, "T#", "#");
                try {
                    JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name");
                } catch (Exception ex) {
                    throw new NullParamException("SQL语句（" + sqlStr + "）中的表名：" + tableName + "找不到对应的变量值。\r\n" + ex.getMessage());
                }
                sqlStr = StringUtils.replace(sqlStr, tmp, JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name"));
            } else if (StringUtils.startsWith(tmp, "I#")) {
                valueList.add(JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "TC#")) {
                valueList.add(JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "TK#")) {
                valueList.add(JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "EC#")) {
                valueList.add(JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(tmp, "EC#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "EK#")) {
                valueList.add(JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(tmp, "EK#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            }
        }
        
        String displaySqlStr = sqlStr;
        
        try (Connection conn = getDBConn(JsonUtils.getInstance().getJsonObject(tablesInfoJson, tableName))) {
            try (PreparedStatement ps = conn.prepareStatement(sqlStr)) {
                if (!valueList.isEmpty()) {
                    for (int i = 0; i < valueList.size(); i++) {
                        ps.setString(i + 1, valueList.get(i));
                        displaySqlStr = StringUtils.replaceOnce(displaySqlStr, "?",  "'" + valueList.get(i) + "'");
                    }
                }

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    if (rs.next()) {
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnName(i + 1);
                            hmReturn.put(columnName, StringUtils.isBlank(rs.getString(columnName)) ? "" : rs.getString(columnName));
                        }
                    }
                    if (rs.next()) {
                        throw new TooManyRowsException("ExecuteVar节点中的SQL查询语句只允许查询出一条记录。");
                    }
                }
            }
        } finally {
            LogHelp.getInstance().info("执行 Tasks -> Task -> Execute -> ExecuteVar 查询SQL：\r\n" + displaySqlStr);
        }
        
        return hmReturn;
    }

    public void executeDMLData() throws Exception {

        ArrayList<String> paramList = SqlStrUtils.getInstance().getParam(sqlStr);
        ArrayList<String> valueList = new ArrayList();
        String tableName = null;

        for (String tmp : paramList) {
            if (StringUtils.startsWith(tmp, "T#")) {
                tableName = StringUtils.substringBetween(tmp, "T#", "#");
                try {
                    JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name");
                } catch (Exception ex) {
                    throw new NullParamException("SQL语句（" + sqlStr + "）中的表名：" + tableName + "找不到对应的变量值。\r\n" + ex.getMessage());
                }
                sqlStr = StringUtils.replace(sqlStr, tmp, JsonUtils.getInstance().getString(tablesInfoJson, tableName + ".name"));
            } else if (StringUtils.startsWith(tmp, "I#")) {
                valueList.add(JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "TC#")) {
                valueList.add(JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "TK#")) {
                valueList.add(JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "EC#")) {
                valueList.add(JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(tmp, "EC#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            } else if (StringUtils.startsWith(tmp, "EK#")) {
                valueList.add(JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(tmp, "EK#", "#")));
                sqlStr = StringUtils.replace(sqlStr, tmp, "?");
            }
        }
        
        String displaySqlStr = sqlStr;
        
        try (Connection conn = getDBConn(JsonUtils.getInstance().getJsonObject(tablesInfoJson, tableName))) {
            try (PreparedStatement ps = conn.prepareStatement(sqlStr)) {
                if (!valueList.isEmpty()) {
                    for (int i = 0; i < valueList.size(); i++) {
                        ps.setString(i + 1, valueList.get(i));
                        displaySqlStr = StringUtils.replaceOnce(displaySqlStr, "?",  "'" + valueList.get(i) + "'");
                    }
                }
                ps.execute();
            }
        } finally {
            LogHelp.getInstance().info("执行 Tasks -> Task -> Execute -> ExecuteStr SQL：\r\n" + displaySqlStr);
        }
    }
}
