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

package org.kitodo.mediaserver.core.processors;

import org.apache.commons.lang3.StringUtils;
import org.kitodo.mediaserver.core.api.IWorkDescriptor;
import org.kitodo.mediaserver.core.config.IdentifierProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An implementation of a work descriptor creating a persistent url of a work.
 */
public class WorkPurlCreator implements IWorkDescriptor {

    IdentifierProperties identifierProperties;

    @Autowired
    public void setIdentifierProperties(IdentifierProperties identifierProperties) {
        this.identifierProperties = identifierProperties;
    }

    /**
     * Generates a PURL for a work entity.
     *
     * @param work the work entity
     * @return a string with the purl
     */
    @Override
    public String describe(Work work) {

        String workId = work.getId();
        if (StringUtils.isNotBlank(identifierProperties.getReplacementRegex())
            && identifierProperties.getReplacement() != null) {
            workId = work.getId().replaceAll(identifierProperties.getReplacementRegex(), identifierProperties.getReplacement());
        }

        return identifierProperties.getLandingPagePattern().replaceFirst("\\{workId\\}", workId);

    }
}
