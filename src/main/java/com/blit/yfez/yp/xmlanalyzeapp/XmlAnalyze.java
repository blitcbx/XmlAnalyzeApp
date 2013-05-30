/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp;

import net.sf.json.JSONArray;

/**
 *
 * @author CBX
 */
public interface XmlAnalyze {
    public JSONArray execute(String xmlFilePath, String... inputValueList) throws Exception;
}
