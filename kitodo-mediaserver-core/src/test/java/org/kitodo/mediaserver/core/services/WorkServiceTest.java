package org.kitodo.mediaserver.core.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.IdentifierRepository;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for the JpaWorkDataWriter.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorkRepository.class)
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@ComponentScan(value = "org.kitodo.mediaserver.core.services")
@DataJpaTest
public class WorkServiceTest {

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private IdentifierRepository identifierRepository;

    @Autowired
    private WorkService workService;

    @Before
    public void init() {
        Work work1 = new Work("id1", "title1");

        Set<Identifier> identifiers = new HashSet<>();
        identifiers.add(new Identifier("urn1", "urn", work1));
        identifiers.add(new Identifier("doi1", "doi", work1));

        work1.setIdentifiers(identifiers);
        workRepository.save(work1);

    }

    @Test
    public void workWithIdentifiersIsImportedIfNotPresent() {
        //given
        Work newWork = new Work("newId", "title");
        Set<Identifier> newIdentifiers = new HashSet<>();
        newIdentifiers.add(new Identifier("myurn", "urn", newWork));
        newIdentifiers.add(new Identifier("mydoi", "doi", newWork));
        newWork.setIdentifiers(newIdentifiers);

        //when
        workService.importWork(newWork);

        //then
        Iterable<Work> works = workRepository.findAll();
        assertThat(works.spliterator().getExactSizeIfKnown()).isEqualTo(2);

        Iterable<Identifier> identifiers = identifierRepository.findAll();
        assertThat(identifiers.spliterator().getExactSizeIfKnown()).isEqualTo(4);

        Optional<Work> work = workRepository.findById("newId");
        assertThat(work.isPresent()).isTrue();
        assertThat(work.get().getTitle()).isEqualTo("title");
        assertThat(work.get().getIdentifiers()).isNotNull();
        assertThat(work.get().getIdentifiers().size()).isEqualTo(2);

    }

    @Test
    public void workWithIdentifiersIsUpdatedByImportIfPresent() {
        //given
        Work newWork = new Work("id1", "title");
        Set<Identifier> newIdentifiers = new HashSet<>();
        Identifier id1 = new Identifier("newurn", "urn", newWork);
        Identifier id2 = new Identifier("newdoi", "doi", newWork);
        newIdentifiers.add(id1);
        newIdentifiers.add(id2);
        newWork.setIdentifiers(newIdentifiers);

        //when
        workService.importWork(newWork);

        //then
        Iterable<Work> works = workRepository.findAll();
        assertThat(works.spliterator().getExactSizeIfKnown()).isEqualTo(1);

        Iterable<Identifier> identifiers = identifierRepository.findAll();
        assertThat(identifiers.spliterator().getExactSizeIfKnown()).isEqualTo(2);

        Optional<Work> work = workRepository.findById("id1");
        assertThat(work.isPresent()).isTrue();
        assertThat(work.get().getTitle()).isEqualTo("title");
        assertThat(work.get().getIdentifiers()).isNotNull();
        assertThat(work.get().getIdentifiers().size()).isEqualTo(2);
        assertThat(work.get().getIdentifiers().contains(id1));
        assertThat(work.get().getIdentifiers().contains(id2));

    }

}
