/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.usevar;

import com.blit.yfez.yp.xmlanalyzeapp.exception.InputValueErrorException;
import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.sql.ExecuteSql;
import com.blit.yfez.yp.xmlanalyzeapp.thirdpartysupport.ReflectionControl;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class UseVarXml {

    private Element taskElement;
    private JSONObject inputParamValueJson;
    private JSONObject eachKeyJson;
    private JSONObject databaseJson;

    public UseVarXml(Element taskElement, JSONObject inputParamValueJson) {
        this.taskElement = taskElement;
        this.inputParamValueJson = inputParamValueJson;
    }

    public UseVarXml(Element taskElement, JSONObject databaseJson, JSONObject inputParamValueJson) {
        this.taskElement = taskElement;
        this.databaseJson = databaseJson;
        this.inputParamValueJson = inputParamValueJson;
    }

    public UseVarXml(Element taskElement, JSONObject databaseJson, JSONObject inputParamValueJson, JSONObject eachKeyJson) {
        this.taskElement = taskElement;
        this.databaseJson = databaseJson;
        this.inputParamValueJson = inputParamValueJson;
        this.eachKeyJson = eachKeyJson;
    }

    public JSONObject getJsonValue() throws Exception {

        JSONObject jsonUseVar = new JSONObject();

        for (Element useVarElement : taskElement.getChildren("UseVar")) {
            String name = useVarElement.getAttributeValue("name");
            String type = useVarElement.getAttributeValue("type");
            String useVarStr = useVarElement.getTextTrim();

            if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
                throw new NullParamException("Tasks -> Task -> UseVar节点下必须包含name、type属性。");
            } else {
                switch (type) {
                    case "String":
                        if (StringUtils.startsWith(useVarStr, "I#") && StringUtils.endsWith(useVarStr, "#")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(inputParamValueJson, StringUtils.substringBetween(useVarStr, "I#", "#")));
                        } else if (StringUtils.startsWith(useVarStr, "TC#") && StringUtils.endsWith(useVarStr, "#")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(eachKeyJson, StringUtils.substringBetween(useVarStr, "TC#", "#")));
                        } else if (StringUtils.startsWith(useVarStr, "TK#") && StringUtils.endsWith(useVarStr, "#")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), JsonUtils.getInstance().getString(jsonUseVar, StringUtils.substringBetween(useVarStr, "TK#", "#")));
                        } else {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), useVarStr);
                        }
                        break;
                    case "SQL":
                        if (StringUtils.contains(useVarStr, "T#")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), new ExecuteSql(useVarElement.getTextTrim(), databaseJson, inputParamValueJson, eachKeyJson, jsonUseVar).getJsonSelect());
                        } else {
                            throw new InputValueErrorException("Tasks -> Task -> UseVar节点type属性为SQL时，查询语句必须包含数据库表别名（例：T#sc_tdyxyh#）。");
                        }
                        break;
                    case "Class":
                        Element classPathElement = useVarElement.getChild("ClassPath");
                        Element methodElement = useVarElement.getChild("Method");
                        if (classPathElement == null || methodElement == null) {
                            throw new NullParamException("Tasks -> Task -> UseVar节点type属性为“Class”时，必须具备“ClassPath”和“Method”节点。");
                        }

                        String classPath = classPathElement.getTextTrim();
                        String methodName = methodElement.getAttributeValue("name");
                        String methodReturnType = methodElement.getAttributeValue("returnType");
                        if (StringUtils.isBlank(classPath)) {
                            throw new NullParamException("Tasks -> Task -> UseVar -> ClassPath节点必须填写方法路径。");
                        }
                        if (StringUtils.isBlank(methodName) || StringUtils.isBlank(methodReturnType)) {
                            throw new NullParamException("Tasks -> Task -> UseVar -> Method节点必须填写“name”和“returnType”属性。");
                        }

                        List<Element> paramElementList = methodElement.getChildren("Param");

                        if (StringUtils.equalsIgnoreCase(methodReturnType, "String")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), new ReflectionControl(classPath, methodName, paramElementList, inputParamValueJson, databaseJson, eachKeyJson, jsonUseVar).callClassReturnStr());
                        } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Array")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), new ReflectionControl(classPath, methodName, paramElementList, inputParamValueJson, databaseJson, eachKeyJson, jsonUseVar).callClassReturnJsonArray());
                        } else if (StringUtils.equalsIgnoreCase(methodReturnType, "Object")) {
                            jsonUseVar.put(useVarElement.getAttributeValue("name"), new ReflectionControl(classPath, methodName, paramElementList, inputParamValueJson, databaseJson, eachKeyJson, jsonUseVar).callClassReturnJsonObject());
                        }
                        break;
                    case "Array":
                        throw new UnsupportedOperationException("Tasks -> Task -> UseVar节点type属性暂不支持Array数据类型。");
                    default:
                        return null;
                }
            }
        }

        return jsonUseVar;
    }
}
