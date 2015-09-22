package org.wso2.carbon.governance.dp.executors;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.governance.registry.extensions.interfaces.Execution;
import org.wso2.carbon.governance.registry.extensions.internal.GovernanceRegistryExtensionsComponent;
import org.wso2.carbon.governance.registry.extensions.internal.GovernanceRegistryExtensionsDataHolder;
import org.wso2.carbon.registry.core.Association;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.ResourceImpl;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.demo.clients.DataPowerClient;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import java.io.*;
import java.util.Map;

import static org.wso2.carbon.governance.dp.executors.utils.ExecutorConstants.*;

public class SoapServiceToDPExecutor implements Execution{

    private static final Log LOG = LogFactory.getLog(SoapServiceToDPExecutor.class);

    private String soapServiceMediaType = "application/vnd.wso2-soap-service+xml";
    private String dpEndpoint = null;
    private String dpUsername = null;
    private String dpPassword = null;
    private String dpDomain = null;
    private String tempDirectoryPath = "/home/daneshk/Desktop/temp/";
    private Registry registry;
    private DataPowerClient dpClient;

    /**
     * This method is called when the execution class is initialized.
     * All the execution classes are initialized only once.
     *
     * @param parameterMap Static parameter map given by the user.
     *                     These are the parameters that have been given in the
     *                     lifecycle configuration as the parameters of the executor.
     */
    @Override
    public void init(Map parameterMap) {
        SecretResolver secretResolver = SecretResolverFactory.create((OMElement) null, false);
        // Retrieves the secured password as follows
        secretResolver.init(GovernanceRegistryExtensionsComponent.getSecretCallbackHandlerService()
                .getSecretCallbackHandler());
        if (secretResolver.isInitialized()) {
            dpUsername = secretResolver.resolve(DP_USERNAME);
            dpPassword = secretResolver.resolve(DP_PASSWORD);
        }
        if (parameterMap.get(DP_ENDPOINT) != null) {
            dpEndpoint = parameterMap.get(DP_ENDPOINT).toString();
        }
        if (parameterMap.get(DP_USERNAME) != null) {
            dpUsername = parameterMap.get(DP_USERNAME).toString();
        }
        if (parameterMap.get(DP_PASSWORD) != null) {
            dpPassword = parameterMap.get(DP_PASSWORD).toString();
        }
        if (parameterMap.get(DP_DOMAIN) != null) {
            dpDomain = parameterMap.get(DP_DOMAIN).toString();
        }
        if (parameterMap.get(DP_TEMP_DIRPATH) != null) {
            tempDirectoryPath = parameterMap.get(DP_TEMP_DIRPATH).toString();
        }

        dpClient = new DataPowerClient(dpUsername, dpPassword, dpEndpoint, dpDomain);
        String user = CarbonContext.getThreadLocalCarbonContext().getUsername();
        try {
            registry = GovernanceRegistryExtensionsDataHolder.getInstance().getRegistryService().getGovernanceUserRegistry(user, CarbonContext.getThreadLocalCarbonContext().getTenantId());
        } catch (RegistryException e) {
            LOG.error("Error while getting registry", e);
        }
    }

    /**
     * @param context      The request context that was generated from the registry core.
     *                     The request context contains the resource, resource PATH and other
     *                     variables generated during the initial call.
     * @param currentState The current lifecycle state.
     * @param targetState  The target lifecycle state.
     * @return Returns whether the execution was successful or not.
     */
    @Override
    public boolean execute(RequestContext context, String currentState, String targetState) {
        Resource resource = context.getResource();
        try {
            String resourceAbsolutePath = resource.getPath();
            String resourceRelativePath = resourceAbsolutePath.substring("/_system/governance".length());
            writeContentToFile(resource);
            pushContentToDP(dpClient, resource);

            Association[] associations = registry.getAllAssociations(resourceRelativePath);
            for(Association assoc: associations) {
                Resource assocResource = registry.get(assoc.getDestinationPath());
                writeContentToFile(assocResource);
                pushContentToDP(dpClient, assocResource);
            }

        } catch (RegistryException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void pushContentToDP(DataPowerClient dpClient, Resource resource) throws RegistryException {

        String filename = ((ResourceImpl) resource).getName();
        if ("".equals(FilenameUtils.getExtension(filename))) {
            filename = filename + ".xml";
        }

        byte[] data = SerializationUtils.serialize((Serializable) resource.getContent());
        String encodedString = Base64Utils.encode(data);
        String response = dpClient.setFile("local://" +filename,encodedString);
        LOG.info("new File: "+ filename +"added to DP store" + response);
    }

    private void writeContentToFile(Resource resource) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // read this file into InputStream
            inputStream = resource.getContentStream();
            // write the inputStream to a FileOutputStream
            File tempDirectory = new File(tempDirectoryPath);
            if (!tempDirectory.exists() && !tempDirectory.mkdirs()) {
                throw new IOException("temp directory could not be created! Path: " + tempDirectory);
            }

            String filename = ((ResourceImpl) resource).getName();
            if ("".equals(FilenameUtils.getExtension(filename))) {
                filename = filename + ".xml";
            }
            outputStream =
                    new FileOutputStream(new File(tempDirectory, filename));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            LOG.info("new File: "+ ((ResourceImpl) resource).getName() +" created in the temp directory: " +tempDirectoryPath);
        } catch (IOException | RegistryException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
