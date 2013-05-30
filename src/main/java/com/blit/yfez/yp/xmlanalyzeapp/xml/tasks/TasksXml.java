/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.tasks;

import com.blit.yfez.yp.xmlanalyzeapp.exception.InputValueErrorException;
import com.blit.yfez.yp.xmlanalyzeapp.utils.JsonUtils;
import com.blit.yfez.yp.xmlanalyzeapp.xml.tasks.execute.ExecuteXml;
import com.blit.yfez.yp.xmlanalyzeapp.xml.usevar.UseVarXml;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class TasksXml {

    private Element tasksElement;
    private JSONObject inputJson;
    private JSONObject databaseInfoJson;

    public TasksXml(Element tasksElement, JSONObject inputJson, JSONObject databaseInfoJson) {
        this.tasksElement = tasksElement;
        this.inputJson = inputJson;
        this.databaseInfoJson = databaseInfoJson;
    }

    public JSONArray getJsonValue() throws Exception {

        List<Element> taskElementList = tasksElement.getChildren("Task");
        JSONArray returnJsonArray = new JSONArray();

        for (Element taskElement : taskElementList) {

            String taskUseVar = taskElement.getAttributeValue("var");

            if (taskUseVar == null) {
                JSONObject taskValueJson = new UseVarXml(taskElement, databaseInfoJson, inputJson).getJsonValue();
                JSONArray tmpArray = new ExecuteXml(taskElement, databaseInfoJson, inputJson, taskValueJson).getJsonValue();
                if (tmpArray != null && !tmpArray.isEmpty()) {
                    returnJsonArray.add(tmpArray);
                }
            } else {
                if (StringUtils.isNotBlank(taskUseVar)) {
                    if (!StringUtils.startsWith(taskUseVar, "I#")) {
                        throw new InputValueErrorException("Tasks -> Task -> var属性必须使用InputPools变量。");
                    }
                }

                JSONArray valueArray = JsonUtils.getInstance().getJsonArray(inputJson, StringUtils.substringBetween(taskUseVar, "I#", "#"));

                for (int i = 0; i < valueArray.size(); i++) {
                    JSONObject tasksVarJson = valueArray.getJSONObject(i);
                    JSONObject taskValueJson = new UseVarXml(taskElement, databaseInfoJson, inputJson, tasksVarJson).getJsonValue();
                    returnJsonArray.add(new ExecuteXml(taskElement, databaseInfoJson, inputJson, tasksVarJson, taskValueJson).getJsonValue());
                }
            }
        }

        return returnJsonArray;
    }
}