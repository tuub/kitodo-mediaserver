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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.db.entities.Work;
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

    private ActionService actionService;

    public WorkRepository getWorkRepository() {
        return workRepository;
    }

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
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
     * Get all works belonging to a given collection.
     *
     * @param collectionName the name of the collection
     * @return a list of works
     */
    public List<Work> getAllWorksInCollection(String collectionName) {
        return workRepository.findByCollectionsName(collectionName);
    }

    /**
     * Update a single work.
     * @param work the work to be updated
     */
    public void updateWork(Work work) {
        workRepository.save(work);
    }

    /**
     * Get the lock comment for a work.
     * @param work the Work
     * @return the lock comment
     */
    public String getLockComment(Work work) {
        ActionData actionData = actionService.getLastPerformedAction(work, "workLockAction");
        if (actionData != null && actionData.getParameter() != null && actionData.getParameter().get("comment") != null) {
            return actionData.getParameter().get("comment");
        }
        return "";
    }

    /**
     * Locks or unlocks a work.
     * @param work the Work
     * @param enabled lock or unlock
     * @param comment lock comment
     * @throws Exception action exceptions
     */
    public void lockWork(Work work, Boolean enabled, String comment, Boolean reduceMets) throws Exception {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("enabled", enabled.toString());
        parameter.put("comment", comment);
        parameter.put("reduceMets", reduceMets.toString());

        actionService.request(work, "workLockAction", parameter);
        actionService.performRequested(work, "workLockAction", parameter);
    }

    /**
     * Deletes a work.
     * @param work the work entity
     */
    public void deleteWork(Work work) {
        workRepository.delete(work);
    }
}
