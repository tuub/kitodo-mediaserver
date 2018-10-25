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

import java.io.IOException;
import org.kitodo.mediaserver.core.actions.FullPDFConvertAction;
import org.kitodo.mediaserver.core.actions.PreproduceDerivativesAction;
import org.kitodo.mediaserver.core.actions.PreproduceFullPDFAction;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IFullConverter;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.conversion.SimpleIMFullPDFConverter;
import org.kitodo.mediaserver.core.conversion.SimpleIMSingleFileConverter;
import org.kitodo.mediaserver.core.processors.AppendingWatermarker;
import org.kitodo.mediaserver.core.processors.PatternExtractor;
import org.kitodo.mediaserver.core.processors.ScalingWatermarker;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Bean configurations for conversions, used by fileserver, importer and ui.
 */
@Configuration
public class ConversionConfiguration {

    private static final String METS_READER_CONCAT_SEPARATOR = " ; ";

    @Autowired
    private ConversionProperties conversionProperties;

    @Autowired
    private ImporterProperties importerProperties;

    @Autowired
    private FileserverProperties fileserverProperties;

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
    public IMetsReader allMasterFilesMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/allMasterFilesFromMets.xsl"));
        return xsltMetsReader;
    }

    @Bean
    public IMetsReader fullPdfUrlMetsreader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/fullPdfUrlFromMets.xsl"));
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
    public IWatermarker appendingWatermarker() {
        return new AppendingWatermarker();
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

    /**
     * An action bean for converting a work to a full PDF (for preproduction).
     *
     * @return the bean
     * @throws Exception by severe errors
     */
    @Bean
    public IAction preproduceFullPDFConvertAction() throws Exception {
        FullPDFConvertAction fullPDFConvertAction = new FullPDFConvertAction();
        fullPDFConvertAction.setMetsReader(allMasterFilesMetsReader());
        fullPDFConvertAction.setReadResultParser(listToMapParser());
        fullPDFConvertAction.setConverter(preprduceFullPDFConverter());
        return fullPDFConvertAction;
    }

    /**
     * An action bean for preproducing full PDF.
     *
     * @return the bean
     * @throws Exception by severe errors
     */
    @Bean
    public IAction preproduceFullPDFAction() throws Exception {
        PreproduceFullPDFAction preproduceFullPDFAction = new PreproduceFullPDFAction();
        preproduceFullPDFAction.setConvertAction(preproduceFullPDFConvertAction());
        preproduceFullPDFAction.setMetsReader(fullPdfUrlMetsreader());
        preproduceFullPDFAction.setReadResultParser(listToMapParser());
        return preproduceFullPDFAction;
    }

    /**
     * An action bean for preproducing derivatives. My be configured to be used in the importer or the ui.
     *
     * @return the bean
     * @throws IOException by severe errors
     */
    @Bean(name = "preproduceDerivativesAction")
    public IAction preproduceDerivativesAction() throws IOException {
        PreproduceDerivativesAction preproduceDerivativesAction = new PreproduceDerivativesAction();
        preproduceDerivativesAction.setConvertAction(preproduceSingleFileAction());
        preproduceDerivativesAction.setReadResultParser(listToMapParser());
        preproduceDerivativesAction.setValueConcatSeparator(METS_READER_CONCAT_SEPARATOR);
        preproduceDerivativesAction.setMetsReader(requestUrlsMetsReader());
        return preproduceDerivativesAction;
    }

    /**
     * A conversion action bean to be used by the fileserver if a file is not present.
     * Uses the scaling watermarker.
     *
     * @return a ConversionAction
     */
    @Bean(name = "scalingWatermarkingConvertAction")
    public IAction scalingConversionAction() throws Exception {
        SingleFileConvertAction singleFileConvertAction = new SingleFileConvertAction();
        singleFileConvertAction.setMetsReader(masterFileMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.setConverter(singleFileOnDemandConverterScalingWatermarker());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    /**
     * A conversion action bean to be used by the fileserver if a file is not present.
     * Uses the appendingwatermarker.
     *
     * @return a ConversionAction
     */
    @Bean(name = "appendingWatermarkingConvertAction")
    public IAction appendingConversionAction() throws Exception {
        SingleFileConvertAction singleFileConvertAction = new SingleFileConvertAction();
        singleFileConvertAction.setMetsReader(masterFileMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.setConverter(singleFileOnDemandConverterAppendingWatermarker());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    /**
     * An action bean for converting a work to a full PDF on demand.
     *
     * @return the bean
     * @throws Exception by severe errors
     */
    @Bean
    public IAction fullPDFConvertAction() throws Exception {
        FullPDFConvertAction fullPDFConvertAction = new FullPDFConvertAction();
        fullPDFConvertAction.setConverter(fullPDFConverter());
        fullPDFConvertAction.setMetsReader(allMasterFilesMetsReader());
        fullPDFConvertAction.setReadResultParser(listToMapParser());
        return fullPDFConvertAction;
    }

    /**
     * A single file converter for on-demand-conversions. Uses caching according to the configurations.
     * This bean uses the scaling watermarker.
     *
     * @return the converter
     */
    @Bean
    public IConverter singleFileOnDemandConverterScalingWatermarker() {
        SimpleIMSingleFileConverter simpleIMSingleFileConverter = new SimpleIMSingleFileConverter();
        simpleIMSingleFileConverter.setConversionTargetPath(fileserverProperties.getCachePath());
        simpleIMSingleFileConverter.setSaveConvertedFile(fileserverProperties.isCaching());
        simpleIMSingleFileConverter.setWatermarker(scalingWatermarker());
        return simpleIMSingleFileConverter;
    }

    /**
     * A single file converter for on-demand-conversions. Uses caching according to the configurations.
     * This bean uses the appending watermarker.
     *
     * @return the converter
     */
    @Bean
    public IConverter singleFileOnDemandConverterAppendingWatermarker() {
        SimpleIMSingleFileConverter simpleIMSingleFileConverter = new SimpleIMSingleFileConverter();
        simpleIMSingleFileConverter.setConversionTargetPath(fileserverProperties.getCachePath());
        simpleIMSingleFileConverter.setSaveConvertedFile(fileserverProperties.isCaching());
        simpleIMSingleFileConverter.setWatermarker(appendingWatermarker());
        return simpleIMSingleFileConverter;
    }

    /**
     * A converter bean for converting a work to a full PDF on demand.
     *
     * @return the bean
     * @throws Exception by severe errors
     */
    @Bean
    public IFullConverter fullPDFConverter() {
        SimpleIMFullPDFConverter simpleIMFullPDFConverter = new SimpleIMFullPDFConverter();
        simpleIMFullPDFConverter.setConversionTargetPath(fileserverProperties.getCachePath());
        simpleIMFullPDFConverter.setSaveConvertedFile(fileserverProperties.isCaching());
        return simpleIMFullPDFConverter;
    }

    /**
     * A converter bean for converting a work to a full PDF (for preproduction).
     *
     * @return the bean
     * @throws Exception by severe errors
     */
    @Bean
    public IFullConverter preprduceFullPDFConverter() {
        SimpleIMFullPDFConverter simpleIMFullPDFConverter = new SimpleIMFullPDFConverter();
        simpleIMFullPDFConverter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        simpleIMFullPDFConverter.setSaveConvertedFile(true);
        return simpleIMFullPDFConverter;
    }


}
