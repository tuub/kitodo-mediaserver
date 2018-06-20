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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.User;
import org.kitodo.mediaserver.ui.config.WebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebMvcConfiguration.class)
@AutoConfigureMockMvc
@DataJpaTest // entityManager Bean
public class LoginControllerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    String username1 = "max";
    String password1 = "üpoi";
    String username2 = "ilse";
    String password2 = "!xyZ1";

    @Before
    public void init() {
        User user1 = new User(username1, passwordEncoder.encode(password1));
        User user2 = new User(username2, passwordEncoder.encode(password2));
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    public void loginSuccessfully() throws Exception {

        mockMvc
            .perform( post("/login")
                .with(csrf())
                .param("username", username1)
                .param("password", password1)
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/") );

    }

    @Test
    public void loginWithWrongPassword() throws Exception {

        mockMvc
            .perform( post("/login")
                .with(csrf())
                .param("username", username1)
                .param("password", "nope")
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/login?error") );

    }

    @Test
    public void loginWithOtherUsersPassword() throws Exception {

        mockMvc
            .perform( post("/login")
                .with(csrf())
                .param("username", username1)
                .param("password", password2)
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/login?error") );

    }

    @Test
    public void loginWithUnknownUser() throws Exception {

        mockMvc
            .perform( post("/login")
                .with(csrf())
                .param("username", "bertha")
                .param("password", "hu")
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/login?error") );

    }

    @Test
    public void loginWithEmptyPassword() throws Exception {

        mockMvc
            .perform( post("/login")
                .with(csrf())
                .param("username", username1)
                .param("password", "")
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/login?error") );

    }

}
