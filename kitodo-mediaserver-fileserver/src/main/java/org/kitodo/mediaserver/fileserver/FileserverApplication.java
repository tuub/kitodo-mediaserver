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

package org.kitodo.mediaserver.fileserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Starter of the fileserver application.
 */
@SpringBootApplication
public class FileserverApplication extends SpringBootServletInitializer {

    /**
     * Starts the fileserver application when deployed as war in a separate servlet container.
     *
     * @param application the application
     * @return a builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application
                .sources(FileserverApplication.class)
                .properties(
                "spring.config.name:"
                        + "default,"
                        + "local,"
                        + "application");
    }

    /**
     * Starts the fileserver appplication when deployed with spring boot.
     *
     * @param args external arguments
     */
    public static void main(String[] args) {

        new SpringApplicationBuilder(FileserverApplication.class)
                .properties(
                        "spring.config.name:"
                                + "default,"
                                + "local,"
                                + "application")
                .build().run(args);
    }
}
