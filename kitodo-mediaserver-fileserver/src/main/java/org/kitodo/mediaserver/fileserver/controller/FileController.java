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

package org.kitodo.mediaserver.fileserver.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.actions.SingleFileConvertAction;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.exceptions.HttpForbiddenException;
import org.kitodo.mediaserver.core.exceptions.HttpNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A controller responsible for delivering files, if necessary converting from a master file.
 *
 * <p>
 * The controller checks if a work is enabled before delivering files. If the work is disabled,
 * a 403 forbidden response is returned.
 * If a file is not present and cannot be produced, a 404 not found response is returned.
 */
@Controller
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileserverProperties fileserverProperties;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private SingleFileConvertAction singleFileConvertAction;

    /**
     * Controller method mapped to a path with a workId.
     *
     * <p>
     * Delivers a file if present. If not present, the file is produced from a master file using the
     * information in the METS-file of the work.
     *
     * @param workId the id of the work read from the path
     * @param request the http request
     * @param response the http response
     * @throws HttpForbiddenException if disabling is configured and the work is disabled
     * @throws HttpNotFoundException if the file is not found and couldn't be produced
     */
    @GetMapping(value = "${fileserver.filePathPattern}")
    public void getFile(
            @PathVariable("workId") String workId,
            HttpServletRequest request,
            HttpServletResponse response)
            throws HttpForbiddenException, HttpNotFoundException {

        Work work;
        Optional<Work> optionalWork = workRepository.findById(workId);

        if (!optionalWork.isPresent()) {
            String message = "Work with id " + workId + " not found";
            LOGGER.info(message);
            throw new HttpNotFoundException(message);

        } else {
            work = optionalWork.get();
            if (!work.isEnabled()) {
                String message = "Work with id " + workId + " is disabled";
                LOGGER.info(message);
                throw new HttpForbiddenException(message);
            }
        }
        String completePath = request.getRequestURL().toString();

        String derivativePath = StringUtils.substringAfter(completePath, workId);

        File derivative = new File(work.getPath(), derivativePath);

        if (!derivative.exists()) {
            derivative = new File(fileserverProperties.getCachePath(), workId + "/" + derivativePath);
        }

        InputStream inputStream = null;
        try {
            if (derivative.exists() && derivative.isFile()) {

                // Perform a touch on the file to set last accessed time
                FileUtils.touch(derivative);

                inputStream = new FileInputStream(derivative);
                //response.setContentType(); TODO

                String message = "Delivering already present file " + completePath + " from location "
                        + derivative.getAbsolutePath();
                LOGGER.info(message);

            } else {

                String message = "The requested file " + completePath + " allegedly located at "
                        + derivative.getAbsolutePath() + " does not exist. Trying to convert the file.";
                LOGGER.info(message);

                //call conversion handler to produce the file
                try {
                    Map<String, String> parameterMap = new HashMap<>();
                    parameterMap.put("derivativePath", workId + derivativePath);
                    parameterMap.put("requestUrl", completePath);

                    inputStream = singleFileConvertAction.perform(work, parameterMap);

                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }

            }

            if (inputStream == null) {
                throw new HttpNotFoundException("File not found and could not be converted.");
            }

            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
            response.getOutputStream().close();
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            throw new HttpNotFoundException(e.toString());
        }
    }
}
