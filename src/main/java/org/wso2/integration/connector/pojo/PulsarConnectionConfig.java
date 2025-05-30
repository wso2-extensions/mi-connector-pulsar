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

package org.wso2.integration.connector.pojo;

import org.apache.commons.lang3.StringUtils;
import org.wso2.integration.connector.exception.PulsarConnectorException;

public class PulsarConnectionConfig {

    private String serviceUrl;
    private Integer operationTimeoutSeconds;
    private Integer statsIntervalSeconds;
    private Integer numIoThreads;
    private Integer numListenerThreads;
    private Boolean useTcpNoDelay;
    private Integer requestTimeoutMs;
    private Integer maxLookupRequest;
    private Integer keepAliveIntervalSeconds;
    private Long maxBackoffInterval;
    private Integer concurrentLookupRequest;
    private Integer maxConcurrentLookupRequests;
    private Integer connectionMaxIdleSeconds;
    private Integer connectionTimeoutMs;
    private Integer connectionsPerBroker;
    private Boolean enableBusyWait;
    private Boolean enableTransaction;
    private Long initialBackoffInterval;
    private String listenerName;
    private Integer lookupTimeoutMs;
    private Integer maxLookupRedirects;
    private Integer maxNumberOfRejectedRequestPerConnection;
    private Long memoryLimitBytes;

    public String getServiceUrl() {

        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) throws PulsarConnectorException {

        if (StringUtils.isNotEmpty(serviceUrl)) {
            this.serviceUrl = serviceUrl;
        } else {
            throw new PulsarConnectorException("Mandatory parameter 'serviceUrl' is not set.");
        }
    }

    public Integer getOperationTimeoutSeconds() {

        return operationTimeoutSeconds;
    }

    public void setOperationTimeoutSeconds(String operationTimeoutSeconds) {

        if (StringUtils.isNotEmpty(operationTimeoutSeconds)) {
            this.operationTimeoutSeconds = Integer.parseInt(operationTimeoutSeconds);
        }
    }

    public Integer getStatsIntervalSeconds() {

        return statsIntervalSeconds;
    }

    public void setStatsIntervalSeconds(String statsIntervalSeconds) {

        if (StringUtils.isNotEmpty(statsIntervalSeconds)) {
            this.statsIntervalSeconds = Integer.parseInt(statsIntervalSeconds);
        }
    }

    public Integer getNumIoThreads() {

        return numIoThreads;
    }

    public void setNumIoThreads(String numIoThreads) {

        if (StringUtils.isNotEmpty(numIoThreads)) {
            this.numIoThreads = Integer.parseInt(numIoThreads);
        }
    }

    public Integer getNumListenerThreads() {

        return numListenerThreads;
    }

    public void setNumListenerThreads(String numListenerThreads) {

        if (StringUtils.isNotEmpty(numListenerThreads)) {
            this.numListenerThreads = Integer.parseInt(numListenerThreads);
        }
    }

    public Boolean getUseTcpNoDelay() {

        return useTcpNoDelay;
    }

    public void setUseTcpNoDelay(String useTcpNoDelay) {

        if (StringUtils.isNotEmpty(useTcpNoDelay)) {
            this.useTcpNoDelay = Boolean.parseBoolean(useTcpNoDelay);
        }
    }

    public Integer getRequestTimeoutMs() {

        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(String requestTimeoutMs) {

        if (StringUtils.isNotEmpty(requestTimeoutMs)) {
            this.requestTimeoutMs = Integer.parseInt(requestTimeoutMs);
        }
    }

    public Integer getKeepAliveIntervalSeconds() {

        return keepAliveIntervalSeconds;
    }

    public void setKeepAliveIntervalSeconds(String keepAliveIntervalSeconds) {

        if (StringUtils.isNotEmpty(keepAliveIntervalSeconds)) {
            this.keepAliveIntervalSeconds = Integer.parseInt(keepAliveIntervalSeconds);
        }
    }

    public Long getMaxBackoffInterval() {

        return maxBackoffInterval;
    }

    public void setMaxBackoffInterval(String maxBackoffInterval) {

        if (StringUtils.isNotEmpty(maxBackoffInterval)) {
            this.maxBackoffInterval = Long.parseLong(maxBackoffInterval);
        }
    }

    public Integer getConcurrentLookupRequest() {

        return concurrentLookupRequest;
    }

    public void setConcurrentLookupRequest(String concurrentLookupRequest) {

        if (StringUtils.isNotEmpty(concurrentLookupRequest)) {
            this.concurrentLookupRequest = Integer.parseInt(concurrentLookupRequest);
        }
    }

    public Integer getMaxConcurrentLookupRequests() {

        return maxConcurrentLookupRequests;
    }

    public void setMaxConcurrentLookupRequests(String maxConcurrentLookupRequests) {

        if (StringUtils.isNotEmpty(maxConcurrentLookupRequests)) {
            this.maxConcurrentLookupRequests = Integer.parseInt(maxConcurrentLookupRequests);
        }
    }

    public Integer getConnectionMaxIdleSeconds() {

        return connectionMaxIdleSeconds;
    }

    public void setConnectionMaxIdleSeconds(String connectionMaxIdleSeconds) {

        if (StringUtils.isNotEmpty(connectionMaxIdleSeconds)) {
            this.connectionMaxIdleSeconds = Integer.parseInt(connectionMaxIdleSeconds);
        }
    }

    public Integer getConnectionTimeoutMs() {

        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(String connectionTimeoutMs) {

        if (StringUtils.isNotEmpty(connectionTimeoutMs)) {
            this.connectionTimeoutMs = Integer.parseInt(connectionTimeoutMs);
        }
    }

    public Integer getConnectionsPerBroker() {

        return connectionsPerBroker;
    }

    public void setConnectionsPerBroker(String connectionsPerBroker) {

        if (StringUtils.isNotEmpty(connectionsPerBroker)) {
            this.connectionsPerBroker = Integer.parseInt(connectionsPerBroker);
        }
    }

    public Boolean getEnableBusyWait() {

        return enableBusyWait;
    }

    public void setEnableBusyWait(String enableBusyWait) {

        if (StringUtils.isNotEmpty(enableBusyWait)) {
            this.enableBusyWait = Boolean.parseBoolean(enableBusyWait);
        }
    }

    public Boolean getEnableTransaction() {

        return enableTransaction;
    }

    public void setEnableTransaction(String enableTransaction) {

        if (StringUtils.isNotEmpty(enableTransaction)) {
            this.enableTransaction = Boolean.parseBoolean(enableTransaction);
        }
    }

    public Long getInitialBackoffInterval() {

        return initialBackoffInterval;
    }

    public void setInitialBackoffInterval(String initialBackoffInterval) {

        if (StringUtils.isNotEmpty(initialBackoffInterval)) {
            this.initialBackoffInterval = Long.parseLong(initialBackoffInterval);
        }
    }

    public String getListenerName() {

        return listenerName;

    }

    public void setListenerName(String listenerName) {

        if (StringUtils.isNotEmpty(listenerName)) {
            this.listenerName = listenerName;
        }
    }

    public Integer getLookupTimeoutMs() {

        return lookupTimeoutMs;
    }

    public void setLookupTimeoutMs(String lookupTimeoutMs) {

        if (StringUtils.isNotEmpty(lookupTimeoutMs)) {
            this.lookupTimeoutMs = Integer.parseInt(lookupTimeoutMs);
        }
    }

    public Integer getMaxLookupRedirects() {

        return maxLookupRedirects;
    }

    public void setMaxLookupRedirects(String maxLookupRedirects) {

        if (StringUtils.isNotEmpty(maxLookupRedirects)) {
            this.maxLookupRedirects = Integer.parseInt(maxLookupRedirects);
        }
    }

    public Integer getMaxLookupRequest() {

        return maxLookupRequest;
    }

    public void setMaxLookupRequest(String maxLookupRequest) {

        if (StringUtils.isNotEmpty(maxLookupRequest)) {
            this.maxLookupRequest = Integer.parseInt(maxLookupRequest);
        }
    }

    public Integer getMaxNumberOfRejectedRequestPerConnection() {

        return maxNumberOfRejectedRequestPerConnection;
    }

    public void setMaxNumberOfRejectedRequestPerConnection(String maxNumberOfRejectedRequestPerConnection) {

        if (StringUtils.isNotEmpty(maxNumberOfRejectedRequestPerConnection)) {
            this.maxNumberOfRejectedRequestPerConnection = Integer.parseInt(maxNumberOfRejectedRequestPerConnection);
        }
    }

    public Long getMemoryLimitBytes() {

        return memoryLimitBytes;
    }

    public void setMemoryLimitBytes(String memoryLimitBytes) {

        if (StringUtils.isNotEmpty(memoryLimitBytes)) {
            this.memoryLimitBytes = Long.parseLong(memoryLimitBytes);
        }
    }
}
