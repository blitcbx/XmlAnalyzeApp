/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.tasks.execute;

import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.sql.ExecuteSql;
import com.blit.yfez.yp.xmlanalyzeapp.thirdpartysupport.ReflectionControl;
import com.blit.yfez.yp.xmlanalyzeapp.utils.Calc;
import com.blit.yfez.yp.xmlanalyzeapp.utils.ConditionStrUtils;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import com.blit.yfez.yp.xmlanalyzeapp.xml.tasks.execute.executevar.ExecuteVarXml;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class ExecuteXml {

    private Element taskElement;
    private JSONObject databaseInfoJson;
    private JSONObject inputJson;
    private JSONObject eachKeyJson;
    private JSONObject taskVarJson;

    public ExecuteXml(Element taskElement, JSONObject databaseInfoJson, JSONObject inputJson, JSONObject taskVarJson) {
        this.taskElement = taskElement;
        this.databaseInfoJson = databaseInfoJson;
        this.inputJson = inputJson;
        this.taskVarJson = taskVarJson;
    }

    public ExecuteXml(Element taskElement, JSONObject databaseInfoJson, JSONObject inputJson, JSONObject eachKeyJson, JSONObject taskVarJson) {
        this.taskElement = taskElement;
        this.databaseInfoJson = databaseInfoJson;
        this.inputJson = inputJson;
        this.eachKeyJson = eachKeyJson;
        this.taskVarJson = taskVarJson;
    }

    public JSONArray getJsonValue() throws Exception {

        Element conditionElement = taskElement.getChild("Condition");

        if (conditionElement != null) {
            String conditionStr = conditionElement.getTextTrim();
            ArrayList<String> conditionList = ConditionStrUtils.getInstance().getParam(conditionStr);
            for (String tmp : conditionList) {
                if (StringUtils.startsWith(tmp, "TK#")) {
                    conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                } else if (StringUtils.startsWith(tmp, "I#")) {
                    conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                } else if (StringUtils.startsWith(tmp, "TC#")) {
                    conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                }
            }
            LogHelp.getInstance().info("执行 Tasks -> Task -> Condition的公式：\r\n" + conditionElement.getTextTrim() + "\r\n的运算值为：\r\n" + conditionStr);
            if (!new Calc().getCalc(conditionStr)) {
                LogHelp.getInstance().info("执行结果为：False，跳出本次循环。");
                return null;
            } else {
                LogHelp.getInstance().info("执行结果为：True");
            }
        }

        List<Element> executeElementList = taskElement.getChildren("Execute");
        for (Element executeElement : executeElementList) {

            JSONArray executeVarArray = null;
            String var = executeElement.getAttributeValue("var");

            if (StringUtils.isNotBlank(var)) {
                try {
                    if (StringUtils.startsWith(var, "TK#")) {
                        executeVarArray = JsonUtils.getInstance().getJsonArray(taskVarJson, StringUtils.substringBetween(var, "TK#", "#"));
                    } else if (StringUtils.startsWith(var, "I#")) {
                        executeVarArray = JsonUtils.getInstance().getJsonArray(inputJson, StringUtils.substringBetween(var, "I#", "#"));
                    } else {
                        throw new NullParamException("Tasks -> Task -> Execute节点下的var属性只支持TK和I类型变量。");
                    }
                } catch (Exception ex) {
                    throw new NullParamException("Tasks -> Task -> Execute节点下的var属性必须为数组。\r\n" + ex.getMessage());
                } finally {
                    if (executeVarArray == null || executeVarArray.isEmpty()) {
                        LogHelp.getInstance().info("执行 Tasks -> Task -> Execute 的var循环数组" + var + "为空。");
                    } else {
                        LogHelp.getInstance().info("执行 Tasks -> Task -> Execute 的var循环变量值" + var + "数组为：" + executeVarArray + "。");
                        LogHelp.getInstance().info("执行 Tasks -> Task -> Execute 的var循环变量值" + var + "数组长度为：" + executeVarArray.size() + "。");
                    }
                }

                for (int i = 0; i < executeVarArray.size(); i++) {

                    JSONObject executeCycleJson = executeVarArray.getJSONObject(i);
                    JSONObject executeVarJson = new ExecuteVarXml(executeElement, databaseInfoJson, inputJson, eachKeyJson, taskVarJson, executeCycleJson).getJsonValue();

                    Element conditionExecuteElement = executeElement.getChild("Condition");

                    if (conditionExecuteElement != null) {
                        String conditionStr = conditionExecuteElement.getTextTrim();
                        ArrayList<String> conditionList = ConditionStrUtils.getInstance().getParam(conditionStr);
                        for (String tmp : conditionList) {
                            if (StringUtils.startsWith(tmp, "I#")) {
                                conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                            } else if (StringUtils.startsWith(tmp, "TC#")) {
                                conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                            } else if (StringUtils.startsWith(tmp, "TK#")) {
                                conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                            } else if (StringUtils.startsWith(tmp, "EC#")) {
                                conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(executeCycleJson, StringUtils.substringBetween(tmp, "EC#", "#")));
                            } else if (StringUtils.startsWith(tmp, "EK#")) {
                                conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(tmp, "EK#", "#")));
                            }
                        }
                        LogHelp.getInstance().info("执行 Tasks -> Task -> Condition的公式：\r\n" + conditionExecuteElement.getTextTrim() + "\r\n的运算值为：\r\n" + conditionStr);
                        if (!new Calc().getCalc(conditionStr)) {
                            LogHelp.getInstance().info("执行结果为：False，跳出本次循环。");
                            continue;
                        } else {
                            LogHelp.getInstance().info("执行结果为：True");
                        }
                    }

                    List<Element> executeStrList = executeElement.getChildren("ExecuteStr");
                    
                    for (Element executeStrElement : executeStrList) {
                        String type = executeStrElement.getAttributeValue("type");
                        switch (type) {
                            case "SQL":
                                new ExecuteSql(executeStrElement.getTextTrim(), databaseInfoJson, inputJson, eachKeyJson, taskVarJson, executeCycleJson, executeVarJson).executeDMLData();
                                break;
                            case "Class":
                                Element classPathElement = executeStrElement.getChild("ClassPath");
                                Element methodElement = executeStrElement.getChild("Method");
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
                                    new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachKeyJson, taskVarJson, executeCycleJson, executeVarJson).callClassReturnStr();
                                } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Object")) {
                                    new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachKeyJson, taskVarJson, executeCycleJson, executeVarJson).callClassReturnJsonObject();
                                } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Array")) {
                                    throw new UnsupportedOperationException("Tasks -> Task -> Execute -> ExecuteVar -> Method中的返回类型不能是Array。");
                                }
                                break;
                            default:
                                throw new UnsupportedOperationException(" Tasks -> Task -> Execute -> ExecuteStr 的type只允许包含SQL/Class类型。");
                        }
                    }
                }
            } else {
                JSONObject executeVarJson = new ExecuteVarXml(executeElement, databaseInfoJson, inputJson, eachKeyJson, taskVarJson).getJsonValue();

                Element conditionExecuteElement = executeElement.getChild("Condition");

                if (conditionExecuteElement != null) {
                    String conditionStr = conditionExecuteElement.getTextTrim();
                    ArrayList<String> conditionList = ConditionStrUtils.getInstance().getParam(conditionStr);
                    for (String tmp : conditionList) {
                        if (StringUtils.startsWith(tmp, "I#")) {
                            conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(inputJson, StringUtils.substringBetween(tmp, "I#", "#")));
                        } else if (StringUtils.startsWith(tmp, "TC#")) {
                            conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(tmp, "TC#", "#")));
                        } else if (StringUtils.startsWith(tmp, "TK#")) {
                            conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(taskVarJson, StringUtils.substringBetween(tmp, "TK#", "#")));
                        } else if (StringUtils.startsWith(tmp, "EK#")) {
                            conditionStr = StringUtils.replace(conditionStr, tmp, JsonUtils.getInstance().getString(executeVarJson, StringUtils.substringBetween(tmp, "EK#", "#")));
                        }
                    }
                    LogHelp.getInstance().info("执行 Tasks -> Task -> Execute -> Condition的公式：\r\n" + conditionExecuteElement.getTextTrim() + "\r\n的运算值为：\r\n" + conditionStr);
                    if (!new Calc().getCalc(conditionStr)) {
                        LogHelp.getInstance().info("执行结果为：False，跳出本次循环。");
                        continue;
                    } else {
                        LogHelp.getInstance().info("执行结果为：True");
                    }
                }

                List<Element> executeStrList = executeElement.getChildren("ExecuteStr");

                for (Element executeStrElement : executeStrList) {
                    String type = executeStrElement.getAttributeValue("type");
                    switch (type) {
                        case "SQL":
                            new ExecuteSql(executeStrElement.getTextTrim(), databaseInfoJson, inputJson, eachKeyJson, taskVarJson, executeVarJson).executeDMLData();
                            break;
                        case "Class":
                            Element classPathElement = executeStrElement.getChild("ClassPath");
                            Element methodElement = executeStrElement.getChild("Method");
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
                                new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachKeyJson, taskVarJson, executeVarJson).callClassReturnStr();
                            } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Object")) {
                                new ReflectionControl(classPath, methodName, paramElementList, inputJson, databaseInfoJson, eachKeyJson, taskVarJson, executeVarJson).callClassReturnJsonObject();
                            } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Array")) {
                                throw new UnsupportedOperationException("Tasks -> Task -> Execute -> ExecuteVar -> Method中的返回类型不能是Array。");
                            }
                            break;
                        default:
                            throw new UnsupportedOperationException(" Tasks -> Task -> Execute -> ExecuteStr 的type只允许包含SQL/Class类型。");
                    }
                }
                
                //List<Element> outputStrList = executeElement.getChildren("Output");
            }
        }

        JSONArray array = new JSONArray();
        array.add("1");
        array.add("2");
        
        return array;
    }
}