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

package org.kitodo.mediaserver.fileserver.config;

import java.io.IOException;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.conversion.SimpleIMSingleFileConverter;
import org.kitodo.mediaserver.core.processors.AppendingWatermarker;
import org.kitodo.mediaserver.core.processors.PatternExtractor;
import org.kitodo.mediaserver.core.processors.ScalingWatermarker;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Spring configuration of the fileserver module.
 */
@Configuration
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@ComponentScan({"org.kitodo.mediaserver.core", "org.kitodo.mediaserver.local"})
public class FileserverConfiguration {

    @Autowired
    private FileserverProperties fileserverProperties;

    @Autowired
    private ConversionProperties conversionProperties;

    /**
     * A mets reader getting the master file for a given derivate.
     *
     * @return the mets reader
     */
    @Bean
    public IMetsReader masterFileMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource(fileserverProperties.getMasterFileReaderXsl()));
        return xsltMetsReader;
    }

    /**
     * A list2map parser using colon as separator.
     *
     * @return the parser
     */
    @Bean
    public IReadResultParser listToMapParser() {
        SimpleList2MapParser parser = new SimpleList2MapParser();
        parser.setMapSeparator(":");
        return parser;
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
     * Pattern extractor for jpeg paths.
     * @return the bean
     */
    @Bean
    public IExtractor patternExtractor() {
        PatternExtractor patternExtractor = new PatternExtractor();
        patternExtractor.setRegexList(conversionProperties.getPathExtractionPatterns());

        return patternExtractor;
    }

    /**
     * The media server utilities.
     * @return the bean
     */
    @Bean
    public MediaServerUtils mediaServerUtils() {
        return new MediaServerUtils();
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

    @Bean
    public IWatermarker scalingWatermarker() {
        return new ScalingWatermarker();
    }

    @Bean
    public IWatermarker appendingWatermarker() {
        return new AppendingWatermarker();
    }

}
