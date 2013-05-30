/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.analyze;

import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools.InputPoolsXmlAnalyze;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class YPFormat95598ToDC extends InputPoolsXmlAnalyze {

    @Override
    public JSONObject getXmlJsonObject(String xmlStr) throws Exception {

        JSONObject json = null;
        
        //避免JSON出现NULL值的状况
        xmlStr = StringUtils.replace(xmlStr, ":null", ":\"\"");
        
        try {
            json = JSONObject.fromObject(xmlStr);
        } catch (JSONException ex) {
            LogHelp.getInstance().error("输入的字符串不能转换为JSON对象。\r\n" + ex.getMessage(), ex);
        } finally {
            LogHelp.getInstance().info("接收到输入参数：\r\n" + xmlStr);
        }
        
        return json;
    }
}
