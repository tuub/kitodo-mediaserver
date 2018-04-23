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

package org.kitodo.mediaserver.ui.users;

import java.security.Principal;
import java.util.List;
import org.kitodo.mediaserver.core.db.entities.User;
import org.kitodo.mediaserver.ui.exceptions.UserExistsException;
import org.kitodo.mediaserver.ui.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User management.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all users.
     * @param model the model for view
     * @return view name
     */
    @RequestMapping
    public String list(Model model, @ModelAttribute("errorDelete") String errorDelete) {

        if (StringUtils.hasText(errorDelete)) {
            model.addAttribute("error", errorDelete);
        }

        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        return "users/users";
    }

    /**
     * Show the form to create new users.
     * @param model Model for view
     * @return view name
     */
    @GetMapping(value = "/-new")
    // use "-new" because "-" is not allowed in usernames
    public String createForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("userDto", userDto);
        return "users/new";
    }

    /**
     * Creates a new user.
     * @param model the model for view
     * @param userDto User data
     * @param bindingResult validation results
     * @return view name
     */
    @PostMapping(value = "/-new")
    // use "-new" because "-" is not allowed in usernames
    public String createSubmit(
        Model model,
        @ModelAttribute @Validated({UserDto.ValidationMain.class, UserDto.ValidationPasswords.class}) UserDto userDto,
        BindingResult bindingResult
    ) {
        if (!bindingResult.hasErrors()) {
            try {
                userService.saveUser(userDto);
            } catch (UserExistsException e) {
                bindingResult.rejectValue("username", "users.error.user_exists", "users.error.user_exists");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "users/new";
        }
        return "redirect:/users";
    }

    /**
     * Gets the user edit form.
     * @param model the model for view
     * @param username the username that should be edited
     * @return view name
     */
    @GetMapping(value = "/{username}")
    public String editForm(Model model, @PathVariable String username) {
        User user = userService.getUser(username);
        UserDto userDto = new UserDto(user);
        model.addAttribute("userDto", userDto);
        return "users/edit";
    }

    /**
     * Edits the user.
     * @param model the model for view
     * @param username username that should be edited
     * @param userDto user data
     * @param bindingResult validation results
     * @return view name
     */
    @PostMapping(value = "/{username}")
    public String editSubmit(Model model,
                             @PathVariable String username,
                             @ModelAttribute @Validated(UserDto.ValidationMain.class) UserDto userDto,
                             BindingResult bindingResult,
                             Principal principal
    ) {

        // Deactivating yourself is forbidden
        if (username.equals(principal.getName()) && !userDto.getEnabled()) {
            bindingResult.rejectValue("enabled", "users.error.same_user", "users.error.same_user");
        }

        if (!bindingResult.hasErrors()) {
            try {
                userDto.setUsername(username);
                userService.updateUser(userDto);
            } catch (UserNotFoundException e) {
                bindingResult.reject("users.error.username_not_found");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "users/edit";
        }
        return "redirect:/users";
    }

    /**
     * Deletes a user.
     * @param username username that should be deleted
     * @param redirectAttributes request attributes
     * @param principal the user that is currently logged in
     * @return view name
     */
    @PostMapping(value = "/{username}/delete")
    public String deleteSubmit(@PathVariable String username, RedirectAttributes redirectAttributes, Principal principal) {

        // Don't delete yourself
        if (username.equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorDelete", "users.error.same_user");
        } else {
            userService.deleteUser(username);
        }
        return "redirect:/users";
    }
}
