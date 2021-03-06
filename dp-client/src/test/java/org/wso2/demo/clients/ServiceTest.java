package org.wso2.demo.clients;

import java.io.File;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;


public class ServiceTest {

    private static final Log log = LogFactory.getLog(ServiceTest.class);

    public static void main(String[] args) throws Exception {
        setKeyStores();

        String endpoint = "https://xxxx:5550/service/mgmt/current";
        DataPowerClient dpClient = new DataPowerClient("wso2", "xxxxx", endpoint, "WSO2");
        String content = "dummy content";
        //String encodedString = Base64Utils.encode(content.getBytes());
        String response = dpClient.getFile("local://nuwan4.txt" );
        System.out.println("Response : " + response);

        String response1 = dpClient.getNewAccessToken("https://xxxxxx:5050/token", 5050, "account-application", "xxxx", "/getAccount");
        JSONObject obj = new JSONObject(response1);
        String newAccessToken = obj.get("access_token").toString();
        System.out.println("Response1 : " + response1);
        System.out.println("newAccessToken : " + newAccessToken);

        String response2 = dpClient.httpGet("https://10.150.67.66:5051/getAccount", 5051, newAccessToken);
        System.out.println("Response2 : " + response2);

        String res3 = dpClient.httpPOST("https://10.150.67.66:5051/getAccount", 5051, newAccessToken, "{\"location\":{\"lon\":\"80\", \"lat\":\"7\"}}");
        System.out.println("res3 : " + res3);

    }


    private static void addProxy() {
        ProxyServiceAdminClient proxyServiceAdminClient = null;
        try {
            proxyServiceAdminClient = new ProxyServiceAdminClient("https://localhost:9443/services/", "admin", "admin");

            if (proxyServiceAdminClient != null) {
                String proxy = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                        "       name=\"WSO2Test2\"\n" +
                        "       transports=\"https,http\"\n" +
                        "       statistics=\"disable\"\n" +
                        "       trace=\"disable\"\n" +
                        "       startOnLoad=\"true\">\n" +
                        "   <target>\n" +
                        "      <outSequence>\n" +
                        "         <send/>\n" +
                        "      </outSequence>\n" +
                        "      <endpoint>\n" +
                        "         <wsdl service=\"GlobalWeather\"\n" +
                        "               port=\"GlobalWeatherSoap\"\n" +
                        "               uri=\"http://www.webservicex.com/globalweather.asmx?WSDL\"/>\n" +
                        "      </endpoint>\n" +
                        "   </target>\n" +
                        "   <publishWSDL uri=\"http://www.webservicex.com/globalweather.asmx?WSDL\"/>\n" +
                        "   <description/>\n" +
                        "</proxy>";
                OMElement pauloadElement = AXIOMUtil.stringToOM(proxy);
                proxyServiceAdminClient.addProxyService(proxy);
            }
        } catch (Exception e) {
            System.out.println(" add proxy error " + e.getMessage());
        }

    }


    private static void setKeyStores() {
        // set trust store, you need to import server's certificate
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        try {
            System.setProperty("javax.net.ssl.trustStore",
                    getKeyStorePath("client-truststore.jks"));
        } catch (Exception e) {
            log.error("Exception occurred when setting key-store " + e.getMessage());
        }

        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    private static String getKeyStorePath(String keyStoreName) throws Exception {
        File file = new File((new File(".")).getCanonicalPath()
                + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + keyStoreName);
        if (!file.exists()) {
            throw new Exception("Key-Store file can not be found in "
                    + file.getCanonicalPath());
        }
        return file.getCanonicalPath();
    }
}
