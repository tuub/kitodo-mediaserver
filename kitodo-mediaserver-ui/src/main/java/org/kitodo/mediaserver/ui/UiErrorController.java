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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A global error handle controller.
 */
@ControllerAdvice
public class UiErrorController implements ErrorController {

    private static Logger LOGGER = LoggerFactory.getLogger(UiErrorController.class);

    /**
     * A global "Internal server error" handler.
     *
     * @param throwable the thrown exception.
     * @param model the view model
     * @return the view name to be rendered
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String internalServerError(final Throwable throwable, final Model model) {

        String error = "Unknown error";

        if (throwable != null) {
            LOGGER.error("Exception in UI application.", throwable);
            error = throwable.toString();
        }

        model.addAttribute("error", error);

        // Don't use "error" here. It will use a spring internal template.
        return "uierror";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
