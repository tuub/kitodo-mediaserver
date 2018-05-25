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

package org.kitodo.mediaserver.importer.config;

import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.kitodo.mediaserver.importer.processors.WorkDataReader;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.ResourceUtils;

/**
 * Spring configuration of the importer module.
 */
@Configuration
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
public class ImporterConfiguration {

    @Bean
    public ImporterUtils importerUtils() {
        return new ImporterUtils();
    }

    @Bean
    public IDataReader workDataReader() throws Exception {
        WorkDataReader workDataReader = new WorkDataReader();
        workDataReader.setMetsReader(workDataMetsReader());
        workDataReader.setReadResultParser(workDataResultParser());
        return workDataReader;
    }

    @Bean
    public IMetsReader workDataMetsReader() throws Exception {
        XsltMetsReader workDataMetsReader = new XsltMetsReader();
        workDataMetsReader.setXslt(ResourceUtils.getFile("classpath:xslt/getWorkData.xsl"));
        return workDataMetsReader;
    }

    @Bean
    public IReadResultParser workDataResultParser() {
        SimpleList2MapParser workDataResultParser = new SimpleList2MapParser();
        workDataResultParser.setMapSeparator(":");
        return workDataResultParser;
    }


}
