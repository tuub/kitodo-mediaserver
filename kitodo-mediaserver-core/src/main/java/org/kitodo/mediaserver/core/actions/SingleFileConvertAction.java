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

package org.kitodo.mediaserver.core.actions;

import java.io.File;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ConversionException;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class defining the convert action of a single file.
 */
public class SingleFileConvertAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileConvertAction.class);

    private FileserverProperties fileserverProperties;
    private MetsProperties metsProperties;
    private IMetsReader metsReader;
    private IReadResultParser readResultParser;
    private IConverter converter;
    private IExtractor patternExtractor;
    private MediaServerUtils mediaServerUtils;

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    @Autowired
    public void setMetsProperties(MetsProperties metsProperties) {
        this.metsProperties = metsProperties;
    }

    public void setMetsReader(IMetsReader metsReader) {
        this.metsReader = metsReader;
    }

    public void setReadResultParser(IReadResultParser readResultParser) {
        this.readResultParser = readResultParser;
    }

    public void setConverter(IConverter converter) {
        this.converter = converter;
    }

    public void setPatternExtractor(IExtractor patternExtractor) {
        this.patternExtractor = patternExtractor;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    /**
     * Performes a conversion of the requested url a given work.
     *
     * @param work the work entity
     * @param parameter conversion parameter
     * @return an input stream with the conversion or null
     * @throws Exception if anything goes wrong
     */
    public InputStream perform(Work work, Map<String, String> parameter) throws Exception {

        mediaServerUtils.checkForRequiredParameter(parameter, "requestUrl");
        String requestUrl = parameter.get("requestUrl");

        File metsFile = mediaServerUtils.getMetsFileForWork(work);

        /* Gets the path of the original file for the requested file from the mets file */
        List<String> lines = metsReader.read(
                metsFile,
                new AbstractMap.SimpleEntry<>("request_url", requestUrl),
                new AbstractMap.SimpleEntry<>("original_id", metsProperties.getOriginalFileGrpSuffix())
        );
        Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);

        String sourceUrl = metsResult.get("source_url");
        if (StringUtils.isEmpty(sourceUrl)) {
            throw new ConversionException("No source url for requested url " + requestUrl
                    + " found in mets file " + metsFile.getAbsolutePath());
        }

        File sourceFile = mediaServerUtils.getWorkFileFromUrl(work, sourceUrl, fileserverProperties.getRootUrl());

        String size = patternExtractor.extract(requestUrl);
        parameter.put("size", size);

        LOGGER.info("Converting file from master " + sourceUrl);

        return converter.convert(sourceFile, parameter);
    }
}
