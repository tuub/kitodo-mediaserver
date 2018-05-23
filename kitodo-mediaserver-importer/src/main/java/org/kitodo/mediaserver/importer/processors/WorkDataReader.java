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

package org.kitodo.mediaserver.importer.processors;

import java.io.File;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IReadResultParser;
import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;


/**
 * Converts a mets file to a work instance.
 */
public class WorkDataReader implements IDataReader {

    private IMetsReader metsReader;
    private IReadResultParser readResultParser;

    public void setMetsReader(IMetsReader metsReader) {
        this.metsReader = metsReader;
    }

    public void setReadResultParser(IReadResultParser readResultParser) {
        this.readResultParser = readResultParser;
    }

    /**
     * Passes a mets file over to mets reader and mets parser,
     * and then converts the resulting map to a work instance.
     * Returns the work instance.
     *
     * @param mets the mets file
     * @return a Work object
     */
    @Override
    public Work read(File mets) throws Exception {

        // Get a list from given mets file through metsReader
        List<String> workData = metsReader.read(mets);

        // Push the list through readResultParser and get a map
        Map<String, String> workDataMap = (Map<String, String>) readResultParser.parse(workData);

        // Assemble Work object with the values from the map
        Work work = new Work(workDataMap.get("workid"), workDataMap.get("title"));

        Set<Identifier> identifierSet = new HashSet<>();
        for (String key : workDataMap.keySet()) {
            if (StringUtils.startsWith(key, "identifier")) {
                if (StringUtils.isEmpty(workDataMap.get(key))) {
                    continue;
                }
                String type = null;
                String[] qualifier = key.split("\\.", 2);

                if (qualifier.length > 1) {
                    type = qualifier[1];
                }

                identifierSet.add(new Identifier(workDataMap.get(key), type, work));
            }
        }

        work.setIdentifiers(identifierSet);

        return work;
    }
}
