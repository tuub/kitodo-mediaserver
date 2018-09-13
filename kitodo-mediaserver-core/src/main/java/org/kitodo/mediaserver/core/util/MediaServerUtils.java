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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.stereotype.Component;

/**
 * Utilities for the media server.
 */
@Component
public class MediaServerUtils {

    /**
     * Gets the METS file for a work.
     *
     * @param work the work
     * @return the mets file
     * @throws FileNotFoundException if the mets file is not found
     */
    public File getMetsFileForWork(Work work) throws FileNotFoundException {
        return getMetsFileForWork(work, false);
    }

    /**
     * Gets the METS file for a work. If the work is disabled, there should be a backup METS file with the original content.
     *
     * @param work the work
     * @param original true=find the original METS file; false=the default METS file (which is reduced if the work is disabled)
     * @return the mets file
     * @throws FileNotFoundException if the mets file is not found
     */
    public File getMetsFileForWork(Work work, Boolean original) throws FileNotFoundException {

        File metsFile = forceGetMetsFileForWork(work, original);
        if (metsFile == null || !metsFile.isFile()) {
            throw new FileNotFoundException("Mets file not found at " + work.getPath());
        }

        return metsFile;
    }

    /**
     * Gets the original METS file for a work if the work is disabled.
     *
     * @param work the work
     * @return the mets file
     * @throws FileNotFoundException if the mets file is not found
     */
    public File getOriginalMetsFileForWork(Work work) throws FileNotFoundException {
        return getMetsFileForWork(work, true);
    }

    /**
     * Gets the METS file for a work. If the work is disabled, there should be a backup METS file with the original content.
     *
     * @param work the work
     * @param original true=find the original METS file; false=the default METS file (which is reduced if the work is disabled)
     * @return the mets file
     */
    public File forceGetMetsFileForWork(Work work, Boolean original) {
        if (work == null) {
            throw new IllegalArgumentException("The work may not be null.");
        }
        if (original == null) {
            throw new IllegalArgumentException("Parameter original must not be null.");
        }

        return new File(work.getPath(), work.getId() + (original ? "_original" : "") + ".xml");
    }

    /**
     * Gets a file of a work using an URL (typically from the mets file of the work).
     *
     * @param work the work
     * @param url the requested url
     * @param rootUrl the root url of the server
     * @return a file object
     * @throws Exception ny fatal errors
     */
    public File getWorkFileFromUrl(Work work, String url, String rootUrl) {
        if (work == null) {
            throw new IllegalArgumentException("The work may not be null.");
        }
        if (url == null) {
            throw new IllegalArgumentException("The url may not be null.");
        }

        if (url.startsWith("file://")) {
            return new File(StringUtils.substringAfter(url, "file://"));
        }
        if (url.startsWith(rootUrl)) {
            return new File(work.getPath() + StringUtils.substringAfter(url,rootUrl + work.getId()));
        }

        throw new IllegalArgumentException("The url '" + url + "' cannot be transformed."
                    + " The url must start with the fileserver root url " + rootUrl + " or file://");

    }

    /**
     * Gets an internal path to a file of a given work from a url using work id as separator.
     *
     * @param url the original url (e.g. from a mets file)
     * @param rootPath the root path (e.g. at import)
     * @param workId the work id
     * @return a path object with the result
     */
    public Path getInternalPathFromUrl(String url, String rootPath, String workId) {
        String separator = workId + "/";
        String internalWorkPath = separator + StringUtils.substringAfter(url, separator);
        return Paths.get(rootPath, internalWorkPath);
    }

    /**
     * Constructs the url of a mets file.
     *
     * @param rootUrl the rool url of the mediaserver
     * @param workId the id of the work
     * @return the url
     */
    public String getUrlStringForMetsFile(String rootUrl, String workId) {
        return rootUrl + "/" + workId + "/" + workId + ".xml";
    }

    /**
     * Check if all requiered parameter are present in a parameter map.
     *
     * @param parameter the parameter map
     * @param requiredParams all required parameter
     */
    public void checkForRequiredParameter(Map<String, String> parameter, String... requiredParams) {
        if (requiredParams.length > 0 && parameter == null) {
            throw new IllegalArgumentException("The parameter map is null.");
        }
        for (String param : requiredParams) {
            if (StringUtils.isEmpty(parameter.get(param))) {
                throw new IllegalArgumentException("Required parameter " + param + " is not in the parameter map.");
            }
        }
    }

    /**
     * Calls a url to perform a task (like indexing). Only returns a status code.
     *
     * @param urlString the url
     * @param args a map of arguments
     * @return a status code
     * @throws Exception by severe errors
     */
    public int callUrlWithArgs(String urlString, Map<String, String> args) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        int status;

        try {
            URIBuilder builder = new URIBuilder(urlString);
            for (String key : args.keySet()) {
                builder.addParameter(key, args.get(key));
            }
            HttpGet get = new HttpGet(builder.build());
            CloseableHttpResponse response = client.execute(get);

            status = response.getStatusLine().getStatusCode();
            response.close();
            client.close();

        } finally {
            client.close();
        }
        return status;
    }

}
