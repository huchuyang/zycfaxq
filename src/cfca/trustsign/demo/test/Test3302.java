package cfca.trustsign.demo.test;

import java.util.Arrays;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.util.Base64;
import cfca.sadk.x509.certificate.X509Cert;
import cfca.trustsign.common.vo.cs.CertUnbindingVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3302ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.CommonUtil;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3302 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3302ReqVO tx3302ReqVO = new Tx3302ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        tx3302ReqVO.setHead(head);
        CertUnbindingVO certUnbinding = new CertUnbindingVO();
        String userId = "96428FD7E441461DA91C797EC0B02CED";
        certUnbinding.setUserId(userId);
        X509Cert x509Cert = new X509Cert(Base64.decode(SecurityUtil.getSignCert("./jks/admin.pfx", "cfca1234")));
        String serialNo = CommonUtil.formatSerialNo(x509Cert.getStringSerialNumber());
        certUnbinding.setSerialNo(serialNo);

        tx3302ReqVO.setCertUnbinding(certUnbinding);

        System.out.println(Arrays.toString(tx3302ReqVO.getCertUnbinding().getSerialNo().getBytes()));
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3302ReqVO);
        System.out.println("req:" + req);

        String txCode = "3302";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
