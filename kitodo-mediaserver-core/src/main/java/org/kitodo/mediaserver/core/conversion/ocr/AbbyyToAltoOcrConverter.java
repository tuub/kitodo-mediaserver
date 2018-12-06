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

package org.kitodo.mediaserver.core.conversion.ocr;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.kitodo.mediaserver.core.api.IOcrConverter;
import org.mycore.xml.AbbyyToAltoConverter;
import org.mycore.xml.JAXBUtil;
import org.mycore.xml.abbyy.v10.Document;
import org.mycore.xml.alto.v2.Alto;
import org.springframework.stereotype.Component;

/**
 * Convert an ABBYY Finereader OCR file to ALTO format file.
 */
@Component
public class AbbyyToAltoOcrConverter implements IOcrConverter {

    @Override
    public void convert(Path sourceFile, Path destFile) throws Exception {

        // Check the source file format
        String ocrContent = new String(Files.readAllBytes(sourceFile), StandardCharsets.UTF_8);
        if (ocrContent.contains("<alto ")) {
            // Already in ALTO format
            return;
        }
        if (!ocrContent.contains("<document ")) {
            // The file is not in Finereader format.
            throw new UnsupportedOperationException("The source file is not in ABBYY Finereader format.");
        }

        // Read ABBYY file
        Document abbyyDocument;
        try (InputStream inputStream = Files.newInputStream(sourceFile, StandardOpenOption.READ)) {
            abbyyDocument = JAXBUtil.unmarshalAbbyyDocument(inputStream);
        }

        // Convert to ALTO
        AbbyyToAltoConverter converter = new AbbyyToAltoConverter();
        Alto alto = converter.convert(abbyyDocument);

        // Write ALTO file
        if (!Files.exists(destFile.getParent())) {
            Files.createDirectories(destFile.getParent());
        }
        try (OutputStream outStream = Files.newOutputStream(destFile)) {
            JAXBUtil.marshalAlto(alto, outStream);
        }
    }
}
