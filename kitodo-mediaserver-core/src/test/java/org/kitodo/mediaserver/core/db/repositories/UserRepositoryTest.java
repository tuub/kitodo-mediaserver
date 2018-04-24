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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the User repository.
 */
@SpringBootTest(classes = UserRepository.class)
@EnableAutoConfiguration
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1, user2;

    @Before
    public void init() {
        user1 = new User("admin", "Secret1!");
        user2 = new User("user", "Password1$%");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    public void testGetByUsername() {

        User foundAdmin = userRepository.getByUsername("admin");
        assertThat(foundAdmin).isNotNull();

        User foundUser = userRepository.getByUsername("user");
        assertThat(foundUser).isNotNull();

        User foundNothing = userRepository.getByUsername("fred");
        assertThat(foundNothing).isNull();
    }
}
