package cfca.trustsign.demo.test;

import java.io.File;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignKeywordVO;
import cfca.trustsign.common.vo.cs.SignLocationVO;
import cfca.trustsign.common.vo.cs.UploadContractVO;
import cfca.trustsign.common.vo.cs.UploadSignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3203ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;

public class Test3203 {
    public static void main(String[] args) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3203ReqVO tx3203ReqVO = new Tx3203ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime("20160102235959");

        UploadContractVO uploadContract = new UploadContractVO();

        UploadSignInfoVO[] signInfos = new UploadSignInfoVO[1];
        UploadSignInfoVO signInfoVO0 = new UploadSignInfoVO();
        signInfoVO0.setUserId("96428FD7E441461DA91C797EC0B02CED");
        signInfoVO0.setIsProxySign(1);
        signInfoVO0.setLocation("210.74.41.0");

        SignLocationVO[] signLocations0 = new SignLocationVO[1];
        SignLocationVO signLocation0 = new SignLocationVO();
        signLocation0.setSignOnPage("6");
        signLocation0.setSignLocationLBX("300");
        signLocation0.setSignLocationLBY("40");
        signLocation0.setSignLocationRUX("400");
        signLocation0.setSignLocationRUY("120");
        signLocations0[0] = signLocation0;
        signInfoVO0.setSignLocations(signLocations0);

        SignKeywordVO signKeywordUser = new SignKeywordVO();
        signKeywordUser.setKeyword("合同");
        signKeywordUser.setOffsetCoordX("0");
        signKeywordUser.setOffsetCoordY("0");
        signKeywordUser.setImageWidth("150");
        signKeywordUser.setImageHeight("150");

        // signInfoVO0.setSignKeyword(signKeywordUser);
        signInfoVO0.setProjectCode("003");
        signInfoVO0.setAuthorizationTime("20160214171200");
        // signInfoVO0.setSealId("F6B46EA79DCA434896BCAAA48EB92509");
        signInfos[0] = signInfoVO0;

        // UploadSignInfoVO signInfoVO1 = new UploadSignInfoVO();
        // signInfoVO1.setUserId("12979D90E21845EE8BFCB96DA09665D8");
        // signInfoVO1.setIsProxySign(0);
        // signInfoVO1.setLocation("成都");
        // SignLocationVO[] signLocations1 = new SignLocationVO[1];
        // SignLocationVO signLocation1 = new SignLocationVO();
        // signLocation1.setSignOnPage("2");
        // signLocation1.setSignLocationLBX("151");
        // signLocation1.setSignLocationLBY("476");
        // signLocation1.setSignLocationRUX("240");
        // signLocation1.setSignLocationRUY("500");
        // signLocations1[0] = signLocation1;
        // signInfoVO1.setSignLocations(signLocations1);
        // signInfoVO1.setProjectCode("001");
        // signInfoVO1.setAuthorizationTime("20160214171200");
        // signInfos[1] = signInfoVO1;
        //
        // UploadSignInfoVO signInfoVO2 = new UploadSignInfoVO();
        // signInfoVO2.setUserId("35FE921E7CF0F890E050007F01004E8E");
        // signInfoVO2.setIsProxySign(1);
        // signInfoVO2.setLocation("210.74.41.0");
        // SignLocationVO[] signLocations2 = new SignLocationVO[1];
        // SignLocationVO signLocation2 = new SignLocationVO();
        // signLocation2.setSignOnPage("2");
        // signLocation2.setSignLocationLBX("230");
        // signLocation2.setSignLocationLBY("105");
        // signLocation2.setSignLocationRUX("330");
        // signLocation2.setSignLocationRUY("130");
        // signLocations2[0] = signLocation2;
        // signInfoVO2.setSignLocations(signLocations2);
        // signInfoVO2.setProjectCode("003");
        // signInfoVO2.setAuthorizationTime("20160214171200");
        // signInfos[2] = signInfoVO2;

        // uploadContract.setSignInfos(signInfos);
        uploadContract.setContractTypeCode("MM");
        uploadContract.setContractName("测试合同");

        uploadContract.setIsSign(1);
        // uploadContract.setSealId("DCDCBBEEBCDC44D59421C662152CCB55");
        SignKeywordVO signKeyword = new SignKeywordVO();
        signKeyword.setKeyword("合同");
        signKeyword.setOffsetCoordX("0");
        signKeyword.setOffsetCoordY("0");
        signKeyword.setImageWidth("100");
        signKeyword.setImageHeight("100");
        uploadContract.setSignKeyword(signKeyword);

        SignLocationVO[] signLocationsPlat = new SignLocationVO[1];
        SignLocationVO signLocationPlat = new SignLocationVO();
        signLocationPlat.setSignOnPage("6");
        signLocationPlat.setSignLocationLBX("300");
        signLocationPlat.setSignLocationLBY("40");
        signLocationPlat.setSignLocationRUX("400");
        signLocationPlat.setSignLocationRUY("120");
        signLocationsPlat[0] = signLocationPlat;
        // uploadContract.setSignLocations(signLocationsPlat);

        tx3203ReqVO.setHead(head);
        tx3203ReqVO.setUploadContract(uploadContract);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3203ReqVO);
        System.out.println("req:" + req);

        File file = new File("./file/1.pdf");

        String txCode = "3203";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        // long start = System.currentTimeMillis();
        // for (int i = 0; i < 200; i++) {
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature, file);
        System.out.println("res:" + res);
        // }
        // System.out.println("create 100 pdf cost:" + (System.currentTimeMillis() - start));
    }
}
