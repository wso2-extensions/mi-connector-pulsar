/*
 *  Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.integration.connector;

import com.google.gson.JsonObject;
import org.apache.axis2.AxisFault;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.integration.connector.connection.PulsarConnection;
import org.wso2.integration.connector.exception.PulsarConnectorException;
import org.wso2.integration.connector.pojo.ConnectionConfiguration;
import org.wso2.integration.connector.core.AbstractConnector;
import org.wso2.integration.connector.core.connection.ConnectionHandler;
import org.wso2.integration.connector.pojo.JWTAuthConfig;
import org.wso2.integration.connector.pojo.PulsarConnectionConfig;
import org.wso2.integration.connector.pojo.PulsarSecureConnectionConfig;
import org.wso2.integration.connector.utils.Error;
import org.wso2.integration.connector.utils.PulsarConstants;
import org.wso2.integration.connector.utils.PulsarUtils;

public class PulsarConfig extends AbstractConnector implements ManagedLifecycle {

    @Override
    public void connect(MessageContext messageContext) {

        String connectionName = (String) getParameter(messageContext, PulsarConstants.CONNECTION_NAME);

        try {
            ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
            if (!handler.checkIfConnectionExists(PulsarConstants.CONNECTOR_NAME, connectionName)) {
                ConnectionConfiguration configuration = getConnectionConfigFromContext(messageContext);
                PulsarConnection pulsarConnection = new PulsarConnection(configuration);
                try {
                    handler.createConnection(PulsarConstants.CONNECTOR_NAME, connectionName, pulsarConnection,
                            messageContext);
                } catch (NoSuchMethodError e) {
                    handler.createConnection(PulsarConstants.CONNECTOR_NAME, connectionName, pulsarConnection);
                }

            }
        } catch (PulsarConnectorException e) {
            String errorDetail = "[" + connectionName + "] Failed to initiate Pulsar connector configuration.";
            handleError(messageContext, e, Error.INVALID_CONFIGURATION, errorDetail);
        } catch (PulsarClientException e) {
            String errorDetail = "[" + connectionName + "] Failed to create the Pulsar connection.";
            handleError(messageContext, e, Error.CONNECTION_ERROR, errorDetail);
        }
    }

    private ConnectionConfiguration getConnectionConfigFromContext(MessageContext messageContext)
            throws PulsarConnectorException {

        ConnectionConfiguration configuration = new ConnectionConfiguration();
        configuration.setConnectionName((String) getParameter(messageContext, PulsarConstants.CONNECTION_NAME));
        configuration.setUseTlsEncryption((String) getParameter(messageContext, PulsarConstants.USE_TLS),
                (String) getParameter(messageContext, PulsarConstants.SERVICE_URL));

        if (configuration.getUseTlsEncryption()) {
            PulsarSecureConnectionConfig secureConfig = getPulsarSecureConnectionConfigFromContext(messageContext);
            configuration.setConnectionConfig(secureConfig);
        } else {
            PulsarConnectionConfig connectionConfig = getPulsarConnectionConfigFromContext(messageContext, null);
            configuration.setConnectionConfig(connectionConfig);
        }

        setAuthConfigToConnectionConfig(messageContext, configuration);

        return configuration;

    }

    private void setAuthConfigToConnectionConfig(MessageContext messageContext, ConnectionConfiguration configuration)
            throws PulsarConnectorException {
        String authType = (String) getParameter(messageContext, PulsarConstants.AUTH_TYPE);
        if (authType != null) {
            switch (authType.toUpperCase()) {
                case PulsarConstants.AUTH_JWT:
                    JWTAuthConfig jwtAuthConfig = new JWTAuthConfig();
                    jwtAuthConfig.setToken((String) getParameter(messageContext, PulsarConstants.JWT_TOKEN));
                    configuration.setAuthConfig(jwtAuthConfig);
                    break;
                case PulsarConstants.AUTH_OAUTH2:
                    // Handle OAuth2 authentication
                    log.warn("OAuth2 authentication is not supported yet.");
                    break;
                case PulsarConstants.AUTH_TLS:
                    // Handle TLS authentication
                    log.warn("TLS authentication is not supported yet.");
                    break;
                case PulsarConstants.AUTH_NONE:
                    // Handle no authentication
                    break;
                default:
                    throw new PulsarConnectorException("Unsupported authentication type: " + authType
                            + ". Valid types are: " + PulsarConstants.AUTH_JWT + ", "
                            + PulsarConstants.AUTH_OAUTH2 + ", " + PulsarConstants.AUTH_TLS + ", "
                            + PulsarConstants.AUTH_NONE);
            }
        }
    }

    private PulsarConnectionConfig getPulsarConnectionConfigFromContext(MessageContext messageContext,
                                                                        PulsarConnectionConfig config)
            throws PulsarConnectorException {

        if (config == null) {
            config = new PulsarConnectionConfig();
        }

        config.setServiceUrl((String) getParameter(messageContext, PulsarConstants.SERVICE_URL));
        config.setOperationTimeoutSeconds((String) getParameter(messageContext,
                PulsarConstants.OPERATION_TIMEOUT_SECONDS));
        config.setStatsIntervalSeconds((String) getParameter(messageContext, PulsarConstants.STATS_INTERVAL_SECONDS));
        config.setNumIoThreads((String) getParameter(messageContext, PulsarConstants.NUM_IO_THREADS));
        config.setNumListenerThreads((String) getParameter(messageContext, PulsarConstants.NUM_LISTENER_THREADS));
        config.setUseTcpNoDelay((String) getParameter(messageContext, PulsarConstants.USE_TCP_NO_DELAY));
        config.setRequestTimeoutMs((String) getParameter(messageContext, PulsarConstants.REQUEST_TIMEOUT_MS));
        config.setMaxLookupRequest((String) getParameter(messageContext, PulsarConstants.MAX_LOOKUP_REQUESTS));
        config.setKeepAliveIntervalSeconds((String) getParameter(messageContext,
                PulsarConstants.KEEP_ALIVE_INTERVAL_SECONDS));
        config.setMaxBackoffInterval((String) getParameter(messageContext, PulsarConstants.MAX_BACKOFF_INTERVAL));
        config.setConcurrentLookupRequest((String) getParameter(messageContext,
                PulsarConstants.CONCURRENT_LOOKUP_REQUEST));
        config.setMaxConcurrentLookupRequests((String) getParameter(messageContext,
                PulsarConstants.MAX_CONCURRENT_LOOKUP_REQUESTS));
        config.setConnectionMaxIdleSeconds((String) getParameter(messageContext,
                PulsarConstants.CONNECTION_MAX_IDLE_SECONDS));
        config.setConnectionTimeoutMs((String) getParameter(messageContext, PulsarConstants.CONNECTION_TIMEOUT_MS));
        config.setConnectionsPerBroker((String) getParameter(messageContext, PulsarConstants.CONNECTIONS_PER_BROKER));
        config.setEnableBusyWait((String) getParameter(messageContext, PulsarConstants.ENABLE_BUSY_WAIT));
        config.setEnableTransaction((String) getParameter(messageContext, PulsarConstants.ENABLE_TRANSACTION));
        config.setInitialBackoffInterval((String) getParameter(messageContext,
                PulsarConstants.INITIAL_BACKOFF_INTERVAL));
        config.setListenerName((String) getParameter(messageContext, PulsarConstants.LISTENER_NAME));
        config.setLookupTimeoutMs((String) getParameter(messageContext, PulsarConstants.LOOKUP_TIMEOUT_MS));
        config.setMaxLookupRedirects((String) getParameter(messageContext, PulsarConstants.MAX_LOOKUP_REDIRECTS));
        config.setMaxLookupRequest((String) getParameter(messageContext, PulsarConstants.MAX_LOOKUP_REQUEST));
        config.setMaxNumberOfRejectedRequestPerConnection((String) getParameter(messageContext,
                PulsarConstants.MAX_NUMBER_OF_REJECTED_REQUEST_PER_CONNECTION));
        config.setMemoryLimitBytes((String) getParameter(messageContext, PulsarConstants.MEMORY_LIMIT_BYTES));

        return config;
    }

    private PulsarSecureConnectionConfig getPulsarSecureConnectionConfigFromContext(MessageContext messageContext)
            throws PulsarConnectorException {
        PulsarSecureConnectionConfig config = new PulsarSecureConnectionConfig();

        getPulsarConnectionConfigFromContext(messageContext, config);
        config.setUseTls((String) getParameter(messageContext, PulsarConstants.USE_TLS));
        config.setTlsAllowInsecureConnection((String) getParameter(messageContext,
                PulsarConstants.TLS_ALLOW_INSECURE_CONNECTION));
        config.setEnableTlsHostnameVerification((String) getParameter(messageContext,
                PulsarConstants.TLS_HOSTNAME_VERIFICATION_ENABLE));
        config.setTlsTrustCertsFilePath((String) getParameter(messageContext,
                PulsarConstants.TLS_TRUST_CERTS_FILE_PATH));
        config.setTlsProtocols((String) getParameter(messageContext, PulsarConstants.TLS_PROTOCOLS));
        config.setTlsCiphers((String) getParameter(messageContext, PulsarConstants.TLS_CIPHERS));
        config.setUseKeyStoreTls((String) getParameter(messageContext, PulsarConstants.USE_KEY_STORE_TLS));
        config.setTlsTrustStorePath((String) getParameter(messageContext, PulsarConstants.TLS_TRUST_STORE_PATH));
        config.setTlsTrustStorePassword((String) getParameter(messageContext,
                PulsarConstants.TLS_TRUST_STORE_PASSWORD));
        config.setTlsTrustStoreType((String) getParameter(messageContext, PulsarConstants.TLS_TRUST_STORE_TYPE));
        config.setAutoCertRefreshSeconds((String) getParameter(messageContext,
                PulsarConstants.AUTO_CERT_REFRESH_SECONDS));

        return config;
    }

    /**
     * Sets error to context and handle.
     *
     * @param msgCtx      Message Context to set info
     * @param e           Exception associated
     * @param error       Error code
     * @param errorDetail Error detail
     */
    private void handleError(MessageContext msgCtx, Exception e, Error error, String errorDetail) {

        errorDetail = PulsarUtils.maskURLPassword(errorDetail);
        JsonObject resultJSON = PulsarUtils.buildErrorResponse(msgCtx, e, error);
        try {
            JsonUtil.getNewJsonPayload(((Axis2MessageContext)msgCtx).getAxis2MessageContext(), resultJSON.toString(),
                    false, false);
        } catch (AxisFault axisFault) {
            log.error("Error while setting the error payload.", axisFault);
        }
        handleException(errorDetail, e, msgCtx);
    }

    @Override
    public void destroy() {
        ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
        handler.shutdownConnections(PulsarConstants.CONNECTOR_NAME);
    }

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {
        // Implement initialization logic here
    }

}
