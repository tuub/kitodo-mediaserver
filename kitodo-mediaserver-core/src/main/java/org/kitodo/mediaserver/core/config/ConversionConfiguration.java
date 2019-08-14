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

import org.kitodo.mediaserver.core.actions.AbbyyToAltoOcrConvertAction;
import org.kitodo.mediaserver.core.actions.AddFullPdfToMetsAction;
import org.kitodo.mediaserver.core.actions.PreproduceDerivativesAction;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.actions.StandaloneFullPdfFileConvertAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.kitodo.mediaserver.core.api.IOcrConverter;
import org.kitodo.mediaserver.core.api.IOcrReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.ITocReader;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.conversion.AwtImageFileConverter;
import org.kitodo.mediaserver.core.conversion.PdfboxFileConverter;
import org.kitodo.mediaserver.core.conversion.SimpleIMSingleFileConverter;
import org.kitodo.mediaserver.core.conversion.ocr.AbbyyToAltoOcrConverter;
import org.kitodo.mediaserver.core.processors.AppendingWatermarker;
import org.kitodo.mediaserver.core.processors.PatternExtractor;
import org.kitodo.mediaserver.core.processors.ScalingWatermarker;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.processors.XsltMetsTocReader;
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
    public IMetsReader masterFilesMetsReader() {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/filesFromMets.xsl"));
        return xsltMetsReader;
    }

    @Bean
    public IMetsReader requestUrlsMetsReader() {
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

    /**
     * A reader using XSLT to read the fulltext OCR file.
     *
     * @return the reader
     */
    @Bean(name = "ocrReader")
    public IOcrReader xsltOcrReader() {
        XsltOcrReader ocrReader = new XsltOcrReader();
        ocrReader.getFormats().put("<alto ", new ClassPathResource("xslt/ocr/alto.xsl"));
        return ocrReader;
    }

    /**
     * A reader using XSLT to read the table of content from a METS file.
     *
     * @return the reader
     */
    @Bean(name = "tocReader")
    public ITocReader xsltMetsTocReader() {
        XsltMetsTocReader tocReader = new XsltMetsTocReader();
        tocReader.setXslt(new ClassPathResource("xslt/tocFromMets.xsl"));
        return tocReader;
    }

    /**
     * A reader using XSLT to get the fulltext URL from METS file.
     *
     * @return the reader
     */
    @Bean
    public IMetsReader fullPdfUrlMetsreader() {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource("xslt/fullPdfUrlFromMets.xsl"));
        return xsltMetsReader;
    }

    /**
     * An ocr converter to convert an OCR file from ABBYY Finereader format to ALTO format.
     *
     * @return the converter
     */
    @Bean
    public IOcrConverter abbyyToAltoOcrConverter() {
        return new AbbyyToAltoOcrConverter();
    }


    /*
     * FILE CONVERTERS
     */

    /**
     * A single file converter for preproduction of images or pdf using imagemagick
     * and the scaling watermarker, since it produces better results than the appending watermarker.
     *
     * @return the converter
     */
    @Bean
    public IConverter preproduceIMConverterScalingWatermarker() {
        SimpleIMSingleFileConverter simpleIMSingleFileConverter = new SimpleIMSingleFileConverter();
        simpleIMSingleFileConverter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        simpleIMSingleFileConverter.setSaveConvertedFile(true);
        simpleIMSingleFileConverter.setWatermarker(scalingWatermarker());
        return simpleIMSingleFileConverter;
    }

    /**
     * A single file converter for on-demand-conversions using imagemagick. Saves the file on the caching path.
     * This bean uses the appending watermarker since it is faster than the scaling watermarker.
     *
     * @return the converter
     */
    @Bean
    public IConverter onDemandIMConverterAppendingWatermarker() {
        SimpleIMSingleFileConverter simpleIMSingleFileConverter = new SimpleIMSingleFileConverter();
        simpleIMSingleFileConverter.setConversionTargetPath(fileserverProperties.getCachePath());
        simpleIMSingleFileConverter.setSaveConvertedFile(fileserverProperties.isCaching());
        simpleIMSingleFileConverter.setWatermarker(appendingWatermarker());
        return simpleIMSingleFileConverter;
    }

    /**
     * A single file image converter for preproduction using Java AWT.
     *
     * @return the converter
     */
    @Bean
    public IConverter preproduceAwtFileConverter() {
        AwtImageFileConverter converter = new AwtImageFileConverter();
        converter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        converter.setSaveConvertedFile(true);
        return converter;
    }

    /**
     * A single file image converter for on-demand-conversion using Java AWT. Saves the file on the caching path.
     *
     * @return the converter
     */
    @Bean
    public IConverter onDemandAwtFileConverter() {
        AwtImageFileConverter converter = new AwtImageFileConverter();
        converter.setConversionTargetPath(fileserverProperties.getCachePath());
        converter.setSaveConvertedFile(fileserverProperties.isCaching());
        return converter;
    }

    /**
     * A single file PDF converter for preproduction using Apache Pdfbox.
     *
     * @return the converter
     */
    @Bean
    public IConverter preproducePdfboxFileConverter() {
        PdfboxFileConverter converter = new PdfboxFileConverter();
        converter.setConversionTargetPath(importerProperties.getWorkFilesPath());
        converter.setSaveConvertedFile(true);
        return converter;
    }

    /**
     * A single file PDF converter for on-demand-conversion using Apache Pdfbox. Saves the file on the caching path.
     *
     * @return the converter
     */
    @Bean
    public IConverter onDemandPdfboxFileConverter() {
        PdfboxFileConverter converter = new PdfboxFileConverter();
        converter.setConversionTargetPath(fileserverProperties.getCachePath());
        converter.setSaveConvertedFile(fileserverProperties.isCaching());
        return converter;
    }


    /*
     * ACTIONS
     */

    /**
     * An on-demand convert action to convert a single file using Pdfbox for PDF and AWT for images.
     *
     * @return the action
     */
    @Bean(name = "onDemandAwtPdfboxSingleFileConvertAction")
    public IAction onDemandAwtPdfboxSingleFileConvertAction() {
        SingleFileConvertAction convertAction = new SingleFileConvertAction();
        convertAction.setMetsReader(masterFilesMetsReader());
        convertAction.setReadResultParser(listToMapParser());
        convertAction.getConverters().put("image/jpeg", onDemandAwtFileConverter());
        convertAction.getConverters().put("application/pdf", onDemandPdfboxFileConverter());
        convertAction.setPatternExtractor(patternExtractor());
        return convertAction;
    }

    /**
     * A conversion action using imagemagick and appending watermarker for on-demand single file conversion.
     *
     * @return a SingleFileConvertAction
     */
    @Bean(name = "onDemandIMSingleFileConvertAction")
    public IAction appendingConversionAction() {
        SingleFileConvertAction singleFileConvertAction = new SingleFileConvertAction();
        singleFileConvertAction.setMetsReader(masterFilesMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.getConverters().put("application/pdf", onDemandIMConverterAppendingWatermarker());
        singleFileConvertAction.getConverters().put("image/jpeg", onDemandIMConverterAppendingWatermarker());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }


    /**
     * An action for preproducing a single file using imagemagick. Used by the preproduceIMDerivativesAction.
     *
     * @return the action
     */
    @Bean
    public IAction preproduceIMSingleFileConvertAction() {
        SingleFileConvertAction singleFileConvertAction = new SingleFileConvertAction();
        singleFileConvertAction.setMetsReader(masterFilesMetsReader());
        singleFileConvertAction.setReadResultParser(listToMapParser());
        singleFileConvertAction.getConverters().put("application/pdf", preproduceIMConverterScalingWatermarker());
        singleFileConvertAction.getConverters().put("image/jpeg", preproduceIMConverterScalingWatermarker());
        singleFileConvertAction.setPatternExtractor(patternExtractor());
        return singleFileConvertAction;
    }

    /**
     * An on-demand convert action to convert a single file using Pdfbox for PDF and AWT for images.
     * Used by the preproduceDerivativesAction.
     *
     * @return the action
     */
    @Bean
    public IAction preproduceAwtPdfboxSingleFileConvertAction() {
        SingleFileConvertAction convertAction = new SingleFileConvertAction();
        convertAction.setMetsReader(masterFilesMetsReader());
        convertAction.setReadResultParser(listToMapParser());
        convertAction.getConverters().put("image/jpeg", preproduceAwtFileConverter());
        convertAction.getConverters().put("application/pdf", preproducePdfboxFileConverter());
        convertAction.setPatternExtractor(patternExtractor());
        return convertAction;
    }

    /**
     * An action for preproducing derivatives using a java implementation.
     * May be configured to be used in the importer or the ui.
     *
     * @return the bean
     */
    @Bean(name = "preproduceDerivativesAction")
    public IAction preproduceDerivativesAction() {
        PreproduceDerivativesAction preproduceDerivativesAction = new PreproduceDerivativesAction();
        preproduceDerivativesAction.setConvertAction(preproduceAwtPdfboxSingleFileConvertAction());
        preproduceDerivativesAction.setReadResultParser(listToMapParser());
        preproduceDerivativesAction.setValueConcatSeparator(METS_READER_CONCAT_SEPARATOR);
        preproduceDerivativesAction.setMetsReader(requestUrlsMetsReader());
        return preproduceDerivativesAction;
    }

    /**
     * An action for preproducing derivatives using imagemagick.
     * May be configured to be used in the importer or the ui.
     *
     * @return the bean
     */
    @Bean(name = "preproduceIMDerivativesAction")
    public IAction preproduceIMDerivativesAction() {
        PreproduceDerivativesAction preproduceDerivativesAction = new PreproduceDerivativesAction();
        preproduceDerivativesAction.setConvertAction(preproduceIMSingleFileConvertAction());
        preproduceDerivativesAction.setReadResultParser(listToMapParser());
        preproduceDerivativesAction.setValueConcatSeparator(METS_READER_CONCAT_SEPARATOR);
        preproduceDerivativesAction.setMetsReader(requestUrlsMetsReader());
        return preproduceDerivativesAction;
    }

    /**
     * An action for preproduction of pdf files containing a complete work.
     *
     * @return the action
     */
    @Bean(name = "preproduceFullPdfFileConvertAction")
    public IAction preproduceFullPdfFileConvertAction() {
        StandaloneFullPdfFileConvertAction convertAction = new StandaloneFullPdfFileConvertAction();
        convertAction.setMetsReader(masterFilesMetsReader());
        convertAction.setFullPdfReader(fullPdfUrlMetsreader());
        convertAction.setTocReader(xsltMetsTocReader());
        convertAction.setReadResultParser(listToMapParser());
        convertAction.getConverters().put("application/pdf", preproducePdfboxFileConverter());
        return convertAction;
    }

    /**
     * A convert action to transform every OCR file of a work from ABBYY Finereader format to ALTO format.
     */
    @Bean
    public IAction abbyyToAltoOcrConvertAction() {
        AbbyyToAltoOcrConvertAction action = new AbbyyToAltoOcrConvertAction();
        action.setMetsReader(masterFilesMetsReader());
        action.setReadResultParser(listToMapParser());
        action.setOcrConverter(abbyyToAltoOcrConverter());
        return action;
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


}
