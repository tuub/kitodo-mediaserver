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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class OcrLine {

    @XmlElement(name = "ocrWord")
    public List<OcrWord> words = new ArrayList<>();
}
