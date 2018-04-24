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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.kitodo.mediaserver.core.db.entities.User;

/**
 * User data transport object for form submission.
 */
public class UserDto {

    /**
     * Indicator for main validation properties.
     */
    interface ValidationMain {}

    /**
     * Indicator for password validation. To switch it off on editing.
     */
    interface ValidationPasswords {}

    @Size(min = 2, max = 255, message = "{users.error.username_length}", groups = ValidationMain.class)
    @Pattern(regexp = "^[A-Za-z0-9._]+$", message = "{users.error.username_format}", groups = ValidationMain.class)
    private String username;

    @NotBlank(message = "{users.error.password_blank}", groups = ValidationPasswords.class)
    private String password;

    @Size(max = 255, message = "{users.error.name_length}", groups = ValidationMain.class)
    private String name;

    private Boolean enabled = true;

    public UserDto() { }

    /**
     * Create instance based on UserDto.
     * @param user the corresponding UserDto object
     */
    public UserDto(User user) {
        this.setUsername(user.getUsername());
        this.setName(user.getName());
        this.setEnabled(user.isEnabled());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
