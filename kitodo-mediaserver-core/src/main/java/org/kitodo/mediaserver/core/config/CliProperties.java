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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties class for the CLI configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "cli")
public class CliProperties {

    private PerformActions performActions;

    public PerformActions getPerformActions() {
        return performActions;
    }

    public void setPerformActions(PerformActions performActions) {
        this.performActions = performActions;
    }

    public static class PerformActions {

        private Boolean continueOnError;
        private String cron;

        public Boolean getContinueOnError() {
            return continueOnError;
        }

        public void setContinueOnError(Boolean continueOnError) {
            this.continueOnError = continueOnError;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}
