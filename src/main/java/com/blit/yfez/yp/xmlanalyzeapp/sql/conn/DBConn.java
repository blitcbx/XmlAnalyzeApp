/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.sql.conn;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author CBX
 */
public interface DBConn {
    public Connection conn(String ip, String port, String sid, String username, String password, String racConnStr) throws ClassNotFoundException, SQLException;
}

