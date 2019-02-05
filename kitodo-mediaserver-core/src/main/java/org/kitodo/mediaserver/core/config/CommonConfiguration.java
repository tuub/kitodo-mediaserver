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

package org.kitodo.mediaserver.core.config;

import org.kitodo.mediaserver.core.actions.CleanMetsTitleEntriesAction;
import org.kitodo.mediaserver.core.actions.DoiRegisterAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.kitodo.mediaserver.core.api.IWorkDescriptor;
import org.kitodo.mediaserver.core.processors.WorkPurlCreator;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.core.processors.XsltMetsTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Bean configurations for common features.
 */
@Configuration
public class CommonConfiguration {

    @Autowired
    private IdentifierProperties identifierProperties;

    @Bean
    public IMetsReader doiMetsReader() {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource(identifierProperties.getDoiDataReaderXsl()));
        return xsltMetsReader;
    }

    @Bean
    public IWorkDescriptor workPurlCreator() {
        return new WorkPurlCreator();
    }

    @Bean(name = "registerDoi")
    public IAction doiRegisterAction() {
        DoiRegisterAction doiRegisterAction = new DoiRegisterAction();
        doiRegisterAction.setWorkDescriptor(workPurlCreator());
        doiRegisterAction.setMetsReader(doiMetsReader());
        return doiRegisterAction;
    }

    @Bean
    public IMetsTransformer cleanMetsTitleEntries() {
        return new XsltMetsTransformer(new ClassPathResource("xslt/cleanMetsTitleEntries.xsl"));
    }

    @Bean
    public IAction cleanMetsTitleEntriesAction() {
        CleanMetsTitleEntriesAction cleanMetsTitleEntriesAction = new CleanMetsTitleEntriesAction();
        cleanMetsTitleEntriesAction.setMetsTransformer(cleanMetsTitleEntries());
        return cleanMetsTitleEntriesAction;
    }
}
