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
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.collection.spi.PersistentCollection;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
     * @param cv The annotatedObject
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

            try {
                boolean found = ((Long) query.getSingleResult()) > 0;
                if (found) {
                    return clazz;
                }
            } catch (NoResultException e) {
                // Nothing to do, the query did not find any result
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

        if (IntactCore.isInitialized(ao.getAliases())) {
            aliases = ao.getAliases();
        } else {
            aliases = IntactContext.getCurrentInstance().getDaoFactory().getAliasDao(AnnotatedObjectUtils.getAliasClassType(ao.getClass()))
                    .getByParentAc(ao.getAc());
        }

        return aliases;
    }

    /**
     * Retrieves the interactions from an experiment, initializing them if necessary.
     *
     * @param experiment the experiment
     * @return The returned interactions are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Interaction> ensureInitializedInteractions(Experiment experiment) {
        Collection<Interaction> interactions;

        if (IntactCore.isInitialized(experiment.getInteractions())) {
            interactions = experiment.getInteractions();
        } else {
            interactions = IntactContext.getCurrentInstance().getDaoFactory().getInteractionDao().getByExperimentAc(experiment.getAc(), 0, Integer.MAX_VALUE);
        }

        return interactions;
    }

    /**
     * Retrieves the experiment from an interaction, initializing them if necessary.
     *
     * @param interaction the interaction
     * @return The returned experiments are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Experiment> ensureInitializedExperiments(Interaction interaction) {
        Collection<Experiment> experiments;

        if (IntactCore.isInitialized(interaction.getExperiments())) {
            experiments = interaction.getExperiments();
        } else {
            experiments = IntactContext.getCurrentInstance().getDaoFactory().getExperimentDao().getByInteractionAc(interaction.getAc());
        }

        return experiments;
    }

    /**
     * Retrieves the experiment from a publication, initializing them if necessary.
     *
     * @param publication the publication
     * @return The returned experiments are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Experiment> ensureInitializedExperiments(Publication publication) {
        Collection<Experiment> experiments;

        if (IntactCore.isInitialized(publication.getExperiments())) {
            experiments = publication.getExperiments();
        } else {
            experiments = IntactContext.getCurrentInstance().getDaoFactory().getExperimentDao().getByPubAc(publication.getAc());
        }

        return experiments;
    }

    /**
     * Retrieves the ranges from a feature, initializing them if necessary.
     *
     * @param feature the feature
     * @return The returned ranges are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Range> ensureInitializedRanges(Feature feature) {
        Collection<Range> ranges;

        if (IntactCore.isInitialized(feature.getRanges())) {
            ranges = feature.getRanges();
        } else {
            ranges = IntactContext.getCurrentInstance().getDaoFactory().getRangeDao().getByFeatureAc(feature.getAc());
        }

        return ranges;
    }

    /**
     * Retrieves the components from an interaction, initializing them if necessary.
     * Do not set the initialized collection of components because components cannot be orphan
     *
     * @param interaction the interaction
     * @return The returned components are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Component> ensureInitializedParticipants(Interaction interaction) {
        Collection<Component> components;

        if (IntactCore.isInitialized(interaction.getComponents())) {
            components = interaction.getComponents();
        } else {
            components = IntactContext.getCurrentInstance().getDaoFactory().getComponentDao().getByInteractionAc(interaction.getAc());
        }

        return components;
    }

    /**
     * Retrieves the confidences from an interaction, initializing them if necessary.
     * Do not set the initialized collection of confidences because confidences cannot be orphan
     * @param interaction the interaction
     * @return The returned confidences are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Confidence> ensureInitializedConfidences(Interaction interaction) {
        Collection<Confidence> confidences;

        if (IntactCore.isInitialized(interaction.getConfidences())) {
            confidences = interaction.getConfidences();
        } else {
            confidences = IntactContext.getCurrentInstance().getDaoFactory().getConfidenceDao().getByInteractionAc(interaction.getAc());
        }

        return confidences;
    }

    /**
     * Retrieves the parameters from an interaction, initializing them if necessary.
     * Do not set the initialized collection of parameters because parameters cannot be orphan
     * @param interaction the interaction
     * @return The returned confidences are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<InteractionParameter> ensureInitializedInteractionParameters(Interaction interaction) {
        Collection<InteractionParameter> parameters;

        if (IntactCore.isInitialized(interaction.getParameters())) {
            parameters = interaction.getParameters();
        } else {
            parameters = IntactContext.getCurrentInstance().getDaoFactory().getInteractionParameterDao().getByInteractionAc(interaction.getAc());
        }

        return parameters;
    }

    /**
     * Retrieves the parameters from a participant, initializing them if necessary.
     * Do not set the initialized collection of parameters because parameters cannot be orphan
     * @param component the component
     * @return The returned parameters are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<ComponentParameter> ensureInitializedComponentParameters(Component component) {
        Collection<ComponentParameter> parameters;

        if (IntactCore.isInitialized(component.getParameters())) {
            parameters = component.getParameters();
        } else {
            parameters = IntactContext.getCurrentInstance().getDaoFactory().getComponentParameterDao().getByComponentAc(component.getAc());
        }

        return parameters;
    }

    /**
     * Retrieves the features from a participant, initializing them if necessary.
     * Do not set the initialized collection of features because features cannot be orphan
     * @param component the component
     * @return The returned features are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<Feature> ensureInitializedFeatures(Component component) {
        Collection<Feature> features;

        if (IntactCore.isInitialized(component.getFeatures())) {
            features = component.getFeatures();
        } else {
            features = IntactContext.getCurrentInstance().getDaoFactory().getFeatureDao().getByComponentAc(component.getAc());
        }

        return features;
    }

    /**
     * Retrieves the experimental preparations from a participant, initializing them if necessary.
     * Do not set the initialized collection of experimental preparations because cannot be orphan
     * @param component the component
     * @return The returned experimental preparations are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<CvExperimentalPreparation> ensureInitializedExperimentalPreparations(Component component) {
        Collection<CvExperimentalPreparation> expPrep;

        if (IntactCore.isInitialized(component.getExperimentalPreparations())) {
            expPrep = component.getExperimentalPreparations();
        } else {
            expPrep = IntactContext.getCurrentInstance().getDaoFactory().getComponentDao().getExperimentalPreparationsForComponentAc(component.getAc());
        }

        return expPrep;
    }

        /**
     * Retrieves the experimental roles from a participant, initializing them if necessary.
     * Do not set the initialized collection of experimental roles because cannot be orphan
     * @param component the component
     * @return The returned experimental roles are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<CvExperimentalRole> ensureInitializedExperimentalRoles(Component component) {
        Collection<CvExperimentalRole> roles;

        if (IntactCore.isInitialized(component.getExperimentalRoles())) {
            roles = component.getExperimentalRoles();
        } else {
            roles = IntactContext.getCurrentInstance().getDaoFactory().getComponentDao().getExperimentalRolesForComponentAc(component.getAc());
        }

        return roles;
    }

            /**
     * Retrieves the participant detection methods from a participant, initializing them if necessary.
     * Do not set the initialized collection of participant detection methods because cannot be orphan
     * @param component the component
     * @return The returned participant detection methods are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<CvIdentification> ensureInitializedParticipantIdentificationMethods(Component component) {
        Collection<CvIdentification> partDet;

        if (IntactCore.isInitialized(component.getParticipantDetectionMethods())) {
            partDet = component.getParticipantDetectionMethods();
        } else {
            partDet = IntactContext.getCurrentInstance().getDaoFactory().getComponentDao().getParticipantIdentificationMethodsForComponentAc(component.getAc());
        }

        return partDet;
    }

     /**
     * Retrieves the confidences from a participant, initializing them if necessary.
     * Do not set the initialized collection of confidences because confidences cannot be orphan
     * @param component the component
     * @return The returned confidences are ensured to be initialized
     * @since 2.4.0
     */
    public static Collection<ComponentConfidence> ensureInitializedComponentConfidences(Component component) {
        Collection<ComponentConfidence> confidences;

        if (IntactCore.isInitialized(component.getConfidences())) {
            confidences = component.getConfidences();
        } else {
            confidences = IntactContext.getCurrentInstance().getDaoFactory().getComponentConfidenceDao().getByComponentAc(component.getAc());
        }

        return confidences;
    }

    /**
     * Ensures that the lifecycle events are initialized.
     * @param publication The publication
     * @return the initialized events
     * @since 2.5.0
     */
    public static List<LifecycleEvent> ensureInitializedLifecycleEvents(Publication publication) {
       List<LifecycleEvent> events;

        if (IntactCore.isInitialized(publication.getLifecycleEvents())) {
            events = publication.getLifecycleEvents();
        } else {
            events = IntactContext.getCurrentInstance().getDaoFactory().getLifecycleEventDao().getByPublicationAc(publication.getAc());
        }

        return events;
    }

    /**
     * Ensures that the preferences are initialized.
     * @param user The user
     * @return the initialized objects
     * @since 2.5.0
     */
    public static Collection<Preference> ensureInitializedPreferences(User user) {
       Collection<Preference> preferences;

        if (IntactCore.isInitialized(user.getPreferences())) {
            preferences = user.getPreferences();
        } else {
            preferences = IntactContext.getCurrentInstance().getDaoFactory().getPreferenceDao().getByUserAc(user.getAc());
        }

        return preferences;
    }

    /**
     * Ensures that the roles are initialized.
     * @param user The user
     * @return the initialized objects
     * @since 2.5.0
     */
    public static Collection<Role> ensureInitializedRoles(User user) {
       Collection<Role> roles;

        if (IntactCore.isInitialized(user.getPreferences())) {
            roles = user.getRoles();
        } else {
            roles = IntactContext.getCurrentInstance().getDaoFactory().getRoleDao().getByUserAc(user.getAc());
        }

        return roles;
    }

    /**
     * Ensures that the sequence is accessible, reloading it from the database when lazy.
     * @param polymer the polymer
     * @return The returned sequence is ensured to be initialized
     * @since 2.4.0
     */
    public static String ensureInitializedSequence(Polymer polymer) {
        Collection<SequenceChunk> sequenceChunks;

        if (IntactCore.isInitialized(polymer.getSequenceChunks())) {
            sequenceChunks = polymer.getSequenceChunks();
        } else {
            sequenceChunks = IntactContext.getCurrentInstance().getDaoFactory()
                    .getEntityManager().createQuery("select sc from SequenceChunk sc where sc.parentAc = :parentAc")
                    .setParameter("parentAc", polymer.getAc()).getResultList();
        }

        return polymer.getSequence(sequenceChunks);

    }

    /**
     * Checks if the object is managed.
     * @param io the IntactObject
     * @return true is the object is found to be managed, otherwise false.
     */
    public static boolean isManaged( IntactObject io ) {
        return IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().contains( io );
    }

    /**
     * Checks if the given object is detached (i.e. has an AC and is not managed).
     * @param io the IntactObject
     * @return true is the object is found to be detached, otherwise false.
     */
    public static boolean isDetached( IntactObject io ) {
        return ( io.getAc() != null && ! isManaged( io ) );
    }
}
