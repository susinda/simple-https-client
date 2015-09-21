package org.wso2.demo.clients;

import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * This class authenticate stubs with valid session cookie
 */
public class AuthenticateStub {
    private static final Log log = LogFactory.getLog(AuthenticateStub.class);

    /**
     * Stub authentication method
     *
     * @param stub          valid stub
     * @param sessionCookie session cookie
     */
    public static void authenticateStub(String sessionCookie, Stub stub) {
        long soTimeout = 5 * 60 * 1000; // Three minutes

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(soTimeout);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
        if (log.isDebugEnabled()) {
            log.debug("AuthenticateStub : Stub created with session " + sessionCookie);
        }
    }

    public static Stub authenticateStub(Stub stub, String sessionCookie, String backendURL) {
        long soTimeout = 5 * 60 * 1000; // Three minutes

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(soTimeout);
        System.out.println("XXXXXXXXXXXXXXXXXXX" +
                backendURL +  client.getServiceContext().getAxisService().getName().replaceAll("[^a-zA-Z]", ""));
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
        option.setTo(new EndpointReference(backendURL +  client.getServiceContext().getAxisService().getName().replaceAll("[^a-zA-Z]", "")));
        if (log.isDebugEnabled()) {
            log.debug("AuthenticateStub : Stub created with session " + sessionCookie);
        }

        return stub;
    }

    /**
     * Authenticate the given web service stub against the Product user manager. This
     * will make it possible to use the stub for invoking Product admin services.
     *
     * @param stub Axis2 service stub which needs to be authenticated
     */
    public static void authenticateStub(String userName, String password, Stub stub) {
        setBasicAccessSecurityHeaders(userName, password, true, stub._getServiceClient());
    }

    public static void setBasicAccessSecurityHeaders(String userName, String password, boolean rememberMe,  ServiceClient serviceClient) {

        String userNamePassword = userName + ":" + password;
        String encodedString = Base64Utils.encode(userNamePassword.getBytes());

        String authorizationHeader = "Basic " + encodedString;

        List<Header> headers = new ArrayList<Header>();

        Header authHeader = new Header("Authorization", authorizationHeader);
        headers.add(authHeader);

        if (rememberMe) {
            Header rememberMeHeader = new Header("RememberMe", "true");
            headers.add(rememberMeHeader);
        }

        serviceClient.getOptions().setProperty(HTTPConstants.HTTP_HEADERS, headers);
    }
}