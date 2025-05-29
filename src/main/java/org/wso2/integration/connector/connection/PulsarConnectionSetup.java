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

package org.wso2.integration.connector.connection;

import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.SizeUnit;
import org.wso2.integration.connector.exception.PulsarConnectorException;
import org.wso2.integration.connector.pojo.ConnectionConfiguration;
import org.wso2.integration.connector.pojo.PulsarConnectionConfig;
import org.wso2.integration.connector.pojo.PulsarSecureConnectionConfig;
import org.wso2.integration.connector.utils.PulsarConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PulsarConnectionSetup {

    public ClientBuilder constructClientBuilder(ConnectionConfiguration configuration, ClientBuilder builder)
            throws PulsarConnectorException {

        PulsarConnectionConfig connectionConfig = configuration.getConnectionConfig();
        if (connectionConfig != null) {
            builder = updateClientBuilderForPlainConnection(connectionConfig, builder);
            if (connectionConfig instanceof PulsarSecureConnectionConfig) {
                PulsarSecureConnectionConfig secureConfig = (PulsarSecureConnectionConfig) connectionConfig;
                builder = updateClientBuilderForSecureConnection(secureConfig, builder);
            }
        } else {
            throw new PulsarConnectorException("The connection configuration is null.");
        }

        if (configuration.getAuthConfig() != null) {
            builder.authentication(configuration.getAuthConfig().getAuthentication());
        }

        return builder;

    }

    public ClientBuilder updateClientBuilderForSecureConnection(PulsarSecureConnectionConfig secureConfig,
                                                                ClientBuilder clientBuilder) {

        if (secureConfig.useTls() != null) {
            clientBuilder.enableTls(secureConfig.useTls());
        }
        if (secureConfig.getTlsAllowInsecureConnection() != null) {
            clientBuilder.allowTlsInsecureConnection(secureConfig.getTlsAllowInsecureConnection());
        }
        if (secureConfig.getEnableTlsHostnameVerification() != null) {
            clientBuilder.enableTlsHostnameVerification(secureConfig.getEnableTlsHostnameVerification());
        }
        if (secureConfig.getTlsTrustCertsFilePath() != null) {
            clientBuilder.tlsTrustCertsFilePath(secureConfig.getTlsTrustCertsFilePath());
        }
        if (secureConfig.getTlsProtocols() != null) {
            clientBuilder.tlsProtocols(secureConfig.getTlsProtocols());
        }
        if (secureConfig.getTlsCiphers() != null) {
            clientBuilder.tlsCiphers(secureConfig.getTlsCiphers());
        }
        if (secureConfig.getUseKeyStoreTls() != null) {
            clientBuilder.useKeyStoreTls(secureConfig.getUseKeyStoreTls());
        }
        if (secureConfig.getTlsTrustStorePath() != null) {
            clientBuilder.tlsTrustStorePath(secureConfig.getTlsTrustStorePath());
        }
        if (secureConfig.getTlsTrustStorePassword() != null) {
            clientBuilder.tlsTrustStorePassword(secureConfig.getTlsTrustStorePassword());
        }
        if (secureConfig.getTlsTrustStoreType() != null) {
            clientBuilder.tlsTrustStoreType(secureConfig.getTlsTrustStoreType());
        }
        if (secureConfig.getAutoCertRefreshSeconds() != null) {
            clientBuilder.autoCertRefreshSeconds(secureConfig.getAutoCertRefreshSeconds());
        }

        return clientBuilder;
    }

    public ClientBuilder updateClientBuilderForPlainConnection(PulsarConnectionConfig connectionConfig,
                                                               ClientBuilder clientBuilder) {

        Map<String, Object> configMap = new HashMap<>();

        if (connectionConfig.getServiceUrl() != null) {
            clientBuilder.serviceUrl(connectionConfig.getServiceUrl());
        }
        if (connectionConfig.getOperationTimeoutSeconds() != null) {
            clientBuilder.operationTimeout(connectionConfig.getOperationTimeoutSeconds(), java.util.concurrent.TimeUnit.SECONDS);
        }
        if (connectionConfig.getStatsIntervalSeconds() != null) {
            clientBuilder.statsInterval(connectionConfig.getStatsIntervalSeconds(), java.util.concurrent.TimeUnit.SECONDS);
        }
        if (connectionConfig.getNumIoThreads() != null) {
            clientBuilder.ioThreads(connectionConfig.getNumIoThreads());
        }
        if (connectionConfig.getNumListenerThreads() != null) {
            clientBuilder.listenerThreads(connectionConfig.getNumListenerThreads());
        }
        if (connectionConfig.getUseTcpNoDelay() != null) {
            clientBuilder.enableTcpNoDelay(connectionConfig.getUseTcpNoDelay());
        }
        if (connectionConfig.getRequestTimeoutMs() != null) {
            clientBuilder.operationTimeout(connectionConfig.getRequestTimeoutMs(), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        if (connectionConfig.getKeepAliveIntervalSeconds() != null) {
            clientBuilder.keepAliveInterval(connectionConfig.getKeepAliveIntervalSeconds(), java.util.concurrent.TimeUnit.SECONDS);
        }
        if (connectionConfig.getMaxBackoffInterval() != null) {
            clientBuilder.maxBackoffInterval(connectionConfig.getMaxBackoffInterval(), TimeUnit.MILLISECONDS);
        }
        if (connectionConfig.getConcurrentLookupRequest() != null) {
            configMap.put(PulsarConstants.CONCURRENT_LOOKUP_REQUEST, connectionConfig.getConcurrentLookupRequest());
        }
        if (connectionConfig.getMaxConcurrentLookupRequests() != null) {
            clientBuilder.maxConcurrentLookupRequests(connectionConfig.getMaxConcurrentLookupRequests());
        }
        if (connectionConfig.getConnectionMaxIdleSeconds() != null) {
            clientBuilder.connectionMaxIdleSeconds(connectionConfig.getConnectionMaxIdleSeconds());
        }
        if (connectionConfig.getConnectionTimeoutMs() != null) {
            clientBuilder.connectionTimeout(connectionConfig.getConnectionTimeoutMs(), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        if (connectionConfig.getConnectionsPerBroker() != null) {
            clientBuilder.connectionsPerBroker(connectionConfig.getConnectionsPerBroker());
        }
        if (connectionConfig.getEnableBusyWait() != null) {
            clientBuilder.enableBusyWait(connectionConfig.getEnableBusyWait());
        }
        if (connectionConfig.getEnableTransaction() != null) {
            clientBuilder.enableTransaction(connectionConfig.getEnableTransaction());
        }
        if (connectionConfig.getInitialBackoffInterval() != null) {
            clientBuilder.startingBackoffInterval(connectionConfig.getInitialBackoffInterval(), TimeUnit.MILLISECONDS);
        }
        if (connectionConfig.getListenerName() != null) {
            clientBuilder.listenerName(connectionConfig.getListenerName());
        }
        if (connectionConfig.getLookupTimeoutMs() != null) {
            clientBuilder.lookupTimeout(connectionConfig.getLookupTimeoutMs(), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        if (connectionConfig.getMaxLookupRedirects() != null) {
            clientBuilder.maxLookupRedirects(connectionConfig.getMaxLookupRedirects());
        }
        if (connectionConfig.getMaxLookupRequest() != null) {
            clientBuilder.maxLookupRequests(connectionConfig.getMaxLookupRequest());
        }
        if (connectionConfig.getMaxNumberOfRejectedRequestPerConnection() != null) {
            clientBuilder.maxNumberOfRejectedRequestPerConnection(connectionConfig.getMaxNumberOfRejectedRequestPerConnection());
        }
        if (connectionConfig.getMemoryLimitBytes() != null) {
            clientBuilder.memoryLimit(connectionConfig.getMemoryLimitBytes(), SizeUnit.BYTES);
        }

        if (!configMap.isEmpty()) {
            clientBuilder.loadConf(configMap);
        }

        return clientBuilder;

    }

}
