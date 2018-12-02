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

import java.io.IOException;
import org.kitodo.mediaserver.core.actions.DoiRegisterAction;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
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
    private DoiProperties doiProperties;

    @Bean
    public IMetsReader doiMetsReader() throws IOException {
        XsltMetsReader xsltMetsReader = new XsltMetsReader();
        xsltMetsReader.setXslt(new ClassPathResource(doiProperties.getDoiDataReaderXsl()));
        return xsltMetsReader;
    }

    @Bean(name = "registerDoi")
    public IAction doiRegisterAction() throws IOException {
        DoiRegisterAction doiRegisterAction = new DoiRegisterAction();
        doiRegisterAction.setMetsReader(doiMetsReader());
        return doiRegisterAction;
    }

}
