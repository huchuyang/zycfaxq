package cfca.trustsign.demo.test;

import java.io.FileOutputStream;
import java.io.IOException;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.P10Request;
import cfca.sadk.util.P12FileKit;
import cfca.sadk.x509.certificate.X509Cert;
import cfca.trustsign.common.vo.cs.CertApplyVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3303ReqVO;
import cfca.trustsign.common.vo.response.tx3.Tx3303ResVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3303 {
    public static void main(String[] args) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3303ReqVO tx3303ReqVO = new Tx3303ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        tx3303ReqVO.setHead(head);
        CertApplyVO certApply = new CertApplyVO();
        String userId = "F55CB8E25552482E94BFC18995417B70";
        certApply.setUserId(userId);
        P10Request p10Request = SecurityUtil.getP10Request();
        String p10 = SecurityUtil.getP10(p10Request, 2048);
        certApply.setP10(p10);

        tx3303ReqVO.setCertApply(certApply);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3303ReqVO);
        System.out.println("req:" + req);

        String txCode = "3303";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);

        Tx3303ResVO tx3303ResVO = jsonObjectMapper.readValue(res, Tx3303ResVO.class);
        X509Cert x509Cert = new X509Cert(tx3303ResVO.getCertApply().getSignCert().getBytes("UTF-8"));
        byte[] p12Data = P12FileKit.RSACombineP12Data(x509Cert, p10Request.getPrivateKey(), "111111");
        FileOutputStream fos = new FileOutputStream("./file/test.pfx");
        fos.write(p12Data);
        fos.close();
    }
}
