package org.kitodo.mediaserver.core.db.repositories;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Collection;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the work1 repository.
 */
@SpringBootTest(classes = WorkRepository.class)
@EnableAutoConfiguration
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@RunWith(SpringRunner.class)
@DataJpaTest
public class WorkRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkRepository workRepository;

    private Work work1, work2;

    @Before
    public void init() {
        work1 = new Work("123", "This is my test title");
        work2 = new Work("124", "This is my second test title");

        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.flush();
    }

    @Test
    public void testFindByTitleContaining() {

        List<Work> foundTestTitle = workRepository.findByTitleContaining("test title");
        assertThat(foundTestTitle).isNotNull();
        assertThat(foundTestTitle.size()).isEqualTo(2);

        List<Work> foundSecond = workRepository.findByTitleContaining("second");
        assertThat(foundSecond).isNotNull();
        assertThat(foundSecond.size()).isEqualTo(1);
        assertThat(foundSecond.get(0)).isEqualTo(work2);

        List<Work> foundNotInAnyTitle = workRepository.findByTitleContaining("notInAnyTitle");
        assertThat(foundNotInAnyTitle).isNotNull();
        assertThat(foundNotInAnyTitle.size()).isEqualTo(0);

    }

    @Test
    public void testFindById() {
        Optional<Work> found123 = workRepository.findById("123");
        assertThat(found123.isPresent()).isTrue();
        assertThat(found123.get()).isEqualTo(work1);

        Optional<Work> foundNonExisting = workRepository.findById("999");
        assertThat(foundNonExisting.isPresent()).isFalse();
    }

    @Test
    public void testWorkWithCollections() {
        // given
        Set<Collection> collections = new HashSet<>();
        Collection fooCollection = new Collection("foo");
        Collection barCollection = new Collection("bar");
        collections.add(fooCollection);
        collections.add(barCollection);

        Work work = new Work("myId", "myTitle");
        work.setCollections(collections);

        workRepository.save(work);

        // when
        Optional<Work> foundWork = workRepository.findById("myId");

        //then
        assertThat(foundWork.isPresent()).isTrue();
        Work myWork = foundWork.get();
        assertThat(myWork).isNotNull();
        assertThat(myWork.getId()).isEqualTo("myId");
        assertThat(myWork.getTitle()).isEqualTo("myTitle");
        assertThat(myWork.getCollections()).isNotEmpty();
        assertThat(myWork.getCollections().size()).isEqualTo(2);
    }
}
