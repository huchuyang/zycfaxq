package cfca.trustsign.demo.test;

import java.io.File;
import java.io.FileOutputStream;

import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.util.TimeUtil;

public class TestDownloadFiles {
    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        String contractNos = "MM20170914000000002@MM20170914000000001@MM20171011000000149";
        byte[] fileBtye = httpConnector.getFile("platId/" + Request.PLAT_ID + "/contractNos/" + contractNos + "/batchDownloading");
        if (fileBtye == null || fileBtye.length == 0) {
            return;
        }

        try {
            String filePath = "./file";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath + File.separator + TimeUtil.getCurrentTime(TimeUtil.FORMAT_14) + ".zip");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBtye);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
