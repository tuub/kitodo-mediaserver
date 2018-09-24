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

import java.util.List;
import javax.mail.MessagingException;

/**
 * Interface for a notification service.
 */
public interface INotifier {

    /**
     * Appends a message to a notification.
     *
     * @param message the message
     */
    void add(String message);

    /**
     * Appends a message to a notification including timestamp.
     *
     * @param message the message
     */
    void addWithTimestamp(String message);

    /**
     * Sends a notification.
     */
    void send(String subject, List<String> recipients) throws MessagingException;
}
