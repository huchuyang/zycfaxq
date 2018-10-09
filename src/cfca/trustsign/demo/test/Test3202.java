package cfca.trustsign.demo.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3202ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3202 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3202ReqVO tx3202ReqVO = new Tx3202ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        List<CreateContractVO> createContractlist = new ArrayList<CreateContractVO>();

        CreateContractVO createContract = new CreateContractVO();
        createContract.setTemplateId("1");
        createContract.setIsSign(1);
        createContract.setSignLocation("Signature2;Signature3");

        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("Text1", "2016");
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
        // createContract.setInvestmentInfo(fieldMap);
        createContract.setTextValueInfo(fieldMap);

        SignInfoVO[] signInfos = new SignInfoVO[1];
        SignInfoVO signInfoVO0 = new SignInfoVO();
        signInfoVO0.setUserId("7DD861A118C746529EC5F6EFC56C2196");
        signInfoVO0.setIsProxySign(1);
        signInfoVO0.setLocation("210.74.41.0");
        signInfoVO0.setProjectCode("003");
        signInfoVO0.setIsCheckProjectCode(0);
        signInfoVO0.setSignLocation("Signature1");
        signInfoVO0.setAuthorizationTime("20160214171200");
        signInfos[0] = signInfoVO0;
        createContract.setSignInfos(signInfos);

        CreateContractVO createContract2 = new CreateContractVO();
        createContract2.setTemplateId("1");
        createContract2.setTextValueInfo(fieldMap);
        createContract2.setIsSaveTextValue(1);
        createContract2.setSignInfos(signInfos);

        createContractlist.add(createContract);
        createContractlist.add(createContract2);

        tx3202ReqVO.setHead(head);
        tx3202ReqVO.setBatchNo("B250");
        tx3202ReqVO.setCreateContracts(createContractlist.toArray(new CreateContractVO[0]));

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3202ReqVO);
        System.out.println("req:" + req);

        String txCode = "3202";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
