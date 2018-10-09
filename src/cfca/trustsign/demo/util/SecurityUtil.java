package cfca.trustsign.demo.util;

import java.security.PrivateKey;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.util.Base64;
import cfca.sadk.util.CertUtil;
import cfca.sadk.util.EncryptUtil;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.util.P10Request;
import cfca.sadk.util.Signature;
import cfca.sadk.x509.certificate.X509Cert;
import cfca.trustsign.demo.constant.SystemConst;

public class SecurityUtil {
    private static Session session = null;

    static {
        String deviceName = JCrypto.JSOFT_LIB;
        try {
            JCrypto.getInstance().initialize(deviceName, null);
            session = JCrypto.getInstance().openSession(deviceName);
        } catch (PKIException e) {
            e.printStackTrace();
        }
    }

    public static String getSignCert(String pfxPath, String pfxPWD) throws PKIException {
        try {
            X509Cert x509Cert = CertUtil.getCertFromPFX(pfxPath, pfxPWD);

            return Base64.toBase64String(x509Cert.getEncoded());
        } catch (Exception e) {
            throw new PKIException("get signature cert fail", e);
        }
    }

    public static String p1SignMessage(String pfxPath, String pfxPWD, byte[] sourceData) throws PKIException {
        try {
            PrivateKey privateKey = KeyUtil.getPrivateKeyFromPFX(pfxPath, pfxPWD);

            Signature signature = new Signature();
            byte[] signatureByte = signature.p1SignMessage(Mechanism.SHA256_RSA, sourceData, privateKey, session);
            return new String(signatureByte, SystemConst.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new PKIException("P1 signature message fail", e);
        }
    }

    public static String p7SignByHash(String pfxPath, String pfxPWD, byte[] sourceData) throws PKIException {
        try {
            PrivateKey privateKey = KeyUtil.getPrivateKeyFromPFX(pfxPath, pfxPWD);
            X509Cert x509Cert = CertUtil.getCertFromPFX(pfxPath, pfxPWD);

            Signature signature = new Signature();
            byte[] signatureByte = signature.p7SignByHash(Mechanism.SHA256_RSA, sourceData, privateKey, x509Cert, session);
            return new String(signatureByte, SystemConst.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new PKIException("P7 hash signature fail", e);
        }
    }

    public static String p7SignMessageDetach(String pfxPath, String pfxPWD, byte[] sourceData) throws PKIException {
        try {
            PrivateKey privateKey = KeyUtil.getPrivateKeyFromPFX(pfxPath, pfxPWD);
            X509Cert signCert = CertUtil.getCertFromPFX(pfxPath, pfxPWD);

            Signature signature = new Signature();
            byte[] signatureByte = signature.p7SignMessageDetach(Mechanism.SHA256_RSA, sourceData, privateKey, signCert, session);
            return new String(signatureByte);
        } catch (Exception e) {
            throw new PKIException("P7 detach signature fail", e);
        }
    }

    public static String p7SignMessageDetach(String jksPath, String jksPWD, String alias, String sourceData) throws PKIException {
        try {
            PrivateKey privateKey = KeyUtil.getPrivateKeyFromJKS(jksPath, jksPWD, alias);
            X509Cert signCert = CertUtil.getCertFromJKS(jksPath, jksPWD, alias);

            Signature signature = new Signature();
            byte[] signatureByte = signature.p7SignMessageDetach(Mechanism.SHA256_RSA, CommonUtil.getBytes(sourceData), privateKey, signCert, session);
            return new String(signatureByte);
        } catch (Exception e) {
            throw new PKIException("P7 detach signature fail", e);
        }
    }

    public static Signature verifyP7Detach(byte[] original, byte[] signature) throws PKIException {
        Signature sign = new Signature();
        if (!sign.p7VerifyMessageDetach(original, signature, session)) {
            throw new PKIException("verify P7 detach signature fail");
        }
        return sign;
    }

    public static Signature verifyP7Detach(String original, String signature) throws PKIException {
        return verifyP7Detach(CommonUtil.getBytes(original), CommonUtil.getBytes(signature));
    }

    public static X509Cert getX509CertFromSignature(String signature) throws PKIException {
        return new Signature().getSignerX509CertFromP7SignData(CommonUtil.getBytes(signature));
    }

    public static String encryptBySM4(String plaintext, String password) throws PKIException {
        return CommonUtil.getString(EncryptUtil.encryptMessageBySM4(CommonUtil.getBytes(plaintext), password));
    }

    public static String decryptBySM4(String ciphertext, String password) throws PKIException {
        return CommonUtil.getString(EncryptUtil.decryptMessageBySM4(CommonUtil.getBytes(ciphertext), password));
    }

    public static P10Request getP10Request() throws PKIException {
        return new P10Request(session);
    }

    public static String getP10(P10Request p10Request, int length) throws PKIException {
        return CommonUtil.getString(p10Request.generatePKCS10Request(new Mechanism(Mechanism.SHA1_RSA), length, session));
    }

    public static void main(String[] args) throws PKIException {
        verifyP7Detach(
                "{\"head\":{\"txTime\":\"20170825150039\"},\"costTime\":0,\"person\":{\"personName\":\"zzds\",\"identTypeCode\":\"Z\",\"identNo\":\"345345345345324534535342523\",\"email\":\"3fd8135ced064c1499796bc13d150aa1@com.cn\",\"address\":\"beijing\",\"authenticationMode\":\"gonganbu\"},\"isVerifyBankCard\":0,\"bankCardInfo\":{\"panNo\":\"6226220130776106\",\"cardType\":\"10\",\"mobilePhone\":\"18611645767\",\"validDate\":\"2612\",\"cvn2\":\"091\"}",
                "MIIFvQYJKoZIhvcNAQcCoIIFrjCCBaoCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCA/EwggPtMIIC1aADAgECAhAQAAAAAAAAAAAAACAAVgJSMA0GCSqGSIb3DQEBBQUAMFkxCzAJBgNVBAYTAkNOMTAwLgYDVQQKEydDaGluYSBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxGDAWBgNVBAMTD0NGQ0EgVEVTVCBPQ0ExMTAeFw0xMzA4MjMwMjAyMTJaFw0xODA5MDExNTU5NTlaMHwxCzAJBgNVBAYTAkNOMRUwEwYDVQQKEwxDRkNBIFRFU1QgQ0ExETAPBgNVBAsTCExvY2FsIFJBMRIwEAYDVQQLEwlFbXBsb3llZXMxLzAtBgNVBAMUJjA0MUBaMjAxMDA4MTYwMDFAZGV2QWRtaW5UZXN0QDAwMDAwMDAyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5WX08wmULyp4UkmdIxLJ7QvaDvKoHb6+JhLuR7f2YqD89G5QGZd0dEtourjiU9NTOi7xL+UsYiaTltBGXDP+CHyw1NnlQrxTEQAB4AJgaehFBGCq1FmPqxzIcvAFQOcYHhjM8tjwfgnzUcNYcX3Oos6Ya/hRNTN2eW3G5Xn8B+NIs1Zv34WseOlaWCqk4+8T4HOzUyNqtK2UdZ6bZ3ojz+yFipouwmpXW5gVzmzGHodsOcjBh7ccaZOsJjY276OLDVB4u/3mXwFXAFMLE6TR8sBSmJgpsNtl+3VT+TvqIWRYsjcLQVYJlgI6Z9r7I31mU0+0fnJJB9tP+OXzapVsQIDAQABo4GNMIGKMB8GA1UdIwQYMBaAFPwLvESaDjGhg6mBhyceBULGv1b4MAsGA1UdDwQEAwIFoDAdBgNVHQ4EFgQU2KLMtrWIKCNY72Q59T2i5f0/sqswOwYDVR0lBDQwMgYIKwYBBQUHAwIGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwEGCCsGAQUFBwMIMA0GCSqGSIb3DQEBBQUAA4IBAQAgL6V/aaATREaIGcVU1tZU381WVkmfwJ7Q35roYuyCMNmClZt0l9E1jnKKv85GhPd9lkuHwiQjTZ5dA/hgZsQeRRLbXXhGOOkRG+7XQAM4AuuOieR6hd4SqK5NoasBqTZnaQkptrtscNWW1YApPzBNnAByzbkVztdoDYshMnyArgW8m0bhzoaDb/sx63dMX0i6EHKh8inAEnvvJNahKpWWr/F6QwOyAm1VBl2smnDKfisuuChffGooDV2YEQanidzrMUTv2Pnsze1YL16VBRwyP1grzeHBpaWeLPoLWtZHpXL6vnVU4oWii4/tzx4riZGvm+oFbW87BP0QRMsrRvW8MYIBlDCCAZACAQEwbTBZMQswCQYDVQQGEwJDTjEwMC4GA1UEChMnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRgwFgYDVQQDEw9DRkNBIFRFU1QgT0NBMTECEBAAAAAAAAAAAAAAIABWAlIwCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAASCAQAHzGx80fBMgIN+gddLUuGR9vvMzebOcz8/d0tWaJXhvrDm9DYiA7xmnPB00QGJBmrO/B0Dk+WuTSZM+SE9wtFRY2vg97Y70phSjke1Frusi6rhYhtRzBQZ3Rzq8nSrRqkZGa4r/4vY1UdLuiGQwVIg1eYA7y+aUvybwltPgmdWMiPwtpOcy+aQNpRJE4Yy1hA2GmTOlAZBsrc9I/ISwv7unvNloPQP6MmkBo1IUCmJtnJUy9EaB2swhidTRGs5KObDY09sWlOJLKMHbo/NTn+CHYLb7yZi3t1C2hEigrhIhRePNZ/KkREcXRZFrpZjZp6CCIcjMSfpJJ0q2kImCMOj");
    }
}
