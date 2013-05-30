/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.analyze;

import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXmlAnalyze;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class YPFormatSplitByCommas extends InputPoolsXmlAnalyze {

    private Connection getConn() throws ClassNotFoundException, SQLException {
        String url = "jdbc:sqlserver://10.100.81.34:8090;databaseName=CSGDC.DataETLDB;integratedSecurity=false";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection conn;
        conn = DriverManager.getConnection(url, "sa", "nwsj@blit2011");
        return conn;
    }

    private JSONObject dbInfo(String aliasName) throws ClassNotFoundException, SQLException {

        JSONObject dbObject = new JSONObject();

        try (Connection conn = getConn()) {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select ");
            strSql.append("  AreaCode, DbIp 'ip', DbPort 'port', DbSid 'sid', ");
            strSql.append("  DbUserName 'username', DbPassword 'password', DbRacConnStr 'racConnStr', DbType ");
            strSql.append("from ");
            strSql.append("  dbo.DbInfo ");
            strSql.append("where ");
            strSql.append("  AliasName = ?");
            try (PreparedStatement ps = conn.prepareStatement(strSql.toString())) {
                ps.setString(1, aliasName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dbObject.put("AreaCode", rs.getString("AreaCode"));
                        dbObject.put("ip", rs.getString("ip"));
                        dbObject.put("port", rs.getString("port"));
                        dbObject.put("sid", rs.getString("sid"));
                        dbObject.put("username", rs.getString("username"));
                        dbObject.put("password", rs.getString("password"));
                        dbObject.put("racConnStr", rs.getString("racConnStr"));
                        dbObject.put("DbType", rs.getString("DbType"));
                    }
                }
            }
        }

        return dbObject;
    }

    @Override
    public JSONObject getXmlJsonObject(String xmlStr) throws Exception {

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        String[] listStr = StringUtils.split(xmlStr, ",");
        for (String tmp : listStr) {
            JSONObject jsonTmp = new JSONObject();
            jsonTmp.put("code", tmp.trim());
            //jsonTmp.put("dbInfo", dbInfo(tmp.trim()));
            array.add(jsonTmp);
        }

        json.put("codes", array);

        return json;
    }

    public static void main(String[] args) {
        try {
            System.out.println(new YPFormatSplitByCommas().getXmlJsonObject("nn_scmis, lz_scmis").toString());
        } catch (Exception ex) {
            Logger.getLogger(YPFormatSplitByCommas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
