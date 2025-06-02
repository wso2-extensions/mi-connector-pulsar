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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.wso2.integration.connector.ProducerKey;
import org.wso2.integration.connector.core.ConnectException;
import org.wso2.integration.connector.core.connection.Connection;
import org.wso2.integration.connector.core.connection.ConnectionConfig;
import org.wso2.integration.connector.exception.PulsarConnectorException;
import org.wso2.integration.connector.pojo.ConnectionConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PulsarConnection implements Connection {

    protected Log log = LogFactory.getLog(this.getClass());

    private PulsarClient client;

    private Map<ProducerKey, Producer<String>> producerCache = new ConcurrentHashMap<>();

    public PulsarConnection(ConnectionConfiguration configuration) throws PulsarConnectorException, PulsarClientException {
        ClientBuilder clientBuilder = PulsarClient.builder();
        PulsarConnectionSetup connectionSetup = new PulsarConnectionSetup();
        connectionSetup.constructClientBuilder(configuration, clientBuilder);
        this.client = clientBuilder.build();
    }

    public PulsarClient getClient() {

        return client;
    }

    public Map<ProducerKey, Producer<String>> getProducerCache() {

        return producerCache;
    }

    public void putProducer(ProducerKey key, Producer<String> producer) {
        producerCache.put(key, producer);
    }

    public Producer<String> getProducer(ProducerKey key) {
        return producerCache.get(key);
    }

    @Override
    public void connect(ConnectionConfig connectionConfig) throws ConnectException {
        //Nothing to do here
    }

    @Override
    public void close() throws ConnectException {
        if (client != null) {
            try {
                client.close();
            } catch (PulsarClientException e) {
                log.error("Error closing Pulsar client", e);
            }
        }
        for (Producer<String> producer : producerCache.values()) {
            if (producer != null) {
                try {
                    producer.close();
                } catch (PulsarClientException e) {
                    log.error("Error closing Pulsar producer", e);
                }
            }
        }
        producerCache.clear();
    }
}
