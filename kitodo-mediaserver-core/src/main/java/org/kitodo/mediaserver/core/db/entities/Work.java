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
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * Entity for digitized works.
 */
@Entity
public class Work {

    private String id;
    private String title;
    private String path;
    private String hostId;
    private Instant indexTime;
    private Set<Collection> collections;
    private String allowedNetwork = "global";

    protected Work() {}

    public Work(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Instant getIndexTime() {
        return indexTime;
    }

    public void setIndexTime(Instant indexTime) {
        this.indexTime = indexTime;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "work_collection",
            joinColumns = @JoinColumn(name = "work_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "collection_name", referencedColumnName = "name"))
    public Set<Collection> getCollections() {
        return collections;
    }

    public void setCollections(Set<Collection> collections) {
        this.collections = collections;
    }

    public String getAllowedNetwork() {
        return allowedNetwork;
    }

    public void setAllowedNetwork(String allowedNetwork) {
        this.allowedNetwork = allowedNetwork;
    }

}
