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

package org.kitodo.mediaserver.ui.works;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kitodo.mediaserver.core.actions.CacheDeleteAction;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.WorkNotFoundException;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.ui.config.UiProperties;
import org.kitodo.mediaserver.ui.util.KeyValueParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller for works.
 */
@Controller
@RequestMapping("/works")
public class WorkController {

    private WorkService workService;

    private UiProperties uiProperties;

    private CacheDeleteAction cacheDeleteAction;

    public WorkService getWorkService() {
        return workService;
    }

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    public UiProperties getUiProperties() {
        return uiProperties;
    }

    @Autowired
    public void setUiProperties(UiProperties uiProperties) {
        this.uiProperties = uiProperties;
    }

    public CacheDeleteAction getCacheDeleteAction() {
        return cacheDeleteAction;
    }

    @Autowired
    public void setCacheDeleteAction(CacheDeleteAction cacheDeleteAction) {
        this.cacheDeleteAction = cacheDeleteAction;
    }

    /**
     * Lists all works and searches for works.
     *
     * @param model the model
     * @param pageable pagination and sorting
     * @param search search pattern
     * @return view name
     */
    @RequestMapping
    public String list(Model model,
                       @PageableDefault(sort = "title") Pageable pageable,
                       @RequestParam(required = false) String search,
                       @ModelAttribute("errorUpdate") String errorUpdate) {

        if (StringUtils.hasText(errorUpdate)) {
            model.addAttribute("error", errorUpdate);
        }

        Page<Work> page;

        if (StringUtils.hasText(search)) {
            // do a search...
            KeyValueParser parser = new KeyValueParser(uiProperties.getWorks().getSearchableFields());
            List<Map.Entry<String, String>> fields = parser.parse(search);
            page = workService.searchWorks(fields, pageable);
            model.addAttribute("search", search);
            model.addAttribute("searchFields", fields);
        } else {
            // just get all works...
            page = workService.findAll(pageable);
        }

        // build the sorting parameter string to be used in MVC
        // TODO: find a better solution for building sorting parameter
        Sort.Order order = pageable.getSort().iterator().next();
        String sort = order.getProperty() + "," + (order.getDirection().isAscending() ? "asc" : "desc");

        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        model.addAttribute("sizes", uiProperties.getPagination().getElementsPerPage().getAvailableValues());
        model.addAttribute("availableFields", uiProperties.getWorks().getSearchableFields());
        return "works/works";
    }

    /**
     * Get lock comment for work.
     * @param id ID of the work
     * @return the lock comment
     * @throws WorkNotFoundException if the work doesn't exist
     */
    @GetMapping(value = "/{id}/lockcomment", produces = "application/json")
    @ResponseBody
    public Map<String, String> getLockComment(@PathVariable String id) throws WorkNotFoundException {
        Work work = workService.getWork(id);
        String comment = workService.getLockComment(work);
        return Collections.singletonMap("response", comment);
    }

    /**
     * Enable or disable a work.
     * @param id Id of the work
     * @param enabled new lock state
     * @param redirectAttributes cookies for error handling
     * @return view name
     */
    @PostMapping("/{id}")
    public String edit(@PathVariable String id,
                       @RequestParam String enabled,
                       @RequestParam String comment,
                       RedirectAttributes redirectAttributes) {

        Work work;
        try {
            work = workService.getWork(id);
            workService.lockWork(work, enabled.toLowerCase().equals("on"), comment);
        } catch (WorkNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorUpdate", "works.error.work_not_found");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorUpdate", "works.error.lock_failed");
        }
        return "redirect:/works";
    }

    /**
     * Delete the work cache.
     * @param id Id of the work
     * @param redirectAttributes cookies for error handling
     * @return view name
     */
    @PostMapping("/{id}/cache/delete")
    public String cacheDelete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Work work;
        try {
            work = workService.getWork(id);
            Map<String, String> parameterMap = new HashMap<>();
            cacheDeleteAction.perform(work, parameterMap);
        } catch (WorkNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorCacheDelete", "works.error.work_not_found");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCacheDelete", "works.error.cache_delete_failed");
        }
        return "redirect:/works";
    }
}
