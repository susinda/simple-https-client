package org.wso2.demo.clients;

import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.axiom.om.OMElement;
//import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axis2.AxisFault;
import org.apache.axis2.util.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.stream.XMLStreamException;

public class DataPowerClient {


    //$curl -k https:// partnerauth-stage.united.com/token  -d "grant_type=client_credentials&scope=<scopes>&client_id=clientID&client_secret=clientSecret"
    private static final String OAUTH_GRANT_TYPE = "grant_type";
    private static final String GRANT_TYPE_VALUE = "client_credentials";
    private static final String OAUTH_CLIENT_ID = "client_id";
    private static final String OAUTH_CLIENT_SECRET = "client_secret";
    private static final String OAUTH_SCOPE = "scope";

    //we need to make these read from property file or from handler parameters
    private String username;
    private String password;
    private String endpoint;
    private String domain;

    private AxisServiceClient client;
    private static final Log log = LogFactory.getLog(DataPowerClient.class);

    public DataPowerClient(String username, String password, String endpoint, String domain) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.domain = domain;
        client = new AxisServiceClient();
    }

    public String getFile(String fileName) { // fileName -

        String getFileTemplate = getTemplateFromFile("getFileTemplate.txt");
        String payload = String.format(getFileTemplate, domain, fileName);

        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(payload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {

            try {
                result = client.sendReceiveWithBasicAuth(payloadElement,
                        endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Invalid xml payload " + fault.getMessage());
            }
        }

        if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }

    public String setFile(String fileName, String fileContent) {

        String setFileTemplate = getTemplateFromFile("setFileTemplate.txt");
        if (setFileTemplate == null) {
            return null;
        }

        String encodedString = Base64Utils.encode(fileContent.getBytes());
        String setPayload = String.format(setFileTemplate, domain, fileName, encodedString);
        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(setPayload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {
            try {
                result = client.sendReceiveWithBasicAuth(
                        payloadElement, endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Error in sending " + fault.getMessage());
            }
        }

        if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }

    public String deleteFile(String fileName) { // name=temporary:///test7.xml

        String deleteFileTemplate = getTemplateFromFile("deleteFileTemplate.txt");
        if (deleteFileTemplate == null) {
            return null;
        }

        String deletePayload = String.format(deleteFileTemplate, domain, fileName);
        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(deletePayload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {
            try {
                result = client.sendReceiveWithBasicAuth(payloadElement, endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Error in Sending " + fault.getMessage());
            }
        }
        if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }


    public String getConfig(String configName, String className) { // name=

        String getConfigTemplate = getTemplateFromFile("getConfigTemplate.txt");
        String payload = String.format(getConfigTemplate, domain, configName, className);

        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(payload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {

            try {
                result = client.sendReceiveWithBasicAuth(payloadElement,
                        endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Error in sending" + fault.getMessage());
            }
        }

        if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }

    public String setConfig(String name, String className) {
        return "";
    }

    public String deleteConfig(String componentName, String className) {

        String deleteConfig = getTemplateFromFile("deleteConfigTemplate.txt");
        String payload = String.format(deleteConfig, domain, componentName, className);

        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(payload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {

            try {
                result = client.sendReceiveWithBasicAuth(payloadElement,
                        endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Invalid xml payload " + fault.getMessage());
            }
        }

        if (result != null) {
            saveConfig();
            return result.toString();

        } else {
            return "";
        }
    }


    public String saveConfig() {

        String saveConfigTemplate = getTemplateFromFile("saveConfigTemplate.txt");
        String saveConfigPayload = String.format(saveConfigTemplate, domain);

        OMElement payloadElement = null;
        try {
            payloadElement = AXIOMUtil.stringToOM(saveConfigPayload);
        } catch (XMLStreamException exp) {
            log.error("Invalid xml payload " + exp.getMessage());
        }

        OMElement result = null;
        if (payloadElement != null) {

            try {
                result = client.sendReceiveWithBasicAuth(payloadElement,
                        endpoint, username, password);
            } catch (AxisFault fault) {
                log.error("Invalid xml payload " + fault.getMessage());
            }
        }

        if (result != null) {
            return result.toString();
        } else {
            return "";
        }
    }

    public String redeploy(String fileName, String fileContent) {

        deleteFile(fileName);
        return setFile(fileName, fileContent);
    }


    private static String getFileFromResources(String fileName) throws IOException {
//        File file = new File((new File(".")).getCanonicalPath()
//                + File.separator + "src" + File.separator + "main"
//                + File.separator + "resources" + File.separator + fileName);

        File file = new File((new File(".")).getCanonicalPath()
                + File.separator + "repository" + File.separator + "resources"
                + File.separator + "templates" + File.separator + fileName);

        log.info("file.getAbsolutePath()" + file.getAbsolutePath());
        log.info("file.getCanonicalPath()" + file.getCanonicalPath());

        if (!file.exists()) {
            throw new IOException("File can not be found in " + file.getCanonicalPath());
        }
        return file.getCanonicalPath();
    }

    private String getTemplateFromFileNew(String fileName)  {
        InputStream in = this.getClass().getResourceAsStream(fileName);
        //String template = getStringFromInputStream(in);
        //log.info("template " +template);
        //return template;
        return "";
    }

    private static final String getTemplateFromFile(String filePath){
        String content = null;
        String absolutePath = null;
        try {
            absolutePath = getFileFromResources(filePath);
            if (absolutePath != null) {
                content = new Scanner(new File(absolutePath)).useDelimiter("\\Z").next();
            }
        } catch (IOException ioe) {
            log.error("File " + filePath + " can not be found in resources");
        }
        return content;
    }

    private static String getStringFromInputStream1(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }



    public String getNewAccessToken(String tokenEndpoint, int port, String clientID, String clientSecret, String scope) {

        if (tokenEndpoint.isEmpty()|| clientID.isEmpty() || clientSecret.isEmpty()) {
            log.warn("No information available to generate Token.");
            return null;
        }

        String tokenResponse = null;

        try {
            //Generate New Access Token
            HttpClient tokenEPClient = getHttpClient(port, "https", false);
            HttpPost httpTokpost = new HttpPost(tokenEndpoint);
            List<NameValuePair> tokParams = new ArrayList<NameValuePair>(4);
            tokParams.add(new BasicNameValuePair(OAUTH_GRANT_TYPE, GRANT_TYPE_VALUE));
            tokParams.add(new BasicNameValuePair(OAUTH_CLIENT_ID, clientID));
            tokParams.add(new BasicNameValuePair(OAUTH_CLIENT_SECRET, clientSecret));
            tokParams.add(new BasicNameValuePair(OAUTH_SCOPE, scope));

            httpTokpost.setEntity(new UrlEncodedFormEntity(tokParams, "UTF-8"));
            HttpResponse tokResponse = tokenEPClient.execute(httpTokpost);
            HttpEntity tokEntity = tokResponse.getEntity();

           //TODO NEED uncomment or decide a proper handling for status other than 200
           // if (tokResponse.getStatusLine().getStatusCode() != 200) {
           //     throw new RuntimeException("Error occurred while calling token endpoint: HTTP error code : " +
           //             tokResponse.getStatusLine().getStatusCode());
           // } else {
                String responseStr = EntityUtils.toString(tokEntity);
                JSONObject obj = new JSONObject(responseStr);
                tokenResponse = obj.toString();
            //}
        } catch (ClientProtocolException e) {
            log.error("Error while creating token - Invalid protocol used", e);
        } catch (UnsupportedEncodingException e) {
            System.out.print("Error while preparing request for token/revoke APIs" + e.getMessage());
        } catch (IOException e) {
            log.error("Error while creating tokens - " + e.getMessage(), e);
        } catch (JSONException e) {
            log.error("Error while parsing response from token api", e);
        }

        return tokenResponse;
    }


    public String httpGet(String tokenEndpoint, int port, String accessToken) {

        String resourceResponse = null;
        try {
            HttpClient httpclient = getHttpClient(port, "https", false);
            HttpGet httpGet = new HttpGet(tokenEndpoint);
            httpGet.addHeader("Authorization", "Bearer " + accessToken);
            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
            int responseCode = response.getStatusLine().getStatusCode();
            System.out.print("responseCode " + responseCode);
            HttpEntity tokEntity = response.getEntity();
            String responseStr = EntityUtils.toString(tokEntity);
            JSONObject obj = new JSONObject(responseStr);
            resourceResponse = obj.toString();
        } catch (IOException ex) {
            // handle exception
        } catch (JSONException e) {
            log.error("Error while parsing response from token api", e);
        }
        return resourceResponse;
    }

    public String httpPOST(String endpoint, int port, String accessToken, String payload) {

        String resourceResponse = null;
        try {
            HttpClient httpclient = getHttpClient(port, "https", false);
            HttpPost post = new HttpPost(endpoint);
            post.addHeader("Authorization", "Bearer " + accessToken);
            HttpEntity entity = new StringEntity(payload);
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post); // the client executes the request and gets a response
            int responseCode = response.getStatusLine().getStatusCode();
            System.out.print("responseCode " + responseCode);
            HttpEntity tokEntity = response.getEntity();
            String responseStr = EntityUtils.toString(tokEntity);
            JSONObject obj = new JSONObject(responseStr);
            resourceResponse = obj.toString();
        } catch (IOException ex) {
            // handle exception
        } catch (JSONException e) {
            log.error("Error while parsing response from token api", e);
        }
        return resourceResponse;
    }


    private HttpClient getHttpClient(int port, String protocol, boolean insecure) {
        SchemeRegistry registry = new SchemeRegistry();

        SSLSocketFactory socketFactory = null;

        if (insecure) {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null,
                        new TrustManager[]{new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {

                                return null;
                            }

                            public void checkClientTrusted(
                                    X509Certificate[] certs, String authType) {

                            }

                            public void checkServerTrusted(
                                    X509Certificate[] certs, String authType) {

                            }
                        }}, new SecureRandom());
            } catch (KeyManagementException e) {
                System.out.print("Error in http client init " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.out.print("Error in http client init " + e.getMessage());
            }

            socketFactory = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        } else {
            X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier(hostnameVerifier);
        }


        if ("https".equals(protocol)) {
            if (port >= 0) {
                registry.register(new Scheme("https", port, socketFactory));
            } else {
                registry.register(new Scheme("https", 443, socketFactory));
            }
        } else if ("http".equals(protocol)) {
            if (port >= 0) {
                registry.register(new Scheme("http", port, PlainSocketFactory.getSocketFactory()));
            } else {
                registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            }
        }
        HttpParams params = new BasicHttpParams();
        ThreadSafeClientConnManager tcm = new ThreadSafeClientConnManager(registry);
        HttpClient client = new DefaultHttpClient(tcm, params);
        return client;
    }
}