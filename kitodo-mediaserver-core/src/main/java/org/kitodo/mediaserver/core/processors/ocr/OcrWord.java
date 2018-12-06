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

package org.kitodo.mediaserver.core.processors.ocr;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class OcrWord {

    @XmlValue
    public String word = "";

    @XmlAttribute
    public float x = 0;

    @XmlAttribute
    public float y = 0;

    @XmlAttribute
    public float width = 0;

    @XmlAttribute
    public float height = 0;

    public OcrWord() {}

    public OcrWord(String word) {
        this.word = word;
    }
}
