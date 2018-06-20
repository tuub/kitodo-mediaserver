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

import java.io.IOException;
import org.kitodo.mediaserver.core.actions.CacheDeleteAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.util.FileDeleter;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.kitodo.mediaserver.importer.checks.JpaWorkChecker;
import org.kitodo.mediaserver.importer.processors.WorkDataReader;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.kitodo.mediaserver.importer.validators.FileOccurrenceValidation;
import org.kitodo.mediaserver.importer.validators.ImportDataAndFilesValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring configuration of the importer module.
 */
@Configuration
@Import({FileserverProperties.class})
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@ComponentScan(value = "org.kitodo.mediaserver.core.services")
public class ImporterConfiguration {

    @Autowired
    private ImporterProperties importerProperties;

    @Bean
    public ImporterUtils importerUtils() {
        return new ImporterUtils();
    }

    @Bean
    public MediaServerUtils mediaServerUtils() {
        return new MediaServerUtils();
    }

    @Bean
    public IMetsValidation fileOccurrenceValidaton() throws IOException {
        FileOccurrenceValidation fileOccurrenceValidation = new FileOccurrenceValidation();
        fileOccurrenceValidation.setMetsUrlReader(fileUrlReader());
        return fileOccurrenceValidation;
    }

    @Bean
    public IImportValidation importValidation() throws IOException {
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
    public IDataReader workDataReader() throws Exception {
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

    @Bean
    public IWorkChecker workChecker() {
        return new JpaWorkChecker();
    }

    @Bean
    public FileDeleter fileDeleter() {
        return new FileDeleter();
    }

    @Bean
    public IAction cacheDeleteaction() {
        return new CacheDeleteAction();
    }

}
