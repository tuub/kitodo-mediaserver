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

package org.kitodo.mediaserver.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.kitodo.mediaserver.core.api.INotifier;
import org.kitodo.mediaserver.core.config.NotifierProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


/**
 * Simple notifier.
 */
@Component
public class Notifier implements INotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Notifier.class);

    private JavaMailSender mailSender;
    private NotifierProperties notifierProperties;

    @Autowired
    public void setNotifierProperties(NotifierProperties notifierProperties) {
        this.notifierProperties = notifierProperties;
    }

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Appends a message to a notification.
     *
     * @param buffer The notification buffer
     * @param message the message
     */
    @Override
    public void add(StringBuffer buffer, String message) {
        buffer.append(getTime());
        buffer.append("\t\t");
        buffer.append(message);
        buffer.append("\n");
    }

    /**
     * Sends a notification.
     *
     * @param buffer The notification buffer that holds the concatenated notification
     * @param subject The mail subject
     */
    @Override
    public void send(StringBuffer buffer, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(buffer.toString());
        message.setFrom(notifierProperties.getEmailFrom());
        message.setTo(notifierProperties.getEmailTo());
        mailSender.send(message);
    }

    /**
     * Returns string with current datetime in a configured format.
     */
    protected String getTime() {
        String format = notifierProperties.getTimestampFormat();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
                .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

        return formatter.format(Instant.now());
    }
}
