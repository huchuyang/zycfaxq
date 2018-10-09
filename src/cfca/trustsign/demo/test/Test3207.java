package cfca.trustsign.demo.test;

import java.io.FileInputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignContractByKeywordVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.cs.SignKeywordVO;
import cfca.trustsign.common.vo.request.tx3.Tx3207ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3207 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3207ReqVO tx3207ReqVO = new Tx3207ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        SignContractByKeywordVO signContractByKeyword = new SignContractByKeywordVO();
        signContractByKeyword.setContractNo("MM20171025000000508");

        SignKeywordVO signKeyword = new SignKeywordVO();
        signKeyword.setKeyword("以下简称");
        signKeyword.setOffsetCoordX("0");
        signKeyword.setOffsetCoordY("0");
        signKeyword.setImageWidth("100");
        signKeyword.setImageHeight("100");
        signContractByKeyword.setSignKeyword(signKeyword);

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
        // signInfo.setImageData(Base64.toBase64String(imageBytes));
         signInfo.setSealId("443890B0FE3344B8A893BD832B6969E3");
        signContractByKeyword.setSignInfo(signInfo);

        tx3207ReqVO.setHead(head);
        tx3207ReqVO.setSignContractByKeyword(signContractByKeyword);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3207ReqVO);
        System.out.println("req:" + req);

        String txCode = "3207";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
