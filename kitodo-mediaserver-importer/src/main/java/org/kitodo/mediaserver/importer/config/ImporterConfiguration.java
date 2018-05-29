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

import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.importer.processors.WorkDataReader;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.kitodo.mediaserver.importer.validators.FileOccurrenceValidation;
import org.kitodo.mediaserver.importer.validators.ImportDataAndFilesValidation;

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

    /**
     * A file occurrence validator with a fileUrlReader as mets reader.
     * @return the validator
     * @throws IOException by a severe error
     */
    @Bean
    public IMetsValidation fileOccurrenceValidaton() throws IOException {
        FileOccurrenceValidation fileOccurrenceValidation = new FileOccurrenceValidation();
        fileOccurrenceValidation.setMetsUrlReader(fileUrlReader());
        return fileOccurrenceValidation;
    }

    /**
     * A validator for the imported work.
     *
     * @return the validator
     * @throws IOException by a severe error
     */
    @Bean
    public IImportValidation importValidation() throws IOException {
        IImportValidation importValidation = new ImportDataAndFilesValidation();
        ((ImportDataAndFilesValidation) importValidation).setFileOccurenceValidation(fileOccurrenceValidaton());
        return importValidation;
    }

    /**
     * A mets reader getting all files with a certain fileGrp.
     *
     * @return the mets reader
     * @throws IOException by a severe error
     */
    @Bean
    public IMetsReader fileUrlReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        File xslt = ResourceUtils.getFile("classpath*:xslt/getPathsFromGivenFileGrp.xsl");
        xsltMetsReader.setXslt(xslt);
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
    public IMetsReader workDataMetsReader() throws Exception {
        XsltMetsReader workDataMetsReader = new XsltMetsReader();
        workDataMetsReader.setXslt(ResourceUtils.getFile("classpath*:xslt/getWorkData.xsl"));
        return workDataMetsReader;
    }

    @Bean
    public IReadResultParser workDataResultParser() {
        SimpleList2MapParser workDataResultParser = new SimpleList2MapParser();
        workDataResultParser.setMapSeparator(":");
        return workDataResultParser;
    }
}
