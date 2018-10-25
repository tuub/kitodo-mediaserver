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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * An action to be used for preproducing full PDF at import.
 * This action can be called either directly using the actionsBeforeIndexing configuration
 * or ordered using actionsToRequestAsynchronously.
 */
public class PreproduceFullPDFAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreproduceFullPDFAction.class);

    private IAction convertAction;
    private IMetsReader metsReader;
    private IReadResultParser readResultParser;
    private MediaServerUtils mediaServerUtils;

    public void setConvertAction(IAction convertAction) {
        this.convertAction = convertAction;
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

    /**
     * Performs the action of preproducing derivatives.
     *
     * <p>
     * Requires a parameter map with a fileGrp entry.
     *
     * @param work      a work entity
     * @param parameter a map of parameter
     * @return an object with the result of the action, if any.
     * @throws Exception by fatal errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        LOGGER.info("Preproducing full PDF for work " + work.getId());

        File metsFile = mediaServerUtils.getMetsFileForWork(work);
        String workIdRegex = ".*?(" + Pattern.quote(work.getId()) + "/.*)";

        List<String> lines = metsReader.read(metsFile);
        Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);
        String fullPdfUrl = metsResult.get("fullPdfUrl");

        if (!StringUtils.hasText(fullPdfUrl)) {
            throw new Exception("No full PDF file URL found in METS file.");
        }

        Map<String, String> convertParams = new HashMap<>();
        convertParams.put("derivativePath", fullPdfUrl.replaceFirst(workIdRegex, "$1"));
        try {
            convertAction.perform(work, convertParams);
        } catch (Exception e) {
            throw new Exception("Error preproducing full PDF for work " + work.getId() + ": " + e, e);
        }

        return null;
    }
}
