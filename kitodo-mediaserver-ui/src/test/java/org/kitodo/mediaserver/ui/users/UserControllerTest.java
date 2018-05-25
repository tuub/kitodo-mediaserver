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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.User;
import org.kitodo.mediaserver.core.db.repositories.UserRepository;
import org.kitodo.mediaserver.ui.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebMvcConfig.class)
@ComponentScan(value = "org.kitodo.mediaserver.core.services")
@AutoConfigureMockMvc
@DataJpaTest
public class UserControllerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testList() throws Exception {

        String username1 = "user1";
        String username2 = "user2";

        User user1 = new User(username1, username1);
        user1.setName(username1);
        User user2 = new User(username2, username2);
        user2.setName(username2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(2);

        mockMvc
            .perform( get("/users") )
            .andExpect( status().isOk() )
            .andExpect( model().attribute("users", hasSize(2)) )
            .andExpect( model().attribute("users", hasItem(
                allOf(
                    hasProperty("username", is(username1)),
                    hasProperty("name", is(username1)),
                    hasProperty("enabled", is(true))
                )
            )) )
            .andExpect( model().attribute("users", hasItem(
                allOf(
                    hasProperty("username", is(username2)),
                    hasProperty("name", is(username2)),
                    hasProperty("enabled", is(true))
                )
            )) )
            .andExpect( view().name("users/users") );

        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateSubmit() throws Exception {

        String username = "user";

        assertThat(userRepository.count()).isEqualTo(0);

        mockMvc
            .perform(
                post("/users/-new")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", username)
                    .param("name", username)
                    .param("password", username)
                    .sessionAttr("userDto", new UserDto())
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( model().attribute("userDto", hasProperty("username", is(username))) )
            .andExpect( model().attribute("userDto", hasProperty("name", is(username))) )
            .andExpect( model().attribute("userDto", hasProperty("password", is(username))) )
            .andExpect( redirectedUrl("/users") );

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditForm() throws Exception {

        String username1 = "user1";

        User user1 = new User(username1, username1);
        user1.setName(username1);
        entityManager.persist(user1);
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc
            .perform( get("/users/" + username1) )
            .andExpect( status().isOk() )
            .andExpect( model().attribute("userDto", hasProperty("username", is(username1))) )
            .andExpect( model().attribute("userDto", hasProperty("name", is(username1))) )
            .andExpect( model().attribute("userDto", hasProperty("enabled", is(user1.isEnabled()))) )
            .andExpect( view().name("users/edit") );

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditSubmit() throws Exception {

        String username1 = "user1";
        String newName = "abc";
        String newPassword = "test";
        Boolean newEnabled = true;

        UserDto user1 = new UserDto();
        user1.setUsername(username1);
        user1.setPassword(username1);
        user1.setEnabled(true);

        userService.saveUser(user1);

        User oldUser = userRepository.getByUsername(username1);
        String oldPassword = oldUser.getPassword();

        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc
            .perform(
                post("/users/" + username1)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("name", newName)
                    .param("password", newPassword)
                    .param("enabled", newEnabled ? "on" : "off")
                    .sessionAttr("userDto", user1)
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( model().attribute("userDto", hasProperty("username", is(username1))) )
            .andExpect( model().attribute("userDto", hasProperty("name", is(newName))) )
            .andExpect( model().attribute("userDto", hasProperty("password", is(newPassword))) )
            .andExpect( redirectedUrl("/users") );

        assertThat(userRepository.count()).isEqualTo(1);

        User editedUser = userRepository.getByUsername(username1);

        assertThat(editedUser.getName()).isEqualTo(newName);
        assertThat(editedUser.getPassword()).isNotEqualTo(oldPassword);
        assertThat(editedUser.isEnabled()).isEqualTo(newEnabled);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "user1")
    public void testEditDisableYourself() throws Exception {

        String username1 = "user1";
        Boolean newEnabled = false;

        UserDto user1 = new UserDto();
        user1.setUsername(username1);
        user1.setPassword(username1);
        user1.setEnabled(true);

        userService.saveUser(user1);

        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc
            .perform(
                post("/users/" + username1)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("name", username1)
                    .param("password", "")
                    .param("enabled", newEnabled ? "on" : "off")
                    .sessionAttr("userDto", user1)
            )
            .andExpect( status().isOk() )
            .andExpect( model().attribute("userDto", hasProperty("username", is(username1))) )
            .andExpect( model().attribute("userDto", hasProperty("name", is(username1))) )
            .andExpect( model().attribute("userDto", hasProperty("password", is(""))) )
            .andExpect( model().hasErrors() )
            .andExpect( view().name("users/edit") );

        assertThat(userRepository.count()).isEqualTo(1);

        User editedUser = userRepository.getByUsername(username1);

        assertThat(editedUser.isEnabled()).isNotEqualTo(newEnabled);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete() throws Exception {

        String username1 = "user1";
        String username2 = "user2";

        User user1 = new User(username1, username1);
        User user2 = new User(username2, username2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(2);

        mockMvc
            .perform(
                post("/users/" + username1 + "/delete")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/users") )
            .andExpect( flash().attribute("errorDelete", isEmptyOrNullString()) );

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "user1")
    public void deleteYourself() throws Exception {

        String username1 = "user1";

        User user1 = new User(username1, username1);
        entityManager.persist(user1);
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc
            .perform(
                post("/users/" + username1 + "/delete")
                    .with(csrf())
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/users") )
            .andExpect( flash().attribute("errorDelete", is("users.error.same_user")) );

        assertThat(userRepository.count()).isEqualTo(1);
    }

}
