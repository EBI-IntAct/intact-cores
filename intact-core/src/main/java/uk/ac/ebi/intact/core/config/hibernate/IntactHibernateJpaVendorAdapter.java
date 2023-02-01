/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.config.hibernate;

import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.spi.PersistenceProvider;

/**
 * TODO write description of the class.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactHibernateJpaVendorAdapter extends HibernateJpaVendorAdapter {

    @Override
    public PersistenceProvider getPersistenceProvider() {
        return new IntactHibernatePersistenceProvider();
    }
}
