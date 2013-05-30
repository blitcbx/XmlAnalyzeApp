/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class SqlStrUtils {

    private volatile static SqlStrUtils uniqueInstance;

    private SqlStrUtils() {
    }

    public static SqlStrUtils getInstance() {
        if (uniqueInstance == null) {
            synchronized (SqlStrUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SqlStrUtils();
                }
            }
        }
        return uniqueInstance;
    }

    public Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
                    int value1 = 0, value2 = 0;
                    try {
                        value1 = entry1.getValue();
                        value2 = entry2.getValue();
                    } catch (NumberFormatException e) {
                        value1 = 0;
                        value2 = 0;
                    }
                    return value1 - value2;
                }
            });
            Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
            Map.Entry<String, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        
        return sortedMap;
    }

    public ArrayList<String> getParam(String strSql) {

        LinkedHashMap<String, Integer> hmParam = new LinkedHashMap<>();
        ArrayList<String> sortList = new ArrayList<>();

        String[] iList = StringUtils.substringsBetween(strSql, "I#", "#");
        String[] tList = StringUtils.substringsBetween(strSql, "T#", "#");
        String[] tcList = StringUtils.substringsBetween(strSql, "TC#", "#");
        String[] tkList = StringUtils.substringsBetween(strSql, "TK#", "#");
        String[] ecList = StringUtils.substringsBetween(strSql, "EC#", "#");
        String[] ekList = StringUtils.substringsBetween(strSql, "EK#", "#");
        
        if (iList != null) {
            for (String tmp : iList) {
                int index = StringUtils.indexOf(strSql, "I#" + tmp + "#");
                hmParam.put("I#" + tmp + "#", index);
            }
        }
        
        if (tList != null) {
            for (String tmp : tList) {
                int index = StringUtils.indexOf(strSql, "T#" + tmp + "#");
                hmParam.put("T#" + tmp + "#", index);
            }
        }
        
        if (tcList != null) {
            for (String tmp : tcList) {
                int index = StringUtils.indexOf(strSql, "TC#" + tmp + "#");
                hmParam.put("TC#" + tmp + "#", index);
            }
        }
        
        if (tkList != null) {
            for (String tmp : tkList) {
                int index = StringUtils.indexOf(strSql, "TK#" + tmp + "#");
                hmParam.put("TK#" + tmp + "#", index);
            }
        }

        if (ecList != null) {
            for (String tmp : ecList) {
                int index = StringUtils.indexOf(strSql, "EC#" + tmp + "#");
                hmParam.put("EC#" + tmp + "#", index);
            }
        }
        
        if (ekList != null) {
            for (String tmp : ekList) {
                int index = StringUtils.indexOf(strSql, "EK#" + tmp + "#");
                hmParam.put("EK#" + tmp + "#", index);
            }
        }

        hmParam = (LinkedHashMap<String, Integer>) sortMapByValue(hmParam);

        for (Iterator<String> iter = hmParam.keySet().iterator(); iter.hasNext();) {
            sortList.add(iter.next());
        }

        return sortList;
    }
}
