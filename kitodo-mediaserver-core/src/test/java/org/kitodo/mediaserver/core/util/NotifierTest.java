package org.kitodo.mediaserver.core.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.config.NotifierProperties;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Watermarker.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class NotifierTest {

    private Notifier notifier = new Notifier();
    private StringBuffer notification = new StringBuffer();

    @Before
    public void init() {
        NotifierProperties notifierProperties = mock(NotifierProperties.class);
        when(notifierProperties.getEmailFrom()).thenReturn("kitodo@example.org");
        when(notifierProperties.getEmailTo()).thenReturn("kitodo@example.org");
        when(notifierProperties.getTimestampFormat()).thenReturn("yyyy-MM-dd HH:mm:ss");
        notifier.setNotifierProperties(notifierProperties);
    }

    @Test
    public void canAddMessageToNotificationBuffer() {
        // given
        String message = "Test message.";
        String timestamp = notifier.getTime();

        // when
        notifier.add(notification, message);

        // then
        String expected = timestamp + "\t\tTest message.\n";
        assertThat(notification.toString()).isEqualTo(expected);
    }

    /*
    @Test
    public void canAddTimestampedMessageToNotificationBuffer() {
        // given
        String message = "Test message.";
        String timestamp = notifier.getTime();

        // when
        notifier.add(notification, message);

        // then
        String expected = timestamp + "\t\tTest message.\n";
        assertThat(notification.toString()).isEqualTo(expected);
    }
    */

    @Test
    public void canSendNotificationBuffer() {
        // given
        String message1 = "Test message 1";
        String timestamp1 = notifier.getTime();
        String message2 = "Test message 2";
        String timestamp2 = notifier.getTime();
        String message3 = "Test message 2";
        String timestamp3 = notifier.getTime();

        // when
        notifier.add(notification, message1);
        notifier.add(notification, message2);
        notifier.add(notification, message3);

        // then
        String expected = timestamp1 + "\t\t" + message1 + "\n"
                + timestamp2 + "\t\t" + message2 + "\n"
                + timestamp3 + "\t\t" + message3 + "\n";
        assertThat(notification.toString()).isEqualTo(expected);
    }
}