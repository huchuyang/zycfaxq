package cfca.trustsign.demo.test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import cfca.com.itextpdf.text.DocumentException;
import cfca.com.itextpdf.text.pdf.PdfReader;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.util.FileUtil;
import cfca.sadk.system.FileHelper;
import cfca.sadk.util.Base64;
import cfca.seal.sadk.DonePdfSeal;
import cfca.seal.sadk.PrePdfReader;
import cfca.seal.sadk.PrePdfSeal;
import cfca.seal.sadk.PrePdfSeal.EncryptionAlgorithm;
import cfca.seal.sadk.PrePdfSealExtra;
import cfca.seal.sadk.security.deferred.ReservedPdfPKCS7;
import cfca.seal.sadk.security.deferred.ReservedPdfPKCS7.Type;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.LocalSignVO;
import cfca.trustsign.common.vo.request.tx3.Tx3401ReqVO;
import cfca.trustsign.common.vo.response.tx3.Tx3401ResVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.CommonUtil;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

public class Test3401 {
    public static void main(String[] args) throws PKIException, IOException, DocumentException, GeneralSecurityException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3401ReqVO tx3401ReqVO = new Tx3401ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        LocalSignVO localSign = new LocalSignVO();

        localSign.setStubNo("00009");
        localSign.setUserId("96428FD7E441461DA91C797EC0B02CED");
        localSign.setProjectCode("003");

        byte[] pdfBytes = FileUtil.getBytesFromFile("./file/3.pdf");
        byte[] imageBytes = FileUtil.getBytesFromFile("./image/赵六.png");

        PrePdfSeal prePdfSeal = new PrePdfSeal(null, null, imageBytes, 0.68f, EncryptionAlgorithm.RSA, PrePdfSeal.HashAlgorithm.SHA256, Type.EXTERNAL_RSA_PKCS7);

        PrePdfSealExtra prePdfSealExtra = new PrePdfSealExtra("签名", TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));
        PrePdfReader prePdfReader = new PrePdfReader(pdfBytes);

        DonePdfSeal donePdfSeal = new DonePdfSeal();
        donePdfSeal.initPdfSeal(prePdfSeal);
        donePdfSeal.initPdfSealExtra(prePdfSealExtra);
        donePdfSeal.initPdfReader(prePdfReader);
        donePdfSeal.updateLocationInfoByBlankSignature("Signature1", true);// 签名域方式
        // donePdfSeal.updateLocationInfoByKeyword("(以下无正文，为签字盖章页。)");//关键字签署
        // donePdfSeal.offsetCoordLocationInfo(50, 50);//关键字定位（偏移量）

        ReservedPdfPKCS7 reservedPdfPKCS7 = new ReservedPdfPKCS7();

        // pdfBytes = donePdfSeal.createReservedPdfSeal("http://210.74.41.195/timestamp", "", "", 0, reservedPdfPKCS7);
        pdfBytes = donePdfSeal.createReservedPdfSeal(0, reservedPdfPKCS7);

        String signatureAttr = Base64.toBase64String(reservedPdfPKCS7.hashPdf);
        localSign.setSignatureAttr(signatureAttr);

        tx3401ReqVO.setHead(head);
        tx3401ReqVO.setLocalSign(localSign);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3401ReqVO);
        System.out.println("req:" + req);

        if (CommonUtil.isEmpty(req)) {
            System.out.println("req is null");
            return;
        }

        String txCode = "3401";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);

        FileHelper.write("D:/file/222.pdf", pdfBytes);

        Tx3401ResVO tx3401ResVO = jsonObjectMapper.readValue(res, Tx3401ResVO.class);
        if (tx3401ResVO.getLocalSign() != null) {
            String signatureOfAttr = tx3401ResVO.getLocalSign().getSignatureOfAttr();
            pdfBytes = new DonePdfSeal().mergeReservedPdfSeal(new PdfReader(pdfBytes), reservedPdfPKCS7.fieldName, Base64.decode(signatureOfAttr));
            FileHelper.write("./file/3_finish.pdf", pdfBytes);
        }
    }
}
