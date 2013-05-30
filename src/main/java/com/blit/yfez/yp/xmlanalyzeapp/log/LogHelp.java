/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.log;


import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 该类负责日志的输出记录，配合log4j.properties文件。
 *
 * @author CBX
 */
public class LogHelp {

    //private volatile static LogHelp uniqueInstance;
    private static LogHelp uniqueInstance;
    private Logger logger;

    private LogHelp() {
        logger = Logger.getLogger(LogHelp.class);
        String webinfdir;

        try {
            webinfdir = new File(LogHelp.class.getClassLoader().getResource("").getPath()).getParentFile().getParent();
            PropertyConfigurator.configure(webinfdir + File.separatorChar + "XmlAnalyzeApp.log4j.properties");
        } catch (Exception ex) {
            PropertyConfigurator.configure("G:\\ETL_APP\\ExtratData\\XmlAnalyzeApp.log4j.properties");
        }
    }

    /**
     * 双重检查枷锁，检查该实例是否已经被创建。
     *
     * @param impClass 异常出现的类
     * @return
     */
    public static LogHelp getInstance() {

        //if (uniqueInstance == null) {
        //synchronized (LogHelp.class) {
        // if (uniqueInstance == null) {
        uniqueInstance = new LogHelp();
        //  }
        // }
        //}
        return uniqueInstance;
    }

    /**
     * 输出信息级别的操作日志
     *
     * @param msg 输入的信息
     */
    public void info(String msg) {
        logger.info(msg);
    }

    /**
     * 输出错误级别的操作日志
     *
     * @param msg 输入的信息
     */
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
