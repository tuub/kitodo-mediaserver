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
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A JPA implementation of the work checker.
 */
@Component("jpaWorkChecker")
public class JpaWorkChecker implements IWorkChecker {

    private WorkRepository workRepository;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    /**
     * Checks if a work is already present in the database.
     *
     * @param work the work to be checked
     * @return a work object of the already present work or null, if none present
     * @throws Exception if a severe error occurs
     */
    @Override
    public Work check(Work work) {

        Work presentWork = null;

        Optional<Work> optionalPresentWork = workRepository.findById(work.getId());
        if (optionalPresentWork.isPresent()) {
            presentWork = optionalPresentWork.get();
        }

        return presentWork;
    }
}
