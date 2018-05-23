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

package org.kitodo.mediaserver.importer.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties for the importer.
 */
@Configuration
@ConfigurationProperties(prefix = "importer")
public class ImporterProperties {

    private String hotfolderPath;
    private String importingFolderPath;
    private String errorFolderPath;
    private String workIdRegex;
    private List<String> validationFileGrps;

    public String getHotfolderPath() {
        return hotfolderPath;
    }

    public void setHotfolderPath(String hotfolderPath) {
        this.hotfolderPath = hotfolderPath;
    }

    public String getImportingFolderPath() {
        return importingFolderPath;
    }

    public void setImportingFolderPath(String importingFolderPath) {
        this.importingFolderPath = importingFolderPath;
    }

    public String getErrorFolderPath() {
        return errorFolderPath;
    }

    public void setErrorFolderPath(String errorFolderPath) {
        this.errorFolderPath = errorFolderPath;
    }

    public String getWorkIdRegex() {
        return workIdRegex;
    }

    public void setWorkIdRegex(String workIdRegex) {
        this.workIdRegex = workIdRegex;
    }

    public List<String> getValidationFileGrps() {
        return validationFileGrps;
    }

    public void setValidationFileGrps(List<String> validationFileGrps) {
        this.validationFileGrps = validationFileGrps;
    }
}
