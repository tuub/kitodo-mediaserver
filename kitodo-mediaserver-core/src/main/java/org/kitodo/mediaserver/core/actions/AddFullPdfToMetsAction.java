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
import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Adds a Full PDF DOWNLOAD entry to a METS file.
 */
public class AddFullPdfToMetsAction implements IAction {

    private IMetsTransformer metsTransformer;
    private MediaServerUtils mediaServerUtils;
    private FileserverProperties fileserverProperties;
    private MetsProperties metsProperties;

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    @Autowired
    public void setMetsProperties(MetsProperties metsProperties) {
        this.metsProperties = metsProperties;
    }

    public AddFullPdfToMetsAction(IMetsTransformer metsTransformer) {
        this.metsTransformer = metsTransformer;
    }

    /**
     * Adds the pdf entry to the mets file.
     * If destFile parameter is unset, sourceFile gets overwritten.
     *
     * @param work a work entity
     * @param parameter a map of parameter, may be null
     * @return null
     * @throws Exception by severe errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        String destFilename = null;
        if (parameter != null) {
            destFilename = parameter.get("destFile");
        }
        File sourceMets = mediaServerUtils.getMetsFileForWork(work);
        File destMets = StringUtils.hasText(destFilename) ? new File(destFilename) : sourceMets;

        try {
            metsTransformer.transform(sourceMets, destMets,
                new AbstractMap.SimpleEntry<>("rootUrl", fileserverProperties.getRootUrl()),
                new AbstractMap.SimpleEntry<>("workId", work.getId()),
                new AbstractMap.SimpleEntry<>("downloadGrpId", metsProperties.getDownloadFileGrp())
            );
        } catch (Exception e) {
            throw new Exception("AddFullPdfToMetsAction failed.", e);
        }

        return null;
    }
}
