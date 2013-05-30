/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.thirdpartysupport;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;

/**
 *
 * @author CBX
 */
public class SendToXSMPPlatform {

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @param params 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public void doPost(String servletUrl, HashMap<String, String> outMap) throws Exception {

        URL url = new URL(servletUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setDoOutput(true);
        urlConn.setRequestProperty("Content-type", "application/x-java-serialized-object");
        urlConn.setRequestMethod("POST");
        urlConn.connect();
        OutputStream os = urlConn.getOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(outMap);
            oos.flush();
        }

        urlConn.getInputStream();
    }

    public String sendRequest(String mpaddr, String lcbbh, String ssfjid, String dydj, JSONObject sendObject) {
        
        HashMap<String, String> hmSend = new HashMap<>();
        
        hmSend.put("lcbbh", lcbbh);
        hmSend.put("bmId", ssfjid);
        hmSend.put("app_no", sendObject.getString("app_no"));
        hmSend.put("staDate", sendObject.getString("staDate"));
        hmSend.put("endDate", sendObject.getString("endDate"));
        hmSend.put("mc", sendObject.getString("mc"));
        hmSend.put("dydj", dydj);
        hmSend.put("bh", sendObject.getString("bh"));
        hmSend.put("ssbyqmc", sendObject.getString("ssbyqmc"));
        hmSend.put("srdl", sendObject.getString("srdl"));
        hmSend.put("scdl", sendObject.getString("scdl"));
        hmSend.put("xsl", sendObject.getString("xsl"));
        hmSend.put("xsycms", sendObject.getString("xsycms"));
        hmSend.put("xsdxlx", sendObject.getString("xsdxlx"));
        hmSend.put("ssfjmc", sendObject.getString("ssfjmc"));
        hmSend.put("ssfjid", sendObject.getString("ssfjid"));
        hmSend.put("sstq", sendObject.getString("sstq"));
        hmSend.put("xlfzrid", sendObject.getString("xlfzrid"));
        hmSend.put("xlfzr", sendObject.getString("xlfzr"));       
        hmSend.put("tqfzrid", sendObject.getString("tqfzrid"));
        hmSend.put("tqfzr", sendObject.getString("tqfzr"));
        
        try {
            doPost(mpaddr, hmSend);
        } catch (Exception ex) {
            Logger.getLogger(SendToXSMPPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
