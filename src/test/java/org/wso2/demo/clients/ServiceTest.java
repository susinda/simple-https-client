package org.wso2.demo.clients;

import java.io.File;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ServiceTest {

    private static final Log log = LogFactory.getLog(ServiceTest.class);

    public static void main(String[] args) throws Exception {
        setKeyStores();
        String endpoint = "https://xx.xx.67.66:5550/service/mgmt/current";
        String payload = "<man:request domain=\"WSO2\" xmlns:man=\"http://www.datapower.com/schemas/management\">" +
                "<man:get-file name=\"config://WSO2.cfg\"/>" +
                "</man:request>";
        OMElement pauloadElement = AXIOMUtil.stringToOM(payload);
        System.out.println("pauloadElement : " + pauloadElement.toString());

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = null;
        try {
            response = axisServiceClient.sendReceiveWithBasicAuth(pauloadElement, endpoint, "xxxxxx", "xxxxxxxx");
        } catch (AxisFault e) {
            log.error("axisServiceClient.sendReceive failed " + e.getMessage());
            System.out.println("axisServiceClient.sendReceive failed " + e.getMessage());
        }
        log.info("Response : " + response);
        System.out.println("Response : " + response);
    }


    private static void setKeyStores() {
        // set trust store, you need to import server's certificate
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        try {
            System.setProperty("javax.net.ssl.trustStore",
                    getKeyStorePath("wso2carbon.jks"));
        } catch (Exception e) {
            log.error("Exception occured when setting keystore " + e.getMessage());
        }

        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    private static String getKeyStorePath(String keyStoreName) throws Exception {
        File file = new File((new File(".")).getCanonicalPath()
                + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + keyStoreName);
        if (!file.exists()) {
            throw new Exception("Key Store file can not be found in "
                    + file.getCanonicalPath());
        }
        return file.getCanonicalPath();
    }
}
