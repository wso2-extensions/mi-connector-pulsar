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

package org.wso2.integration.connector.utils;

import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.MessageId;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.integration.connector.exception.PulsarConnectorException;

import java.util.regex.Matcher;

import static org.apache.synapse.SynapseConstants.PASSWORD_PATTERN;
import static org.apache.synapse.SynapseConstants.URL_PATTERN;

public class PulsarUtils {

    private static final Log log = LogFactory.getLog(PulsarUtils.class);

    private PulsarUtils() {
        // Utility class, prevent instantiation
    }

    public static void setErrorPropertiesToMessageContext(MessageContext messageContext, Throwable e, Error error) {

        messageContext.setProperty(SynapseConstants.ERROR_CODE, error.getErrorCode());
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, error.getErrorMessage());
        messageContext.setProperty(SynapseConstants.ERROR_DETAIL, e.getMessage());
        messageContext.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
    }

    public static JsonObject buildSuccessResponse(MessageId messageId) {

        // Create a new JSON payload
        JsonObject resultJson = new JsonObject();

        // Add the basic success information
        resultJson.addProperty("success", "true");
        resultJson.addProperty("messageId", messageId.toString());

        return resultJson;
    }

    public static JsonObject buildErrorResponse(MessageContext messageContext, Throwable e, Error error) {

        // Create a new JSON payload
        JsonObject resultJson = new JsonObject();

        // Add the basic success information
        resultJson.addProperty("success", "false");

        JsonObject errorJson = new JsonObject();

        setErrorPropertiesToMessageContext(messageContext, e, error);

        errorJson.addProperty("code", error.getErrorCode());
        errorJson.addProperty("message", error.getErrorMessage());
        errorJson.addProperty("detail", e.getMessage());

        resultJson.add("error", errorJson);

        return resultJson;
    }

    /**
     * Mask the password of the connection url with ***
     *
     * @param url the actual url
     * @return the masked url
     */
    public static String maskURLPassword(String url) {

        final Matcher urlMatcher = URL_PATTERN.matcher(url);
        String maskUrl;
        if (urlMatcher.find()) {
            final Matcher pwdMatcher = PASSWORD_PATTERN.matcher(url);
            maskUrl = pwdMatcher.replaceFirst(":***@");
            return maskUrl;
        }
        return url;
    }
}
