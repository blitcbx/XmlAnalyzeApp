/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CBX
 */
public class Calc {

    private boolean twoFiguresContrast(String inputMsg) {

        String[] operatorList = new String[]{"=", "<>"};
        String a1 = null;
        String a2 = null;
        String operator = null;

        for (String tmp : operatorList) {
            if (StringUtils.contains(inputMsg, tmp)) {
                a1 = StringUtils.isBlank(StringUtils.split(inputMsg, tmp)[0]) ? "" : StringUtils.split(inputMsg, tmp)[0].trim();
                a2 = StringUtils.isBlank(StringUtils.split(inputMsg, tmp)[1]) ? "" : StringUtils.split(inputMsg, tmp)[1].trim();
                operator = tmp.trim();
            }
        }

        switch (operator) {
            case "=":
                if (StringUtils.equalsIgnoreCase(a1, "null")) {
                    a1 = null;
                }
                if (StringUtils.equalsIgnoreCase(a2, "null")) {
                    a2 = null;
                }
                return StringUtils.equals(a1, a2);
            case "<>":
                if (StringUtils.equalsIgnoreCase(a1, "null")) {
                    a1 = null;
                }
                if (StringUtils.equalsIgnoreCase(a2, "null")) {
                    a2 = null;
                }
                return !StringUtils.equals(a1, a2);
            default:
                break;
        }
        return false;
    }

    private boolean orOperator(String inputMsg) {
        
        String[] orList = StringUtils.splitByWholeSeparator(inputMsg, " or ");
        
        for (String tmp : orList) {
            if (twoFiguresContrast(tmp)) {
                return true;
            }
        }

        return false;
    }

    private boolean andOperator(String inputMsg) {
        String[] andList = null;
        if (StringUtils.contains(inputMsg, " and ")) {
            andList = StringUtils.splitByWholeSeparator(inputMsg, " and ");
        }
        for (String tmp : andList) {
            if (StringUtils.contains(tmp, " or ")) {
                if (!orOperator(tmp)) {
                    return false;
                }
            } else {
                if (!twoFiguresContrast(tmp)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean getCalc(String msg) {
        if (StringUtils.contains(msg, " and ")) {
            return andOperator(msg);
        } else if (StringUtils.contains(msg, " or ")) {
            return orOperator(msg);
        } else {
            return twoFiguresContrast(msg);
        }
    }

    public static void main(String[] args) {
        Calc calc = new Calc();
        System.out.println(calc.getCalc("1<>0"));
    }
}
