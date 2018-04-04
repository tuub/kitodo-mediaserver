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

/**
 * UI application starter.
 */
@SpringBootApplication
public class UiApplication {

    /**
     * Everything starts here.
     * @param args program arguments
     */
    public static void main(String[] args) {

        new SpringApplicationBuilder(UiApplication.class)
                .properties(
                        "spring.config.name:"
                                + "default,"
                                + "local,"
                                + "application")
                .build().run(args);
    }
}
