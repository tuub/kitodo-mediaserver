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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.api.ITocReader;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.conversion.FileEntry;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ConversionException;
import org.kitodo.mediaserver.core.processors.Toc;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * An action to convert all master images to one PDF file.
 */
public class StandaloneFullPdfFileConvertAction implements IAction {

    private MetsProperties metsProperties;
    private IMetsReader metsReader;
    private IMetsReader fullPdfMetsReader;
    private ITocReader tocReader;
    private IReadResultParser readResultParser;
    private Map<String, IConverter> converters = new HashMap<>();
    private MediaServerUtils mediaServerUtils;

    @Autowired
    public void setMetsProperties(MetsProperties metsProperties) {
        this.metsProperties = metsProperties;
    }

    public void setMetsReader(IMetsReader metsReader) {
        this.metsReader = metsReader;
    }

    public void setTocReader(ITocReader tocReader) {
        this.tocReader = tocReader;
    }

    public void setReadResultParser(IReadResultParser readResultParser) {
        this.readResultParser = readResultParser;
    }

    public void setFullPdfReader(IMetsReader fullPdfMetsReader) {
        this.fullPdfMetsReader = fullPdfMetsReader;
    }

    public Map<String, IConverter> getConverters() {
        return converters;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    /**
     * Performes a conversion of a given work.
     *
     * @param work the work entity
     * @param parameter conversion parameter
     * @return an input stream with the conversion or null
     * @throws Exception if anything goes wrong
     */
    public InputStream perform(Work work, Map<String, String> parameter) throws Exception {

        if (parameter == null) {
            parameter = new HashMap<>();
        }

        File metsFile = mediaServerUtils.getMetsFileForWork(work);
        String workIdRegex = ".*?(" + Pattern.quote(work.getId()) + "/.*)";

        List<String> lines = fullPdfMetsReader.read(metsFile,
            new AbstractMap.SimpleEntry<>("downloadGrpId", metsProperties.getDownloadFileGrp()));
        Map<String, String> fullPdfResult = (Map<String, String>) readResultParser.parse(lines);
        String fullPdfUrl = fullPdfResult.get("fullPdfUrl");

        if (!StringUtils.hasText(fullPdfUrl)) {
            throw new Exception("No full PDF file URL found in METS file.");
        }

        parameter.put("derivativePath", fullPdfUrl.replaceFirst(workIdRegex, "$1"));

        /* Gets the path of the original file for the requested file from the mets file */
        lines = metsReader.read(
            metsFile,
            new AbstractMap.SimpleEntry<>("sourceGrpId", metsProperties.getOriginalFileGrp()),
            new AbstractMap.SimpleEntry<>("fulltextGrpId", metsProperties.getFulltextFileGrp())
        );

        final Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);
        final TreeMap<Integer, Map<String, FileEntry>> pages = mediaServerUtils.parseMetsFilesResult(metsResult, work);
        final Toc toc = tocReader.read(metsFile.toPath());

        parameter.put("target_mime", "application/pdf");

        Map<String, Object> convertParams = new HashMap<>(parameter);
        convertParams.put("toc", toc);

        IConverter converter = converters.get(parameter.get("target_mime"));
        if (converter == null) {
            throw new ConversionException("No converter set for MIME type '" + parameter.getOrDefault("target_mime", "") + "'");
        }

        return converter.convert(pages, convertParams);
    }
}
