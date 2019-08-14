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

package org.kitodo.mediaserver.core.processors;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.kitodo.mediaserver.core.api.ITocReader;
import org.springframework.core.io.ClassPathResource;

/**
 * Reads table of content from METS file using XSLT.
 */
public class XsltMetsTocReader implements ITocReader {

    private ClassPathResource xslt;

    public ClassPathResource getXslt() {
        return xslt;
    }

    public void setXslt(ClassPathResource xslt) {
        this.xslt = xslt;
    }

    /**
     * Read the given file and return the parsed TOC.
     *
     * @param file File to read from
     * @return The parsed TOC
     * @throws Exception on errors
     */
    @Override
    public Toc read(Path file) throws Exception {

        // Transform METS file to TOC XML format
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt.getInputStream()));
        StringWriter stringWriter = new StringWriter();
        Source source = new StreamSource(Files.newInputStream(file));
        Result result = new StreamResult(stringWriter);
        transformer.transform(source, result);

        // Load TOC XML format into Toc object
        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(Toc.class).createUnmarshaller();
        return (Toc)jaxbUnmarshaller.unmarshal(new StringReader(stringWriter.toString()));
    }
}
