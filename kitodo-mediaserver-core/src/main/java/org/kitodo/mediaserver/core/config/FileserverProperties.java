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
 * Configuration class for the fileserver properties.
 */
@Configuration
@ConfigurationProperties(prefix = "fileserver")
public class FileserverProperties {

    private boolean caching;
    private String cachePath;
    private String cacheClearCron;
    private Long cacheClearSince;
    private String filePathPattern;
    private String rootUrl;
    private String masterFileReaderXsl;

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getCacheClearCron() {
        return cacheClearCron;
    }

    public void setCacheClearCron(String cacheClearCron) {
        this.cacheClearCron = cacheClearCron;
    }

    public Long getCacheClearSince() {
        return cacheClearSince;
    }

    public void setCacheClearSince(Long cacheClearSince) {
        this.cacheClearSince = cacheClearSince;
    }

    public String getFilePathPattern() {
        return filePathPattern;
    }

    public void setFilePathPattern(String filePathPattern) {
        this.filePathPattern = filePathPattern;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getMasterFileReaderXsl() {
        return masterFileReaderXsl;
    }

    public void setMasterFileReaderXsl(String masterFileReaderXsl) {
        this.masterFileReaderXsl = masterFileReaderXsl;
    }
}
