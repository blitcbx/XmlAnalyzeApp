/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.inputpools;

import com.blit.yfez.yp.xmlanalyzeapp.exception.InputValueErrorException;
import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.exception.SameAliasNameException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class InputPoolsXml {

    private Element inputPoolsElement;
    private String[] inputValueList;

    public InputPoolsXml(Element inputPoolsElement, String[] inputValueList) {
        this.inputPoolsElement = inputPoolsElement;
        this.inputValueList = inputValueList;
    }

    public JSONObject getJsonValue() throws Exception {

        JSONObject json = new JSONObject();
        String useHead = inputPoolsElement.getAttributeValue("useHead");
        if (StringUtils.isBlank(useHead)) {
            throw new NullParamException("InputPools节点中必须指定useHead属性。");
        }

        List<Element> inputElementList = inputPoolsElement.getChildren("Input");

//        if (inputElementList.isEmpty()) {
//            throw new NullInputElementException("InputPools节点中必须包含Input节点。");
//        }

        if (inputElementList.size() != this.inputValueList.length) {
            throw new InputValueErrorException("输入的参数列表与InputPools中的参数列表数量不同。");
        }

        for (int i = 0; i < inputElementList.size(); i++) {

            Element inputElement = inputElementList.get(i);

            String alias = inputElement.getAttributeValue("alias");
            if (StringUtils.isBlank(alias)) {
                throw new NullParamException("InputPools中必须为元素指定alias元素。");
            }

            String type = inputElement.getAttributeValue("type");
            if (StringUtils.isBlank(type)) {
                throw new NullParamException("InputPools中必须为元素指定type元素。");
            }

            Attribute useClass = inputElement.getAttribute("useClass");

            switch (type) {
                case "XML":
                    if (inputElement.getAttribute("useClass") == null) {
                        throw new NullParamException("输入变量：" + alias + "为XML类型，必须与‘useClass’节点搭配使用。");
                    } else {
                        InputPoolsXmlAnalyze xmlAna = null;
                        try {
                            xmlAna = (InputPoolsXmlAnalyze) Class.forName(useClass.getValue()).newInstance();
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                            throw new Exception("输入变量：" + alias + "中的useClass（" + useClass.getValue() + "）实例化失败。\r\n" + ex.getMessage());
                            //Logger.getLogger(InputPoolsXml.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (json.containsKey(alias)) {
                            throw new NullParamException("配置表中的变量名：" + alias + "，已经被使用。");
                        } else {
                            json.put(alias, xmlAna.getXmlJsonObject(inputValueList[i]));
                        }
                    }
                    break;
                case "Object":
                    if (inputElement.getAttribute("useClass") == null) {
                        throw new NullParamException("输入变量：" + alias + "为Object类型，必须与‘useClass’节点搭配使用。");
                    } else {
                        InputPoolsXmlAnalyze xmlAna = null;
                        try {
                            xmlAna = (InputPoolsXmlAnalyze) Class.forName(useClass.getValue()).newInstance();
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                            throw new Exception("输入变量：" + alias + "中的useClass（" + useClass.getValue() + "）实例化失败。\r\n" + ex.getMessage());
                            //Logger.getLogger(InputPoolsXml.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (json.containsKey(alias)) {
                            throw new SameAliasNameException("配置表中的变量名：" + alias + "，已经被使用。");
                        } else {
                            json.put(alias, xmlAna.getXmlJsonObject(inputValueList[i]));
                            LogHelp.getInstance().info("接收到输入参数：\r\n" + inputValueList[i]);
                        }
                    }
                    break;
                case "String":
                    if (inputElement.getAttribute("useClass") != null) {
                        throw new UnsupportedOperationException("暂不对String类型提供useType属性支持。");
                    } else {
                        if (json.containsKey(alias)) {
                            throw new SameAliasNameException("配置表中的变量名：" + alias + "，已经被使用。");
                        } else {
                            json.put(alias, inputValueList[i]);
                            LogHelp.getInstance().info("接收到输入参数：\r\n" + inputValueList[i]);
                        }
                    }
                    break;
                default:
                    throw new NullParamException("输入变量：" + alias + "中的type只能是XML或者String类型。");
            }
        }

        return json;
    }
}