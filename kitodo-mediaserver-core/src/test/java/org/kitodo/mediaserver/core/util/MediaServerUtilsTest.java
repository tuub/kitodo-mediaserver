package org.kitodo.mediaserver.core.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Before;
import org.junit.Test;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.conversion.FileEntry;
import org.kitodo.mediaserver.core.db.entities.Work;

/**
 * Tests for the utilities.
 */
public class MediaServerUtilsTest {

    private MediaServerUtils mediaServerUtils;
    private Map<String, String> parameter;
    private Work work;
    private String workPath = "/work/path/id/";

    @Before
    public void init() {
        mediaServerUtils = new MediaServerUtils();

        parameter = new HashMap<>();
        parameter.put("foo", "bar");
        parameter.put("bar", "");
        parameter.put("hink", null);

        work = new Work("id", "title");
        work.setPath(workPath);
    }

    @Test
    public void shouldReturnThePathWithoutProtocol() {
        //given
        String url = "file:///my/path";

        //when
        File result = mediaServerUtils.getWorkFileFromUrl(work, url, null);

        //then
        assertThat(result.getAbsolutePath()).isEqualTo("/my/path");
    }

    @Test
    public void shouldReturnThePathAfterSite() {
        //given
        String url = "https://www.example.org/files/id/jpeg/hepp.jpg";
        String rootUrl = "https://www.example.org/files/";

        //when
        File result = mediaServerUtils.getWorkFileFromUrl(work, url, rootUrl);

        //then
        assertThat(result.getAbsolutePath()).isEqualTo(workPath + "jpeg/hepp.jpg");
    }

    @Test
    public void shouldReturnAnInternalPath() {
        //given
        String url = "https://www.example.org/files/myWorkId/jpeg/hepp.jpg";
        String workId = "myWorkId";
        String rootUrl = "/path/to/import/temp";

        //when
        Path result = mediaServerUtils.getInternalPathFromUrl(url, rootUrl, workId);

        //then
        assertThat(result).isEqualTo(Paths.get("/path/to/import/temp/myWorkId/jpeg/hepp.jpg"));
    }

    @Test
    public void shouldReturnUrlForMetsFile() {
        //given
        String workid = "123456";
        String rootUrl = "https://example.org/files";

        //when
        String url = mediaServerUtils.getUrlStringForMetsFile(rootUrl, workid);

        //then
        assertThat(url).isEqualTo("https://example.org/files/123456/123456.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNonPresentArgument() {
        mediaServerUtils.checkForRequiredParameter(parameter, "kilroy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyArgument() {
        mediaServerUtils.checkForRequiredParameter(parameter, "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullArgument() {
        mediaServerUtils.checkForRequiredParameter(parameter, "hink");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForOneNonPresentArgument() {
        mediaServerUtils.checkForRequiredParameter(parameter, "foo", "qwert");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullParameterMap() {
        mediaServerUtils.checkForRequiredParameter(null, "foo");
    }

    @Test
    public void shouldThrowNoExceptionByPresentArgument() {
        mediaServerUtils.checkForRequiredParameter(parameter, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullWork() {
        mediaServerUtils.getWorkFileFromUrl(null, "url", "rootUrl");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullUrl() {
        mediaServerUtils.getWorkFileFromUrl(work, null, "rootUrl");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidUrl() {
        mediaServerUtils.getWorkFileFromUrl(work, "bullshit", "rootUrl");
    }

    @Test
    public void parseMetsFilesResult() {
        // given
        FileserverProperties fileserverProperties = new FileserverProperties();
        fileserverProperties.setRootUrl("http://a/");
        mediaServerUtils.setFileserverProperties(fileserverProperties);

        Map<String, String> metsResult = new HashMap<>();
        metsResult.put("1", "http://a/UA123/image1.tif|image/tiff|image/jpeg|http://a/UA123/fulltext1.xml");
        metsResult.put("2", "http://a/UA123/image2.tif|image/tiff|image/jpeg|http://a/UA123/fulltext2.xml");
        metsResult.put("3", "http://a/UA123/image3.tif|image/tiff||http://a/UA123/fulltext3.xml");
        metsResult.put("4", "http://a/UA123/image4.tif|image/tiff||");

        Work work = new Work("UA123", "Flugblatt");
        work.setPath("/srv/path/UA123");

        // when
        TreeMap<Integer, Map<String, FileEntry>> pages = mediaServerUtils.parseMetsFilesResult(metsResult, work);

        //then
        assertThat(pages).isNotNull().hasSize(4);

        org.hamcrest.MatcherAssert.assertThat(pages, allOf(
            hasEntry(
                is(1),
                allOf(
                    hasEntry(is("master"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/image1.tif"))),
                        hasProperty("mimeType", is("image/tiff"))
                    )),
                    hasEntry(is("fulltext"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/fulltext1.xml"))),
                        hasProperty("mimeType", is(nullValue()))
                    )),
                    hasEntry(is("target"), allOf(
                        hasProperty("file", is(nullValue())),
                        hasProperty("mimeType", is("image/jpeg"))
                    ))
                )
            ),
            hasEntry(
                is(2),
                allOf(
                    hasEntry(is("master"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/image2.tif"))),
                        hasProperty("mimeType", is("image/tiff"))
                    )),
                    hasEntry(is("fulltext"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/fulltext2.xml"))),
                        hasProperty("mimeType", is(nullValue()))
                    )),
                    hasEntry(is("target"), allOf(
                        hasProperty("file", is(nullValue())),
                        hasProperty("mimeType", is("image/jpeg"))
                    ))
                )
            ),
            hasEntry(
                is(3),
                allOf(
                    hasEntry(is("master"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/image3.tif"))),
                        hasProperty("mimeType", is("image/tiff"))
                    )),
                    hasEntry(is("fulltext"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/fulltext3.xml"))),
                        hasProperty("mimeType", is(nullValue()))
                    )),
                    not(hasEntry(is("target"), anything()))
                )
            ),
            hasEntry(
                is(4),
                allOf(
                    hasEntry(is("master"), allOf(
                        hasProperty("file", hasProperty("path", is("/srv/path/UA123/image4.tif"))),
                        hasProperty("mimeType", is("image/tiff"))
                    )),
                    not(hasEntry(is("fulltext"), anything())),
                    not(hasEntry(is("target"), anything()))
                )
            )
        ));

    }


}
