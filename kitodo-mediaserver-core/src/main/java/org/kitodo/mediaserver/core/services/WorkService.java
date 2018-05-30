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

package org.kitodo.mediaserver.core.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.IdentifierRepository;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.db.specifications.WorkJpaSpecification;
import org.kitodo.mediaserver.core.exceptions.WorkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Manage Works.
 */
@Service
public class WorkService {

    private WorkRepository workRepository;

    private IdentifierRepository identifierRepository;

    public WorkRepository getWorkRepository() {
        return workRepository;
    }

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setIdentifierRepository(IdentifierRepository identifierRepository) {
        this.identifierRepository = identifierRepository;
    }

    /**
     * Find all works.
     * @param pageable paging and sorting
     * @return Page with Works
     */
    public Page<Work> findAll(Pageable pageable) {
        return workRepository.findAll(pageable);
    }

    /**
     * Searches for works by defining key:value search terms.
     * @param criterias key:value list for fields
     * @param pageable paging and sorting
     * @return Page with Works
     */
    public Page<Work> searchWorks(final List<Map.Entry<String, String>> criterias, Pageable pageable) {
        WorkJpaSpecification workJpaSpecification = new WorkJpaSpecification(criterias);
        return workRepository.findAll(workJpaSpecification, pageable);
    }

    /**
     * Get a single Work.
     * @param id id of the needed work
     * @return the Work
     * @throws WorkNotFoundException if no work was found
     */
    public Work getWork(String id) throws WorkNotFoundException {
        Optional<Work> work = workRepository.findById(id);
        if (work.isPresent()) {
            return work.get();
        }
        throw new WorkNotFoundException();
    }

    /**
     * Update a single work.
     * @param work the work to be updated
     */
    public void updateWork(Work work) {
        workRepository.save(work);
    }

    /**
     * Save a work at import.
     *
     * <p>
     * This method has to delete all already present identifiers in case the work was previously imported,
     * they are not automatically replaced.
     *
     * @param work the work entity
     */
    @Transactional
    public void importWork(Work work) {
        identifierRepository.deleteByWork(work);
        workRepository.save(work);
    }
}
