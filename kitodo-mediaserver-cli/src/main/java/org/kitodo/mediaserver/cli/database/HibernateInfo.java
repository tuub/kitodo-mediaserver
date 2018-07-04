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

package org.kitodo.mediaserver.cli.database;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateInfo {

    private static Metadata metadata;

    private static SessionFactoryImplementor sessionFactory;

    private static SessionFactoryServiceRegistry serviceRegistry;

    public static Metadata getMetadata() {
        return metadata;
    }

    public static void setMetadata(Metadata metadata) {
        HibernateInfo.metadata = metadata;
    }

    public static SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    public static void setSessionFactory(SessionFactoryImplementor sessionFactory) {
        HibernateInfo.sessionFactory = sessionFactory;
    }

    public static SessionFactoryServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static void setServiceRegistry(SessionFactoryServiceRegistry serviceRegistry) {
        HibernateInfo.serviceRegistry = serviceRegistry;
    }
}
