package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.AttachmentVO;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.cs.WatermarkVO;
import cfca.trustsign.common.vo.request.tx3.Tx3201ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3201 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3201ReqVO tx3201ReqVO = new Tx3201ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        Map<String, String> fieldMap = new HashMap<String, String>();
        CreateContractVO createContract = new CreateContractVO();

        createContract.setIsSign(1);
        createContract.setSealId("76F8EF752F276352E05311016B0A86CB");//添加印章
        createContract.setTemplateId("MM_1396");
        createContract.setContractName("关于XX合同");
        createContract.setSignLocation("Signature1;Signature2");
        fieldMap.put("Text1", "2018");
        fieldMap.put("Text2", "3");
        fieldMap.put("Text3", "16");
        fieldMap.put("Text4", "孙一");
        fieldMap.put("Text5", "222321199112050001");
        fieldMap.put("Text6", "1");
        fieldMap.put("Text7", "1");
        fieldMap.put("Text8", "1");
        fieldMap.put("Text9", "10000");
        fieldMap.put("Text10", "1");
        fieldMap.put("Text11", "10000");
        fieldMap.put("Text12", "1");
        fieldMap.put("Text13", "10000");
        fieldMap.put("Text14", "1");
        fieldMap.put("Text15", "30000");
        fieldMap.put("Text16", "10000");
        fieldMap.put("Text17", "10000");
        fieldMap.put("Text18", "10000");
        fieldMap.put("Text19", "1");
        fieldMap.put("Text20", "1");

        // setInvestmentInfo方法废弃，使用setTextValueInfo代替
        //createContract.setInvestmentInfo(fieldMap);
        createContract.setTextValueInfo(fieldMap);
        createContract.setIsSaveTextValue(1);

        SignInfoVO[] signInfos = new SignInfoVO[2];
        SignInfoVO signInfoVO0 = new SignInfoVO();
            signInfoVO0.setUserId("6219D66F64587642E05312016B0A37AD");
        signInfoVO0.setIsProxySign(0);
        signInfoVO0.setLocation("211.94.108.226");
        signInfoVO0.setProjectCode("003");
        // signInfoVO0.setIsCheckProjectCode(1);
       // signInfoVO0.setSignLocation("Signature1;Signature2;Signature3;Signature4;Signature5;Signature6;Signature7;Signature8;Signature9;Signature10;Signature11;Signature12;Signature13;Signature14;Signature15;Signature16;Signature17;Signature18;Signature19;Signature20;Signature21;Signature22;Signature23;Signature24;Signature25;Signature26;Signature27;Signature28;Signature29;Signature30;Signature31;Signature32;Signature33;Signature34;Signature35;Signature36;Signature37;Signature38;Signature39;Signature40;Signature41;Signature42;Signature43;Signature44;Signature45;Signature46;Signature47;Signature48;Signature49;Signature50;Signature51;Signature52;Signature53;Signature54;Signature55;Signature56;Signature57;Signature58;Signature59;Signature60;Signature61;Signature62;Signature63;Signature64;Signature65;Signature66;Signature67;Signature68;Signature69;Signature70;Signature71;Signature72;Signature73;Signature74;Signature75;Signature76;Signature77;Signature78;Signature79;Signature80;Signature81;Signature82;Signature83;Signature84;Signature85;Signature86;Signature87;Signature88;Signature89;Signature90;Signature91;Signature92;Signature93;Signature94;Signature95;Signature96;Signature97;Signature98;Signature99;Signature100");
        signInfoVO0.setSignLocation("Signature1;Signature2;Signature3;");
        signInfoVO0.setAuthorizationTime("20160801095509");
        signInfoVO0.setSealId("76F8B5FC27530BE3E05312016B0A9FEE");
        signInfos[0] = signInfoVO0;

         SignInfoVO signInfoVO1 = new SignInfoVO();
         signInfoVO1.setUserId("76EB6B5198E52119E05311016B0A87C3");
         signInfoVO1.setIsProxySign(0);
         signInfos[1] = signInfoVO1;

        createContract.setSignInfos(signInfos);

        // 使用水印时用到
        WatermarkVO watermark = new WatermarkVO();
        watermark.setWatermarkOnPage("1");
        watermark.setWatermarkLBX("100");
        watermark.setWatermarkLBY("100");
        watermark.setWatermarkWidth("100");
        watermark.setWatermarkHeight("100");
        FileInputStream fis = new FileInputStream("E:/JOB/zycfzs/png/C_94.png");
        byte[] imageBytes = new byte[fis.available()];
        fis.read(imageBytes);
        fis.close();
        watermark.setWatermarkData(Base64.toBase64String(imageBytes));
        // createContract.setWatermark(watermark);

        // 使用附件时用到
        AttachmentVO attachment = new AttachmentVO();
        attachment.setAttachmentName("123.txt");
        attachment.setAttachmentContent("11111王五");
        // createContract.setAttachment(attachment);

        tx3201ReqVO.setHead(head);
        tx3201ReqVO.setCreateContract(createContract);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3201ReqVO);
        System.out.println("req:" + req);

        String txCode = "3201";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
