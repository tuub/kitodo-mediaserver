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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.kitodo.mediaserver.core.processors.Toc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A converter to produce PDF files using Apache PDFBox package.
 */
public class PdfboxFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfboxFileConverter.class);

    @Autowired
    private ObjectFactory<PdfboxPage> pageFactory;

    @Override
    public InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, Object> parameter) throws Exception {

        checkParams(pages, parameter, "derivativePath", "target_mime");

        int size = getConversionSize(parameter);

        // if the cache file already exists, there is another thread already performing the conversion.
        Map.Entry<File, Boolean> convertedFile = createDerivativeFile((String)parameter.get("derivativePath"));

        if (!convertedFile.getValue()) {
            try {

                // Set up memory usage settings for PDF conversion
                File tmpDir = new File(System.getProperty("java.io.tmpdir"));
                MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting
                    .setupMixed(conversionPropertiesPdf.getMaxMemory() * 1024 * 1024)
                    .setTempDir(tmpDir);

                // Initialize PDF document
                PdfboxDocument document = new PdfboxDocument(memoryUsageSetting);

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
                    Date date = sdf.parse((String)parameter.get("creationDate"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    document.setProductionDate(cal);
                }
                if (parameter.get("authors") != null) {
                    document.setAuthor((String)parameter.get("authors"));
                }
                document.setTitle((String)parameter.get("title"));
                document.setToc((Toc)parameter.get("toc"));

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
                document.save(convertedFile.getKey().getAbsolutePath());

            } catch (Exception e) {
                convertedFile.getKey().delete();
                throw e;
            }
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile.getKey());

        cleanDerivativeFile(convertedFile.getKey());

        return convertedInputStream;
    }
}
