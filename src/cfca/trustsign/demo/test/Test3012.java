package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SealUpdateVO;
import cfca.trustsign.common.vo.cs.SealVO;
import cfca.trustsign.common.vo.request.tx3.Tx3012ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3012 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3012ReqVO tx3012ReqVO = new Tx3012ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        SealUpdateVO sealUpdate = new SealUpdateVO();
        sealUpdate.setUserId("9C9E731AEE444B498F7B5DCFBA0CD0E8");

        SealVO sealVO = new SealVO();
        sealVO.setSealId("11EE61AAEF744F32949E2AB881AEE1EA");

        FileInputStream fis = new FileInputStream("./image/赵六.png");
        byte[] imageBytes = new byte[fis.available()];
        fis.read(imageBytes);
        fis.close();
        sealVO.setImageData(Base64.toBase64String(imageBytes));
        sealUpdate.setSeal(sealVO);

        tx3012ReqVO.setHead(head);
        tx3012ReqVO.setSealUpdate(sealUpdate);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3012ReqVO);
        System.out.println("req:" + req);

        String txCode = "3012";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
