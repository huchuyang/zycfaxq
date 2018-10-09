package cfca.trustsign.demo.test;

import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SealDeleteVO;
import cfca.trustsign.common.vo.cs.SealVO;
import cfca.trustsign.common.vo.request.tx3.Tx3013ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3013 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3013ReqVO tx3013ReqVO = new Tx3013ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        SealDeleteVO sealDelete = new SealDeleteVO();
        sealDelete.setUserId("F95ED2BF390B4DCF8AE175A97112EDDD");

        SealVO sealVO = new SealVO();
        sealVO.setSealId("D320C37C27144FB6949E649F10596939");
        sealDelete.setSeal(sealVO);

        tx3013ReqVO.setHead(head);
        tx3013ReqVO.setSealDelete(sealDelete);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3013ReqVO);
        System.out.println("req:" + req);

        String txCode = "3013";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
