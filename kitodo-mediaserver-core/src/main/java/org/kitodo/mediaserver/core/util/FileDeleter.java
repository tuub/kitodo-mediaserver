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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.stream.Stream;
import org.springframework.util.FileSystemUtils;

/**
 * Deletes files (and folders).
 */
public class FileDeleter {

    /**
     * Deletes everything in a path.
     * @param path path to a directory which should be cleared
     * @throws IOException on access errors
     */
    public void delete(Path path) throws IOException {
        FileSystemUtils.deleteRecursively(path);
    }

    /**
     * Deletes all files in a path (and empty folders) that are older than given time.
     * @param path path to a directory which should be cleared
     * @param age timespam since last modified in seconds
     * @throws IOException on access errors
     */
    public void delete(Path path, Long age) throws IOException {

        Stream<Path> items = Files.list(path);
        Path item;
        Instant limit = Instant.now().minusSeconds(age);

        while (items.iterator().hasNext()) {
            item = items.iterator().next();
            if (Files.isDirectory(item)) {
                // recursively delete directory child items
                delete(item, age);
            } else {
                // check the modified date and delete if older than limit
                FileTime fileTime = Files.getLastModifiedTime(item);
                if (fileTime.toInstant().isBefore(limit)) {
                    Files.deleteIfExists(item);
                }
            }
        }

        if (!Files.list(path).iterator().hasNext()) {
            // directory is empty, delete it too
            Files.deleteIfExists(path);
        }

    }

}
