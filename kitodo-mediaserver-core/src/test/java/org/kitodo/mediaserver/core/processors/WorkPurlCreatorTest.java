package org.kitodo.mediaserver.core.processors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.config.IdentifierProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WorkPurlCreatorTest {

    WorkPurlCreator workPurlCreator;
    IdentifierProperties identifierProperties;

    @Before
    public void init() throws FileNotFoundException {

        workPurlCreator = new WorkPurlCreator();
        identifierProperties = mock(IdentifierProperties.class);

        when(identifierProperties.getReplacement()).thenReturn("_");
        when(identifierProperties.getReplacementRegex()).thenReturn("[-\\s]");
        when(identifierProperties.getLandingPagePattern()).thenReturn("https://example.org/{workId}");

        workPurlCreator.setIdentifierProperties(identifierProperties);
    }

    @Test
    public void hyphensAreReplaced() {
        // given
        Work work = new Work("BV-987243-2", "foo");

        // when
        String purl = workPurlCreator.describe(work);

        //then
        assertThat(purl).isEqualTo("https://example.org/BV_987243_2");
    }

    @Test
    public void spacesAndHyphensAreReplaced() {
        // given
        Work work = new Work("BV 987-243 2", "foo");

        // when
        String purl = workPurlCreator.describe(work);

        //then
        assertThat(purl).isEqualTo("https://example.org/BV_987_243_2");
    }

    @Test
    public void normalIdLeftIntact() {
        // given
        Work work = new Work("9900345867214687", "foo");

        // when
        String purl = workPurlCreator.describe(work);

        //then
        assertThat(purl).isEqualTo("https://example.org/9900345867214687");
    }

    @Test
    public void underscoreLeftIntact() {
        // given
        Work work = new Work("UATUB_717-0203", "foo");

        // when
        String purl = workPurlCreator.describe(work);

        //then
        assertThat(purl).isEqualTo("https://example.org/UATUB_717_0203");
    }

}
