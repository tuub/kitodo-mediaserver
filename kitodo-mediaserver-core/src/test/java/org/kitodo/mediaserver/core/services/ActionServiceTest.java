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

package org.kitodo.mediaserver.core.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.TestConfiguration;
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ActionServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test for the User repository.
 */
@SpringBootTest(classes = TestConfiguration.class)
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@RunWith(SpringRunner.class)
@ComponentScan(value = "org.kitodo.mediaserver.core.services")
@DataJpaTest
public class ActionServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActionService actionService;

    Work work1;
    Work work2;
    Map<String, String> parameter1;
    Map<String, String> parameter2;
    Map<String, String> parameter3;
    ActionData actionData1;
    ActionData actionData2;
    ActionData actionData3;

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
    }

    public void persist() {
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
    public void requestAction() throws ActionServiceException {

        // given
        init();
        entityManager.persist(work1);
        // empty action table

        // when
        actionService.request(work1, "action1", parameter1);

        // then
        ActionData actionData = actionService.getUnperformedAction(work1, "action1", parameter1);
        assertThat(actionData).isNotNull();
        assertThat(actionData.getWork()).isEqualTo(work1);
        assertThat(actionData.getParameter()).containsKey("key1");
        assertThat(actionData.getParameter().get("key1")).isEqualTo("value1");
        assertThat(actionData.getRequestTime()).isBetween(Instant.now().minusSeconds(2), Instant.now());
        assertThat(actionData.getStartTime()).isNull();
        assertThat(actionData.getEndTime()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestWithNull() throws ActionServiceException {

        // given
        // empty DB

        // when
        actionService.request(null, "action1", null);

        // then
        // IllegalArgumentException
    }

    @Test(expected = ActionServiceException.class)
    public void requestActionAlreadyRequested() throws ActionServiceException {

        // given
        init();
        actionData1.setRequestTime(Instant.now());

        entityManager.persist(work1);
        entityManager.persist(actionData1);

        // when
        actionService.request(work1, "action1", parameter1);

        // then
        // ActionServiceException -> already requested
    }

    @Test
    public void performRequestedAction() throws Exception {

        // given
        init();

        // for mockAction have a look at TestConfiguration
        actionData1 = new ActionData(work1, "mockAction", parameter1);
        actionData1.setRequestTime(Instant.now());

        entityManager.persist(work1);
        entityManager.persist(actionData1);

        // when
        Object action = actionService.performRequested(actionData1);

        // then
        assertThat(action).isNotNull();
        assertThat(action).isInstanceOf(String.class);
        assertThat(action).isEqualTo("performed");
        assertThat(actionData1.getStartTime()).isBetween(Instant.now().minusSeconds(2), Instant.now());
        assertThat(actionData1.getEndTime()).isBetween(Instant.now().minusSeconds(2), Instant.now());
    }

    @Test
    public void shouldPerformActionImmediately() throws Exception {
        //given
        init();

        //when
        Object action = actionService.performImmediately(work1, "mockAction", null);

        //then
        assertThat(action).isNotNull();
        assertThat(action).isInstanceOf(String.class);
        assertThat(action).isEqualTo("performed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void performImmediatelyWithNoWorkThrowsException() throws Exception {
        //given
        init();

        //when
        actionService.performImmediately(null, "mockAction", null);
    }

    @Test(expected = ClassNotFoundException.class)
    public void performImmediatelyWithInvalidActionThrowsException() throws Exception {
        //given
        init();

        //when
        actionService.performImmediately(work1, "invalidAction", null);
    }

    @Test(expected = ActionServiceException.class)
    public void performImmediatelyWithBeanNotAnActionThrowsException() throws Exception {
        //given
        init();

        //when
        actionService.performImmediately(work1, "mockNoAction", null);
    }

    @Test(expected = ClassNotFoundException.class)
    public void performRequestedActionWithInvalidActionName() throws Exception {

        // given
        init();

        // for mockAction have a look at TestConfiguration
        actionData1 = new ActionData(work1, "invalidAction", parameter1);
        actionData1.setRequestTime(Instant.now());

        entityManager.persist(work1);
        entityManager.persist(actionData1);

        // when
        Object action = actionService.performRequested(actionData1);

        // then
        // ClassNotFoundException
    }

    @Test(expected = ActionServiceException.class)
    public void performRequestedActionWithInvalidActionBean() throws Exception {

        // given
        init();

        // for mockAction have a look at TestConfiguration
        actionData1 = new ActionData(work1, "mockNoAction", parameter1);
        actionData1.setRequestTime(Instant.now());

        entityManager.persist(work1);
        entityManager.persist(actionData1);

        // when
        Object action = actionService.performRequested(actionData1);

        // then
        // ActionServiceException -> not an IAction bean
    }

    @Test(expected = ActionServiceException.class)
    public void performRequestedActionAlreadyRunning() throws Exception {

        // given
        init();

        // for mockAction have a look at TestConfiguration
        actionData1 = new ActionData(work1, "mockAction", parameter1);
        actionData1.setRequestTime(Instant.now());
        actionData1.setStartTime(Instant.now());

        entityManager.persist(work1);
        entityManager.persist(actionData1);

        actionData2 = new ActionData(work1, "mockAction", parameter1);
        actionData2.setRequestTime(Instant.now());

        // when
        Object action = actionService.performRequested(actionData2);

        // then
        // ActionServiceException -> already running
    }

    @Test
    public void getLastPerformedAction() {

        // given
        init();
        persist();
        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));

        // when
        ActionData actionData = actionService.getLastPerformedAction(work2, "action2");

        // then
        assertThat(actionData).isNotNull();
        assertThat(actionData.getWork()).isEqualTo(work2);
        assertThat(actionData).isEqualTo(actionData2);
        assertThat(actionData.getParameter()).containsKey("key2");
        assertThat(actionData.getParameter().get("key2")).isEqualTo("value2");
    }

    @Test
    public void getUnperformedAction() {

        // given
        init();
        persist();
        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));

        // when
        ActionData actionData = actionService.getUnperformedAction(work2, "action2", parameter3);

        // then
        assertThat(actionData).isNotNull();
        assertThat(actionData.getWork()).isEqualTo(work2);
        assertThat(actionData).isEqualTo(actionData3);
        assertThat(actionData.getParameter()).containsKey("key3");
        assertThat(actionData.getParameter().get("key3")).isEqualTo("value3");
    }

    @Test
    public void getUnperformedActionWithWorkNull() {

        // given
        init();
        persist();

        // when
        ActionData actionData = actionService.getUnperformedAction(null, "action2", parameter3);

        // then
        assertThat(actionData).isNull();
    }

    @Test
    public void getUnperformedActionWithParameterNull() {

        // given
        init();
        persist();

        // when
        ActionData actionData = actionService.getUnperformedAction(work2, "action2", null);

        // then
        assertThat(actionData).isNull();
    }

    @Test
    public void getUnperformedActionWithActionNameNull() {

        // given
        init();
        persist();

        // when
        ActionData actionData = actionService.getUnperformedAction(work2, null, parameter3);

        // then
        assertThat(actionData).isNull();
    }

    @Test
    public void getRunningAction() {

        // given
        init();
        persist();
        actionData1.setRequestTime(Instant.now().minusSeconds(100));
        actionData1.setStartTime(Instant.now().minusSeconds(90));
        actionData1.setEndTime(Instant.now().minusSeconds(80));

        actionData2.setRequestTime(Instant.now().minusSeconds(70));
        actionData2.setStartTime(Instant.now().minusSeconds(60));
        actionData2.setEndTime(Instant.now().minusSeconds(50));

        actionData3.setRequestTime(Instant.now().minusSeconds(40));
        actionData3.setStartTime(Instant.now().minusSeconds(30));

        // when
        ActionData actionData = actionService.getRunningAction(work2, "action2", parameter3);

        // then
        assertThat(actionData).isNotNull();
        assertThat(actionData.getWork()).isEqualTo(work2);
        assertThat(actionData).isEqualTo(actionData3);
        assertThat(actionData.getParameter()).containsKey("key3");
        assertThat(actionData.getParameter().get("key3")).isEqualTo("value3");
    }

}
