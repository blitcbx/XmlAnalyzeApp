/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools;

import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author CBX
 */
public abstract class InputPoolsXmlAnalyze {
    
    public abstract JSONObject getXmlJsonObject(String xmlStr) throws Exception;
    
    public Document getXmlDoc(String xmlStr) throws JDOMException, IOException {
        
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
            return document;
        } catch (JDOMException ex) {
            throw new JDOMException("接收到的输入参数：\r\n" + xmlStr + "\r\n不是标准的XML文件，请检查后重新输入。\r\n" + 
                    ex.getMessage());
            //Logger.getLogger(InputPoolsXmlAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InputPoolsXmlAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            LogHelp.getInstance().info("接收到输入参数：\r\n" + xmlStr);
        }
        
        return null;
    }
}