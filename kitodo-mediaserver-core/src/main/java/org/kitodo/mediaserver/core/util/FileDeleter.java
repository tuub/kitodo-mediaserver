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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

/**
 * Deletes files (and folders).
 */
@Component
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
        delete(path, age, false);
    }

    /**
     * Deletes all files in a path (and empty folders) that are older than given time.
     * @param path path to a directory which should be cleared
     * @param age timespam since last modified in seconds
     * @param keepRoot whether to keep the root folder or not
     * @throws IOException on access errors
     */
    public void delete(Path path, Long age, Boolean keepRoot) throws IOException {

        final Instant limit = Instant.now().minusSeconds(age != null ? age : 0L);

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Boolean isRoot = dir.equals(path);
                if ((!isRoot || !keepRoot) && !Files.list(dir).iterator().hasNext()) {
                    // directory is empty, delete it too
                    Files.deleteIfExists(dir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // check the modified date and delete if older than limit
                if (age == null || attrs.lastModifiedTime().toInstant().isBefore(limit)) {
                    Files.deleteIfExists(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }

}
