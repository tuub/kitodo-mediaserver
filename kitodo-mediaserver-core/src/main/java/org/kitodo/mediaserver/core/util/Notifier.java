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
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.kitodo.mediaserver.core.api.INotifier;
import org.kitodo.mediaserver.core.config.NotifierProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Simple notifier.
 */
@Component
@Scope("prototype")
public class Notifier implements INotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Notifier.class);

    private StringBuffer notification = new StringBuffer();
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
     * @param message the message
     */
    @Override
    public void add(String message) {
        notification.append(message);
        notification.append("\n");
    }

    /**
     * Appends a message to a notification, including timestamp.
     *
     * @param message the message
     */
    @Override
    public void addWithTimestamp(String message) {
        notification.append(getTime());
        notification.append("\t\t");
        add(message);
    }

    /**
     * Sends a notification and deletes the notification buffer.
     *
     * @param subject The mail subject
     * @param recipients The list of recipients
     */
    @Override
    public void send(String subject, List<String> recipients) {

        try {
            if (recipients.size() > 0 && StringUtils.isNotBlank(notification.toString())) {
                if (StringUtils.isNotBlank(notifierProperties.getSubjectPrefix())) {
                    subject = notifierProperties.getSubjectPrefix() + subject;
                }
                String [] emailTo = recipients.toArray(new String[0]);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper mailHelper = new MimeMessageHelper(message, false, "utf-8");
                mailHelper.setSubject(subject);
                mailHelper.setText(notification.toString());
                mailHelper.setFrom(notifierProperties.getEmailFrom());
                mailHelper.setTo(emailTo);
                mailSender.send(message);
                notification = new StringBuffer();
                LOGGER.info("Sent notification email to " + String.join(",", emailTo));
            }
        } catch (MessagingException e) {
            LOGGER.error("Error sending email: " + e, e);
        }
    }

    /**
     * Appends a message to a notification and immediately sends it.
     *
     * @param message the message
     * @param subject the subject
     * @param recipients The list of recipients
     */
    public void addAndSend(String message, String subject, List<String> recipients) {
        add(message);
        send(subject, recipients);
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

    /**
     * Returns string with current notification buffer.
     */
    public String getCollectedNotification() {
        return notification.toString();
    }
}
