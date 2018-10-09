package cfca.trustsign.demo.test;

import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SealQueryVO;
import cfca.trustsign.common.vo.request.tx3.Tx3014ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3014 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3014ReqVO tx3014ReqVO = new Tx3014ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        SealQueryVO sealQuery = new SealQueryVO();
        sealQuery.setUserId("F95ED2BF390B4DCF8AE175A97112EDDD");
//        sealQuery.setSealId("02FEF340669D4A3EBAF1040453722856");

        tx3014ReqVO.setHead(head);
        tx3014ReqVO.setSealQuery(sealQuery);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3014ReqVO);
        System.out.println("req:" + req);

        String txCode = "3014";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
