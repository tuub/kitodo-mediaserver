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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kitodo.mediaserver.core.actions.CacheDeleteAction;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.exceptions.WorkNotFoundException;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.ui.config.UiProperties;
import org.kitodo.mediaserver.ui.util.KeyValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkController.class);

    private WorkService workService;

    private WorkRepository workRepository;

    private UiProperties uiProperties;

    private CacheDeleteAction cacheDeleteAction;

    public WorkService getWorkService() {
        return workService;
    }

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
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
                       @ModelAttribute("success") String success,
                       @ModelAttribute("error") String error) {

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
        model.addAttribute("reduceMets", uiProperties.getWorks().getReduceMets());
        return "works/works";
    }

    /**
     * Perform actions on one ore more works.
     *
     * @param workIds An array of work IDs to perform the actions on
     * @param action the action name to perform
     * @param params a parameter map for the actions
     * @param redirectAttributes for sending error messages to next view
     * @return the view name to render
     */
    @PostMapping
    public String action(@RequestParam(required = false) List<String> workIds,
                         @RequestParam String action,
                         @RequestParam Map<String, String> params,
                         RedirectAttributes redirectAttributes) {

        if (workIds == null) {
            redirectAttributes.addFlashAttribute("error", "works.error.no_work_selected");
            return "redirect:/works";
        }

        // Parse dynamic action parameters. Keep only keys with specific pattern
        Pattern pattern = Pattern.compile("^params\\[([a-z]+)\\]$");
        Matcher matcher;
        Map<String, String> parsedParams = new HashMap<>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            matcher = pattern.matcher(param.getKey());
            if (matcher.find()) {
                parsedParams.put(matcher.group(1), param.getValue());
            }
        }

        Iterable<Work> works = workRepository.findAllById(workIds);

        // run action for every work
        for (Work work : works) {
            switch (action) {

                case "cache-clear":
                    try {
                        cacheDeleteAction.perform(work, new HashMap<>());
                    } catch (Exception e) {
                        LOGGER.error("Failed to clear cache.", e);
                        redirectAttributes.addFlashAttribute("error", "works.error.cache_delete_failed");
                    }
                    break;

                case "work-lock":
                    try {
                        workService.lockWork(
                            work,
                            parsedParams.getOrDefault("enabled", "").equalsIgnoreCase("on"),
                            parsedParams.getOrDefault("comment", ""),
                            parsedParams.getOrDefault("reduce", "").equalsIgnoreCase("on")
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to lock/unlock work.", e);
                        redirectAttributes.addFlashAttribute("error", "works.error.lock_failed");
                    }
                    break;

                default:
                    break;
            }
        }

        // set success message
        if (!redirectAttributes.getFlashAttributes().containsKey("error")) {
            switch (action) {
                case "cache-clear":
                    redirectAttributes.addFlashAttribute("success", "works.success.cache_delete");
                    break;
                case "work-lock":
                    redirectAttributes.addFlashAttribute("success", "works.success.work_locked");
                    break;
                default:
                    break;
            }
        }

        return "redirect:/works";
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
}
