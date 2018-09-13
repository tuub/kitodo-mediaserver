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

package org.kitodo.mediaserver.ui.config;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties class for the UI configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "ui", ignoreInvalidFields = true)
public class UiProperties {

    @Valid
    private Pagination pagination;

    @Valid
    private Works works;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Works getWorks() {
        return works;
    }

    public void setWorks(Works works) {
        this.works = works;
    }

    public static class Pagination {

        @Valid
        private ElementsPerPage elementsPerPage;

        public ElementsPerPage getElementsPerPage() {
            return elementsPerPage;
        }

        public void setElementsPerPage(ElementsPerPage elementsPerPage) {
            this.elementsPerPage = elementsPerPage;
        }

        public static class ElementsPerPage {

            private List<Integer> availableValues;

            public List<Integer> getAvailableValues() {
                return availableValues;
            }

            public void setAvailableValues(List<Integer> availableValues) {
                this.availableValues = availableValues;
            }
        }
    }

    public static class Works {

        private List<String> searchableFields;

        private Boolean reduceMets;

        private Map<String, ActionDefinition> actions;

        public List<String> getSearchableFields() {
            return searchableFields;
        }

        public void setSearchableFields(List<String> searchableFields) {
            this.searchableFields = searchableFields;
        }

        public Boolean getReduceMets() {
            return reduceMets;
        }

        public void setReduceMets(Boolean reduceMets) {
            this.reduceMets = reduceMets;
        }

        public Map<String, ActionDefinition> getActions() {
            return actions;
        }

        public void setActions(Map<String, ActionDefinition> actions) {
            this.actions = actions;
        }

    }

    public static class ActionDefinition {

        private String label;

        private String action;

        private boolean enabled;

        private Map<String, String> parameters;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }
    }
}
