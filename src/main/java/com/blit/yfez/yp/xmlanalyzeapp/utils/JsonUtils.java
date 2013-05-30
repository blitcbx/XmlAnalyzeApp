/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.utils;

import java.util.Iterator;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class JsonUtils {

    private volatile static JsonUtils uniqueInstance;

    private JsonUtils() {
    }

    public static JsonUtils getInstance() {
        if (uniqueInstance == null) {
            synchronized (JsonUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new JsonUtils();
                }
            }
        }
        return uniqueInstance;
    }
    
    private String keyContainsIgnoreCase(JSONObject json, String key) {
        Set<String> keySet = json.keySet();
        for (Iterator<String> iter = keySet.iterator(); iter.hasNext();) {
            String tmp = iter.next();
            if (StringUtils.equalsIgnoreCase(tmp, key)) {
                return tmp;
            }
        }
        return "";
    }

    private Object findJson(JSONObject inJson, String reachStr) throws Exception {

        String[] findStrList = StringUtils.split(reachStr, ".");
        Object returnObj = inJson;

        for (String tmp : findStrList) {

            JSONObject tmpObject;
            String tmpJsonKey = null;

            try {
                tmpObject = JSONObject.fromObject(returnObj);
            } catch (JSONException ex) {
                throw new JSONException("转换为Json对象出错。\r\n" + ex.getMessage());
            }

            if (!tmpObject.containsKey((StringUtils.contains(tmp, "{") && StringUtils.contains(tmp, "}")) ? StringUtils.substringBeforeLast(tmp, "{") : tmp)) {
                String newKeyStr = keyContainsIgnoreCase(tmpObject, StringUtils.substringBeforeLast(tmp, "{"));
                if (StringUtils.contains(tmp, "{")) {
                    tmpJsonKey = StringUtils.replace(tmp, StringUtils.substringBeforeLast(tmp, "{"), newKeyStr);
                } else {
                    tmpJsonKey = newKeyStr;
                }
            } else {
                tmpJsonKey = tmp;
            }

            if (StringUtils.isNotBlank(tmpJsonKey)) {
                if (StringUtils.contains(tmpJsonKey, "{") && StringUtils.contains(tmpJsonKey, "}")) {
                    int index = -1;
                    try {
                        index = Integer.parseInt(StringUtils.substringBetween(tmpJsonKey, "{", "}"));
                    } catch (NumberFormatException ex) {
                        throw new NumberFormatException("在{}中包含的字符串：" + StringUtils.substringBetween(tmpJsonKey, "{", "}") + "，不能转换为数字。\r\n" + ex.getMessage());
                    }
                    JSONArray tmpArray;
                    try {
                        tmpArray = JSONArray.fromObject(tmpObject.get(StringUtils.substringBeforeLast(tmpJsonKey, "{")));
                    } catch (JSONException ex) {
                        throw new JSONException("转换为Json对象出错。\r\n" + ex.getMessage());
                    }
                    if ((index != -1) && (index + 1 > tmpArray.size())) {
                        throw new JSONException("JsonArray索引越界。");
                    } else {
                        returnObj = tmpArray.get(index);
                    }
                } else {
                    returnObj = tmpObject.get(tmpJsonKey);
                }
            } else {
                throw new Exception("输入的查找字符串：" + reachStr + "，在查找到节点：" + tmp + "时，找不到对应的值");
            }
        }

        return returnObj;
    }

    public String getString(JSONObject inJson, String reachStr) throws Exception {
        Object obj = findJson(inJson, reachStr);
        return obj.toString();
    }

    public JSONObject getJsonObject(JSONObject inJson, String reachStr) throws Exception {
        Object obj = findJson(inJson, reachStr);
        JSONObject jsonObj;
        try {
            jsonObj = JSONObject.fromObject(obj);
        } catch (JSONException ex) {
            throw new JSONException("转换为JsonObject对象失败。\r\n" + ex.getMessage());
        }
        return jsonObj;
    }

    public JSONArray getJsonArray(JSONObject inJson, String reachStr) throws Exception {
        Object obj = findJson(inJson, reachStr);
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.fromObject(obj);
        } catch (JSONException ex) {
            throw new JSONException("转换为JsonObject对象失败。\r\n" + ex.getMessage());
        }
        return jsonArray;
    }
}
