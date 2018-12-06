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

    private String originalFileGrp;
    private String fulltextFileGrp;
    private String downloadFileGrp;
    private String workLockReduceMetsXsl;

    public String getOriginalFileGrp() {
        return originalFileGrp;
    }

    public void setOriginalFileGrp(String originalFileGrp) {
        this.originalFileGrp = originalFileGrp;
    }

    public String getFulltextFileGrp() {
        return fulltextFileGrp;
    }

    public void setFulltextFileGrp(String fulltextFileGrp) {
        this.fulltextFileGrp = fulltextFileGrp;
    }

    public String getDownloadFileGrp() {
        return downloadFileGrp;
    }

    public void setDownloadFileGrp(String downloadFileGrp) {
        this.downloadFileGrp = downloadFileGrp;
    }

    public String getWorkLockReduceMetsXsl() {
        return workLockReduceMetsXsl;
    }

    public void setWorkLockReduceMetsXsl(String workLockReduceMetsXsl) {
        this.workLockReduceMetsXsl = workLockReduceMetsXsl;
    }
}
