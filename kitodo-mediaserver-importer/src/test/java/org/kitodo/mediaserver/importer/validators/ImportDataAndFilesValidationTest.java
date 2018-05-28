package org.kitodo.mediaserver.importer.validators;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
public class ImportDataAndFilesValidationTest {

    private ImportDataAndFilesValidation importDataAndFilesValidation;

    @Before
    public void init() throws Exception {
        importDataAndFilesValidation = new ImportDataAndFilesValidation();

        ImporterProperties importerPropertiesMock = mock(ImporterProperties.class);
        when(importerPropertiesMock.getWorkIdRegex()).thenReturn("\\w+");
        importDataAndFilesValidation.setImporterProperties(importerPropertiesMock);

        IMetsValidation fileOccurenceValidationMock = mock(FileOccurrenceValidation.class);
        doNothing().when(fileOccurenceValidationMock).validate(isA(File.class), Mockito.any());
        importDataAndFilesValidation.setFileOccurenceValidation(fileOccurenceValidationMock);
    }

    @Test
    public void workWithIdValid() throws Exception {
        // given
        Work work = new Work("id", "title");

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }

    @Test(expected = ValidationException.class)
    public void workWithIdContainingSlashInvalid() throws Exception {
        // given
        Work work = new Work("id/id", "title");

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }

    @Test(expected = ValidationException.class)
    public void workWithIdContainingSpaceInvalid() throws Exception {
        // given
        Work work = new Work("id id", "title");

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }

    @Test(expected = ValidationException.class)
    public void workWithEmptyIdInvalid() throws Exception {
        // given
        Work work = new Work("", "title");

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }

    @Test(expected = ValidationException.class)
    public void workWithoutTitleInvalid() throws Exception {
        // given
        Work work = new Work("id", null);

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }

    @Test(expected = ValidationException.class)
    public void workWithEmptyTitleInvalid() throws Exception {
        // given
        Work work = new Work("id", "");

        // when
        importDataAndFilesValidation.validate(work, mock(File.class));
    }


}
