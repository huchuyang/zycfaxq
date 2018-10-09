package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.EnterpriseVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3299ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3299 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3299ReqVO tx3299ReqVO = new Tx3299ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");
        tx3299ReqVO.setHead(head);

        EnterpriseVO enterprise = new EnterpriseVO();
        enterprise.setEnterpriseName("XX网络科技有限公司");
        enterprise.setIdentTypeCode("8");
        enterprise.setIdentNo("110000017039721");
        tx3299ReqVO.setEnterprise(enterprise);
        tx3299ReqVO.setBeginIndex("2");
        tx3299ReqVO.setEndIndex("3");

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3299ReqVO);
        System.out.println("req:" + req);

        String txCode = "3299";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
