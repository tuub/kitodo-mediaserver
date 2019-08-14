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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of documents table of contents.
 */
@XmlRootElement
public class Toc {

    private List<TocItem> tocItems;

    @XmlElement(name = "tocItem")
    public List<TocItem> getTocItems() {
        return tocItems;
    }

    public void setTocItems(List<TocItem> tocItems) {
        this.tocItems = tocItems;
    }
}
