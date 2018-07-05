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

package org.kitodo.mediaserver.core.services;

import org.kitodo.mediaserver.core.api.INotificationService;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Simple notification service.
 */
public class NotificationService implements INotificationService {

    private StringBuffer notification = new StringBuffer();

    private JavaMailSender mailSender;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Appends a message to a notification.
     *
     * @param message the message
     */
    @Override
    public synchronized void appendMessage(String message) {
        notification.append(message);
        notification.append("\n");
    }

    /**
     * Sends a notification.
     */
    @Override
    public synchronized void sendNotification(String subject) {

    }
}
