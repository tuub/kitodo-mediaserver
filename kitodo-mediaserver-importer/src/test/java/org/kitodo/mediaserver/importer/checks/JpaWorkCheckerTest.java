package org.kitodo.mediaserver.importer.checks;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.ActionRepository;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for the jpa work checker.
 */
@RunWith(SpringRunner.class)
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@DataJpaTest
@SpringBootTest(classes = WorkRepository.class)
public class JpaWorkCheckerTest {

    @Autowired
    private WorkRepository workRepository;

    private IWorkChecker jpaWorkChecker;

    @Before
    public void init() {
        Work work1 = new Work("id1", "title1");

        workRepository.save(work1);

        jpaWorkChecker = new JpaWorkChecker();
        ((JpaWorkChecker) jpaWorkChecker).setWorkRepository(workRepository);
    }

    @Test
    public void workReturnedWhenPresent() throws Exception {
        //given
        Work newWork = new Work("id1", "new title");

        //when
        Work presentWork = jpaWorkChecker.check(newWork);

        //then
        assertThat(presentWork).isNotNull();
        assertThat(presentWork.getId()).isEqualTo(newWork.getId());
        assertThat(presentWork).isNotEqualTo(newWork);
    }

    @Test
    public void nullReturnedWhenNotPresent() throws Exception {
        //given
        Work newWork = new Work("newId", "new title");

        //when
        Work presentWork = jpaWorkChecker.check(newWork);

        //then
        assertThat(presentWork).isNull();
    }

}
