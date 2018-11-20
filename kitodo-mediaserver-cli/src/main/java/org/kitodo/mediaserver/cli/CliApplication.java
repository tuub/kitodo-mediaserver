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

package org.kitodo.mediaserver.cli;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CliApplication {

    /**
     * Starts the CLI application.
     *
     * @param args external arguments
     */
    public static void main(String[] args) {

        String appDir = getApplicationDirectory();

        new SpringApplicationBuilder(CliApplication.class)
            .properties(
                "spring.config.name:"
                    + "default,"
                    + "local,"
                    + "secrets,"
                    + "application,"
                    + "dev",
                "spring.config.additional-location:" + appDir + "/," + appDir + "/config/")
            .build().run(args);
    }

    /**
     * Get the directory where this application is located.
     *
     * @return file system path
     */
    private static String getApplicationDirectory() {

        String path = CliApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // If we are in a JAR file, remove the part from inside the JAR
        path = path.split("!")[0];

        // If we are in a JAR file, remove the JAR filename
        if (!Files.isDirectory(Paths.get(path))) {
            path = Paths.get(path).getParent().toString();
        }

        return path;
    }
}
