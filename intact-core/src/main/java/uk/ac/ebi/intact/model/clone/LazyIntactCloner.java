/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.clone;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import java.util.Collection;

/**
 * IntAct Object cloner.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: IntactCloner.java 15374 2010-11-15 12:40:49Z brunoaranda $
 */
@org.springframework.stereotype.Component
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
@Qualifier("lazyCloner")
public class LazyIntactCloner extends IntactCloner{

    public LazyIntactCloner() {
        super();
    }


    public boolean isCollectionClonable(Collection col) {
        return Hibernate.isInitialized(col);
    }
}
