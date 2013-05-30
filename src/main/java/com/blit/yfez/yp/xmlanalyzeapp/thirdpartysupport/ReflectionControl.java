/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.thirdpartysupport;

import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class ReflectionControl {

    private String classPath;
    private String methodName;
    private List<Element> paramElementList;
    private JSONObject inputJson;
    private JSONObject databaseInfoJson;
    private JSONObject eachKeyJson;
    private JSONObject taskVarJson;
    private JSONObject executeCycleJson;
    private JSONObject executeVarJson;

    public ReflectionControl(String classPath, String methodName, List<Element> paramElementList, JSONObject inputJson, JSONObject databaseInfoJson, JSONObject eachKeyJson, JSONObject taskVarJson) {
        this.classPath = classPath;
        this.methodName = methodName;
        this.paramElementList = paramElementList;
        this.inputJson = inputJson;
        this.databaseInfoJson = databaseInfoJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
    }

    public ReflectionControl(String classPath, String methodName, List<Element> paramElementList, JSONObject inputJson, JSONObject databaseInfoJson, JSONObject eachKeyJson, JSONObject taskVarJson, JSONObject executeVarJson) {
        this.classPath = classPath;
        this.methodName = methodName;
        this.paramElementList = paramElementList;
        this.inputJson = inputJson;
        this.databaseInfoJson = databaseInfoJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
        this.executeVarJson = executeVarJson;
    }

    public ReflectionControl(String classPath, String methodName, List<Element> paramElementList, JSONObject inputJson, JSONObject databaseInfoJson, JSONObject eachKeyJson, JSONObject taskVarJson, JSONObject executeCycleJson, JSONObject executeVarJson) {
        this.classPath = classPath;
        this.methodName = methodName;
        this.paramElementList = paramElementList;
        this.inputJson = inputJson;
        this.databaseInfoJson = databaseInfoJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
        this.executeCycleJson = executeCycleJson;
        this.executeVarJson = executeVarJson;
    }

    public String callClassReturnStr() throws ClassNotFoundException, Exception {

        Class<?> newClass = null;

        LogHelp.getInstance().info("开始调用外部类：\r\n" + "路径：" + classPath + "\r\n方法名：" + methodName + "\r\n返回值类型：String");
        
        try {
            newClass = Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("输入的类路径：" + classPath + "查找失败，请验证。\r\n" + ex.getMessage());
        }

        Class<?>[] classArr = new Class<?>[paramElementList.size()];
        Object[] valueObject = new Object[paramElementList.size()];

        for (int i = 0; i < paramElementList.size(); i++) {
            Element paramElement = paramElementList.get(i);
            String typeName = paramElement.getAttributeValue("type");
            String paramStr = paramElement.getTextTrim();

            if (StringUtils.equalsIgnoreCase(typeName, "Object")) {

                classArr[i] = JSONObject.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName);
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName);
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }

                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i].toString());
            } else if (StringUtils.equalsIgnoreCase(typeName, "String")) {

                classArr[i] = String.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getString(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }

                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
            } else if (StringUtils.equalsIgnoreCase(typeName, "Array")) {

                classArr[i] = JSONArray.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }

                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
            }
        }

        Method method = null;
        try {
            method = newClass.getMethod(methodName, classArr);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new Exception("输入的方法名：" + methodName + "引用失败，请验证。\r\n" + ex.getMessage());
        }

        Object returnObj = null;

        try {
            returnObj = method.invoke(newClass.newInstance(), valueObject);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new Exception("方法参数值传入失败，请验证。\r\n" + ex.getMessage(), ex);       
        } finally {
            LogHelp.getInstance().info("调用返回值为：" + (String) returnObj);
        }

        return (String) returnObj;
    }

    public JSONArray callClassReturnJsonArray() throws ClassNotFoundException, Exception {

        Class<?> newClass = null;
        
        LogHelp.getInstance().info("开始调用外部类：\r\n" + "路径：" + classPath + "\r\n方法名：" + methodName + "\r\n返回值类型：JSONArray");

        try {
            newClass = Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("输入的类路径：" + classPath + "查找失败，请验证。\r\n" + ex.getMessage());
        }

        Class<?>[] classArr = new Class<?>[paramElementList.size()];
        Object[] valueObject = new Object[paramElementList.size()];

        for (int i = 0; i < paramElementList.size(); i++) {
            Element paramElement = paramElementList.get(i);
            String typeName = paramElement.getAttributeValue("type");
            String paramStr = paramElement.getTextTrim();

            if (StringUtils.equalsIgnoreCase(typeName, "Object")) {

                classArr[i] = JSONObject.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        //JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName);
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    //valueObject[i] = JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName);
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
                
            } else if (StringUtils.equalsIgnoreCase(typeName, "String")) {

                classArr[i] = String.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getString(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
                
            } else if (StringUtils.equalsIgnoreCase(typeName, "Array")) {

                classArr[i] = JSONArray.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
            }
        }

        Method method = null;
        try {
            method = newClass.getMethod(methodName, classArr);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new Exception("输入的方法名：" + methodName + "引用失败，请验证。\r\n" + ex.getMessage());
        }

        Object returnObj = null;
        
        try {
            returnObj = method.invoke(newClass.newInstance(), valueObject);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new Exception("方法参数值传入失败，请验证。\r\n" + ex.getMessage());
        } finally {
            LogHelp.getInstance().info("调用返回值为：" + (JSONArray) returnObj);
        }

        return (JSONArray) returnObj;
    }

    public JSONObject callClassReturnJsonObject() throws ClassNotFoundException, Exception {

        Class<?> newClass = null;

        LogHelp.getInstance().info("开始调用外部类：\r\n" + "路径：" + classPath + "\r\n方法名：" + methodName + "\r\n返回值类型：JSONObject");

        try {
            newClass = Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("输入的类路径：" + classPath + "查找失败，请验证。\r\n" + ex.getMessage());
        }

        Class<?>[] classArr = new Class<?>[paramElementList.size()];
        Object[] valueObject = new Object[paramElementList.size()];

        for (int i = 0; i < paramElementList.size(); i++) {
            Element paramElement = paramElementList.get(i);
            String typeName = paramElement.getAttributeValue("type");
            String paramStr = paramElement.getTextTrim();

            if (StringUtils.equalsIgnoreCase(typeName, "Object")) {

                classArr[i] = JSONObject.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonObject(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
                
            } else if (StringUtils.equalsIgnoreCase(typeName, "String")) {

                classArr[i] = String.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getString(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
                
            } else if (StringUtils.equalsIgnoreCase(typeName, "Array")) {

                classArr[i] = JSONArray.class;
                if (StringUtils.startsWith(paramStr, "T#")) {
                    String tableName = StringUtils.substringBetween(paramStr, "T#", "#");
                    try {
                        JsonUtils.getInstance().getJsonObject(databaseInfoJson, tableName + ".name");
                    } catch (Exception ex) {
                        throw new NullParamException("输入的表名：" + paramStr + "找不到对应的变量值。\r\n" + ex.getMessage());
                    }
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(databaseInfoJson, tableName + ".name");
                } else if (StringUtils.startsWith(paramStr, "I#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(inputJson, StringUtils.substringBetween(paramStr, "I#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(eachKeyJson, StringUtils.substringBetween(paramStr, "TC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "TK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(taskVarJson, StringUtils.substringBetween(paramStr, "TK#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EC#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeCycleJson, StringUtils.substringBetween(paramStr, "EC#", "#"));
                } else if (StringUtils.startsWith(paramStr, "EK#")) {
                    valueObject[i] = JsonUtils.getInstance().getJsonArray(executeVarJson, StringUtils.substringBetween(paramStr, "EK#", "#"));
                } else {
                    valueObject[i] = paramStr;
                }
                
                LogHelp.getInstance().info("接收到第 " + (i + 1) + " 个输入参数，输入值：" + valueObject[i]);
            }
        }

        Method method = null;
        try {
            method = newClass.getMethod(methodName, classArr);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new Exception("输入的方法名：" + methodName + "引用失败，请验证。\r\n" + ex.getMessage());
        }

        Object returnObj = null;
        
        try {
            returnObj = method.invoke(newClass.newInstance(), valueObject);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new Exception("方法参数值传入失败，请验证。\r\n" + ex.getMessage());
        } finally {
            LogHelp.getInstance().info("调用返回值为：" + (JSONObject) returnObj);
        }

        return (JSONObject) returnObj;
    }
}
