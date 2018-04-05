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

package org.kitodo.mediaserver.core.db.repositories;

import java.util.List;
import org.kitodo.mediaserver.core.db.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for UI users.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Finds User by username.
     * @param username the username to search for
     * @return a User
     */
    User getByUsername(String username);

    /**
     * Finds all users sorted by username.
     * @return a list of all users
     */
    List<User> findAllByOrderByUsernameAsc();
}
