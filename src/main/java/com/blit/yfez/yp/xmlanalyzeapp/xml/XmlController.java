/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml;

import com.blit.yfez.yp.xmlanalyzeapp.xml.databaseinfo.DatabaseInfoXml;
import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXml;
import com.blit.yfez.yp.xmlanalyzeapp.xml.tasks.TasksXml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author CBX
 */
public class XmlController {
    
    public Document initXml(String xmlPath) throws FileNotFoundException, JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document;
        
        try (InputStream file = new FileInputStream(xmlPath)) {
            document = builder.build(file); //获得文档对象
        }

        return document;
    }
    
    public JSONObject inputPoolsExecute(Document doc, String... inputValueList) throws Exception {
        return new InputPoolsXml(doc.getRootElement().getChild("InputPools"), inputValueList).getJsonValue();
    }
    
    public JSONObject databaseInfoExecute(Document doc) throws Exception {
        return new DatabaseInfoXml(doc.getRootElement().getChild("DatabaseInfo")).getJsonValue();
    }
    
    public JSONArray tasksExecute(Document doc, JSONObject inputValueJson, JSONObject databaseInfoJson) throws Exception {
        return new TasksXml(doc.getRootElement().getChild("Tasks"), inputValueJson, databaseInfoJson).getJsonValue();
    }
}