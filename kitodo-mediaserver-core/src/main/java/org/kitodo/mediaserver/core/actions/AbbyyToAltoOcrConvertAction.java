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
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IOcrConverter;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.conversion.FileEntry;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An action to convert all OCR files of a work from ABBYY Finereader format to ALTO format.
 */
public class AbbyyToAltoOcrConvertAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbbyyToAltoOcrConvertAction.class);

    private MediaServerUtils mediaServerUtils;
    private MetsProperties metsProperties;
    private IMetsReader metsReader;
    private IReadResultParser readResultParser;
    private IOcrConverter ocrConverter;

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
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

    public void setOcrConverter(IOcrConverter ocrConverter) {
        this.ocrConverter = ocrConverter;
    }

    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        File metsFile = mediaServerUtils.getMetsFileForWork(work);

        // Get all OCR files of this work
        List<String> lines = metsReader.read(
            metsFile,
            new AbstractMap.SimpleEntry<>("sourceGrpId", metsProperties.getOriginalFileGrp()),
            new AbstractMap.SimpleEntry<>("fulltextGrpId", metsProperties.getFulltextFileGrp())
        );
        Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);
        TreeMap<Integer, Map<String, FileEntry>> pages = mediaServerUtils.parseMetsFilesResult(metsResult, work);

        // Convert every single OCR file
        pages.values().forEach(page -> {
            FileEntry fileEntry = page.get("fulltext");
            if (fileEntry != null) {
                try {
                    Path file = fileEntry.getFile().toPath();
                    ocrConverter.convert(file, file);
                } catch (Exception ex) {
                    LOGGER.error("Could not convert OCR file '" + fileEntry + "'.", ex);
                }
            }
        });

        return null;
    }
}
