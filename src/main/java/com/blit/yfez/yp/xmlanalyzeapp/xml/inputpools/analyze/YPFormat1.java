/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.analyze;

import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXmlAnalyze;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 * <DataMark sender="生产MIS系统" sendTime="" zoneId ="">
 *   <Mark>
 *     <!--<TableName>TdTroubleRecordService</TableName>--> 
 *     <Key>
 *       <PKValue>12344</PKValue>
 *       <Flag>1</Flag>
 *     </Key> 
 *   </Mark> 
 * </DataMark>
 * @author CBX
 */
public class YPFormat1 extends InputPoolsXmlAnalyze {

    @Override
    public JSONObject getXmlJsonObject(String xmlStr) throws Exception {

        Document xmlDoc;
        JSONObject json = new JSONObject();

        try {
            xmlDoc = super.getXmlDoc(xmlStr);
        } catch (JDOMException | IOException ex) {
            throw ex;
        }

        Element rootElement = xmlDoc.getRootElement();
        json.put("sender", rootElement.getAttributeValue("sender"));
        json.put("sendTime", rootElement.getAttributeValue("sendTime"));
        json.put("zoneId", rootElement.getAttributeValue("zoneId"));

        Element markElement = rootElement.getChild("Mark");

        JSONObject keyJson = new JSONObject();
        
        JSONArray keyArray = new JSONArray();
        List<Element> keyElementList = markElement.getChildren("Key");
        for (Element keyElement : keyElementList) {
            JSONObject keyJsonObject = new JSONObject();
            JSONArray pkvalueArray = new JSONArray();
            
            if (StringUtils.contains(keyElement.getChildTextTrim("PKValue"), ",")) {
                String[] pkvalueList = StringUtils.split(keyElement.getChildTextTrim("PKValue"), ",");
                pkvalueArray.addAll(Arrays.asList(pkvalueList));
                keyJsonObject.put("PKValue", pkvalueArray);
            } else {
                keyJsonObject.put("PKValue", keyElement.getChildTextTrim("PKValue"));
            }
            
            keyJsonObject.put("Flag", keyElement.getChildTextTrim("Flag"));
            keyArray.add(keyJsonObject);
        }
        
        keyJson.put("Key", keyArray);

        json.put("Mark", keyJson);

        return json;
    }

    public static void main(String[] args) {
        try {
            System.out.println(new YPFormat1().getXmlJsonObject("<DataMark sender=\"生产MIS系统\" sendTime=\"\" zoneId =\"\"><Mark><TableName>TdTroubleRecordService</TableName><Key><PKValue>12344</PKValue><Flag>1</Flag></Key><Key><PKValue>123454</PKValue><Flag>1</Flag></Key></Mark></DataMark>"));
        } catch (Exception ex) {
            Logger.getLogger(YPFormat1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
