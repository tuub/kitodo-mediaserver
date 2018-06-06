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

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.db.entities.Work;
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
@SpringBootTest(classes = ActionRepository.class)
@EnableAutoConfiguration
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@RunWith(SpringRunner.class)
@DataJpaTest
public class ActionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActionRepository actionRepository;

    Work work1;
    Work work2;
    Map<String, String> parameter1;
    Map<String, String> parameter2;
    Map<String, String> parameter3;
    ActionData actionData1;
    ActionData actionData2;
    ActionData actionData3;

    @Before
    public void init() {
        work1 = new Work("xyz", "title1");
        work2 = new Work("abc", "title2");

        parameter1 = new HashMap<>();
        parameter1.put("key1", "value1");

        parameter2 = new HashMap<>();
        parameter2.put("key2", "value2");

        parameter3 = new HashMap<>();
        parameter3.put("key3", "value3");

        actionData1 = new ActionData(work1, "action1", parameter1);
        actionData2 = new ActionData(work2, "action2", parameter2);
        actionData3 = new ActionData(work2, "action2", parameter3);

        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.persist(actionData1);
        entityManager.persist(actionData2);
        entityManager.persist(actionData3);
    }

    @After
    public void cleanUp() {
        for (Object entity : Arrays.asList(actionData1, actionData2, actionData3, work1, work2)) {
            try {
                entityManager.remove(entity);
            } catch (Exception ex) {}
        }
    }

    @Test
    public void findLastFinishedAction() {

        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));
        actionData1.setEndTime(Instant.now().minusSeconds(80));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));
        actionData3.setStartTime(Instant.now().minusSeconds(30));
        actionData3.setEndTime(Instant.now().minusSeconds(20));

        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameOrderByEndTimeDesc(work2, "action2");

        // then
        assertThat(actionDatas).isNotNull();
        assertThat(actionDatas.size()).isEqualTo(2);
        assertThat(actionDatas).containsExactly(actionData3, actionData2);
        assertThat(actionDatas.get(0).getParameter()).containsKey("key3");
        assertThat(actionDatas.get(0).getParameter().get("key3")).isEqualTo("value3");
    }

    @Test
    public void findUnfinishedAction() {

        // given
        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));

        // when
        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameAndEndTimeIsNull(work2, "action2");

        // then
        assertThat(actionDatas).isNotNull();
        assertThat(actionDatas.size()).isEqualTo(1);
        assertThat(actionDatas).containsExactly(actionData3);
    }

    @Test
    public void findRunningAction() {

        // given
        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));

        // when
        List<ActionData> actionDatas = actionRepository.findByWorkAndActionNameAndStartTimeIsNotNullAndEndTimeIsNull(work1, "action1");

        // then
        assertThat(actionDatas).isNotNull();
        assertThat(actionDatas.size()).isEqualTo(1);
        assertThat(actionDatas).containsExactly(actionData1);
    }
}
