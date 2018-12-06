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

package org.kitodo.mediaserver.core.processors.ocr;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.kitodo.mediaserver.core.api.IOcrReader;
import org.springframework.core.io.ClassPathResource;

/**
 * OCR reader using XSLT.
 */
public class XsltOcrReader implements IOcrReader {

    private Map<String, ClassPathResource> formats = new HashMap<>();

    public Map<String, ClassPathResource> getFormats() {
        return formats;
    }

    @Override
    public OcrPage read(Path file) throws Exception {

        ClassPathResource xslt = null;
        String ocrContent = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);

        // Remove Unicode BOM from string (Transformer will fail otherwise)
        if (ocrContent.startsWith("\uFEFF")) {
            ocrContent = ocrContent.substring(1);
        }

        // Determine the OCR file format
        for (String pattern : formats.keySet()) {
            if (ocrContent.contains(pattern)) {
                xslt = formats.get(pattern);
                break;
            }
        }
        if (xslt == null) {
            throw new Exception("No XSLT file found for file '" + file + "'");
        }

        // Transform OCR file to generic format
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt.getInputStream()));
        StringWriter stringWriter = new StringWriter();
        Source source = new StreamSource(new StringReader(ocrContent));

        //Source source = new StreamSource(Files.newBufferedReader(file, StandardCharsets.UTF_8));
        Result result = new StreamResult(stringWriter);
        transformer.transform(source, result);

        // Load OCR generic format into page object
        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(OcrPage.class).createUnmarshaller();
        return (OcrPage) jaxbUnmarshaller.unmarshal(new StringReader(stringWriter.toString()));
    }
}
