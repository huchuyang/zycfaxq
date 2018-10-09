package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SealAddVO;
import cfca.trustsign.common.vo.cs.SealVO;
import cfca.trustsign.common.vo.request.tx3.Tx3011ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3011 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3011ReqVO tx3011ReqVO = new Tx3011ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20180929152740");

        SealAddVO sealAddVO = new SealAddVO();
        sealAddVO.setUserId("6B9BEDC0AEE27308E05311016B0A9110");

        SealVO sealVO = new SealVO();

        FileInputStream fis = new FileInputStream("E:/JOB/zycfzs/png/C_94.png");
        byte[] imageBytes = new byte[fis.available()];
        fis.read(imageBytes);
        fis.close();
        sealVO.setImageData(Base64.toBase64String(imageBytes));
        sealAddVO.setSeal(sealVO);

        tx3011ReqVO.setHead(head);
        tx3011ReqVO.setSealAdd(sealAddVO);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3011ReqVO);
        System.out.println("req:" + req);

        String txCode = "3011";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
