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
@Component("setAllowedNetworkAction")
public class SetAllowedNetworkAction implements IAction {

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
     * Sets the allowedNetwork on a work, sets a comment and optionally creates a reduced METS/MODS file.
     *
     * @param work      a work entity
     * @param parameter a map of parameter; network, comment, reduceMets
     * @return always null
     * @throws Exception no exception thrown in this implementation
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        String network = parameter.getOrDefault("network", "");
        Boolean reduceMets = Boolean.parseBoolean(parameter.getOrDefault("reduceMets", Boolean.FALSE.toString()));

        File mets;
        File originalMets;

        if (!"disabled".equals(network)) {
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

            if (!originalMets.exists()) {
                Files.move(mets.toPath(), originalMets.toPath());

                // create reduced METS file
                TransformerFactory factory = TransformerFactory.newInstance();
                ClassPathResource resource = new ClassPathResource(metsProperties.getWorkLockReduceMetsXsl());
                Transformer transformer = factory.newTransformer(new StreamSource(resource.getInputStream()));
                transformer.transform(new StreamSource(originalMets), new StreamResult(mets));
            }
        }

        work.setAllowedNetwork(network);
        workRepository.save(work);

        return null;
    }
}
