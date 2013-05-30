/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.tasks.execute.executevar;

import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.sql.ExecuteSql;
import com.blit.yfez.yp.xmlanalyzeapp.thirdpartysupport.ReflectionControl;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import java.util.HashMap;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class ExecuteVarXml {

    private Element executeElement;
    private JSONObject databaseInfoJson;
    private JSONObject inputJson;
    private JSONObject eachTaskKeyJson;
    private JSONObject taskVarJson;
    private JSONObject executeCycleJson;

    public ExecuteVarXml(Element executeElement, JSONObject databaseInfoJson, JSONObject inputJson,
            JSONObject eachTaskKeyJson, JSONObject taskVarJson, JSONObject executeCycleJson) {
        this.executeElement = executeElement;
        this.databaseInfoJson = databaseInfoJson;
        this.inputJson = inputJson;
        this.eachTaskKeyJson = eachTaskKeyJson;
        this.taskVarJson = taskVarJson;
        this.executeCycleJson = executeCycleJson;
    }

    public ExecuteVarXml(Element executeElement, JSONObject databaseInfoJson, JSONObject inputJson,
            JSONObject eachTaskKeyJson, JSONObject taskVarJson) {
        this.executeElement = executeElement;
        this.databaseInfoJson = databaseInfoJson;
        this.inputJson = inputJson;
        this.eachTaskKeyJson = eachTaskKeyJson;
        this.taskVarJson = taskVarJson;
    }

    public JSONObject getJsonValue() throws Exception {

        JSONObject executeVarJson = new JSONObject();

        for (Element executeVarElement : executeElement.getChildren("ExecuteVar")) {
            String name = executeVarElement.getAttributeValue("name");
            String type = executeVarElement.getAttributeValue("type");
            String executeVarStr = executeVarElement.getTextTrim();

            if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
                throw new NullParamException("Tasks -> Task -> Execute -> ExecuteVar节点下必须包含name、type属性。");
            } else {
                switch (type) {
                    case "String":
                        if (StringUtils.startsWith(executeVarStr, "I#") && StringUtils.endsWith(executeVarStr, "#")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(executeVarStr, "I#", "#")));
                        } else if (StringUtils.startsWith(executeVarStr, "TC#") && StringUtils.endsWith(executeVarStr, "#")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(eachTaskKeyJson, StringUtils.substringBetween(executeVarStr, "TC#", "#")));
                        } else if (StringUtils.startsWith(executeVarStr, "TK#") && StringUtils.endsWith(executeVarStr, "#")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(executeVarStr, "TK#", "#")));
                        } else if (StringUtils.startsWith(executeVarStr, "EC#") && StringUtils.endsWith(executeVarStr, "#")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(executeVarStr, "EC#", "#")));
                        } else if (StringUtils.startsWith(executeVarStr, "EK#") && StringUtils.endsWith(executeVarStr, "#")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(executeVarStr, "EK#", "#")));
                        } else {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), executeVarStr);
                        }
                        break;
                    case "SQL":
                        if (StringUtils.contains(executeVarStr, "T#")) {
                            HashMap<String, String> hmExecuteVar = new ExecuteSql(executeVarElement.getTextTrim(), databaseInfoJson, inputJson, eachTaskKeyJson, taskVarJson, executeCycleJson, executeVarJson).getExecuteVarSelect();
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), hmExecuteVar);
                            if (hmExecuteVar.isEmpty()) {
                                LogHelp.getInstance().info("执行 Tasks -> Task -> Execute -> ExecuteVar 中的SQL语句：\r\n" + executeVarElement.getTextTrim() + "\r\n查询为空。");
                            }
                        } else {
                            throw new NullParamException("Tasks -> Task -> UseVar节点type属性为SQL时，查询语句必须包含数据库表别名（例：T#sc_tdyxyh#）。");
                        }
                        break;
                    case "Class":
                        Element classPathElement = executeVarElement.getChild("ClassPath");
                        Element methodElement = executeVarElement.getChild("Method");
                        if (classPathElement == null || methodElement == null) {
                            throw new NullParamException("Tasks -> Task -> UseVar节点type属性为“Class”时，必须具备“ClassPath”和“Method”节点。");
                        }

                        String classPath = classPathElement.getTextTrim();
                        String methodName = methodElement.getAttributeValue("name");
                        String methodReturnType = methodElement.getAttributeValue("returnType");
                        if (StringUtils.isBlank(classPath)) {
                            throw new NullParamException("Tasks -> Task -> Execute -> ExecuteVar -> ClassPath节点必须填写方法路径。");
                        }
                        if (StringUtils.isBlank(methodName) || StringUtils.isBlank(methodReturnType)) {
                            throw new NullParamException("Tasks -> Task -> Execute -> ExecuteVar -> Method节点必须填写“name”和“returnType”属性。");
                        }

                        List<Element> paramElementList = methodElement.getChildren("Param");

                        if (StringUtils.equalsIgnoreCase(methodReturnType, "String")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachTaskKeyJson, taskVarJson, executeCycleJson, executeVarJson).callClassReturnStr());
                        } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Object")) {
                            executeVarJson.put(executeVarElement.getAttributeValue("name"), new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachTaskKeyJson, taskVarJson, executeCycleJson, executeVarJson).callClassReturnJsonObject());
                        } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Array")) {
                            throw new UnsupportedOperationException("Tasks -> Task -> Execute -> ExecuteVar -> Method中的返回类型不能是Array。");
                        }
                        break;
                    case "Array":
                        throw new UnsupportedOperationException("Tasks -> Task -> UseVar节点type属性暂不支持Array数据类型。");
                    default:
                        return null;
                }
            }
        }

        return executeVarJson;
    }
}
