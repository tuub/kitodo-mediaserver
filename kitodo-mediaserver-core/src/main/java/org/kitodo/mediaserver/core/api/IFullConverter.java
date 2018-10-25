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

package org.kitodo.mediaserver.core.api;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface IFullConverter {
    InputStream convert(Map<Integer, File> pages, Map<String, String> parameter) throws Exception;
}
