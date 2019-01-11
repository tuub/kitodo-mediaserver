/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.core.conversion;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.kitodo.mediaserver.core.api.IDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.security.action.GetPropertyAction;

/**
 * A converter to produce PDF files using Apache PDFBox package.
 */
public class PdfboxFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfboxFileConverter.class);

    @Autowired
    private ObjectFactory<PdfboxPage> pageFactory;

    @Override
    public InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, String> parameter) throws Exception {

        checkParams(pages, parameter, "derivativePath", "target_mime");

        int size = getConversionSize(parameter);

        File convertedFile = new File(conversionTargetPath, parameter.get("derivativePath"));

        // if the cache file already exists, there is another thread already performing the conversion.
        boolean fileAlreadyExists = createCacheFile(convertedFile);

        if (!fileAlreadyExists) {
            try {

                // Set up memory usage settings for PDF conversion
                File tmpDir = new File(AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
                MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting
                    .setupMixed(conversionPropertiesPdf.getMaxMemory() * 1024 * 1024)
                    .setTempDir(tmpDir);

                // Initialize PDF document
                IDocument document = new PdfboxDocument(memoryUsageSetting);

                // Set ICC color profile (needed for PDF/A)
                try {
                    ICC_Profile iccProfile = ICC_Profile.getInstance(conversionPropertiesPdf.getIccProfile());
                    document.setIccProfile(iccProfile);
                } catch (Exception defaultEx) {
                    throw new IllegalArgumentException("Could not load default ICC profile.", defaultEx);
                }

                // Set metadata
                if (parameter.get("creationDate") != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
                    Date date = sdf.parse(parameter.get("creationDate"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    document.setProductionDate(cal);
                }
                if (parameter.get("authors") != null) {
                    String[] authors = parameter.get("authors").split(";");
                    for (String author : authors) {
                        document.addAuthor(author);
                    }
                }
                document.setTitle(parameter.get("title"));

                // Set up all pages
                for (Map<String, FileEntry> metsPage : pages.values()) {
                    PdfboxPage page = pageFactory.getObject();
                    page.setImagePath(metsPage.get("master").getFile().getAbsolutePath());
                    page.setSize(size);

                    // Add optional Fulltext file
                    FileEntry fulltextEntry = metsPage.get("fulltext");
                    if (fulltextEntry != null) {
                        page.setFulltextPath(fulltextEntry.getFile().getAbsolutePath());
                    }

                    document.getPages().add(page);
                }

                // Save PDF file
                document.save(convertedFile.getAbsolutePath());

            } catch (Exception e) {
                convertedFile.delete();
                throw e;
            }
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile);

        if (!saveConvertedFile) {
            LOGGER.info("Deleting file " + convertedFile.getAbsolutePath());
            convertedFile.delete();
        }

        return convertedInputStream;
    }
}
