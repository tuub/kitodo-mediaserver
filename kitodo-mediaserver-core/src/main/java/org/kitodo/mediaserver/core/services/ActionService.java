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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.ActionRepository;
import org.kitodo.mediaserver.core.exceptions.ActionServiceException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Run Actions.
 */
@Service
public class ActionService {

    private ApplicationContext applicationContext;

    private ActionRepository actionRepository;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setActionRepository(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    private ActionService() {}

    /**
     * Request a action and make it persistent.
     * @param work the Work
     * @param actionName action bean name to be used
     * @param parameter parameter list
     */
    public void request(Work work, String actionName, Map<String, String> parameter) throws ActionServiceException {

        if (work == null) {
            throw new IllegalArgumentException("work must not be null.");
        }
        if (!StringUtils.hasText(actionName)) {
            throw new IllegalArgumentException("actionName must be an action Bean name.");
        }

        // if this exact action is already requested and not finished, break
        ActionData actionData = getUnperformedAction(work, actionName, parameter);
        if (actionData != null) {
            throw new ActionServiceException("The requested actionName='" + actionName + "' for workId='" + work.getId()
                + "' has already been requested.");
        }

        actionData = new ActionData(work, actionName, parameter);
        actionData.setRequestTime(Instant.now());
        actionRepository.save(actionData);
    }

    /**
     * Run a saved ActionData.
     * @param work the Work
     * @param actionName action bean name to be used
     * @param parameter parameter list
     * @return the result of the Action
     * @throws Exception on Action errors
     */
    public Object performRequested(Work work, String actionName, Map<String, String> parameter) throws Exception {

        checkParams(work, actionName);

        // Does this action request exist?
        ActionData actionData = getUnperformedAction(work, actionName, parameter);
        if (actionData == null) {
            throw new ActionServiceException("This actionName='" + actionName + "' for workId='" + work.getId() + "' was not found.");
        }

        return performRequested(actionData);
    }

    /**
     * Run a saved ActionData.
     * @param actionData the ActionData object referencing the action to be performed
     * @return the result of the Action
     * @throws Exception on Action errors
     */
    public Object performRequested(ActionData actionData) throws Exception {

        if (actionData == null) {
            throw new IllegalArgumentException("Parameter actionData must not be null.");
        }

        // Is this action already running?
        ActionData persistentActionData = getRunningAction(actionData.getWork(), actionData.getActionName(), actionData.getParameter());
        if (persistentActionData != null) {
            throw new ActionServiceException("This actionName='" + actionData.getActionName()
                + "' for workId=" + actionData.getWork().getId() + " is already running: startTime=" + persistentActionData.getStartTime());
        }

        // Run the action...
        actionData.setStartTime(Instant.now());
        actionRepository.save(actionData);

        IAction actionInstance = getActionInstance(actionData.getActionName());

        Object result;

        try {
            result = actionInstance.perform(actionData.getWork(), actionData.getParameter());

            actionData.setEndTime(Instant.now());
            actionRepository.save(actionData);

        } catch (Exception ex) {

            actionData.setStartTime(null);
            actionRepository.save(actionData);

            throw ex;
        }

        return result;
    }

    /**
     * Performs an named action immediately without persisting.
     * Use case: when actions are configured to be performed under certain conditions.
     *
     * @param work the work on which the action is performed
     * @param actionName the name of the action bean
     * @param parameter a map of parameter
     * @return the result of the action
     * @throws Exception by severe errors
     */
    public Object performImmediately(Work work, String actionName, Map<String, String> parameter) throws Exception {

        checkParams(work, actionName);

        IAction actionInstance = getActionInstance(actionName);

        return actionInstance.perform(work, parameter);
    }

    /**
     * Gets the bean instance of an action.
     *
     * @param actionName the name of the action
     * @return the action instance bean
     * @throws Exception if there is no such bean or the bean is no IAction implementation
     */
    private IAction getActionInstance(String actionName) throws Exception {
        Object actionInstance;

        // Does the action bean exist?
        try {
            actionInstance = applicationContext.getBean(actionName);
        } catch (BeansException ex) {
            throw new ClassNotFoundException("No Bean found for actionName='" + actionName + "'");
        }

        // Is the bean a usable Action?
        if (!(actionInstance instanceof IAction)) {
            throw new ActionServiceException("The Bean with actionName='" + actionName + "' is not a valid action.");
        }
        return (IAction) actionInstance;
    }

    /**
     * Finds the last finished action.
     * @param work the Work
     * @param actionName the action bean name
     * @return the ActionData
     */
    public ActionData getLastPerformedAction(Work work, String actionName) {
        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameOrderByEndTimeDesc(work, actionName);
        if (actionDatas != null && actionDatas.size() > 0) {
            return actionDatas.get(0);
        }
        return null;
    }

    /**
     * Finds a requested action that hasn't already been performed, whether it is currently running or not.
     * @param work the Work
     * @param actionName the action bean name
     * @param parameter the parameter list
     * @return the ActionData
     */
    public ActionData getUnperformedAction(Work work, String actionName, Map<String, String> parameter) {
        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameAndEndTimeIsNull(work, actionName);
        return getMatchingAction(actionDatas, parameter);
    }

    /**
     * Finds the currently running action.
     * @param work the Work
     * @param actionName the action bean name
     * @param parameter the parameter list
     * @return the ActionData
     */
    public ActionData getRunningAction(Work work, String actionName, Map<String, String> parameter) {
        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameAndStartTimeIsNotNullAndEndTimeIsNull(work, actionName);
        return getMatchingAction(actionDatas, parameter);
    }

    /**
     * Filters a ActionData list for a matching parameter list.
     * @param actionDatas ActionData list
     * @param parameter the parameter list
     * @return the matching ActionData
     */
    private ActionData getMatchingAction(List<ActionData> actionDatas, Map<String, String> parameter) {
        if (actionDatas == null) {
            throw new IllegalArgumentException("actionDatas must not be null.");
        }
        for (ActionData actionData : actionDatas) {
            if ( (parameter == null && actionData.getParameter() == null)
                || (parameter != null && parameter.equals(actionData.getParameter())) ) {
                return actionData;
            }
        }
        return null;
    }

    /**
     * Checks parameter for valid values.
     *
     * @param work a work entity
     * @param actionName an action name
     */
    private void checkParams(Work work, String actionName) {
        if (work == null) {
            throw new IllegalArgumentException("Parameter work must not be null.");
        }
        if (!StringUtils.hasText(actionName)) {
            throw new IllegalArgumentException("Parameter actionName must not be null.");
        }
    }


}
