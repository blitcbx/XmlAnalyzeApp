/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.sql.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author CBX
 */
public class MSSqlDBConn implements DBConn {
   
    @Override
    public Connection conn(String ip, String port, String sid, String username, String password, String racConnStr) throws ClassNotFoundException, SQLException {
        String url = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + sid;
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        Connection conn;
        conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
