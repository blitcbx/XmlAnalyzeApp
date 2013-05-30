/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.analyze;

import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXmlAnalyze;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 * <DBSET RESULT="0">
 *   <R>
 *     <C N="ORG_NO">020101</C>
 *     <C N="APP_NO">130131528862</C>
 *     <C N="WORK_NO">1</C>
 *   </R>
 * </DBSET>
 */
/**
 *
 * @author CBX
 */
public class YPFormat95598 extends InputPoolsXmlAnalyze  {
    
    @Override
    public JSONObject getXmlJsonObject(String xmlStr) throws Exception {
        
        Document xmlDoc;
        JSONObject json = new JSONObject();

        try {
            xmlDoc = super.getXmlDoc(xmlStr);
        } catch (JDOMException | IOException ex) {
            throw ex;
        }
        
        List<Element> listCElement = xmlDoc.getRootElement().getChild("R").getChildren("C");    
        for (Element cElement : listCElement) {
            json.put(cElement.getAttributeValue("N").trim(), cElement.getTextTrim());
        }

        return json;
    }
}
