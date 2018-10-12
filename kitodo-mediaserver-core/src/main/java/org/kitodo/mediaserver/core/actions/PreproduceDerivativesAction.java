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
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An action to be used for preproducing derivatives at import.
 * This action can be called either directly using the actionsBeforeIndexing configuration
 * or ordered using actionsToRequestAsynchronously.
 */
public class PreproduceDerivativesAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreproduceDerivativesAction.class);

    private MediaServerUtils mediaServerUtils;
    private IMetsReader metsReader;
    private IReadResultParser readResultParser;
    private IAction convertAction;
    private String valueConcatSeparator;

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    public void setMetsReader(IMetsReader metsReader) {
        this.metsReader = metsReader;
    }

    public void setReadResultParser(IReadResultParser readResultParser) {
        this.readResultParser = readResultParser;
    }

    public void setConvertAction(IAction convertAction) {
        this.convertAction = convertAction;
    }

    public void setValueConcatSeparator(String valueConcatSeparator) {
        this.valueConcatSeparator = valueConcatSeparator;
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

        mediaServerUtils.checkForRequiredParameter(parameter, "fileGrp");

        String fileGrp = parameter.get("fileGrp");
        String fileId = parameter.get("fileId");
        String workIdRegex = ".*?(" + work.getId() + "/.*)";

        File metsFile = mediaServerUtils.getMetsFileForWork(work);

        // Gets the request url(s) from the mets file
        List<String> lines = metsReader.read(
                metsFile,
                new AbstractMap.SimpleEntry<>("fileGrp", fileGrp),
                new AbstractMap.SimpleEntry<>("fileId", fileId != null ? fileId : "")
        );
        Map<String, String> metsResult = (Map<String, String>) readResultParser.parse(lines);

        String requestUrlString = metsResult.get("request_url");
        if (requestUrlString != null) {
            for (String requestUrl : requestUrlString.split(valueConcatSeparator)) {
                LOGGER.info("Preproducing file " + requestUrl);

                Map<String, String> convertParams = new HashMap<>();
                convertParams.put("requestUrl", requestUrl);
                convertParams.put("derivativePath", requestUrl.replaceFirst(workIdRegex, "$1"));
                try {
                    convertAction.perform(work, convertParams);
                } catch (Exception e) {
                    LOGGER.error("Error converting file " + requestUrl + ": " + e, e);
                }
            }
        }

        return null;
    }
}
