package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.GetContractSignatureAttrVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.cs.SignLocationVO;
import cfca.trustsign.common.vo.request.tx3.Tx3204ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3204 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3204ReqVO tx3204ReqVO = new Tx3204ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        GetContractSignatureAttrVO getContractSignatureAttr = new GetContractSignatureAttrVO();
        getContractSignatureAttr.setContractNo("MM20171025000000407");

        SignInfoVO signInfo = new SignInfoVO();
        signInfo.setUserId("96428FD7E441461DA91C797EC0B02CED");
        signInfo.setLocation("210.74.41.0");
        signInfo.setSignCert(SecurityUtil.getSignCert("./jks/test.pfx", "11111111"));

        // 传图片或传sealId的方式任选其一，传图片优先级高
        FileInputStream fis = new FileInputStream("./image/赵六.png");
        byte[] imageBytes = new byte[fis.available()];
        fis.read(imageBytes);
        fis.close();
        // signInfo.setImageData(Base64.toBase64String(imageBytes));
        // signInfo.setSealId("6ACEC0B88FC44FD2AB2166D1320ADCEF");
        getContractSignatureAttr.setSignInfo(signInfo);

        // 签名域和坐标的方式任选其一，坐标的方式优先
        SignLocationVO signLocation = new SignLocationVO();
        signLocation.setSignOnPage("1");
        signLocation.setSignLocationLBX("85");
        signLocation.setSignLocationLBY("550");
        signLocation.setSignLocationRUX("140");
        signLocation.setSignLocationRUY("575");
        getContractSignatureAttr.setSignLocation(signLocation);
        // getContractSignatureAttr.getSignInfo().setSignLocation("Signature1");

        // 签名算法为P7时使用
        getContractSignatureAttr.setIsSignP7(1);

        tx3204ReqVO.setHead(head);
        tx3204ReqVO.setGetContractSignatureAttr(getContractSignatureAttr);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3204ReqVO);
        System.out.println("req:" + req);

        String txCode = "3204";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
