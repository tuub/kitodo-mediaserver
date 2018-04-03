package org.kitodo.mediaserver.core.db.repositories;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

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
    private Identifier identifier1, identifier2, identifier3;

    @Before
    public void init() {
        work1 = new Work("123", "This is my test title");
        work2 = new Work("124", "This is my second test title");

        identifier1 = new Identifier("doi", null, work1);
        identifier2 = new Identifier("urn", "urn", work1);
        identifier3 = new Identifier("doi2", "doi", work2);

        entityManager.persist(work1);
        entityManager.persist(work2);
        entityManager.persist(identifier1);
        entityManager.persist(identifier2);
        entityManager.persist(identifier3);
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
        assertThat(found123.isPresent());
        assertThat(found123.get()).isEqualTo(work1);

        Optional<Work> foundNonExisting = workRepository.findById("999");
        assertThat(!foundNonExisting.isPresent());
    }

    @Test
    public void testFindByIdentifiers() {
        Work foundDoi = workRepository.findByIdentifiers(new Identifier("doi", null));
        assertThat(foundDoi).isNotNull();
        assertThat(foundDoi).isEqualTo(work1);

        Work foundUrn = workRepository.findByIdentifiers(new Identifier("urn", null));
        assertThat(foundUrn).isNotNull();
        assertThat(foundUrn).isEqualTo(work1);

        Work foundDoi2 = workRepository.findByIdentifiers(new Identifier("doi2", "doi"));
        assertThat(foundDoi2).isNotNull();
        assertThat(foundDoi2).isEqualTo(work2);

        Work foundNoting = workRepository.findByIdentifiers(new Identifier("nothingInTheDb",null));
        assertThat(foundNoting).isNull();
    }
}
