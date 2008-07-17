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
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.filter.CvObjectFilterGroup;
import uk.ac.ebi.intact.model.util.filter.IntactObjectFilterPredicate;
import uk.ac.ebi.intact.model.util.filter.XrefCvFilter;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

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
public class AnnotatedObjectUtils {

    private AnnotatedObjectUtils() {
    }

    /**
     * Trims a shortlabel if it is too long to be inserted in the database.
     *
     * @return the shortlabel.
     */
    public static String prepareShortLabel( String shortLabel ) {
        boolean modified = false;

        if ( shortLabel == null ) {

            throw new NullPointerException( "Must define a non null short label" );

        } else {
            // delete leading and trailing spaces.
            shortLabel = shortLabel.trim();

            if ( "".equals( shortLabel ) ) {
                throw new IllegalArgumentException(
                        "Must define a non empty short label" );
            }

            if ( shortLabel.length() >= AnnotatedObject.MAX_SHORT_LABEL_LEN ) {
                shortLabel = shortLabel.substring( 0, AnnotatedObject.MAX_SHORT_LABEL_LEN );
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
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvDatabase db ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( db == null ) {
            throw new NullPointerException( "CvDatabase must not be null." );
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedCvObject(db);

        return searchXrefs(ao, new XrefCvFilter(cvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with the given CvDatabase MI.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param dbMi the non null CvDatabase filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefsByDatabase( AnnotatedObject<X,?> ao, String dbMi ) {
        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( dbMi == null ) {
            throw new NullPointerException( "dbMi must not be null." );
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
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvDatabase db, CvXrefQualifier qu ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( db == null ) {
            throw new NullPointerException( "CvDatabase must not be null." );
        }
        if ( qu == null ) {
            throw new NullPointerException( "CvXrefQualifier must not be null." );
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
     * @param ao the non null AnnotatedObject to search on.
     * @param dbMi the non null CvDatabase filter.
     * @param qualifierMi the non null CvXrefQualifier filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefs( AnnotatedObject<X,?> ao, String dbMi, String qualifierMi ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( dbMi == null ) {
            throw new NullPointerException( "dbMi must not be null." );
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
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvXrefQualifier qu ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( qu == null ) {
            throw new NullPointerException( "CvXrefQualifier must not be null." );
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedCvObject(qu);

        return searchXrefs(ao, new XrefCvFilter(new CvObjectFilterGroup(), cvFilterGroup));
    }

    /**
     * Search for all Xrefs having Xref with the given CvXrefQualifier.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param qualifierMi the non null CvXrefQualifier filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static <X extends Xref> Collection<X> searchXrefsByQualifier( AnnotatedObject<X,?> ao, String qualifierMi ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( qualifierMi == null ) {
            throw new NullPointerException( "qualifierMi must not be null." );
        }

        CvObjectFilterGroup cvFilterGroup = new CvObjectFilterGroup();
        cvFilterGroup.addIncludedIdentifier(qualifierMi);

        return searchXrefs(ao, new XrefCvFilter(new CvObjectFilterGroup(), cvFilterGroup));
    }

    /**
     * Gets the generic Xref type for an AnnotatedObject class
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     */
    public static Class<? extends Xref> getXrefClassType(Class<? extends AnnotatedObject> clazz)  {
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
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     *
     * @since 1.6.1
     */
    public static Class<? extends Alias> getAliasClassType(Class<? extends AnnotatedObject> clazz)  {
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

    /**
     * Finds an Annotations with a topic that has an MI or label equal to the value provided
     * @param annotatedObject The annotatedObject to find the annotation
     * @param miOrLabel The MI (use it when possible) or the shortLabel
     * @return The annotation with that CvTopic. Null if no annotation for that CV is found
     *
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
     * @return true or false
     *
     * @since 1.9.0
     */
    public static <X extends Xref> boolean containTheSameXrefs(XrefCvFilter xrefFilter, AnnotatedObject<X,?> ... aos ) {
        List<List<X>> listOfXrefLists = new ArrayList<List<X>>(aos.length);

        for (AnnotatedObject<X,?> ao : aos) {
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

            for (int i=0; i<referenceList.size(); i++) {
                if (!(referenceList.get(i).equals(xrefList.get(i)))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Retrieve the xrefs from an annotated object that comply with the filter.
     * @param ao The annotated object
     * @param xrefFilter The xref filter
     * @return The collection of filtered xrefs
     *
     * @since 1.9.0
     */
    public static <X extends Xref> List<X> searchXrefs(AnnotatedObject<X,?> ao, XrefCvFilter xrefFilter) {
        List<X> xrefList = new ArrayList<X>();
        CollectionUtils.select(ao.getXrefs(), new IntactObjectFilterPredicate(xrefFilter), xrefList);
        return xrefList;
    }

    /**
     * Find all annotations having any of the provided CvTopics.
     *
     * @param annotatedObject the object to serch on.
     * @param topics          the topics we are searching.
     * @return a non null collection of Annotation.
     */
    public static Collection<Annotation> findAnnotationsByCvTopic( AnnotatedObject<?, ?> annotatedObject,
                                                                   Collection<CvTopic> topics ) {
        if ( annotatedObject == null ) {
            throw new NullPointerException( "You must give a non null annotatedObject" );
        }

        if ( topics == null ) {
            throw new NullPointerException( "You must give a non null collection of CvTopic" );
        }

        Collection<Annotation> annotations = new ArrayList<Annotation>();
        if( ! topics.isEmpty() ) {
            for ( Annotation annotation : annotatedObject.getAnnotations() ) {
                if( topics.contains( annotation.getCvTopic() )) {
                    annotations.add( annotation );
                }
            }
        }
        return annotations;
    }

    /**
     * Check if the object state is "new" or "managed". This check is useful in those
     * cases where we need to check if the collections (annotations, aliases and xrefs) are
     * accessible and won't throw a LazyInitializationException if accessed.
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
}
