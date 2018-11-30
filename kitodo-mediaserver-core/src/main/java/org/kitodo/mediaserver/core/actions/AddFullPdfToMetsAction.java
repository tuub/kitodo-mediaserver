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
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Adds a Full PDF DOWNLOAD entry to a METS file.
 * If destFile parameter is unset, sourceFile gets overwritten.
 */
public class AddFullPdfToMetsAction implements IAction {

    private IMetsTransformer metsTransformer;
    private MediaServerUtils mediaServerUtils;
    private FileserverProperties fileserverProperties;

    public void setMetsTransformer(IMetsTransformer metsTransformer) {
        this.metsTransformer = metsTransformer;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    public AddFullPdfToMetsAction(IMetsTransformer metsTransformer) {
        this.metsTransformer = metsTransformer;
    }

    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        String destFilename = parameter.get("destFile");
        File sourceMets = mediaServerUtils.getMetsFileForWork(work);
        File destMets = StringUtils.hasText(destFilename) ? new File(destFilename) : sourceMets;

        try {
            metsTransformer.transform(sourceMets, destMets,
                new AbstractMap.SimpleEntry<>("rootUrl", fileserverProperties.getRootUrl()),
                new AbstractMap.SimpleEntry<>("workId", work.getId())
            );
        } catch (Exception e) {
            throw new Exception("AddFullPdfToMetsAction failed.", e);
        }

        return null;
    }
}
