package cfca.trustsign.demo.test;

import java.io.File;
import java.io.FileOutputStream;

import cfca.trustsign.demo.connector.HttpConnector;
import cfca.trustsign.demo.constant.Request;

public class TestDownloadFile {
    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        String contractNo = "MM20171027000000102";
        byte[] fileBtye = httpConnector.getFile("platId/" + Request.PLAT_ID + "/contractNo/" + contractNo + "/downloading");
        if (fileBtye == null || fileBtye.length == 0) {
            return;
        }

        try {
            String filePath = "./file";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath + File.separator + contractNo + ".pdf");
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
