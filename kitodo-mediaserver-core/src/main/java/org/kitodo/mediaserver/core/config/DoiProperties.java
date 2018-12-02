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
 * Configuration class for the doi properties.
 */
@Configuration
@ConfigurationProperties(prefix = "doi")
public class DoiProperties {

    private String dataCiteURL;
    private String dataCiteUser;
    private String dataCitePassword;
    private String doiLandingPagePattern;
    private String doiDataReaderXsl;

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

    public String getDoiLandingPagePattern() {
        return doiLandingPagePattern;
    }

    public void setDoiLandingPagePattern(String doiLandingPagePattern) {
        this.doiLandingPagePattern = doiLandingPagePattern;
    }

    public String getDoiDataReaderXsl() {
        return doiDataReaderXsl;
    }

    public void setDoiDataReaderXsl(String doiDataReaderXsl) {
        this.doiDataReaderXsl = doiDataReaderXsl;
    }
}
