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

package org.kitodo.mediaserver.core.db.entities;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * ActionData entity for managing actions.
 */
@Entity
public class ActionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "action_parameter")
    // @CollectionTable(name = "parameter") defines the table name and the column name in the table
    // -> "parameter_key"=key, "parameter"=value
    private Map<String, String> parameter = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "work_id", referencedColumnName = "id", nullable = false)
    private Work work;

    private String actionName;

    private Instant requestTime;
    private Instant startTime;
    private Instant endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Instant requestTime) {
        this.requestTime = requestTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    private ActionData() {}

    /**
     * Constructs an ActionData object.
     *
     * @param work the work on which the action is performed
     * @param actionName the action name
     * @param parameter a map of parameter
     */
    public ActionData(Work work, String actionName, Map<String, String> parameter) {
        this.work = work;
        this.parameter = parameter;
        this.actionName = actionName;
    }

}
