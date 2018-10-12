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
import org.kitodo.mediaserver.core.actions.PreproduceDerivativesAction;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.conversion.SimpleIMSingleFileConverter;
import org.kitodo.mediaserver.core.processors.PatternExtractor;
import org.kitodo.mediaserver.core.processors.ScalingWatermarker;
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
@ComponentScan({"org.kitodo.mediaserver.core", "org.kitodo.mediaserver.local"})
public class ImporterConfiguration {

    private static final String METS_READER_CONCAT_SEPARATOR = " ; ";

    @Autowired
    private ImporterProperties importerProperties;

    @Autowired
    private ConversionProperties conversionProperties;

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


    /*
     * The section below defines the beans you need for preproducing files at import.
     * TODO Some of the beans are duplicates from the fileserver configuration; these should in a later step be moved
     * to a core configuration
     */
    @Bean
    public IMetsReader masterFileMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/masterFileFromMets.xsl"));
        return xsltMetsReader;
    }

    @Bean
    public IMetsReader requestUrlsMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/fileGrpRequestUrlsFromMets.xsl"));
        return xsltMetsReader;
    }

    @Bean
    public IReadResultParser listToMapParser() {
        SimpleList2MapParser parser = new SimpleList2MapParser();
        parser.setMapSeparator(":");
        parser.setValueConcatSeparator(METS_READER_CONCAT_SEPARATOR);
        return parser;
    }

    @Bean
    public IExtractor patternExtractor() {
        PatternExtractor patternExtractor = new PatternExtractor();
        patternExtractor.setRegexList(conversionProperties.getPathExtractionPatterns());
        return patternExtractor;
    }

    @Bean
    public IWatermarker scalingWatermarker() {
        return new ScalingWatermarker();
    }

    @Bean
    public IConverter preproduceFileConverter() {
        SimpleIMSingleFileConverter simpleIMSingleFileConverter = new SimpleIMSingleFileConverter();
        simpleIMSingleFileConverter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        simpleIMSingleFileConverter.setSaveConvertedFile(true);
        simpleIMSingleFileConverter.setWatermarker(scalingWatermarker());
        return simpleIMSingleFileConverter;
    }

    @Bean
    public IAction preproduceSingleFileAction() throws IOException {
        SingleFileConvertAction singleFileConvertAction = new SingleFileConvertAction();
        singleFileConvertAction.setMetsReader(masterFileMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.setConverter(preproduceFileConverter());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    @Bean(name = "preproduceDerivativesAction")
    public IAction preproduceDerivativesAction() throws IOException {
        PreproduceDerivativesAction preproduceDerivativesAction = new PreproduceDerivativesAction();
        preproduceDerivativesAction.setConvertAction(preproduceSingleFileAction());
        preproduceDerivativesAction.setReadResultParser(listToMapParser());
        preproduceDerivativesAction.setValueConcatSeparator(METS_READER_CONCAT_SEPARATOR);
        preproduceDerivativesAction.setMetsReader(requestUrlsMetsReader());
        return preproduceDerivativesAction;
    }

}
