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
 * Properties class for the mets configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "mets")
public class MetsProperties {

    private String originalFileGrpSuffix;

    public String getOriginalFileGrpSuffix() {
        return originalFileGrpSuffix;
    }

    public void setOriginalFileGrpSuffix(String originalFileGrpSuffix) {
        this.originalFileGrpSuffix = originalFileGrpSuffix;
    }
}
