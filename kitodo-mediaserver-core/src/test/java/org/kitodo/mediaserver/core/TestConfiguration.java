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

package org.kitodo.mediaserver.core;

import org.kitodo.mediaserver.core.api.IAction;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"org.kitodo.mediaserver.core.services"})
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
public class TestConfiguration {

    @Bean
    IAction mockAction() throws Exception {
        IAction mockAction = Mockito.mock(IAction.class);
        when(mockAction.perform(any(), any())).thenReturn("performed");
        return mockAction;
    }

    @Bean
    Object mockNoAction() {
        return new Object();
    }

}
