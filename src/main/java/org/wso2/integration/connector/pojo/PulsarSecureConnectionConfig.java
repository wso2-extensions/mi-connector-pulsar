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

import java.util.Set;

public class PulsarSecureConnectionConfig extends PulsarConnectionConfig {

    private Boolean useTls;
    private Boolean tlsAllowInsecureConnection;
    private Boolean enableTlsHostnameVerification;
    private String tlsTrustCertsFilePath;
    private Set<String> tlsProtocols;
    private Set<String> tlsCiphers;
    private Boolean useKeyStoreTls;
    private String tlsTrustStorePath;
    private String tlsTrustStorePassword;
    private String tlsTrustStoreType;
    private Integer autoCertRefreshSeconds;

    public Boolean useTls() {
        return useTls;
    }

    public void setUseTls(String useTls) {
        if (StringUtils.isNotEmpty(useTls)) {
            this.useTls = Boolean.parseBoolean(useTls);
        }
    }

    public Boolean getTlsAllowInsecureConnection() {
        return tlsAllowInsecureConnection;
    }

    public void setTlsAllowInsecureConnection(String tlsAllowInsecureConnection) {
        if (StringUtils.isNotEmpty(tlsAllowInsecureConnection)) {
            this.tlsAllowInsecureConnection = Boolean.parseBoolean(tlsAllowInsecureConnection);
        }
    }

    public Boolean getEnableTlsHostnameVerification() {
        return enableTlsHostnameVerification;
    }

    public void setEnableTlsHostnameVerification(String enableTlsHostnameVerification) {
        if (StringUtils.isNotEmpty(enableTlsHostnameVerification)) {
            this.enableTlsHostnameVerification = Boolean.parseBoolean(enableTlsHostnameVerification);
        }
    }

    public String getTlsTrustCertsFilePath() {
        return tlsTrustCertsFilePath;
    }

    public void setTlsTrustCertsFilePath(String tlsTrustCertsFilePath) {
        if (StringUtils.isNotEmpty(tlsTrustCertsFilePath)) {
            this.tlsTrustCertsFilePath = tlsTrustCertsFilePath;
        }
    }

    public Set<String> getTlsProtocols() {
        return tlsProtocols;
    }

    public void setTlsProtocols(String tlsProtocols) {
        if (StringUtils.isNotEmpty(tlsProtocols)) {
            this.tlsProtocols = new java.util.HashSet<>(java.util.Arrays.asList(tlsProtocols.split(",")));
        } else {
            this.tlsProtocols = java.util.Collections.emptySet();
        }
    }

    public Set<String> getTlsCiphers() {
        return tlsCiphers;
    }

    public void setTlsCiphers(String tlsCiphers) {
        if (StringUtils.isNotEmpty(tlsCiphers)) {
            this.tlsCiphers = new java.util.HashSet<>(java.util.Arrays.asList(tlsCiphers.split(",")));
        } else {
            this.tlsCiphers = java.util.Collections.emptySet();
        }
    }

    public Boolean getUseKeyStoreTls() {
        return useKeyStoreTls;
    }

    public void setUseKeyStoreTls(String useKeyStoreTls) {
        if (StringUtils.isNotEmpty(useKeyStoreTls)) {
            this.useKeyStoreTls = Boolean.parseBoolean(useKeyStoreTls);
        }
    }

    public String getTlsTrustStorePath() {
        return tlsTrustStorePath;
    }

    public void setTlsTrustStorePath(String tlsTrustStorePath) {
        if (StringUtils.isNotEmpty(tlsTrustStorePath)) {
            this.tlsTrustStorePath = tlsTrustStorePath;
        }
    }

    public String getTlsTrustStorePassword() {
        return tlsTrustStorePassword;
    }

    public void setTlsTrustStorePassword(String tlsTrustStorePassword) {
        if (StringUtils.isNotEmpty(tlsTrustStorePassword)) {
            this.tlsTrustStorePassword = tlsTrustStorePassword;
        }
    }

    public String getTlsTrustStoreType() {
        return tlsTrustStoreType;
    }

    public void setTlsTrustStoreType(String tlsTrustStoreType) {
        if (StringUtils.isNotEmpty(tlsTrustStoreType)) {
            this.tlsTrustStoreType = tlsTrustStoreType;
        }
    }

    public Integer getAutoCertRefreshSeconds() {
        return autoCertRefreshSeconds;
    }

    public void setAutoCertRefreshSeconds(String autoCertRefreshSeconds) {
        if (StringUtils.isNotEmpty(autoCertRefreshSeconds)) {
            this.autoCertRefreshSeconds = Integer.parseInt(autoCertRefreshSeconds);
        }
    }

}
