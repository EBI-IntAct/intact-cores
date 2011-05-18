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
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

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

    /**
     * Check if the proxy or persistent collection is initialized.
     *
     * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
     * @return true if the argument is already initialized, or is not a proxy or collection
     */
    public static boolean isInitialized(Object proxy) {
        return Hibernate.isInitialized(proxy);
    }

    /**
     * Check if the property is initialized. If the named property does not exist
     * or is not persistent, this method always returns <tt>true</tt>.
     *
     * @param proxy        The potential proxy
     * @param propertyName the name of a persistent attribute of the object
     * @return true if the named property of the object is not listed as uninitialized; false otherwise
     */
    public static boolean isPropertyInitialized(Object proxy, String propertyName) {
        return Hibernate.isPropertyInitialized(proxy, propertyName);
    }

    /**
     * Force initialization of a proxy or persistent collection.
     * <p/>
     * Note: This only ensures intialization of a proxy object or collection;
     * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
     *
     * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
     * @throws uk.ac.ebi.intact.core.IntactException
     *          if we can't initialize the proxy at this time, eg. the <tt>Session</tt> was closed
     */
    public static void initialize(Object proxy) throws IntactException {
        try {
            Hibernate.initialize(proxy);
        } catch (HibernateException e) {
            throw new IntactException("Problem initializing proxy", e);
        }
    }

    /**
     * Initializes the AnnotatedObject collections (xrefs, aliases, annotations).
     *
     * @param ao The annotatedObject
     */
    public static void initializeAnnotatedObject(AnnotatedObject ao) {
        initialize(ao.getXrefs());
        initialize(ao.getAliases());
        initialize(ao.getAnnotations());
    }

    /**
     * Initializes the AnnotatedObject collections (xrefs, aliases, annotations).
     *
     * @param ao The annotatedObject
     */
    public static void initializeCvObject(CvObject cv) {
        initializeAnnotatedObject(cv);

        if (cv instanceof CvDagObject) {
            CvDagObject cvdag = (CvDagObject) cv;
            initialize(cvdag.getParents());
            initialize(cvdag.getChildren());
        }
    }

    /**
     * Returns true if the collection has been initialized by hibernate and has pending changes.
     * Modifying a property of an object of the collection will NOT mark as dirty the collection itself.
     *
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
     *
     * @param intactContext the IntactContext current instance
     * @param ac            The accession to look for
     * @return The class for the entity with that accession
     */
    public static Class<? extends AnnotatedObject> classForAc(IntactContext intactContext, String ac) {
        Class[] classes = new Class[]{
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
            String strQuery = "select count(*) from " + clazz.getSimpleName() + " where ac = :ac";
            Query query = intactContext.getDaoFactory().getEntityManager().createQuery(strQuery);
            query.setParameter("ac", ac);

            boolean found = ((Long) query.getSingleResult()) > 0;

            if (found) {
                return clazz;
            }
        }

        throw new IllegalArgumentException("No class for AC: " + ac + " ; searched on: " + Arrays.asList(classes));
    }

    /**
     * Retrieves the annotations from an AnnotatedObject, initializing them if necessary.
     *
     * @param ao The annotated object instance with possibly non-initialized annotations
     * @return The returned annotations are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Annotation> ensureInitializedAnnotations(AnnotatedObject ao) {
        Collection<Annotation> annotations;

        if (IntactCore.isInitialized(ao.getAnnotations())) {
            annotations = ao.getAnnotations();
        } else {
            annotations = IntactContext.getCurrentInstance().getDaoFactory().getAnnotationDao().getByParentAc(ao.getClass(), ao.getAc());
        }

        return annotations;
    }

    /**
     * Retrieves the xrefs from an AnnotatedObject, initializing them if necessary.
     *
     * @param ao The annotated object instance with possibly non-initialized xrefs
     * @return The returned xrefs are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<? extends Xref> ensureInitializedXrefs(AnnotatedObject<?, ?> ao) {
        Collection<? extends Xref> xrefs;

        if (IntactCore.isInitialized(ao.getXrefs())) {
            xrefs = ao.getXrefs();
        } else {
            xrefs = IntactContext.getCurrentInstance().getDaoFactory().getXrefDao(AnnotatedObjectUtils.getXrefClassType(ao.getClass()))
                    .getByParentAc(ao.getAc());
        }

        return xrefs;
    }

    /**
     * Retrieves the aliases from an AnnotatedObject, initializing them if necessary.
     *
     * @param ao The annotated object instance with possibly non-initialized aliases
     * @return The returned aliases are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<? extends Alias> ensureInitializedAliases(AnnotatedObject<?, ?> ao) {
        Collection<? extends Alias> aliases;

        if (IntactCore.isInitialized(ao.getXrefs())) {
            aliases = ao.getAliases();
        } else {
            aliases = IntactContext.getCurrentInstance().getDaoFactory().getAliasDao(AnnotatedObjectUtils.getAliasClassType(ao.getClass()))
                    .getByParentAc(ao.getAc());
        }

        return aliases;
    }
}
