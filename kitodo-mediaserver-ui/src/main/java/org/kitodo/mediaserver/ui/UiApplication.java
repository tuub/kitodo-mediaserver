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

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * UI application starter.
 */
@SpringBootApplication
public class UiApplication extends SpringBootServletInitializer {

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
                        + "application");
    }
}
