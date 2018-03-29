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

import java.io.OutputStream;
import java.net.URI;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.models.ActionControl;

/**
 * Basic implementation of a convertion action.
 */
public class ConvertAction implements IAction {

    private IConverter converter;

    /**
     * Performs a convert action.
     *
     * <p>
     * Uses the injected converter to the convert the master file.
     *
     * @param actionControl the actionControl object with the definition of the specific action.
     * @return              the converted file.
     */
    public OutputStream perform(ActionControl actionControl) throws Exception {

        // Find the master file using the params in the actionControl object TODO
        URI masterFile = null;

        // Convert the file
        OutputStream convertedFile = converter.convert(masterFile, actionControl.getParameter());

        return convertedFile;
    }
}
