package org.kitodo.mediaserver.core.services;

import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.TestConfiguration;
import org.kitodo.mediaserver.core.db.entities.Collection;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the collection repository.
 */
@SpringBootTest(classes = TestConfiguration.class)
@EnableAutoConfiguration
@EntityScan("org.kitodo.mediaserver.core.db.entities")
@RunWith(SpringRunner.class)
@DataJpaTest
public class WorkServiceCollectionTest {

    @Autowired
    private WorkService workService;

    private Work work1, work2, work3, work4;

    /**
     * Test case: works with collections are inserted to the database.
     * New collections should then be added automatically, and the works should be findable over the collections.
     */
    @Before
    public void init() {
        work1 = new Work("id1", "title1");
        work1.setCollections(new HashSet<Collection>() {{
            add(new Collection("coll1"));
            add(new Collection("coll2"));
        }});

        work2 = new Work("id2", "title2");
        work2.setCollections(new HashSet<Collection>() {{
            add(new Collection("coll1"));
            add(new Collection("coll3"));
        }});

        work3 = new Work("id3", "title3");
        work3.setCollections(new HashSet<Collection>() {{
            add(new Collection("coll2"));
        }});

        work4 = new Work("id4", "title4");

        workService.updateWork(work1);
        workService.updateWork(work2);
        workService.updateWork(work3);
        workService.updateWork(work4);
    }


    @Test
    public void coll1ShouldContainTwoWorks() {
        // given
        // works with collections as in init()

        // when
        List<Work> works = workService.getAllWorksInCollection("coll1");

        // then
        assertThat(works).isNotEmpty();
        assertThat(works.size()).isEqualTo(2);
    }

    @Test
    public void coll2ShouldContainTwoWorks() {
        // given
        // works with collections as in init()

        // when
        List<Work> works = workService.getAllWorksInCollection("coll2");

        // then
        assertThat(works).isNotEmpty();
        assertThat(works.size()).isEqualTo(2);
    }

    @Test
    public void coll3ShouldContainJustOneWorkWithId2() {
        // given
        // works with collections as in init()

        // when
        List<Work> works = workService.getAllWorksInCollection("coll3");

        // then
        assertThat(works).isNotEmpty();
        assertThat(works.size()).isEqualTo(1);
        assertThat((works.get(0).getId())).isEqualTo("id2");
    }

    @Test
    public void workUpdateShouldChangeCollection() {
        // given
        // works with collections as in init()
        work2.setCollections(new HashSet<Collection>() {{
            add(new Collection("coll2"));
        }});
        workService.updateWork(work2);

        // when
        List<Work> worksColl1 = workService.getAllWorksInCollection("coll1");
        List<Work> worksColl2 = workService.getAllWorksInCollection("coll2");
        List<Work> worksColl3 = workService.getAllWorksInCollection("coll3");

        // then
        assertThat(worksColl1).isNotEmpty();
        assertThat(worksColl1.size()).isEqualTo(1);

        assertThat(worksColl2).isNotEmpty();
        assertThat(worksColl2.size()).isEqualTo(3);

        assertThat(worksColl3).isEmpty();
    }

    @Test
    public void workDeletedShouldChangeCollection() {
        // given
        // works with collections as in init()
        workService.deleteWork(work1);

        // when
        List<Work> worksColl1 = workService.getAllWorksInCollection("coll1");
        List<Work> worksColl2 = workService.getAllWorksInCollection("coll2");
        List<Work> worksColl3 = workService.getAllWorksInCollection("coll3");

        // then
        assertThat(worksColl1).isNotEmpty();
        assertThat(worksColl1.size()).isEqualTo(1);

        assertThat(worksColl2).isNotEmpty();
        assertThat(worksColl2.size()).isEqualTo(1);

        assertThat(worksColl3).isNotEmpty();
        assertThat(worksColl3.size()).isEqualTo(1);
    }
}
