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
 * Configuration class for the indexing properties.
 */
@Configuration
@ConfigurationProperties(prefix = "indexing")
public class IndexingProperties {

    private String indexScriptUrl;
    private String indexScriptMetsUrlArgName;

    public String getIndexScriptUrl() {
        return indexScriptUrl;
    }

    public void setIndexScriptUrl(String indexScriptUrl) {
        this.indexScriptUrl = indexScriptUrl;
    }

    public String getIndexScriptMetsUrlArgName() {
        return indexScriptMetsUrlArgName;
    }

    public void setIndexScriptMetsUrlArgName(String indexScriptMetsUrlArgName) {
        this.indexScriptMetsUrlArgName = indexScriptMetsUrlArgName;
    }
}
