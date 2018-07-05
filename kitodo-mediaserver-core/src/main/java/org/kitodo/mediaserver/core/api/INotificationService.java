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

/**
 * Interface for a notification service.
 */
public interface INotificationService {

    /**
     * Appends a message to a notification.
     *
     * @param message
     */
    void appendMessage(String message);

    /**
     * Sends a notification.
     */
    void sendNotification(String subject);
}
