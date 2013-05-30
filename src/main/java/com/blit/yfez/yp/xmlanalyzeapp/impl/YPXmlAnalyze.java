/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.impl;

import com.blit.yfez.yp.xmlanalyzeapp.XmlAnalyze;
import com.blit.yfez.yp.xmlanalyzeapp.exception.NullParamException;
import com.blit.yfez.yp.xmlanalyzeapp.log.LogHelp;
import com.blit.yfez.yp.xmlanalyzeapp.xml.XmlController;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 *
 * @author CBX
 */
public class YPXmlAnalyze implements XmlAnalyze {

    @Override
    public JSONArray execute(String xmlFilePath, String... inputValueList) throws Exception {

        JSONObject xmlVarParam = new JSONObject();
        JSONObject databaseParam = new JSONObject();
        JSONArray returnValueArray = new JSONArray();

        Document xmlDocument = null;
        XmlController controller = new XmlController();

        try {
            xmlDocument = controller.initXml(xmlFilePath);
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("指定的XML文件（" + xmlFilePath + "）不存在。\r\n" + ex.getMessage());
        } catch (JDOMException ex) {
            throw new JDOMException("指定的XML文件（" + xmlFilePath + "）不是标准的XML格式。\r\n" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(XmlAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            LogHelp.getInstance().info("开始调用XML文件（" + xmlFilePath + "）。");
        }

        List<Element> inputPoolsElementList = xmlDocument.getRootElement().getChildren("InputPools");
        if (inputPoolsElementList.isEmpty()) {
            throw new NullParamException("找不到“InputPools”节点，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        } else if (inputPoolsElementList.size() > 1) {
            throw new NullParamException("“InputPools”节点在XML中只允许出现一次，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        }
        xmlVarParam = controller.inputPoolsExecute(xmlDocument, inputValueList);

        List<Element> databaseInfoElementList = xmlDocument.getRootElement().getChildren("DatabaseInfo");
        if (databaseInfoElementList.isEmpty()) {
            throw new NullParamException("找不到“DatabaseInfo”节点，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        } else if (databaseInfoElementList.size() > 1) {
            throw new NullParamException("“DatabaseInfo”节点在XML中只允许出现一次，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        }
        databaseParam = controller.databaseInfoExecute(xmlDocument);

        List<Element> tasksElementList = xmlDocument.getRootElement().getChildren("Tasks");
        if (tasksElementList.isEmpty()) {
            throw new NullParamException("找不到“Tasks”节点，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        } else if (tasksElementList.size() > 1) {
            throw new NullParamException("“Tasks”节点在XML中只允许出现一次，请重新检查。输入的XML文件（" + xmlFilePath + "）。");
        }
        returnValueArray = controller.tasksExecute(xmlDocument, xmlVarParam, databaseParam);

        return returnValueArray;
    }

    public static void main(String[] args) {

        XmlAnalyze analyze = new YPXmlAnalyze();
        JSONArray json = new JSONArray();
        try {
//            json = analyze.execute(
//                    "D:\\test\\95598_2_PDA.xml",
//                    new String[]{
//                        "{\"ydsbid\":\"e01b8c82b4e948a7\",\"yhguid\":\"02450105195806100072\"}"
//                       // "{\"ssqx\":\"040100\",\"gddw\":\"昊天你妹\",\"zh\":\"01\",\"glgd\":\"2013010801\",\"gllx\":\"低压故障\",\"ywlx\":\"95598处理\",\"gzxx\":\"故障现象\",\"slry\":\"黄柏然\",\"slbm\":\"配电运行班\",\"slsj\":\"2012-12-19 14:00:00\",\"lybh\":\"201301080910\",\"xlmc\":\"测试线路\",\"gdd\":\"供电地点\",\"dydj\":\"05\",\"sdd\":\"04010012012\",\"jjcd\":\"严重\",\"whcd\":\"一般\",\"gzgs\":\"南供\",\"cqsx\":\"局有局维\",\"hfbz\":\"已回访\",\"hfdh\":\"15578680612\",\"hfsx\":\"1天\",\"gjsj\":\"2013-1-8 15:30:00\", \"gzdz\":\"朝阳广场\" ,\"gzdbh\":\"0401000001\"}"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\TD_SHORTAGE_RECORD.xml",
//                    new String[]{
//                        "<DataMark sender=\"生产MIS系统\" sendTime=\"\" zoneId =\"\"><Mark><TableName>TdShortageRecordService</TableName><Key><PKValue>3021B97820E948BDA471BD295EDA5FF5</PKValue><Flag>1</Flag></Key></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\Xsyc2MP.xml",
//                    new String[]{ 
//                        "{\"gdjdm\":\"040100\",\"qxdw\":\"昊天11111\",\"qxfzr\":\"农宁勇\",\"qxlxdh\":\"13607812239\",\"tq\":\"多云\",\"pcry\":\"黄柏然\",\"pccl\":\"桂A-95598\",\"yjxfsj\":\"2012-12-19 18:00:00\",\"cfsj\":\"\",\"ddsj\":\"2012-12-19 14:00:00\",\"fxgzdsj\":\"2012-12-19 14:30:00\",\"gzpcsj\":\"2012-12-19 15:00:00\",\"hfsdsj\":\"2012-12-19 17:00:00\",\"dydj\":\"05\",\"cqsx\":\"局有局维\",\"tdyy\":\"变压器核爆\",\"xcfl\":\"户外\",\"gzsblx\":\"核裂变变压器\",\"gzjsyy\":\"反应堆温度过高\",\"aqcs\":\"无\",\"xcqxjl\":\"\",\"khyj\":\"已恢复送电。\",\"clyj\":\"同意\", \"gzdbh\":\"04010000001\"}"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\Xsyc2MP.xml",
//                    new String[]{ 
//                        "<DataMark sender=\"计量自动化系统\" sendTime=\"2012-12-17 21:02:01\" zoneId =\"040100\"><Mark><app_no>2012110140402011156</app_no><staDate>2012-11-01</staDate><endDate>2012-12-01</endDate><mc>峙村931</mc><dydj>05</dydj><bh>0402011156</bh><ssbyqmc></ssbyqmc><srdl>1361760.00</srdl><scdl>564608.90</scdl><xsl>58.54</xsl><xsycms>dd</xsycms><xsdxlx>4</xsdxlx><ssfjmc>南宁供电局</ssfjmc><ssfjid>040100</ssfjid><sstq></sstq><xlfzrid></xlfzrid><xlfzr></xlfzr><tqfzrid></tqfzrid><tqfzr></tqfzr></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\TD_SHORTAGE_RECORD.xml",
//                    new String[]{ 
//                        "<DataMark sender=\"生产MIS系统\" sendTime=\"\" zoneId =\"\"><Mark><Key><PKValue>BF8DBE8EA6294B8BBC12C043112E71FB</PKValue><Flag>1</Flag></Key><Key><PKValue>D38E680E3AA8443D923684DCA4E40878</PKValue><Flag>1</Flag></Key></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\PDA_2_95598.xml",
//                    new String[]{
//                        "<DataMark sender=\"广西营配数据仓库数据共享系统\" sendTime=\"2012-11-09 10:38:40\" zoneId=\"040101\"><Mark><TableName>TD_OTHER_RECORD</TableName><Key><PKValue>010926972,012271565</PKValue><Flag>1</Flag></Key></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\DZHYJ_YX_2_SCMIS.xml",
//                    new String[]{
//                        "<DataMark sender=\"广西营配数据仓库数据共享系统\" sendTime=\"2013-03-06 17:17:41\" zoneId=\"020101\"><Mark><TableName>YP_APP_BASE_INFO</TableName><Key><PKValue>010669667,012054029</PKValue><Flag>1</Flag></Key></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\TD_TROUBLE_2_95598.xml",
//                    new String[]{
//                        "<DataMark sender=\"生产MIS系统\" sendTime=\"\" zoneId =\"\"><Mark><Key><PKValue>B8B18432B6554E7687655C5450823608</PKValue><Flag>1</Flag></Key></Mark></DataMark>"
//                    });
//            json = analyze.execute(
//                    "D:\\test\\YP_BDZ_SEND2YX.xml",
//                    new String[]{});
            json = analyze.execute(
                    "D:\\test\\SCMIS_2_EXADATA.xml",
                    new String[]{
                    "1002.南供生产MIS.数据抽取方案", "nn_scmis, lz_scmis"
                    });
        } catch (Exception ex) {
            LogHelp.getInstance().error(ex.getMessage(), ex);
        }

        System.out.println(json);
    }
}
