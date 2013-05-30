/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.xml.databaseinfo;

import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.exception.SameAliasNameException;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;

/**
 *
 * @author CBX
 */
public class DatabaseInfoXml {

    private Element databaseInfoElement;

    public DatabaseInfoXml(Element databaseInfoElement) {
        this.databaseInfoElement = databaseInfoElement;
    }

    private JSONObject getDBPoolsJson(Element dbPoolsElement) throws Exception {

        JSONObject jsonReturn = new JSONObject();

        List<Element> dbElementList = dbPoolsElement.getChildren("DB");
        if (dbElementList.isEmpty()) {
            throw new NullParamException("DBPools节点下必须包含“DB”子节点，请重新检查。");
        }

        for (Element dbElement : dbElementList) {
            JSONObject json = new JSONObject();

            String alias = dbElement.getAttributeValue("alias");
            String ip = dbElement.getAttributeValue("ip");
            String port = dbElement.getAttributeValue("port");
            String sid = dbElement.getAttributeValue("sid");
            String username = dbElement.getAttributeValue("username");
            String password = dbElement.getAttributeValue("password");
            String type = dbElement.getAttributeValue("type");
            String racConnStr = dbElement.getTextTrim();

            if (StringUtils.isBlank(alias)
                    || StringUtils.isBlank(ip)
                    || StringUtils.isBlank(port)
                    || StringUtils.isBlank(sid)
                    || StringUtils.isBlank(username)
                    || StringUtils.isBlank(password)
                    || StringUtils.isBlank(type)) {
                throw new NullParamException("DB节点下必须包含alias、ip、port、sid、username、password、type属性。");
            } else {
                json.put("ip", ip);
                json.put("port", port);
                json.put("sid", sid);
                json.put("username", username);
                json.put("password", password);
                json.put("type", type);
                json.put("racConnStr", racConnStr);
            }

            jsonReturn.put(alias, json);
        }

        return jsonReturn;
    }

    public JSONObject getJsonValue() throws Exception {

        JSONObject databaseInfoJson = new JSONObject();

        String useHead = databaseInfoElement.getAttributeValue("useHead");

        if (StringUtils.isBlank(useHead)) {
            throw new NullParamException("DatabaseInfo节点必须指定属性useHead，请重新检查。");
        }

        List<Element> dbPoolsElementList = databaseInfoElement.getChildren("DBPools");
        if (dbPoolsElementList.isEmpty()) {
            throw new NullParamException("找不到“DBPools”节点，请重新检查。");
        } else if (dbPoolsElementList.size() > 1) {
            throw new NullParamException("“DBPools”节点在DatabaseInfo中只允许出现一次，请重新检查。");
        }

        List<Element> tablesElementList = databaseInfoElement.getChildren("Tables");
        if (tablesElementList.isEmpty()) {
            throw new NullParamException("找不到“Tables”节点，请重新检查。");
        } else if (tablesElementList.size() > 1) {
            throw new NullParamException("“Tables”节点在DatabaseInfo中只允许出现一次，请重新检查。");
        }

        JSONObject dbPoolsJson = getDBPoolsJson(dbPoolsElementList.get(0));

        for (Element tablesElement : tablesElementList.get(0).getChildren("Table")) {
            String name = tablesElement.getAttributeValue("name");
            String belongDB = tablesElement.getAttributeValue("belongDB");
            String alias = tablesElement.getAttributeValue("alias");
            if (StringUtils.isBlank(name)
                    || StringUtils.isBlank(belongDB)
                    || StringUtils.isBlank(alias)) {
                throw new NullParamException("Table节点下必须包含name、belongDB、alias属性。");
            } else {
                if (dbPoolsJson.containsKey(belongDB)) {
                    if (databaseInfoJson.containsKey(alias)) {
                        throw new SameAliasNameException("Table节点下alias属性值：" + alias + "有重名，请重新检查。");
                    } else {
                        JSONObject dbInfoJson = dbPoolsJson.getJSONObject(belongDB);
                        dbInfoJson.put("name", name);
                        dbInfoJson.put("belongDB", belongDB);
                        databaseInfoJson.put(alias, dbInfoJson);
                    }
                } else {
                    throw new NullParamException("在DBPools节点中找不到变量名为：" + belongDB + "的数据库连接。");
                }
            }
        }

        return databaseInfoJson;
    }
}
