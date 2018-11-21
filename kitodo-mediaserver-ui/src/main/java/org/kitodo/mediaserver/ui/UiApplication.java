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

package org.kitodo.mediaserver.ui;

import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;

/**
 * UI application starter.
 */
@SpringBootApplication
public class UiApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiApplication.class);

    private MediaServerUtils mediaServerUtils;

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    /**
     * Starts the UI application when deployed as war in a separate servlet container.
     *
     * @param builder the application builder
     * @return a builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    /**
     * Everything starts here.
     * @param args program arguments
     */
    public static void main(String[] args) {
        configureApplication(new SpringApplicationBuilder()).run(args);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder
            .sources(UiApplication.class)
            .properties("spring.config.name:"
                        + "default,"
                        + "local,"
                        + "secrets,"
                        + "application,"
                        + "dev");
    }

    /**
     * Run after application start.
     *
     * @param event ApplicationReadyEvent
     */
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        if (!mediaServerUtils.isLocalAppConfigLoaded()) {
            LOGGER.warn("No local configuration file loaded (local.yml) or the file is empty. This might cause unexpected problems.");
        }
    }

}
