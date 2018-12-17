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

package org.kitodo.mediaserver.core.actions;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.api.IWorkDescriptor;
import org.kitodo.mediaserver.core.config.IdentifierProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * An action registering a DOI if found in the METS/MODS file.
 */
public class DoiRegisterAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoiRegisterAction.class);

    private IdentifierProperties identifierProperties;
    private MediaServerUtils mediaServerUtils;
    private IMetsReader metsReader;
    private IWorkDescriptor workDescriptor;

    @Autowired
    public void setIdentifierProperties(IdentifierProperties identifierProperties) {
        this.identifierProperties = identifierProperties;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    public void setWorkDescriptor(IWorkDescriptor workDescriptor) {
        this.workDescriptor = workDescriptor;
    }

    public void setMetsReader(IMetsReader metsReader) {
        this.metsReader = metsReader;
    }

    /**
     * Looks for a DOI and an Id in the METS/MODS file. Registers the DOI if found.
     *
     * @param work      a work entity
     * @param parameter a map of parameter
     * @return an object with the result of the action, if any.
     * @throws Exception by fatal errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        File metsFile = mediaServerUtils.getMetsFileForWork(work);
        List<String> metsResult = metsReader.read(metsFile);

        if (CollectionUtils.isEmpty(metsResult)) {
            LOGGER.info("No DOI found for work " + work.getId());
            return null;
        }

        final String doi = metsResult.get(0);
        final String doiUrl = identifierProperties.getDataCiteURL() + doi;
        final String landingPage = workDescriptor.describe(work);
        final String putRequestContent = "doi=" + doi + "\nurl=" + landingPage;

        // Register doi using Http Put
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                identifierProperties.getDataCiteUser(),
                identifierProperties.getDataCitePassword());
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        HttpPut putRequest = new HttpPut(doiUrl);
        StringEntity entity = new StringEntity(putRequestContent);
        entity.setContentType("text/plain;charset=UTF-8");
        putRequest.setEntity(entity);

        HttpResponse response = client.execute(putRequest);
        int status = response.getStatusLine().getStatusCode();

        if (status > 299) {
            throw new Exception("Unexpected status code registering DOI: " + status
                    + ", message: " + response.getStatusLine().getReasonPhrase());
        }
        LOGGER.info("Registered doi " + doi + " successfully with status code " + status);

        return null;
    }
}
