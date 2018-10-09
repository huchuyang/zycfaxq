package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignContractVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3206ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

/**
 * @author hurong
 * @version 1.0
 * @createTime 2018-09-30-11:29   test1
 */
public class Test3206A {

    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3206ReqVO tx3206ReqVO = new Tx3206ReqVO();
        HeadVO head = new HeadVO();
        //设置时间
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        SignContractVO signContract = new SignContractVO();
        //创建合同返回的合同编号
        signContract.setContractNo("QT20180930000000347");

        SignInfoVO signInfo = new SignInfoVO();

        signInfo.setUserId("6219D66F64587642E05312016B0A37AD");
        signInfo.setLocation("211.94.108.226");
        signInfo.setProjectCode("003");
        signInfo.setSignLocation("Signature1;Signature2;Signature3;");
        signInfo.setAuthorizationTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

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
