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

package org.kitodo.mediaserver.importer.exceptions;

/**
 * Exception class for import errors.
 */
public class ImporterException extends Exception {

    public ImporterException(Throwable t) {
        super(t);
    }

    public ImporterException(String message) {
        super(message);
    }

    public ImporterException(String message, Throwable t) {
        super(message, t);
    }

}
