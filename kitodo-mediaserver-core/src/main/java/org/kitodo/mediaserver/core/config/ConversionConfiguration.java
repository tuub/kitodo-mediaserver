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
import org.kitodo.mediaserver.core.actions.AddFullPdfToMetsAction;
import org.kitodo.mediaserver.core.actions.PreproduceDerivativesAction;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.actions.StandaloneFullPdfFileConvertAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.kitodo.mediaserver.core.api.IOcrReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.conversion.AwtImageFileConverter;
import org.kitodo.mediaserver.core.conversion.PdfboxFileConverter;
import org.kitodo.mediaserver.core.conversion.SimpleIMSingleFileConverter;
import org.kitodo.mediaserver.core.processors.AppendingWatermarker;
import org.kitodo.mediaserver.core.processors.PatternExtractor;
import org.kitodo.mediaserver.core.processors.ScalingWatermarker;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.processors.XsltMetsTransformer;
import org.kitodo.mediaserver.core.processors.ocr.XsltOcrReader;
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
    public IMetsReader masterFilesMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/filesFromMets.xsl"));
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
    public IMetsTransformer fullPdfMetsTransformer() {
        return new XsltMetsTransformer(new ClassPathResource("xslt/fullPdfToMets.xsl"));
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
        singleFileConvertAction.setMetsReader(masterFilesMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.getConverters().put("application/pdf", preproduceFileConverter());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    /**
     * An action bean for preproducing derivatives. My be configured to be used in the importer or the ui.
     *
     * @return the bean
     * @throws IOException by severe errors
     */
    @Bean(name = "preproduceDerivativesAction")
    public IAction preproduceDerivativesAction() throws Exception {
        PreproduceDerivativesAction preproduceDerivativesAction = new PreproduceDerivativesAction();
        preproduceDerivativesAction.setConvertAction(awtPdfboxSingleFileConvertAction());
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
        singleFileConvertAction.setMetsReader(masterFilesMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.getConverters().put("application/pdf", singleFileOnDemandConverterScalingWatermarker());
        singleFileConvertAction.getConverters().put("image/jpeg", singleFileOnDemandConverterScalingWatermarker());
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
        singleFileConvertAction.setMetsReader(masterFilesMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.getConverters().put("application/pdf", singleFileOnDemandConverterAppendingWatermarker());
        singleFileConvertAction.getConverters().put("image/jpeg", singleFileOnDemandConverterAppendingWatermarker());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    /**
     * An action bean to add an METS entry for full PDF download to be used after import.
     *
     * @return the action
     */
    @Bean
    public IAction addFullPdfToMetsAction() {
        return new AddFullPdfToMetsAction(fullPdfMetsTransformer());
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
     * A converter to convert master images to target image files.
     */
    @Bean
    public IConverter awtFileConverter() {
        AwtImageFileConverter converter = new AwtImageFileConverter();
        converter.setConversionTargetPath(fileserverProperties.getCachePath());
        converter.setSaveConvertedFile(fileserverProperties.isCaching());
        return converter;
    }

    /**
     * A converter to convert master images to PDF files.
     */
    @Bean
    public IConverter pdfboxFileConverter() {
        PdfboxFileConverter converter = new PdfboxFileConverter();
        converter.setConversionTargetPath(fileserverProperties.getCachePath());
        converter.setSaveConvertedFile(fileserverProperties.isCaching());
        return converter;
    }

    /**
     * A converter to preproduce PDF files.
     */
    @Bean
    public IConverter preproducePdfboxFileConverter() {
        PdfboxFileConverter converter = new PdfboxFileConverter();
        converter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        converter.setSaveConvertedFile(true);
        return converter;
    }

    /**
     * A convert action to convert a single master file to a target file type using PDFBox for PDF and AWT for images.
     */
    @Bean(name = "awtPdfboxSingleFileConvertAction")
    public IAction awtPdfboxSingleFileConvertAction() throws Exception {
        SingleFileConvertAction convertAction = new SingleFileConvertAction();
        convertAction.setMetsReader(masterFilesMetsReader());
        convertAction.setReadResultParser(listToMapParser());
        convertAction.getConverters().put("image/jpeg", awtFileConverter());
        convertAction.getConverters().put("application/pdf", pdfboxFileConverter());
        convertAction.setPatternExtractor(patternExtractor());
        return convertAction;
    }

    /**
     * Preproduce a PDF file with all pages.
     */
    @Bean(name = "preproduceFullPdfFileConvertAction")
    public IAction preproduceFullPdfFileConvertAction() throws Exception {
        StandaloneFullPdfFileConvertAction convertAction = new StandaloneFullPdfFileConvertAction();
        convertAction.setMetsReader(masterFilesMetsReader());
        convertAction.setFullPdfReader(fullPdfUrlMetsreader());
        convertAction.setReadResultParser(listToMapParser());
        convertAction.getConverters().put("application/pdf", preproducePdfboxFileConverter());
        return convertAction;
    }

    /**
     * A reader using XSLT to read the fulltext OCR file.
     */
    @Bean(name = "ocrReader")
    public IOcrReader xsltOcrReader() {
        XsltOcrReader ocrReader = new XsltOcrReader();
        ocrReader.getFormats().put("<alto ", new ClassPathResource("xslt/ocr/alto.xsl"));
        return ocrReader;
    }

    /**
     * A reader using XSLT to get the fulltext URL from METS file.
     */
    @Bean
    public IMetsReader fullPdfUrlMetsreader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/fullPdfUrlFromMets.xsl"));
        return xsltMetsReader;
    }



}
