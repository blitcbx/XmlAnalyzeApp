/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.analyze;

import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXmlAnalyze;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 * <DataMark sender="计量自动化系统" sendTime="2012-12-17 21:02:01" zoneId ="040100">
 *   <Mark>
 *     <app_no>2012110140402011156</app_no>
 *     <staDate>2012-11-01-01</staDate>
 *     <endDate>2012-12-01</endDate>
 *     <mc>峙村931</mc>
 *     <dydj>05</dydj>
 *     <bh>0402011156</bh>
 *     <ssbyqmc></ssbyqmc>
 *     <srdl>1361760.00</srdl>
 *     <scdl>564608.90</scdl>
 *     <xsl>58.54</xsl>
 *     <xsycms>dd</xsycms>
 *     <xsdxlx>4</xsdxlx>
 *     <ssfjmc>南宁供电局</ssfjmc>
 *     <ssfjid>040100</ssfjid>
 *     <sstq></sstq>
 *     <xlfzrid></xlfzrid>
 *     <xlfzr></xlfzr>
 *     <tqfzrid></tqfzrid>
 *     <tqfzr></tqfzr>
 *   </Mark>
 * </DataMark>
 * @author CBX
 */
public class YPFormatXSYC extends InputPoolsXmlAnalyze  {

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

        JSONObject markJson = new JSONObject();

        markJson.put("app_no", markElement.getChildTextTrim("app_no"));
        markJson.put("staDate", markElement.getChildTextTrim("staDate"));
        markJson.put("endDate", markElement.getChildTextTrim("endDate"));
        markJson.put("mc", markElement.getChildTextTrim("mc"));
        markJson.put("dydj", markElement.getChildTextTrim("dydj"));
        markJson.put("bh", markElement.getChildTextTrim("bh"));
        markJson.put("ssbyqmc", markElement.getChildTextTrim("ssbyqmc"));
        markJson.put("srdl", markElement.getChildTextTrim("srdl"));
        markJson.put("scdl", markElement.getChildTextTrim("scdl"));
        markJson.put("xsl", markElement.getChildTextTrim("xsl"));
        markJson.put("xsycms", markElement.getChildTextTrim("xsycms"));
        markJson.put("xsdxlx", markElement.getChildTextTrim("xsdxlx"));
        markJson.put("ssfjmc", markElement.getChildTextTrim("ssfjmc"));
        markJson.put("ssfjid", markElement.getChildTextTrim("ssfjid"));
        markJson.put("sstq", markElement.getChildTextTrim("sstq"));
        markJson.put("xlfzrid", markElement.getChildTextTrim("xlfzrid"));       
        markJson.put("xlfzr", markElement.getChildTextTrim("xlfzr"));
        markJson.put("tqfzrid", markElement.getChildTextTrim("tqfzrid"));
        markJson.put("tqfzr", markElement.getChildTextTrim("tqfzr"));

        json.put("Mark", markJson);

        return json;
    }
    
}
