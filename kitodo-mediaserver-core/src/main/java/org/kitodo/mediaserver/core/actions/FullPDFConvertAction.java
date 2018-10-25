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
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IFullConverter;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ConversionException;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class defining the convert action of a single file.
 */
public class FullPDFConvertAction implements IAction {

    private FileserverProperties fileserverProperties;
    private MetsProperties metsProperties;
    private IMetsReader metsReader;
    private IReadResultParser readResultParser;
    private MediaServerUtils mediaServerUtils;
    private IFullConverter converter;

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

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    public void setConverter(IFullConverter converter) {
        this.converter = converter;
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

        mediaServerUtils.checkForRequiredParameter(parameter, "derivativePath");

        File metsFile = mediaServerUtils.getMetsFileForWork(work);

        /* Gets the path of the original file for the requested file from the mets file */
        List<String> lines = metsReader.read(
            metsFile,
            new AbstractMap.SimpleEntry<>("fileGrp", metsProperties.getOriginalFileGrpSuffix())
        );

        // metsResult is a mat with key=pagenumber, value=URL
        Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);

        if (metsResult == null || metsResult.isEmpty()) {
            throw new ConversionException("No source urls found in mets file " + metsFile.getAbsolutePath());
        }

        // convert pages map to a sorted map of pageNumbers and file pathes
        Map<Integer, File> pages = metsResult.entrySet().stream()
            .collect(Collectors.toMap(
                // key of Map
                entry -> Integer.parseInt(entry.getKey()),
                // value of Map
                entry -> mediaServerUtils.getWorkFileFromUrl(work, entry.getValue(), fileserverProperties.getRootUrl()),
                (oldValue, newValue) -> newValue,
                TreeMap::new
            ));

        parameter.put("target_mime", "application/pdf");

        return converter.convert(pages, parameter);
    }
}
