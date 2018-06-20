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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Login controller.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * Requests to login form.
     *
     * @param request http request for session handling
     * @return the view name to be rendered
     */
    @GetMapping
    public String login(HttpServletRequest request) {
        // Ensure a session is created before login form is rendered
        request.getSession();
        return "login";
    }
}
