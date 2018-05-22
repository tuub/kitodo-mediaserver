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
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties class for the UI configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "ui")
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

            //@Min(value = 1, message = "defaultValue must be greater than 0")
            //public Integer defaultValue;

            private List<Integer> availableValues;

            /*
            public Integer getDefaultValue() {
                return defaultValue;
            }

            public void setDefaultValue(Integer defaultValue) {
                this.defaultValue = defaultValue;
            }
            */

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

        public List<String> getSearchableFields() {
            return searchableFields;
        }

        public void setSearchableFields(List<String> searchableFields) {
            this.searchableFields = searchableFields;
        }
    }
}
