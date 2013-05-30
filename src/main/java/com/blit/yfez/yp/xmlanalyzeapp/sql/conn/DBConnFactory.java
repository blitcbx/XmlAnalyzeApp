/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.sql.conn;

/**
 *
 * @author CBX
 */
public class DBConnFactory {

    public static enum DB_TYPE {
        ORACLE, SYBASE, MYSQL, MSSQL
    }

    public DBConnFactory() {
    }
    
    public DBConn getConn(DBConnFactory.DB_TYPE enumDBType) {
        switch (enumDBType) {
            case ORACLE :
                return new OracleDBConn();
            case MSSQL :
                return new MSSqlDBConn();
            case SYBASE :
                return new SybaseDBConn();
            default:
                return null;
        }
    }
}