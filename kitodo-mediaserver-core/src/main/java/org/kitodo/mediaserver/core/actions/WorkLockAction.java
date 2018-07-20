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
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.config.MetsProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Locks or unlocks a work.
 */
@Component("workLockAction")
public class WorkLockAction implements IAction {

    private WorkRepository workRepository;

    private MediaServerUtils mediaServerUtils;

    private MetsProperties metsProperties;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    @Autowired
    public void setMetsProperties(MetsProperties metsProperties) {
        this.metsProperties = metsProperties;
    }

    /**
     * Locks or unlocks the work.
     * @param work      a work entity
     * @param parameter a map of parameter; enabled: lock or unlock, comment: lock comment
     * @return always null
     * @throws Exception no exception thrown in this implementation
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        mediaServerUtils.checkForRequiredParameter(parameter, "enabled", "reduceMets");

        Boolean enabled = Boolean.parseBoolean(parameter.get("enabled"));
        Boolean reduceMets = Boolean.parseBoolean(parameter.get("reduceMets"));

        File mets;
        File originalMets;

        if (enabled) {
            // restore original METS file (if file exists)
            mets = mediaServerUtils.getMetsFileForWork(work);
            try {
                originalMets = mediaServerUtils.getOriginalMetsFileForWork(work);
                Files.deleteIfExists(mets.toPath());
                Files.move(originalMets.toPath(), mets.toPath());
            } catch (FileNotFoundException ex) {
                // if no original METS file found, just don't use it -> ignore this exception
            }
        } else if (reduceMets) {
            // backup original METS file
            mets = mediaServerUtils.getMetsFileForWork(work);
            originalMets = mediaServerUtils.forceGetMetsFileForWork(work, true);
            Files.move(mets.toPath(), originalMets.toPath());

            // create reduced METS file
            TransformerFactory factory = TransformerFactory.newInstance();
            ClassPathResource resource = new ClassPathResource(metsProperties.getWorkLockReduceMetsXsl());
            Transformer transformer = factory.newTransformer(new StreamSource(resource.getInputStream()));
            transformer.transform(new StreamSource(originalMets), new StreamResult(mets));
        }

        work.setEnabled(enabled);
        workRepository.save(work);

        return null;
    }
}
