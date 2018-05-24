package org.kitodo.mediaserver.importer.checks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.IdentifierRepository;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for the jpa work checker.
 */
@RunWith(SpringRunner.class)
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@DataJpaTest
public class JpaWorkCheckerTest {

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private IdentifierRepository identifierRepository;

    private IWorkChecker jpaWorkChecker;

    @Before
    public void init() {
        Work work1 = new Work("id1", "title1");

        workRepository.save(work1);

        Set<Identifier> identifiers = new HashSet<>();
        identifiers.add(new Identifier("urn1", "urn", work1));
        identifiers.add(new Identifier("doi1", "doi", work1));

        identifierRepository.saveAll(identifiers);

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

    @Test(expected = ImporterException.class)
    public void exceptionWhenIdentifierPresentForDifferentWork() throws Exception {
        //given
        Work newWork = new Work("newId", "new title");
        Set<Identifier> identifiers = new HashSet<>();
        identifiers.add(new Identifier("urn1", "urn", newWork));
        newWork.setIdentifiers(identifiers);

        //when
        jpaWorkChecker.check(newWork);
    }

    @Test
    public void workReturnedWhenIdentifiersCongruentWithWork() throws Exception {
        //given
        Work newWork = new Work("id1", "new title");
        Set<Identifier> identifiers = new HashSet<>();
        identifiers.add(new Identifier("urn1", "urn", newWork));
        identifiers.add(new Identifier("someOtherDoi", "doi", newWork));
        newWork.setIdentifiers(identifiers);

        //when
        Work presentWork = jpaWorkChecker.check(newWork);

        //then
        assertThat(presentWork).isNotNull();
        assertThat(presentWork.getId()).isEqualTo(newWork.getId());
        assertThat(presentWork).isNotEqualTo(newWork);

    }

}
