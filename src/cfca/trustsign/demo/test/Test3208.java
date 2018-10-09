package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignContractByCoordinateVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.cs.SignLocationVO;
import cfca.trustsign.common.vo.request.tx3.Tx3208ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3208 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3208ReqVO tx3208ReqVO = new Tx3208ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        SignContractByCoordinateVO signContractByCoordinate = new SignContractByCoordinateVO();
        signContractByCoordinate.setContractNo("MM20171025000000509");

        SignLocationVO[] signLocations0 = new SignLocationVO[1];
        SignLocationVO signLocation0 = new SignLocationVO();
        signLocation0.setSignOnPage("2");
        signLocation0.setSignLocationLBX("85");
        signLocation0.setSignLocationLBY("550");
        signLocation0.setSignLocationRUX("140");
        signLocation0.setSignLocationRUY("575");
        signLocations0[0] = signLocation0;
        signContractByCoordinate.setSignLocations(signLocations0);

        SignInfoVO signInfo = new SignInfoVO();
        signInfo.setUserId("96428FD7E441461DA91C797EC0B02CED");
        signInfo.setLocation("211.94.108.226");
        signInfo.setAuthorizationTime("20160801095509");
        signInfo.setProjectCode("003");

        // 传图片或传sealId的方式任选其一，传图片优先级高
        FileInputStream fis = new FileInputStream("./image/赵六.png");
        byte[] imageBytes = new byte[fis.available()];
        fis.read(imageBytes);
        fis.close();
        signInfo.setImageData(Base64.toBase64String(imageBytes));
        // signInfo.setSealId("8C5E69F26A7E45F4977687301E120F83");
        signContractByCoordinate.setSignInfo(signInfo);

        tx3208ReqVO.setHead(head);
        tx3208ReqVO.setSignContractByCoordinate(signContractByCoordinate);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3208ReqVO);
        System.out.println("req:" + req);

        String txCode = "3208";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
