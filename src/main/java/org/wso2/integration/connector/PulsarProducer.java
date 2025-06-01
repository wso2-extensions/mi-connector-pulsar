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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.base.BaseUtils;
import org.apache.axis2.util.MessageProcessorSelector;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.HashingScheme;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.ProducerCryptoFailureAction;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.integration.connector.connection.PulsarConnection;
import org.wso2.integration.connector.core.AbstractConnectorOperation;
import org.wso2.integration.connector.core.connection.ConnectionHandler;
import org.wso2.integration.connector.core.ConnectException;
import org.wso2.integration.connector.core.util.ConnectorUtils;
import org.wso2.integration.connector.exception.PulsarConnectorException;
import org.wso2.integration.connector.utils.Error;
import org.wso2.integration.connector.utils.PulsarConstants;
import org.wso2.integration.connector.utils.PulsarUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PulsarProducer extends AbstractConnectorOperation {

    Map<ProducerKey, Producer<String>> producerCache = new ConcurrentHashMap<>();

    @Override
    public void execute(MessageContext messageContext, String responseVariable, Boolean overwriteBody)
            throws ConnectException {

        JsonObject resultJSON;
        try {
            ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
            PulsarConnection pulsarConnection = (PulsarConnection) handler.getConnection(PulsarConstants.CONNECTOR_NAME,
                    getConnectionName(messageContext));
            PulsarClient pulsarClient = pulsarConnection.getClient();

            String topicName = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.PRODUCER_TOPIC_NAME);
            String sendTimeoutMs = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.SEND_TIMEOUT_MS);
            String blockIfQueueFull = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.BLOCK_IF_QUEUE_FULL);
            String maxPendingMessages = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.MAX_PENDING_MESSAGES);
            String maxPendingMessagesAcrossPartitions = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.MAX_PENDING_MESSAGES_ACROSS_PARTITIONS);
            String batchingEnabled = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.BATCHING_ENABLED);
            String batchingMaxMessages = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.BATCHING_MAX_MESSAGES);
            String batchingMaxPublishDelayMicros = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.BATCHING_MAX_PUBLISH_DELAY_MICROS);
            String compressionType = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.COMPRESSION_TYPE);
            String hashingScheme = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.HASHING_SCHEME);
            String messageRoutingMode = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.MESSAGE_ROUTING_MODE);
            String chunkingEnabled = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.CHUNKING_ENABLED);
            String cryptoFailureAction = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.CRYPTO_FAILURE_ACTION);
            String chunkMaxMessageSize = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.CHUNK_MAX_MESSAGE_SIZE);
            String batchingMaxBytes = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.BATCHING_MAX_BYTES);
            String sendMode = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                    PulsarConstants.SEND_MODE);

            Map<String, String> producerConfig = new HashMap<>();
            producerConfig.put(PulsarConstants.SEND_TIMEOUT_MS, sendTimeoutMs);
            producerConfig.put(PulsarConstants.BLOCK_IF_QUEUE_FULL, blockIfQueueFull);
            producerConfig.put(PulsarConstants.MAX_PENDING_MESSAGES, maxPendingMessages);
            producerConfig.put(PulsarConstants.BATCHING_MAX_BYTES, batchingMaxBytes);
            producerConfig.put(PulsarConstants.MAX_PENDING_MESSAGES_ACROSS_PARTITIONS,
                    maxPendingMessagesAcrossPartitions);
            producerConfig.put(PulsarConstants.BATCHING_ENABLED, batchingEnabled);
            producerConfig.put(PulsarConstants.BATCHING_MAX_MESSAGES, batchingMaxMessages);
            producerConfig.put(PulsarConstants.BATCHING_MAX_PUBLISH_DELAY_MICROS, batchingMaxPublishDelayMicros);
            producerConfig.put(PulsarConstants.COMPRESSION_TYPE, compressionType);
            producerConfig.put(PulsarConstants.HASHING_SCHEME, hashingScheme);
            producerConfig.put(PulsarConstants.MESSAGE_ROUTING_MODE, messageRoutingMode);
            producerConfig.put(PulsarConstants.CHUNKING_ENABLED, chunkingEnabled);
            producerConfig.put(PulsarConstants.CHUNK_MAX_MESSAGE_SIZE, chunkMaxMessageSize);
            producerConfig.put(PulsarConstants.CRYPTO_FAILURE_ACTION, cryptoFailureAction);

            Producer<String> producer = getProducer(topicName, producerConfig, pulsarClient);
            TypedMessageBuilder<String> messageBuilder = producer.newMessage();
            getMessagePropertiesFromMessageContextAndConstructMessageBuilder(messageBuilder, messageContext);

            // Send the message
            if (sendMode != null && sendMode.equalsIgnoreCase(PulsarConstants.ASYNC)) {
                messageBuilder.sendAsync()
                        .thenAccept(messageId -> {
                            log.info("Message sent to Apache Pulsar successfully with ID: " + messageId);
                        })
                        .exceptionally(e -> {
                            log.error("Failed to send message to Apache Pulsar topic: " + topicName, e);
                            return null;
                        });
            } else {
                try {
                    MessageId messageId = messageBuilder.send();
                    resultJSON = PulsarUtils.buildSuccessResponse(messageId);
                } catch (PulsarClientException e) {
                    resultJSON = PulsarUtils.buildErrorResponse(messageContext, e, Error.OPERATION_ERROR);
                }
                handleConnectorResponse(messageContext, responseVariable, overwriteBody, resultJSON, null, null);
            }
        } catch (PulsarConnectorException e) {
            String errorDetail = "Error occurred while performing pulsar:publishMessage operation.";
            handleError(messageContext, e, Error.INVALID_CONFIGURATION, errorDetail, responseVariable, overwriteBody);
        } catch (Exception e) {
            String errorDetail = "Error occurred while performing pulsar:publishMessage operation.";
            handleError(messageContext, e, Error.OPERATION_ERROR, errorDetail, responseVariable, overwriteBody);
        }
    }

    /**
     * Sets error to context and handle.
     *
     * @param msgCtx      Message Context to set info
     * @param e           Exception associated
     * @param error       Error code
     * @param errorDetail Error detail
     * @param responseVariable Response variable name
     * @param overwriteBody Overwrite body
     */
    private void handleError(MessageContext msgCtx, Exception e, Error error, String errorDetail,
                             String responseVariable, boolean overwriteBody) {

        errorDetail = PulsarUtils.maskURLPassword(errorDetail);
        JsonObject resultJSON = PulsarUtils.buildErrorResponse(msgCtx, e, error);
        handleConnectorResponse(msgCtx, responseVariable, overwriteBody, resultJSON, null, null);
        handleException(errorDetail, e, msgCtx);
    }

    private void getMessagePropertiesFromMessageContextAndConstructMessageBuilder(
            TypedMessageBuilder<String> messageBuilder, MessageContext messageContext) throws PulsarConnectorException {
        String key = (String) ConnectorUtils.lookupTemplateParamater(messageContext, PulsarConstants.KEY);
        if (key != null) {
            messageBuilder.key(key);
        }

        String sequenceId = (String) ConnectorUtils.lookupTemplateParamater(messageContext, PulsarConstants.SEQUENCE_ID);
        if (sequenceId != null) {
            messageBuilder.sequenceId(Long.parseLong(sequenceId));
        }

        String deliverAfter = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                PulsarConstants.DELIVER_AFTER);
        if (deliverAfter != null) {
            messageBuilder.deliverAfter(Long.parseLong(deliverAfter), TimeUnit.MILLISECONDS);
        }

        String properties = (String) ConnectorUtils.lookupTemplateParamater(messageContext, PulsarConstants.PROPERTIES);
        if (properties != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonArray = mapper.readTree(properties);
                if (jsonArray != null && jsonArray.isArray()) {
                    for (JsonNode node : jsonArray) {
                        if (node.isObject()) {
                            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
                            while (fields.hasNext()) {
                                Map.Entry<String, JsonNode> entry = fields.next();
                                String propertyKey = entry.getKey();
                                JsonNode propertyValue = entry.getValue();
                                if (propertyKey != null && propertyValue.isTextual()) {
                                    messageBuilder.property(propertyKey, propertyValue.asText());
                                } else {
                                    log.warn("Skipping non-textual value or null key in message properties array: "
                                            + entry);
                                }
                            }
                        } else {
                            log.warn("Skipping non-object item in message properties array: " + node);
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new PulsarConnectorException("Error occurred while processing the message properties array."
                        + " Might be due to invalid message properties format. Must be A JSON array of objects,"
                        + " where each object represents a property as a key-value pair. Received : " + properties, e);
            }
        }

        messageBuilder.eventTime(System.currentTimeMillis());

        String value = (String) ConnectorUtils.lookupTemplateParamater(messageContext, PulsarConstants.VALUE);
        if (value != null) {
            messageBuilder.value(value);
        } else {
            try {
                value = getMessage(messageContext);
                messageBuilder.value(value);
            } catch (AxisFault e) {
                throw new PulsarConnectorException("Cannot obtain the message from the message context", e);
            }
        }

    }

    /**
     * Get the messages from the message context and format the messages.
     *
     * @param messageContext Message Context
     */
    private String getMessage(MessageContext messageContext) throws AxisFault {

        Axis2MessageContext axisMsgContext = (Axis2MessageContext) messageContext;
        org.apache.axis2.context.MessageContext msgContext = axisMsgContext.getAxis2MessageContext();
        return formatMessage(msgContext);
    }

    /**
     * Format the messages when the messages are sent to the Apache Pulsar broker.
     *
     * @param messageContext Message Context
     * @return formatted message
     * @throws AxisFault if failed to format
     */
    private static String formatMessage(org.apache.axis2.context.MessageContext messageContext)
            throws AxisFault {

        OMOutputFormat format = BaseUtils.getOMOutputFormat(messageContext);
        MessageFormatter messageFormatter = MessageProcessorSelector.getMessageFormatter(messageContext);
        StringWriter stringWriter = new StringWriter();
        OutputStream out = new WriterOutputStream(stringWriter, format.getCharSetEncoding());
        try {
            messageFormatter.writeTo(messageContext, format, out, true);
        } catch (IOException e) {
            throw new AxisFault("The Error occurs while formatting the message", e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                throw new AxisFault("The Error occurs while closing the output stream", e);
            }
        }
        return stringWriter.toString();
    }

    private Producer<String> getProducer(String topic, Map<String, String> config, PulsarClient client) {
        ProducerKey key = new ProducerKey(topic, config);

        return producerCache.computeIfAbsent(key, k -> {
            try {
                ProducerBuilder<String> builder = client.newProducer(Schema.STRING)
                        .topic(topic);
                applyProducerConfig(builder, config);
                return builder.create();
            } catch (PulsarClientException | PulsarConnectorException e) {
                throw new SynapseException("Failed to create Apache Pulsar Producer", e);
            }
        });
    }

    void applyProducerConfig(ProducerBuilder<String> builder, Map<String, String> config)
            throws PulsarConnectorException {
        if (config.get(PulsarConstants.SEND_TIMEOUT_MS) != null) {
            builder.sendTimeout(Integer.parseInt(config.get(PulsarConstants.SEND_TIMEOUT_MS)), TimeUnit.MILLISECONDS);
        }
        if (config.get(PulsarConstants.BLOCK_IF_QUEUE_FULL) != null) {
            builder.blockIfQueueFull(Boolean.parseBoolean(config.get(PulsarConstants.BLOCK_IF_QUEUE_FULL)));
        }
        if (config.get(PulsarConstants.MAX_PENDING_MESSAGES) != null) {
            builder.maxPendingMessages(Integer.parseInt(config.get(PulsarConstants.MAX_PENDING_MESSAGES)));
        }
        if (config.get(PulsarConstants.MAX_PENDING_MESSAGES_ACROSS_PARTITIONS) != null) {
            builder.maxPendingMessagesAcrossPartitions(
                    Integer.parseInt(config.get(PulsarConstants.MAX_PENDING_MESSAGES_ACROSS_PARTITIONS)));
        }
        if (config.get(PulsarConstants.BATCHING_ENABLED) != null) {
            builder.enableBatching(Boolean.parseBoolean(config.get(PulsarConstants.BATCHING_ENABLED)));
        }
        if (config.get(PulsarConstants.BATCHING_MAX_MESSAGES) != null) {
            builder.batchingMaxMessages(Integer.parseInt(config.get(PulsarConstants.BATCHING_MAX_MESSAGES)));
        }
        if (config.get(PulsarConstants.BATCHING_MAX_BYTES) != null) {
            builder.batchingMaxBytes(Integer.parseInt(config.get(PulsarConstants.BATCHING_MAX_BYTES)));
        }
        if (config.get(PulsarConstants.BATCHING_MAX_PUBLISH_DELAY_MICROS) != null) {
            builder.batchingMaxPublishDelay(Long.parseLong(config.get(PulsarConstants.BATCHING_MAX_PUBLISH_DELAY_MICROS)),
                    TimeUnit.MICROSECONDS);
        }
        if (config.get(PulsarConstants.COMPRESSION_TYPE) != null) {
            try {
                builder.compressionType(CompressionType.valueOf(config.get(PulsarConstants.COMPRESSION_TYPE)));
            } catch (IllegalArgumentException e) {
                throw new PulsarConnectorException("Invalid compression type: "
                        + config.get(PulsarConstants.COMPRESSION_TYPE), e);
            }
        }
        if (config.get(PulsarConstants.HASHING_SCHEME) != null) {
            try {
                builder.hashingScheme(HashingScheme.valueOf(config.get(PulsarConstants.HASHING_SCHEME)));
            } catch (IllegalArgumentException e) {
                throw new PulsarConnectorException("Invalid hashing scheme: "
                        + config.get(PulsarConstants.HASHING_SCHEME), e);
            }
        }
        if (config.get(PulsarConstants.MESSAGE_ROUTING_MODE) != null) {
            try {
                builder.messageRoutingMode(MessageRoutingMode.valueOf(config.get(PulsarConstants.MESSAGE_ROUTING_MODE)));
            } catch (IllegalArgumentException e) {
                throw new PulsarConnectorException("Invalid message routing mode: "
                        + config.get(PulsarConstants.MESSAGE_ROUTING_MODE), e);
            }
        }
        if (config.get(PulsarConstants.CHUNKING_ENABLED) != null) {
            builder.enableChunking(Boolean.parseBoolean(config.get(PulsarConstants.CHUNKING_ENABLED)));
        }
        if (config.get(PulsarConstants.CHUNK_MAX_MESSAGE_SIZE) != null) {
            builder.chunkMaxMessageSize(Integer.parseInt(config.get(PulsarConstants.CHUNK_MAX_MESSAGE_SIZE)));
        }
        if (config.get(PulsarConstants.CRYPTO_FAILURE_ACTION) != null) {
            builder.cryptoFailureAction(
                    ProducerCryptoFailureAction.valueOf(config.get(PulsarConstants.CRYPTO_FAILURE_ACTION)));
        }
    }


    private String getConnectionName(MessageContext messageContext) throws PulsarConnectorException {

        String connectionName = (String) messageContext.getProperty(PulsarConstants.CONNECTION_NAME);
        if (connectionName == null) {
            throw new PulsarConnectorException("Connection name is not set.");
        }
        return connectionName;
    }

}
