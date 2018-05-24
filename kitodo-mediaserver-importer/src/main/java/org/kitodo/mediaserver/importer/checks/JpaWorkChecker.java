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

package org.kitodo.mediaserver.importer.checks;

import java.util.Optional;
import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A JPA implementation of the work checker.
 */
public class JpaWorkChecker implements IWorkChecker {

    private WorkRepository workRepository;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    /**
     * Checks if a work or any of its identifiers is already present in the database.
     *
     * @param work the work to be checked
     * @return a work object of the already present work or null, if none present
     * @throws Exception if a severe error occurs
     */
    @Override
    public Work check(Work work) throws Exception {

        Work presentWork = null;

        Optional<Work> optionalPresentWork = workRepository.findById(work.getId());
        if (optionalPresentWork.isPresent()) {
            presentWork = optionalPresentWork.get();
        }

        if (work.getIdentifiers() != null) {
            for (Identifier identifier : work.getIdentifiers()) {
                Work otherWork = workRepository.findByIdentifiers(identifier);
                if (otherWork != null && !work.getId().equals(otherWork.getId())) {
                    throw new ImporterException("The identifier " + identifier.getIdentifier()
                            + " is already associated with the work " + otherWork.getId() + " and cannot be set for"
                            + " the new work " + work.getId() + ". Interrupting import");
                }
            }
        }

        return presentWork;
    }
}
