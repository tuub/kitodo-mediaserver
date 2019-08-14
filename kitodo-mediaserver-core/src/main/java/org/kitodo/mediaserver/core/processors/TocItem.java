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

package org.kitodo.mediaserver.core.processors;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * A representation of an entry in a table of content.
 */
public class TocItem {

    /**
     * Name of this item as shown in the document structural hierarchy.
     */
    private String name;

    /**
     * The item type, e.g. "chapter", "cover", ...
     */
    private String type;

    private Integer pageNumber;
    private List<TocItem> children;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    @XmlElement(name = "tocItem")
    public List<TocItem> getChildren() {
        return children;
    }

    public void setChildren(List<TocItem> children) {
        this.children = children;
    }
}
