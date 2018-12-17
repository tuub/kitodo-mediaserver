/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for identifier properties.
 */
@Configuration
@ConfigurationProperties(prefix = "identifier")
public class IdentifierProperties {

    private String dataCiteURL;
    private String dataCiteUser;
    private String dataCitePassword;
    private String landingPagePattern;
    private String doiDataReaderXsl;
    private String replacementRegex;
    private String replacement;

    public String getDataCiteURL() {
        return dataCiteURL;
    }

    public void setDataCiteURL(String dataCiteURL) {
        this.dataCiteURL = dataCiteURL;
    }

    public String getDataCiteUser() {
        return dataCiteUser;
    }

    public void setDataCiteUser(String dataCiteUser) {
        this.dataCiteUser = dataCiteUser;
    }

    public String getDataCitePassword() {
        return dataCitePassword;
    }

    public void setDataCitePassword(String dataCitePassword) {
        this.dataCitePassword = dataCitePassword;
    }

    public String getLandingPagePattern() {
        return landingPagePattern;
    }

    public void setLandingPagePattern(String landingPagePattern) {
        this.landingPagePattern = landingPagePattern;
    }

    public String getDoiDataReaderXsl() {
        return doiDataReaderXsl;
    }

    public void setDoiDataReaderXsl(String doiDataReaderXsl) {
        this.doiDataReaderXsl = doiDataReaderXsl;
    }

    public String getReplacementRegex() {
        return replacementRegex;
    }

    public void setReplacementRegex(String replacementRegex) {
        this.replacementRegex = replacementRegex;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
}
