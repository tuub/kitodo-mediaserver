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
import java.util.Calendar;
import org.kitodo.mediaserver.core.api.IDocument;

/**
 * A abstract document for image conversion with some needed fields.
 */
public abstract class AbstractDocument implements IDocument {

    private String title;
    private String author;
    private Calendar productionDate;
    private ICC_Profile iccProfile;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAuthor() {
        return author;
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
    public void setAuthor(String author) {
        this.author = author;
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
