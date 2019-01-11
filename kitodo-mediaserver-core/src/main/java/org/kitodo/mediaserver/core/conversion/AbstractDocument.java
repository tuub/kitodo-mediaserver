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

package org.kitodo.mediaserver.core.conversion;

import java.awt.color.ICC_Profile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.kitodo.mediaserver.core.api.IDocument;

/**
 * A abstract document for image conversion with some needed fields.
 */
public abstract class AbstractDocument implements IDocument {

    private String title;
    private List<String> authors = new ArrayList<>();
    private Calendar productionDate;
    private ICC_Profile iccProfile;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public Calendar getProductionDate() {
        return productionDate;
    }

    @Override
    public ICC_Profile getIccProfile() {
        return iccProfile;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addAuthor(String author) {
        this.authors.add(author);
    }

    @Override
    public void setProductionDate(Calendar cal) {
        this.productionDate = cal;
    }

    @Override
    public void setIccProfile(ICC_Profile profile) {
        this.iccProfile = profile;
    }
}
