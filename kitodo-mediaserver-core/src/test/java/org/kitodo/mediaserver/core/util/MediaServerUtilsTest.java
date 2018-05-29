package org.kitodo.mediaserver.core.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.kitodo.mediaserver.core.db.entities.Work;

import static org.assertj.core.api.Assertions.assertThat;

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



}
