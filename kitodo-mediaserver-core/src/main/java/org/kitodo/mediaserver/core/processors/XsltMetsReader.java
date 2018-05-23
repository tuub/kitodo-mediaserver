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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.kitodo.mediaserver.core.api.IMetsReader;

/**
 * An implementation of IMetsReader using XSLT transformation.
 */
public class XsltMetsReader implements IMetsReader {

    private File xslt;

    public void setXslt(File xslt) {
        this.xslt = xslt;
    }

    /**
     * Reads data from a mets file and returns it as a list of strings.
     *
     * @param mets      the mets file
     * @param parameter optional key-value pairs
     * @return a list of strings with the result
     */
    @Override
    public List<String> read(File mets, Map.Entry<String, String> ... parameter)
            throws ConfigurationException, TransformerException, FileNotFoundException {

        if (xslt == null) {
            throw new ConfigurationException("The required XSLT input stream is not set, "
                    + "please check your spring configuration.");
        }
        if (mets == null) {
            throw new IllegalArgumentException("The mets file argument is null");
        }
        if (!mets.isFile()) {
            throw new IllegalArgumentException("The mets file " + mets.getAbsolutePath() + " is not a file");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslt));

        Arrays.stream(parameter)
                .forEach(param -> transformer.setParameter(param.getKey(), param.getValue()));

        StringWriter stringWriter = new StringWriter();
        transformer.transform(new StreamSource(new FileReader(mets)), new StreamResult(stringWriter));

        String[] resultArray = stringWriter
                .toString()
                .split("\\n");

        return Arrays.stream(resultArray)
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

}
