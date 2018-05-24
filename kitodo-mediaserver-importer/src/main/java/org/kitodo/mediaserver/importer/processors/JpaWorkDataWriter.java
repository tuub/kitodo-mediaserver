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

package org.kitodo.mediaserver.importer.processors;

import javax.transaction.Transactional;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.IdentifierRepository;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.importer.api.IWorkDataWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A work data writer using JPA.
 */
public class JpaWorkDataWriter implements IWorkDataWriter {

    private WorkRepository workRepository;

    private IdentifierRepository identifierRepository;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setIdentifierRepository(IdentifierRepository identifierRepository) {
        this.identifierRepository = identifierRepository;
    }

    /**
     * Writes the data work to the database, updating the work if already present. Replaces all identifiers.
     *
     * @param work the work entity
     * @throws Exception if a severe error occurs
     */
    @Override
    @Transactional
    public void write(Work work) {

        identifierRepository.deleteByWork(work);
        workRepository.save(work);
    }
}
