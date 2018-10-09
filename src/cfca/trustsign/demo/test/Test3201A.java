package cfca.trustsign.demo.test;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignInfoVO;
import cfca.trustsign.common.vo.request.tx3.Tx3201ReqVO;
import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.converter.JsonObjectMapper;
import cfca.trustsign.demo.util.SecurityUtil;
import cfca.trustsign.demo.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hurong
 * @version 1.0
 * @createTime 2018-09-30-10:39
 */
public class Test3201A {
    public static void main(String[] args) throws PKIException {

        HttpConnector httpConnector=new HttpConnector();
        //初始化
        httpConnector.init();

        Tx3201ReqVO tx3201ReqVO = new Tx3201ReqVO();
        //创建头信息对象
        HeadVO head = new HeadVO();

        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

        Map<String, String> fieldMap =new HashMap<String,String>();

        CreateContractVO createContract = new CreateContractVO();
        //客户平台自身是否要签署该协议。0：抄送；1：签署；2：暂不签署；默认为0
        createContract.setIsSign(1);
        //添加印章返回的印章ID，不传时为默认印章
        createContract.setSealId("76F8EF752F276352E05311016B0A86CB");
        //客户平台客户将合同模板提前制作好，提交给安心签管理员后，管理员会反馈合同模板编号
        createContract.setTemplateId("QT_1938");
        //客户自定义合同名称
        createContract.setContractName("关于2018测试合同01");
        fieldMap.put("1", "XX客服:");
        fieldMap.put("2", "2018");
        fieldMap.put("3", "09");
        fieldMap.put("4", "30");
        //Map<String, String>，key为合同模板文本域的标签值，value为往文本域填入的值，替换原来InvestmentInfo属性
        createContract.setTextValueInfo(fieldMap);
        //0：不保存；1：保存；默认为0 如果保存文本域填入信息，可以调用3212接口进行查询
        createContract.setIsSaveTextValue(1);

        SignInfoVO[] signInfos = new SignInfoVO[1];

        SignInfoVO signInfoVO0 = new SignInfoVO();
        //开户时返回的用户ID
        signInfoVO0.setUserId("6219D66F64587642E05312016B0A37AD");
        //0：不代签；1：代签；默认为0。
        // 不代签的用户，可自行登录安心签使用自己的数字证书完成协议签署。
        signInfoVO0.setIsProxySign(0);
        //客户平台将采集到的用户IP或所在地发送给安心签
        signInfoVO0.setLocation("211.94.108.226");
        //如果确定代签，必须保证代签的用户已经对该项目进行了授权
        signInfoVO0.setProjectCode("003");
        //签署人可以签多个，以“;”分割
        signInfoVO0.setSignLocation("Signature_plat");
        //客户平台将采集到的用户输入验证码的时间发送给安心签，格式：yyyyMMddHHmmss
        signInfoVO0.setAuthorizationTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));
        //添加印章返回的印章ID，不传时为默认印章
        signInfoVO0.setSealId("76F8B5FC27530BE3E05312016B0A9FEE");
        signInfos[0] = signInfoVO0;
        createContract.setSignInfos(signInfos);
        tx3201ReqVO.setHead(head);
        tx3201ReqVO.setCreateContract(createContract);
        //转化json数据
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3201ReqVO);
        System.out.println("req:" + req);

        String txCode = "3201";

        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
    }
}
