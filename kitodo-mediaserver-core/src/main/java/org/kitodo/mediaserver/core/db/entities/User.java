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

package org.kitodo.mediaserver.core.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Entity for user in UI.
 */
@Entity
public class User {

    @Id
    @NotBlank(message = "username is required")
    @Size(min = 2, max = 255)
    private String username;

    @NotBlank(message = "password is required")
    @Column(length = 60)
    private String password;

    private Integer enabled;

    protected User() {}

    public User(String username, String password) {
        this.username = username.trim();
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username.trim();
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isEnabled() {
        return enabled == 1;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled ? 1 : 0;
        return this;
    }

}
