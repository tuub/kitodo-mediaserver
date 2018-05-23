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

import java.io.File;
import java.io.IOException;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.kitodo.mediaserver.importer.validators.FileOccurenceValidation;
import org.kitodo.mediaserver.importer.validators.RecordIdentifierValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.ResourceUtils;

/**
 * Spring configuration of the importer module.
 */
@Configuration
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
public class ImporterConfiguration {

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public ImporterUtils importerUtils() {
        return new ImporterUtils();
    }

    @Bean
    public MediaServerUtils mediaServerUtils() {
        return new MediaServerUtils();
    }

    @Bean
    public IMetsValidation fileOccurenceValidator() throws IOException {
        FileOccurenceValidation fileOccurenceValidation = new FileOccurenceValidation();
        fileOccurenceValidation.setMetsUrlReader(fileUrlReader());
        return fileOccurenceValidation;
    }

    @Bean
    public IMetsValidation recordIdentifierValidator() throws IOException {
        RecordIdentifierValidation recordIdentifierValidation = new RecordIdentifierValidation();
        recordIdentifierValidation.setRecordIdentifierReader(recordIdentifierReader());
        return recordIdentifierValidation;
    }

    @Bean
    public IMetsReader fileUrlReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        File xslt = ResourceUtils.getFile("classpath:xslt/getPathsFromGivenFileGrp.xsl");
        xsltMetsReader.setXslt(xslt);
        return xsltMetsReader;
    }

    @Bean
    public IMetsReader recordIdentifierReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        File xslt = ResourceUtils.getFile("classpath:xslt/getRecordIdentifier.xsl");
        xsltMetsReader.setXslt(xslt);
        return xsltMetsReader;
    }

}
