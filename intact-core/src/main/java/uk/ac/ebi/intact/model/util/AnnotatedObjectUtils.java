/*
 * Copyright 2006 The European Bioinformatics Institute.
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
package uk.ac.ebi.intact.model.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.core.persister.IntactCore;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.filter.CvObjectFilterGroup;
import uk.ac.ebi.intact.model.util.filter.IntactObjectFilterPredicate;
import uk.ac.ebi.intact.model.util.filter.XrefCvFilter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Util methods for annotatedObject.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public final class AnnotatedObjectUtils {

    private static final Log log = LogFactory.getLog(AnnotatedObjectUtils.class);

    public static final java.lang.String TEMP_LABEL_PREFIX = "not-defined-";
    private static final java.lang.String TEMP_LABEL_PATTERN = "not-defined-\\d*";

    private AnnotatedObjectUtils() {
    }

    /**
     * Trims a shortlabel if it is too long to be inserted in the database.
     *
     * @return the shortlabel.
     */
    public static String prepareShortLabel(String shortLabel) {
        boolean modified = false;

        if (shortLabel == null) {

            throw new NullPointerException("Must define a non null short label");

        } else {
            // delete leading and trailing spaces.
            shortLabel = shortLabel.trim();

            if ("".equals(shortLabel)) {
                throw new IllegalArgumentException(
                        "Must define a non empty short label");
            }

            if (shortLabel.length() >= AnnotatedObject.RECOMMENDED_SHORT_LABEL_LEN) {
                shortLabel = shortLabel.substring(0, AnnotatedObject.RECOMMENDED_SHORT_LABEL_LEN);
                modified = true;
            }
        }

        return shortLabel;
    }

    /**
     * Search for all Xrefs having Xref with the given CvDatabase.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param db the non null CvDatabase filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs(AnnotatedObject ao, CvDatabase db) {

        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (db == null) {
            throw new NullPointerException("CvDatabase must not be null.");
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedCvObject(db);

        return searchXrefs(ao, new XrefCvFilter(cvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with the given CvDatabase MI.
     *
     * @param ao   the non null AnnotatedObject to search on.
     * @param dbMi the non null CvDatabase filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefsByDatabase(AnnotatedObject<X, ?> ao, String dbMi) {
        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (dbMi == null) {
            throw new NullPointerException("dbMi must not be null.");
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedIdentifier(dbMi);

        return searchXrefs(ao, new XrefCvFilter(cvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with both the given CvDatabase and CvXrefQualifier.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param db the non null CvDatabase filter.
     * @param qu the non null CvXrefQualifier filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs(AnnotatedObject ao, CvDatabase db, CvXrefQualifier qu) {

        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (db == null) {
            throw new NullPointerException("CvDatabase must not be null.");
        }
        if (qu == null) {
            throw new NullPointerException("CvXrefQualifier must not be null.");
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedCvObject(db);

        CvObjectFilterGroup qualifierCvFilterGroup = new CvObjectFilterGroup();
        qualifierCvFilterGroup.addIncludedCvObject(qu);

        return searchXrefs(ao, new XrefCvFilter(cvFilterGroup, qualifierCvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with both the given CvDatabase and CvXrefQualifier MIs.
     *
     * @param ao          the non null AnnotatedObject to search on.
     * @param dbMi        the non null CvDatabase filter.
     * @param qualifierMi the non null CvXrefQualifier filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefs(AnnotatedObject<X, ?> ao, String dbMi, String qualifierMi) {

        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (dbMi == null) {
            throw new NullPointerException("dbMi must not be null.");
        }

        CvObjectFilterGroup databaseCvFilterGroup = new CvObjectFilterGroup();
        databaseCvFilterGroup.addIncludedIdentifier(dbMi);

        CvObjectFilterGroup qualifierCvFilterGroup = new CvObjectFilterGroup();
        qualifierCvFilterGroup.addIncludedIdentifier(qualifierMi);

        return searchXrefs(ao, new XrefCvFilter(databaseCvFilterGroup, qualifierCvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with the given CvXrefQualifier.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param qu the non null CvXrefQualifier filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs(AnnotatedObject ao, CvXrefQualifier qu) {

        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (qu == null) {
            throw new NullPointerException("CvXrefQualifier must not be null.");
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedCvObject(qu);

        return searchXrefs(ao, new XrefCvFilter(new CvObjectFilterGroup(), cvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with the given CvXrefQualifier.
     *
     * @param ao          the non null AnnotatedObject to search on.
     * @param qualifierMi the non null CvXrefQualifier filter.
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefsByQualifier(AnnotatedObject<X, ?> ao, String qualifierMi) {

        if (ao == null) {
            throw new NullPointerException("AnnotatedObject must not be null.");
        }
        if (qualifierMi == null) {
            throw new NullPointerException("qualifierMi must not be null.");
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedIdentifier(qualifierMi);

        return searchXrefs(ao, new XrefCvFilter(new CvObjectFilterGroup(), cvFilterGroup));
    }

    /**
     * Gets the generic Xref type for an AnnotatedObject class
     *
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     */
    public static Class<? extends Xref> getXrefClassType(Class<? extends AnnotatedObject> clazz) {
        clazz = CgLibUtil.removeCglibEnhanced(clazz);

        PropertyDescriptor propDesc = null;
        try {
            propDesc = new PropertyDescriptor("xrefs", clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Method method = propDesc.getReadMethod();

        return getParameterizedType(method.getGenericReturnType());
    }


    /**
     * Gets the generic Xref type for an AnnotatedObject class
     *
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     * @since 1.6.1
     */
    public static Class<? extends Alias> getAliasClassType(Class<? extends AnnotatedObject> clazz) {
        clazz = CgLibUtil.removeCglibEnhanced(clazz);

        PropertyDescriptor propDesc = null;
        try {
            propDesc = new PropertyDescriptor("aliases", clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Method method = propDesc.getReadMethod();

        return getParameterizedType(method.getGenericReturnType());
    }

    public static Collection<Alias> getAliasByType(AnnotatedObject<?, ?> annotatedObject, String miOrLabel) {
        Collection<Alias> matchingAliases = new ArrayList<Alias>();
        for (Alias alias : annotatedObject.getAliases()) {
            if (alias.getCvAliasType() != null
                    && (
                    alias.getCvAliasType().getIdentifier().equals(miOrLabel)
                            ||
                            alias.getCvAliasType().getShortLabel().equals(miOrLabel))
                    ) {
                matchingAliases.add(alias);
            }
        }
        return matchingAliases;
    }

    /**
     * Finds an Annotations with a topic that has an MI or label equal to the value provided
     *
     * @param annotatedObject The annotatedObject to find the annotation
     * @param miOrLabel       The MI (use it when possible) or the shortLabel
     * @return The annotation with that CvTopic. Null if no annotation for that CV is found
     * @since 1.8.0
     */
    public static Annotation findAnnotationByTopicMiOrLabel(AnnotatedObject<?, ?> annotatedObject, String miOrLabel) {
        for (Annotation annotation : annotatedObject.getAnnotations()) {
            final CvTopic topic = annotation.getCvTopic();
            if (topic != null && (miOrLabel.equals(topic.getIdentifier()) || miOrLabel.equals(topic.getShortLabel()))) {
                return annotation;
            }
        }
        return null;
    }

    private static Class getParameterizedType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            return (Class) paramType.getActualTypeArguments()[0];
        }
        return null;
    }

    /**
     * Check if the passed annotated objects contain the same set of filtered Xrefs.
     *
     * @return true or false
     * @since 1.9.0
     */
    public static <X extends Xref> boolean containTheSameXrefs(XrefCvFilter xrefFilter, AnnotatedObject<X, ?>... aos) {
        List<List<X>> listOfXrefLists = new ArrayList<List<X>>(aos.length);

        for (AnnotatedObject<X, ?> ao : aos) {
            listOfXrefLists.add(searchXrefs(ao, xrefFilter));
        }

        List<X> referenceList = listOfXrefLists.get(0);
        listOfXrefLists.remove(0);

        for (List<X> xrefList : listOfXrefLists) {
            if (referenceList.size() != xrefList.size()) {
                return false;
            }
        }

        Comparator<X> xrefComparator = new Comparator<X>() {
            public int compare(X o1, X o2) {
                return o1.getPrimaryId().compareTo(o2.getPrimaryId());
            }
        };

        Collections.sort(referenceList, xrefComparator);

        for (List<X> xrefList : listOfXrefLists) {
            Collections.sort(xrefList, xrefComparator);

            for (int i = 0; i < referenceList.size(); i++) {
                if (!(referenceList.get(i).equals(xrefList.get(i)))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Retrieve the xrefs from an annotated object that comply with the filter.
     *
     * @param ao         The annotated object
     * @param xrefFilter The xref filter
     * @return The collection of filtered xrefs
     * @since 1.9.0
     */
    public static <X extends Xref> List<X> searchXrefs(AnnotatedObject<X, ?> ao, XrefCvFilter xrefFilter) {
        List<X> xrefList = new ArrayList<X>();
        CollectionUtils.select(IntactCore.ensureInitializedXrefs(ao), new IntactObjectFilterPredicate(xrefFilter), xrefList);
        return xrefList;
    }

    /**
     * Find all annotations having any of the provided CvTopics.
     *
     * @param annotatedObject the object to serch on.
     * @param topics          the topics we are searching.
     * @return a non null collection of Annotation.
     */
    public static Collection<Annotation> findAnnotationsByCvTopic(AnnotatedObject<?, ?> annotatedObject,
                                                                  Collection<CvTopic> topics) {
        if (annotatedObject == null) {
            throw new NullPointerException("You must give a non null annotatedObject");
        }

        if (topics == null) {
            throw new NullPointerException("You must give a non null collection of CvTopic");
        }

        Collection<Annotation> annotations = new ArrayList<Annotation>();
        if (!topics.isEmpty()) {
            for (Annotation annotation : IntactCore.ensureInitializedAnnotations(annotatedObject)) {
                if (topics.contains(annotation.getCvTopic())) {
                    annotations.add(annotation);
                }
            }
        }
        return annotations;
    }

    public static Collection<Annotation> getPublicAnnotations(final AnnotatedObject<?, ?> annotatedObject) {

        final Collection<Annotation> publicAnnotations = new ArrayList<Annotation>(annotatedObject.getAnnotations().size());
        final Iterator<Annotation> i = IntactCore.ensureInitializedAnnotations(annotatedObject).iterator();
        while (i.hasNext()) {
            Annotation annotation = i.next();
            if (isCvTopicPublic(annotation.getCvTopic())) {
                publicAnnotations.add(annotation);
            }
        }

        return publicAnnotations;
    }

    public static boolean isCvTopicPublic(CvTopic cvTopic) {
        for (Annotation annotation : IntactCore.ensureInitializedAnnotations(cvTopic)) {
            if (annotation.getCvTopic().getShortLabel().equals(CvTopic.HIDDEN)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the object state is "new" or "managed". This check is useful in those
     * cases where we need to check if the collections (annotations, aliases and xrefs) are
     * accessible and won't throw a LazyInitializationException if accessed.
     *
     * @param annotatedObject The AnnotatedObject to check
     * @return True if is new or managed
     */
    public static boolean isNewOrManaged(AnnotatedObject annotatedObject) {
        // is it new?
        if (annotatedObject.getAc() == null) return true;

        // is it transient? (as in opposition to managed)
        if (IntactContext.currentInstanceExists() &&
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().isTransient(annotatedObject)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if two given annotated objects contain the same set of annotations, xrefs and aliases
     *
     * @param ao1 Annotated object 1
     * @param ao2 Annotated object 2
     * @return if the two annotated objects contain the same set of annnotations, xrefs and aliases
     */
    public static boolean containSameCollections(AnnotatedObject ao1, AnnotatedObject ao2) {
        if (!containSameXrefs(ao1, ao2)) {
            return false;
        }
        if (!containSameAnnotations(ao1, ao2)) {
            return false;
        }
        if (!containSameAliases(ao1, ao2)) {
            return false;
        }
        return true;
    }

    public static boolean containSameAnnotations(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(IntactCore.ensureInitializedAnnotations(ao1), IntactCore.ensureInitializedAnnotations(ao2));
    }

    public static boolean containSameXrefs(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(IntactCore.ensureInitializedXrefs(ao1), IntactCore.ensureInitializedXrefs(ao2));
    }

    public static boolean containSameAliases(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(IntactCore.ensureInitializedAliases(ao1), IntactCore.ensureInitializedXrefs(ao2));
    }

    /**
     * Method to compare Annotation, Xref and Aliases collections
     *
     * @param intactObjects1 Annotations, Xrefs or Aliases
     * @param intactObjects2 Annotations, Xrefs or Aliases
     * @return true if the collections are equal
     */
    private static boolean areCollectionEqual(Collection<? extends IntactObject> intactObjects1, Collection<? extends IntactObject> intactObjects2) {
        if (intactObjects1.size() != intactObjects2.size()) {
            return false;
        }

        List<String> uniqueStrings1 = new ArrayList<String>();

        for (IntactObject io1 : intactObjects1) {
            uniqueStrings1.add(createUniqueString(io1));
        }

        for (IntactObject io2 : intactObjects2) {
            String unique2 = createUniqueString(io2);

            if (!uniqueStrings1.contains(unique2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates unique strings for Annotations,Xrefs and aliases.
     *
     * @param io the object to use
     * @return a unique string for that object
     */
    protected static String createUniqueString(IntactObject io) {
        if (io == null) throw new NullPointerException("IntactObject cannot be null to create a unique String");

        if (io instanceof Annotation) {
            Annotation annot = (Annotation) io;
            String cvId = (annot.getCvTopic() != null) ? annot.getCvTopic().getIdentifier() : "";
            return annot.getAnnotationText() + "__" + cvId;
        } else if (io instanceof Xref) {
            Xref xref = (Xref) io;
            String qualId = (xref.getCvXrefQualifier() != null) ? xref.getCvXrefQualifier().getIdentifier() : "";
            return xref.getPrimaryId() + "__" + xref.getCvDatabase().getIdentifier() + "__" + qualId;
        } else if (io instanceof Alias) {
            Alias alias = (Alias) io;
            String typeId = (alias.getCvAliasType() != null) ? alias.getCvAliasType().getIdentifier() : "";
            return alias.getName() + "__" + typeId;
        }
        return io.toString();
    }

    /**
     * Returns true if the label for the interaction is temporary
     *
     * @param label
     * @return
     * @since 2.1.0
     */
    public static boolean isTemporaryLabel(String label) {
        return label.matches(TEMP_LABEL_PATTERN);
    }

    /**
     * Find the parent object for an IntactObject.
     *
     * @param child the child object
     * @return the parent object. Null if none.
     * @since 2.4.0
     */
    public static AnnotatedObject findParent(IntactObject child) {
        if (child instanceof Publication) {
            return null;
        } else if (child instanceof Experiment) {
            return ((Experiment) child).getPublication();
        } else if (child instanceof Interaction) {
            Interaction interaction = (Interaction) child;

            Collection<Experiment> experiments;

            if (!IntactCore.isInitialized(interaction.getExperiments())) {
                experiments = IntactContext.getCurrentInstance().getDaoFactory().getInteractionDao().getByAc(interaction.getAc()).getExperiments();
                interaction.setExperiments(experiments);
            } else {
                experiments = interaction.getExperiments();
            }

            if (experiments.isEmpty()) {
                return null;
            }

            if (log.isWarnEnabled() && experiments.size() > 1) {
                log.warn("More than one experiment found for child of type Interaction: " + child);
            }

            return experiments.iterator().next();
        } else if (child instanceof Component) {
            return ((Component) child).getInteraction();
        } else if (child instanceof InteractionParameter) {
            return ((InteractionParameter) child).getInteraction();
        } else if (child instanceof Feature) {
            return ((Feature) child).getComponent();
        } else if (child instanceof ComponentParameter) {
            return ((ComponentParameter) child).getComponent();
        } else if (child instanceof Range) {
            return ((Range) child).getFeature();
        } else if (child instanceof Xref) {
            return ((Xref) child).getParent();
        } else if (child instanceof Annotation) {
            throw new IllegalArgumentException("An annotation can have multiple parents");
        } else if (child instanceof Alias) {
            return ((Alias) child).getParent();
        } else {
            //throw new IllegalArgumentException("Cannot find parent for child of type: " +
            //child.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * Checks if the parent collection that contains children of the child type passed is initialized.
     * Can be used as a safety check before executing the removeChild() method.
     *
     * @param parent
     * @param child
     */
    public static boolean isChildrenInitialized(AnnotatedObject parent, IntactObject child) {
        if (parent instanceof Publication && child instanceof Experiment) {
            return IntactCore.isInitialized(((Publication) parent).getExperiments());
        } else if (parent instanceof Experiment && child instanceof Interaction) {
            return IntactCore.isInitialized(((Experiment) parent).getInteractions());
        } else if (parent instanceof Interaction && child instanceof Component) {
            return IntactCore.isInitialized(((Interaction) parent).getComponents());
        } else if (parent instanceof Interaction && child instanceof InteractionParameter) {
            return IntactCore.isInitialized(((Interaction) parent).getParameters());
        } else if (parent instanceof Component && child instanceof Feature) {
            return IntactCore.isInitialized(((Component) parent).getBindingDomains());
        } else if (parent instanceof Component && child instanceof ComponentParameter) {
            return IntactCore.isInitialized(((Component) parent).getParameters());
        } else if (parent instanceof Feature && child instanceof Range) {
            return IntactCore.isInitialized(((Feature) parent).getRanges());
        } else if (child instanceof Xref) {
            parent.removeXref((Xref) child);
            return IntactCore.isInitialized(parent.getXrefs());
        } else if (child instanceof Annotation) {
            return IntactCore.isInitialized(parent.getAnnotations());
        } else if (child instanceof Alias) {
            return IntactCore.isInitialized(parent.getAliases());
        } else {
            throw new IllegalArgumentException("Unexpected combination parent/child - Parent: " +
                    parent.getClass().getSimpleName() + " / Child: " + child.getClass().getSimpleName());
        }
    }

    /**
     * Removes a child from an annotated object, without knowing the exact type (ie. remove a range from a feature).
     *
     * @param parent
     * @param child
     */
    public static void removeChild(AnnotatedObject parent, IntactObject child) {
        if (parent instanceof Publication && child instanceof Experiment) {
            ((Publication) parent).removeExperiment((Experiment) child);
        } else if (parent instanceof Experiment && child instanceof Interaction) {
            ((Experiment) parent).removeInteraction((Interaction) child);
        } else if (parent instanceof Interaction && child instanceof Component) {
            ((Interaction) parent).removeComponent((Component) child);
        } else if (parent instanceof Interaction && child instanceof InteractionParameter) {
            ((Interaction) parent).removeParameter((InteractionParameter) child);
        } else if (parent instanceof Component && child instanceof Feature) {
            ((Component) parent).removeBindingDomain((Feature) child);
        } else if (parent instanceof Component && child instanceof ComponentParameter) {
            ((Component) parent).removeParameter((ComponentParameter) child);
        } else if (parent instanceof Feature && child instanceof Range) {
            ((Feature) parent).removeRange((Range) child);
        } else if (child instanceof Xref) {
            parent.removeXref((Xref) child);
        } else if (child instanceof Annotation) {
            parent.removeAnnotation((Annotation) child);
        } else if (child instanceof Alias) {
            parent.removeAlias((Alias) child);
        } else {
            throw new IllegalArgumentException("Unexpected combination parent/child - Parent: " +
                    parent.getClass().getSimpleName() + " / Child: " + child.getClass().getSimpleName());
        }
    }
}
