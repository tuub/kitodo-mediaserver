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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

/**
 * LocaleResolver which calls multiple LocaleResolvers in chain.
 */
public class ChainedLocaleResolver implements LocaleResolver {

    private List<LocaleResolver> localeResolvers;

    public List<LocaleResolver> getLocaleResolvers() {
        return localeResolvers;
    }

    public void setLocaleResolvers(List<LocaleResolver> localeResolvers) {
        this.localeResolvers = localeResolvers;
    }

    /**
     * Default constructor.
     */
    public ChainedLocaleResolver() {
        localeResolvers = new ArrayList<>();
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = null;
        for (LocaleResolver resolver : getLocaleResolvers()) {
            locale = resolver.resolveLocale(request);
            if (locale != null) {
                return locale;
            }
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        for (LocaleResolver resolver : getLocaleResolvers()) {
            try {
                resolver.setLocale(request, response, locale);
            } catch (UnsupportedOperationException e) {
                // do nothing
            }
        }
    }
}
