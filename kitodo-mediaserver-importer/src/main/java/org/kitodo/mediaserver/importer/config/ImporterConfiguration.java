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

import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.processors.WorkDataReader;
import org.kitodo.mediaserver.importer.validators.FileOccurrenceValidation;
import org.kitodo.mediaserver.importer.validators.ImportDataAndFilesValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring configuration of the importer module.
 */
@Configuration
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@ComponentScan("org.kitodo.mediaserver.core")
public class ImporterConfiguration {

    @Autowired
    private ImporterProperties importerProperties;

    @Bean
    public IMetsValidation fileOccurrenceValidaton() {
        FileOccurrenceValidation fileOccurrenceValidation = new FileOccurrenceValidation();
        fileOccurrenceValidation.setMetsUrlReader(fileUrlReader());
        return fileOccurrenceValidation;
    }

    @Bean
    public IImportValidation importValidation() {
        IImportValidation importValidation = new ImportDataAndFilesValidation();
        ((ImportDataAndFilesValidation) importValidation).setFileOccurenceValidation(fileOccurrenceValidaton());
        return importValidation;
    }

    @Bean
    public IMetsReader fileUrlReader() {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource(importerProperties.getFileUrlReaderXsl()));
        return xsltMetsReader;
    }

    @Bean
    public IDataReader workDataReader() {
        WorkDataReader workDataReader = new WorkDataReader();
        workDataReader.setMetsReader(workDataMetsReader());
        workDataReader.setReadResultParser(workDataResultParser());
        return workDataReader;
    }

    @Bean
    public IMetsReader workDataMetsReader() {
        XsltMetsReader workDataMetsReader = new XsltMetsReader();
        workDataMetsReader.setXslt(new ClassPathResource(importerProperties.getWorkDataReaderXsl()));
        return workDataMetsReader;
    }

    @Bean
    public IReadResultParser workDataResultParser() {
        SimpleList2MapParser workDataResultParser = new SimpleList2MapParser();
        workDataResultParser.setMapSeparator(":");
        return workDataResultParser;
    }

}
