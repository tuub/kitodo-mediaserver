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

import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity for identifiers for digitized works.
 */
@Entity
public class Identifier {

    private String identifier;
    private String type;
    private Work work;

    protected Identifier() {}

    /**
     * Constructor only with identifier and type, useful for search.
     *
     * @param identifier the identifier string
     * @param type the type, my be null
     */
    public Identifier(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    /**
     * Constructor with work object, useful for import.
     *
     * @param identifier the identifier string
     * @param type the type, my be null
     * @param work the referenced work
     */
    public Identifier(String identifier, String type, Work work) {
        this.identifier = identifier;
        this.type = type;
        this.work = work;
    }

    @Id
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "work_id", referencedColumnName = "id", nullable = false)
    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    /**
     * Implementing equals for test purposes.
     *
     * @param obj another object
     * @return true if equal to this, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Identifier other = (Identifier) obj;
        return StringUtils.equals(getIdentifier(), other.getIdentifier())
                && StringUtils.equals(getType(), other.getType());
    }
}
