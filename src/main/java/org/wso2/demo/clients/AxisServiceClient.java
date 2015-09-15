package org.wso2.demo.clients;


import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AxisServiceClient {
    private static final Log log = LogFactory.getLog(AxisServiceClient.class);


    public OMElement sendReceive(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            sender.setOptions(options);
            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        return response;
    }


    public OMElement sendReceiveWithBasicAuth(OMElement payload, String endPointReference, String username, String password)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            //log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(username);
            auth.setPassword(password);
            auth.setPreemptiveAuthentication(true);
            options.setProperty(HTTPConstants.AUTHENTICATE, auth);

            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            //options.setAction("urn:" + null);
            sender.setOptions(options);
            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        return response;
    }

    // axis2 client with header setting
    public OMElement sendReceive(OMElement payload, String endPointReference, String operation,
                                 String localName, String ns, String value) throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            sender.addStringHeader(new QName(ns, localName), value);  // Set headers
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);
            sender.setOptions(options);
            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        return response;
    }

    public OMElement sendReceive(OMElement payload, String endPointReference, String operation,
                                 String contentType)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);
            sender.setOptions(options);
            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        //  Assert.assertNotNull(response);
        return response;
    }

    //one way communication
    public void sendRobust(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        if (log.isDebugEnabled()) {
            log.info("Service Endpoint : " + endPointReference);
            log.info("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);
            sender.setOptions(options);
            sender.sendRobust(payload);
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
    }

    //one way communication
    public void fireAndForget(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        if (log.isDebugEnabled()) {
            log.info("Service Endpoint : " + endPointReference);
            log.info("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);
            sender.setOptions(options);
            sender.fireAndForget(payload);
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
    }
}