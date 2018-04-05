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

import java.util.List;
import org.kitodo.mediaserver.core.db.entities.User;
import org.kitodo.mediaserver.core.db.repositories.UserRepository;
import org.kitodo.mediaserver.ui.exceptions.UserExistsException;
import org.kitodo.mediaserver.ui.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Manage users.
 */
@Service
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gets all users.
     * @return List of users
     */
    public List<User> findAll() {
        return userRepository.findAllByOrderByUsernameAsc();
    }

    /**
     * Gets a single user.
     * @param username username of desired user
     * @return the user object
     */
    public User getUser(String username) {
        return userRepository.getByUsername(username);
    }

    /**
     * Saves a new user.
     * @param userDto user data
     * @throws UserExistsException when a user with the desired username already exists
     */
    public void saveUser(UserDto userDto) throws UserExistsException {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setEnabled(userDto.getEnabled());
        User existingUser = userRepository.getByUsername(user.getUsername());
        if (existingUser != null) {
            throw new UserExistsException();
        }
        userRepository.save(user);
    }

    /**
     * Updates an existing user.
     * @param userDto user data
     * @throws UserNotFoundException when the desired user doesn't exist
     */
    public void updateUser(UserDto userDto) throws UserNotFoundException {
        User user = userRepository.getByUsername(userDto.getUsername());
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (!userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setName(userDto.getName());
        user.setEnabled(userDto.getEnabled());
        userRepository.save(user);
    }

    /**
     * Deletes a user.
     * @param username username of desired user
     */
    public void deleteUser(String username) {
        User user = userRepository.getByUsername(username);
        if (user == null) {
            return;
        }
        userRepository.delete(user);
    }
}
