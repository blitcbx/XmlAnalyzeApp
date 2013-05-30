/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.sql.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author CBX
 */
public class OracleDBConn implements DBConn {

    /**
     *
     * @param ip
     * @param port
     * @param sid
     * @param username
     * @param password
     * @return
     */
    @Override
    public Connection conn(String ip, String port, String sid, String username, String password, String racConnStr) throws ClassNotFoundException, SQLException {
        String url;
        if (StringUtils.isNotBlank(racConnStr)) {
            url = "jdbc:oracle:thin:@" + racConnStr;
        } else {
            url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;
        }
        
        Class.forName("oracle.jdbc.OracleDriver");
        Connection conn;
        conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
