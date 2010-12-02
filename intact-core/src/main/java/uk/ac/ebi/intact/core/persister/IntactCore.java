package uk.ac.ebi.intact.core.persister;
/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.hibernate.Hibernate;
import org.hibernate.collection.PersistentCollection;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import javax.persistence.Query;
import java.util.Arrays;
import java.util.Collection;

/**
 * Global persistence utilities, based on the philosophy of the <code>org.hibernate.Hibernate</code> class.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCore {

    public static boolean isInitialized(Object proxy) {
        return Hibernate.isInitialized(proxy);
    }

    /**
     * Returns true if the collection has been initialized by hibernate and has pending changes.
     * Modifying a property of an object of the collection will NOT mark as dirty the collection itself.
     * @param collection the collection to check
     * @return true if modified and initialized by hibernate
     */
    public static boolean isInitializedAndDirty(Collection collection) {
        if (collection instanceof PersistentCollection) {
             return isInitialized(collection) && ((PersistentCollection) collection).isDirty();
        }

        return isInitialized(collection);
    }

    /**
     * Gets the class for a specific accession.
     * @param intactContext the IntactContext current instance
     * @param ac The accession to look for
     * @return The class for the entity with that accession
     */
    public static Class<? extends AnnotatedObject> classForAc(IntactContext intactContext, String ac) {
        Class[] classes = new Class[] {
                Publication.class,
                Experiment.class,
                InteractionImpl.class, // the order matters here, because otherwise an interaction would return InteractorImpl class
                InteractorImpl.class,
                Component.class,
                Feature.class,
                CvObject.class,
                BioSource.class
        };

        for (Class clazz : classes) {
            String strQuery = "select count(*) from "+clazz.getSimpleName()+" where ac = :ac";
            Query query = intactContext.getDaoFactory().getEntityManager().createQuery(strQuery);
            query.setParameter("ac", ac);

            boolean found = ((Long) query.getSingleResult()) > 0;

            if (found) {
                return clazz;
            }
        }

        throw new IllegalArgumentException("No class for AC: "+ac+" ; searched on: "+ Arrays.asList(classes));
    }
}
