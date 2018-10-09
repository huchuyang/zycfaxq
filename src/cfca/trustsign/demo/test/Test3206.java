package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignContractVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3206ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3206 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3206ReqVO tx3206ReqVO = new Tx3206ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20180102235959");

        SignContractVO signContract = new SignContractVO();
        signContract.setContractNo("MM20180929000000396");
        SignInfoVO signInfo = new SignInfoVO();
        signInfo.setUserId("6219D66F64587642E05312016B0A37AD");
        signInfo.setLocation("211.94.108.226");
        signInfo.setProjectCode("003");
        signInfo.setSignLocation("Signature2");
        signInfo.setAuthorizationTime("20180801095509");

        // 传图片或传sealId的方式任选其一，传图片优先级高
        // FileInputStream fis = new FileInputStream("./image/赵六.png");
        // byte[] imageBytes = new byte[fis.available()];
        // fis.read(imageBytes);
        //fis.close();
        //signInfo.setImageData(Base64.toBase64String(imageBytes));
        // signInfo.setSealId("8C5E69F26A7E45F4977687301E120F83");
        signContract.setSignInfo(signInfo);

        tx3206ReqVO.setHead(head);
        tx3206ReqVO.setSignContract(signContract);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3206ReqVO);
        System.out.println("req:" + req);

        String txCode = "3206";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
