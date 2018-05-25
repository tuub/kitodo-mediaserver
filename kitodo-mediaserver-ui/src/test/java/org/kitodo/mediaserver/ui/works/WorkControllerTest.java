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

package org.kitodo.mediaserver.ui.works;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.ui.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebMvcConfig.class)
@ComponentScan(value = "org.kitodo.mediaserver.core.services")
@AutoConfigureMockMvc
@DataJpaTest // entityManager Bean
@EnableSpringDataWebSupport // Pageable initialisation
public class WorkControllerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testList() throws Exception {

        String workId1 = "work1";
        String workTitle1 = "Title1";
        Boolean workEnabled1 = true;
        String workId2 = "work2";
        String workTitle2 = "Title2";
        Boolean workEnabled2 = true;

        Work work1 = new Work(workId1, workTitle1);
        work1.setEnabled(workEnabled1);
        Work work2 = new Work(workId2, workTitle2);
        work2.setEnabled(workEnabled2);
        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.flush();

        assertThat(workRepository.count()).isEqualTo(2);

        mockMvc
            .perform( get("/works") )
            .andExpect( status().isOk() )
            .andExpect( model().attribute("page", hasProperty("content", hasSize(2))) )
            .andExpect( model().attribute("page", hasProperty("content", hasItem(
                allOf(
                    hasProperty("id", is(workId1)),
                    hasProperty("title", is(workTitle1)),
                    hasProperty("enabled", is(workEnabled1))
                )
            ))) )
            .andExpect( model().attribute("page", hasProperty("content", hasItem(
                allOf(
                    hasProperty("id", is(workId2)),
                    hasProperty("title", is(workTitle2)),
                    hasProperty("enabled", is(workEnabled2))
                )
            ))) )
            .andExpect( view().name("works/works") );

        assertThat(workRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearchWork() throws Exception {

        String workId1 = "work1";
        String workTitle1 = "Title1";
        Boolean workEnabled1 = true;
        String workId2 = "work2";
        String workTitle2 = "Title huh";
        Boolean workEnabled2 = true;

        Work work1 = new Work(workId1, workTitle1);
        work1.setEnabled(workEnabled1);
        Work work2 = new Work(workId2, workTitle2);
        work2.setEnabled(workEnabled2);
        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.flush();

        assertThat(workRepository.count()).isEqualTo(2);

        mockMvc
            .perform( get("/works").param("search", "huh") )
            .andExpect( status().isOk() )
            .andExpect( model().attribute("page", hasProperty("content", hasSize(1))) )
            .andExpect( model().attribute("page", hasProperty("content", hasItem(
                allOf(
                    hasProperty("id", is(workId2)),
                    hasProperty("title", is(workTitle2)),
                    hasProperty("enabled", is(workEnabled2))
                )
            ))) )
            .andExpect( view().name("works/works") );

        assertThat(workRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testLockWork() throws Exception {

        String workId1 = "work1";
        String workTitle1 = "Title1";
        Boolean workEnabled1 = true;
        String workId2 = "work2";
        String workTitle2 = "Title huh";
        Boolean workEnabled2 = true;

        Work work1 = new Work(workId1, workTitle1);
        work1.setEnabled(workEnabled1);
        Work work2 = new Work(workId2, workTitle2);
        work2.setEnabled(workEnabled2);
        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.flush();

        assertThat(workRepository.count()).isEqualTo(2);

        mockMvc
            .perform( post("/works/" + work2.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("enabled", "off")
                .param("comment", "")
            )
            .andExpect( status().is3xxRedirection() )
            .andExpect( redirectedUrl("/works") );

        assertThat(work2.isEnabled()).isFalse();
        assertThat(workRepository.count()).isEqualTo(2);
    }

}
