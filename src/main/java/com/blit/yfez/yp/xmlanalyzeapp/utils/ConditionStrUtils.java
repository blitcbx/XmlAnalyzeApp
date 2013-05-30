/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.utils;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class ConditionStrUtils {
    
    private volatile static ConditionStrUtils uniqueInstance;

    private ConditionStrUtils() {
    }

    public static ConditionStrUtils getInstance() {
        if (uniqueInstance == null) {
            synchronized (ConditionStrUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new ConditionStrUtils();
                }
            }
        }
        return uniqueInstance;
    }
    
    public ArrayList<String> getParam(String conditionStr) {
        
        ArrayList<String> paramList = new ArrayList<>();
        
        String[] iList = StringUtils.substringsBetween(conditionStr, "I#", "#");
        String[] tList = StringUtils.substringsBetween(conditionStr, "T#", "#");
        String[] tcList = StringUtils.substringsBetween(conditionStr, "TC#", "#");
        String[] tkList = StringUtils.substringsBetween(conditionStr, "TK#", "#");
        String[] ecList = StringUtils.substringsBetween(conditionStr, "EC#", "#");
        String[] ekList = StringUtils.substringsBetween(conditionStr, "EK#", "#");
        
        if (iList != null) {
            for (String tmp : iList) {
                paramList.add("I#" + tmp + "#");
            }
        }
        
        if (tList != null) {
            for (String tmp : tList) {
                paramList.add("T#" + tmp + "#");
            }
        }
        
        if (tcList != null) {
            for (String tmp : tcList) {
                paramList.add("TC#" + tmp + "#");
            }
        }
        
        if (tkList != null) {
            for (String tmp : tkList) {
                paramList.add("TK#" + tmp + "#");
            }
        }

       if (ecList != null) {
            for (String tmp : ecList) {
                paramList.add("EC#" + tmp + "#");
            }
        }
        
        if (ekList != null) {
            for (String tmp : ekList) {
                paramList.add("EK#" + tmp + "#");
            }
        }
        
        return paramList;
    }  
}
