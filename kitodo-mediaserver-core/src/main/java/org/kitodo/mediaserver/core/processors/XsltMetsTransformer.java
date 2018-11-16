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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import javax.naming.ConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.FileUtils;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.springframework.core.io.ClassPathResource;

/**
 * Transform a METS file by using a XSL template file.
 */
public class XsltMetsTransformer implements IMetsTransformer {

    private ClassPathResource xslt;

    public void setXslt(ClassPathResource xslt) {
        this.xslt = xslt;
    }

    public XsltMetsTransformer(ClassPathResource xslt) {
        this.xslt = xslt;
    }

    @Override
    public void transform(File sourceMets, File destinationMets, Map.Entry<String, String>... parameter)
        throws ConfigurationException, IOException, TransformerException {

        if (xslt == null) {
            throw new ConfigurationException("The required XSLT input stream is not set, "
                + "please check your spring configuration.");
        }
        if (sourceMets == null) {
            throw new IllegalArgumentException("The sourceMets file argument is null");
        }
        if (destinationMets == null) {
            throw new IllegalArgumentException("The destinationMets file argument is null");
        }
        if (!sourceMets.isFile()) {
            throw new IllegalArgumentException("The sourceMets file " + sourceMets.getAbsolutePath() + " is not a file");
        }
        if (!destinationMets.isFile()) {
            throw new IllegalArgumentException("The destinationMets file " + destinationMets.getAbsolutePath() + " is not a file");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslt.getInputStream()));

        Arrays.stream(parameter)
            .forEach(param -> transformer.setParameter(param.getKey(), param.getValue()));

        Result result;
        StringWriter stringWriter = null;

        // If source and destination file is the same, use a buffer instead of writing to the file directly
        if (sourceMets.getCanonicalPath().equals(destinationMets.getCanonicalPath())) {
            stringWriter = new StringWriter();
            result = new StreamResult(stringWriter);
        } else {
            result = new StreamResult(destinationMets);
        }

        transformer.transform(new StreamSource(new FileReader(sourceMets)), result);

        if (stringWriter != null) {
            FileUtils.writeStringToFile(destinationMets, stringWriter.toString(), "utf-8");
        }
    }
}
