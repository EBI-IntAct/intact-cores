/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryModifier;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryTerm;
import uk.ac.ebi.intact.core.persistence.dao.query.QueryTermConverter;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.core.persistence.dao.query.impl.StandardQueryTermConverter;

import java.util.*;

/**
 * Allows to create a criteria from an {@link uk.ac.ebi.intact.core.persistence.dao.query.impl.SearchableQuery}
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class SearchableCriteriaBuilder {

    private static final Log log = LogFactory.getLog( SearchableCriteriaBuilder.class );

    private SearchableQuery query;
    private Set<String> aliasesCreated;

    private static final String SHORT_LABEL_PROPERTY = "shortLabel";
    private static final String FULL_NAME_PROPERTY = "fullName";
    private static final String AC_PROPERTY = "ac";
    private static final String PRIMARY_ID_PROPERTY = "primaryId";
    private static final String CV_OBJCLASS_PROPERTY = "objClass";

    private QueryTermConverter queryTermConverter;

    public SearchableCriteriaBuilder( SearchableQuery query ) {
        this.query = query;
        aliasesCreated = new HashSet<String>();
        this.queryTermConverter = new StandardQueryTermConverter();
    }

    public SearchableCriteriaBuilder( SearchableQuery query, QueryTermConverter queryTermConverter ) {
        this.query = query;
        aliasesCreated = new HashSet<String>();
        this.queryTermConverter = queryTermConverter;
    }

    /**
     * Completes a provided criteria with the necessary restrictions.
     *
     * @param searchableClass the class to search
     * @param session         a hibernate session
     *
     * @return
     */
    public Criteria createCriteria( Class<? extends Searchable> searchableClass, Session session ) {
        if ( query == null ) {
            throw new NullPointerException( "SearchableQuery cannot be null" );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Search for: " + searchableClass + ", with query: " + query );
        }

        Criteria criteria = session.createCriteria( searchableClass );

        Junction junction;

        if ( query.isDisjunction() ) {
            junction = Restrictions.disjunction();
        } else {
            junction = Restrictions.conjunction();
        }

        // ac
        addRestriction( junction, AC_PROPERTY, query.getAc(), query.isIgnoreCase() );

        // ac or xref id
        QueryPhrase acOrId = query.getAcOrId();
        if ( isValueValid( acOrId ) ) {
            Disjunction disjAcOrXref = Restrictions.disjunction();
            addRestriction( disjAcOrXref, AC_PROPERTY, acOrId, query.isIgnoreCase() );

            // TODO add here only search for primaryId of the xref and wualifier is identity

            Conjunction primaryIdentityConj = Restrictions.conjunction();
            disjAcOrXref.add( primaryIdentityConj );
            junction.add( disjAcOrXref );

            addRestriction( primaryIdentityConj, xrefProperty( criteria, PRIMARY_ID_PROPERTY ), acOrId, query.isIgnoreCase() );
            primaryIdentityConj.add( Restrictions.eq( xrefCvXrefQualifierXrefProperty( criteria, "primaryId" ), CvXrefQualifier.IDENTITY_MI_REF ) );
        }

        // shortLabel
        addRestriction( junction, SHORT_LABEL_PROPERTY, query.getShortLabel(), query.isIgnoreCase() );

        // description
        addRestriction( junction, FULL_NAME_PROPERTY, query.getDescription(), query.isIgnoreCase() );

        // xref - database
        QueryPhrase xrefPrimaryId = query.getXref();
        QueryPhrase xrefDb = query.getCvDatabaseLabel();

        Junction jXref = Restrictions.conjunction();

        if ( isValueValid( xrefPrimaryId ) ) {
            addRestriction( jXref, xrefProperty( criteria, PRIMARY_ID_PROPERTY ), xrefPrimaryId, query.isIgnoreCase() );
        }
        if ( isValueValid( xrefDb ) ) {
            addRestriction( jXref, xrefCvDatabaseProperty( criteria, SHORT_LABEL_PROPERTY ), xrefDb, false );
        }
        if ( isValueValid( xrefPrimaryId ) || isValueValid( xrefDb ) ) {
            junction.add( jXref );
        }

        // annotation
        QueryPhrase annotText = query.getAnnotationText();
        QueryPhrase annotTopic = query.getCvTopicLabel();

        Junction jAnnot = Restrictions.conjunction();

        if ( isValueValid( annotText ) ) {
            addRestriction( jAnnot, annotationProperty( criteria, "annotationText" ), annotText, false );
        }
        if ( isValueValid( annotTopic ) ) {
            addRestriction( jAnnot, annotationCvTopicProperty( criteria, SHORT_LABEL_PROPERTY ), annotTopic, false );
        }
        if ( isValueValid( annotText ) || isValueValid( annotTopic ) ) {
            junction.add( jAnnot );
        }

        // searchable is an interactor
        if ( searchableClass.isAssignableFrom( Interactor.class ) ) {
            // cvInteractionType
            if ( isValueValid( query.getCvInteractionTypeLabel() ) ) {
                Conjunction objAndLabelConj = Restrictions.conjunction();

                addRestriction( objAndLabelConj, cvInteractionTypeProperty( criteria, SHORT_LABEL_PROPERTY ), query.getCvInteractionTypeLabel(), false );
                objAndLabelConj.add( Restrictions.eq( cvIdentificationProperty( criteria, CV_OBJCLASS_PROPERTY ), CvInteractionType.class.getName() ) );

                if ( query.isIncludeCvInteractionTypeChildren() ) {
                    Junction childJunct = getChildrenDisjunctionForCv( criteria,
                                                                       cvInteractionTypeProperty( criteria, SHORT_LABEL_PROPERTY ),
                                                                       cvInteractionTypeProperty( criteria, CV_OBJCLASS_PROPERTY ),
                                                                       CvInteractionType.class, query.getCvInteractionTypeLabel() );

                    if ( childJunct != null ) // children found, create the disjunction
                    {
                        Disjunction disjCvInteractionType = Restrictions.disjunction();
                        disjCvInteractionType.add( objAndLabelConj );
                        disjCvInteractionType.add( childJunct );
                        junction.add( disjCvInteractionType );
                    } else {
                        junction.add( objAndLabelConj );
                    }
                } else {
                    junction.add( objAndLabelConj );
                }
            }
        }

        // searchable is a experiment
        if ( searchableClass.isAssignableFrom( Experiment.class ) ) {
            // cvInteractionDetection
            if ( isValueValid( query.getCvIdentificationLabel() ) ) {
                Conjunction objAndLabelConj = Restrictions.conjunction();
                objAndLabelConj.add( Restrictions.eq( cvIdentificationProperty( criteria, CV_OBJCLASS_PROPERTY ), CvIdentification.class.getName() ) );

                addRestriction( objAndLabelConj, cvIdentificationProperty( criteria, SHORT_LABEL_PROPERTY ), query.getCvIdentificationLabel(), false );

                if ( query.isIncludeCvIdentificationChildren() ) {
                    Junction childJunct = getChildrenDisjunctionForCv( criteria,
                                                                       cvIdentificationProperty( criteria, SHORT_LABEL_PROPERTY ),
                                                                       cvIdentificationProperty( criteria, CV_OBJCLASS_PROPERTY ),
                                                                       CvIdentification.class, query.getCvIdentificationLabel() );

                    if ( childJunct != null ) // children found, create the disjunction
                    {
                        Disjunction disjCvIdentification = Restrictions.disjunction();
                        disjCvIdentification.add( objAndLabelConj );
                        disjCvIdentification.add( childJunct );
                        junction.add( disjCvIdentification );
                    } else {
                        junction.add( objAndLabelConj );
                    }
                } else {
                    junction.add( objAndLabelConj );
                }
            }

            // cvInteractionDetection
            if ( isValueValid( query.getCvInteractionLabel() ) ) {
                Conjunction objAndLabelConj = Restrictions.conjunction();
                objAndLabelConj.add( Restrictions.eq( cvInteractionProperty( criteria, CV_OBJCLASS_PROPERTY ), CvInteraction.class.getName() ) );

                addRestriction( objAndLabelConj, cvInteractionProperty( criteria, SHORT_LABEL_PROPERTY ), query.getCvInteractionLabel(), false);

                if ( query.isIncludeCvInteractionChildren() ) {
                    Junction childJunct = getChildrenDisjunctionForCv( criteria,
                                                                       cvInteractionProperty( criteria, SHORT_LABEL_PROPERTY ),
                                                                       cvInteractionProperty( criteria, CV_OBJCLASS_PROPERTY ),
                                                                       CvInteraction.class, query.getCvInteractionLabel() );

                    if ( childJunct != null )  // children found, create the disjunction
                    {
                        Disjunction disjCvInteraction = Restrictions.disjunction();
                        disjCvInteraction.add( childJunct );
                        disjCvInteraction.add( objAndLabelConj );
                        junction.add( disjCvInteraction );
                    } else {
                        junction.add( objAndLabelConj );
                    }
                } else {
                    junction.add( objAndLabelConj );
                }
            }
        }

        // finally add the junction created by all the properties to the main criteria
        criteria.add( junction );

        return criteria;
    }

    private void addRestriction( Junction junction, String property, QueryPhrase value, boolean ignoreCase ) {
        if ( isValueValid( value ) ) {
            // classify the terms of the phrase in excluded / included
            List<QueryTerm> exclusionTerms = new ArrayList<QueryTerm>();
            List<QueryTerm> inclusionTerms = new ArrayList<QueryTerm>();

            for ( QueryTerm term : value.getTerms() ) {
                if ( term.hasModifier( QueryModifier.EXCLUDE ) ) {
                    exclusionTerms.add( term );
                } else {
                    inclusionTerms.add( term );
                }
            }

            Junction propertyCriterion;

            if ( inclusionTerms.isEmpty() || exclusionTerms.isEmpty() ) {
                propertyCriterion = Restrictions.conjunction();
            } else {
                propertyCriterion = Restrictions.conjunction();
            }

            junction.add( propertyCriterion );

            if ( !inclusionTerms.isEmpty() ) {
                Criterion inclusionCriterion = termDisjunction( property, inclusionTerms, ignoreCase );
                propertyCriterion.add( inclusionCriterion );
            }

            if ( !exclusionTerms.isEmpty() ) {
                Criterion exclusionCriterion = Restrictions.not( termDisjunction( property, exclusionTerms, ignoreCase ) );
                propertyCriterion.add( exclusionCriterion );
            }

        }
    }

    private Criterion termDisjunction( String property, List<QueryTerm> terms, boolean ignoreCase ) {
        Criterion criterion = null;

        if ( terms.size() == 1 ) {
            criterion = termToCriterion( property, terms.get( 0 ), ignoreCase );
        } else if ( terms.size() > 1 ) {
            Disjunction disj = Restrictions.disjunction();
            for ( QueryTerm term : terms ) {
                disj.add( termToCriterion( property, term, ignoreCase ) );
            }
            criterion = disj;
        }

        return criterion;
    }

    private Criterion termToCriterion( String property, QueryTerm term, boolean ignoreCase) {
        String val = term.getValue();


        SimpleExpression criterion;

        if ( isLikeQuery( term ) ) {
            criterion = Restrictions.like( property, val, mathModeForTerm( term ) );
        }
        else {
            criterion = Restrictions.eq (property, val);
        }

        if (ignoreCase)
        {
            criterion.ignoreCase();
        }

        return criterion;

    }

    private void addRestriction( Junction junction, String property, String value, boolean isLike ) {
        if ( isLike ) {
            junction.add( Restrictions.like( property, value ) );
        } else {
            junction.add( Restrictions.eq( property, value ) );
        }
    }

    private boolean isLikeQuery( QueryTerm term ) {
        for ( QueryModifier mod : term.getModifiers() ) {
            if ( mod == QueryModifier.WILDCARD_END || mod == QueryModifier.WILDCARD_START ) {
                return true;
            }
        }

        return false;
    }

    protected MatchMode mathModeForTerm( QueryTerm term ) {
        if ( term.hasModifier( QueryModifier.WILDCARD_START ) && term.hasModifier( QueryModifier.WILDCARD_END ) ) {
            return MatchMode.ANYWHERE;
        } else if ( term.hasModifier( QueryModifier.WILDCARD_START ) ) {
            return MatchMode.END;
        } else if ( term.hasModifier( QueryModifier.WILDCARD_END ) ) {
            return MatchMode.START;
        }
        return MatchMode.EXACT;
    }


    private Junction getChildrenDisjunctionForCv( Criteria criteria, String shortLabelProperty, String objClassProperty, Class<? extends CvObject> cvType, QueryPhrase cvShortLabelPhrase ) {
        if ( cvShortLabelPhrase == null ) {
            return null;
        }

        Disjunction mainDisjunction = Restrictions.disjunction();

        for ( QueryTerm term : cvShortLabelPhrase.getTerms() ) {
            String cvShortLabel = ( String ) term.getValue();
            CvDagObject cvDagObject = ( CvDagObject ) IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getCvObjectDao(cvType).getByShortLabel( cvType, cvShortLabel );

            log.debug( "\tGetting children for: " + term.getValue() );

            if ( cvDagObject == null ) {
                //throw new IntactException("No CvDagObject with label '" + cvShortLabel + "' and type '" + cvType + "' could be found");
                log.debug( "No CvDagObject with label '" + cvShortLabel + "' and type '" + cvType + "' could be found" );
                continue;
            }

            Map<Class<? extends CvObject>, Set<String>> classifiedChildren = getChildrenCvClassified( cvDagObject );

            if ( classifiedChildren.isEmpty() ) {
                return null;
            }

            for ( Map.Entry<Class<? extends CvObject>, Set<String>> childEntry : classifiedChildren.entrySet() ) {
                Junction entryConjunction = Restrictions.conjunction()
                        .add( Restrictions.eq( objClassProperty, childEntry.getKey().getName() ) );

                Disjunction disj = Restrictions.disjunction();

                for ( String label : childEntry.getValue() ) {
                    addRestriction( disj, shortLabelProperty, label, true );
                }

                entryConjunction.add( disj );
                mainDisjunction.add( entryConjunction );
            }
        }

        return mainDisjunction;
    }

    private Map<Class<? extends CvObject>, Set<String>> getChildrenCvClassified( CvDagObject parentCv ) {
        if ( parentCv == null ) {
            throw new IntactException( "CvDagObject is null" );
        }

        Map<Class<? extends CvObject>, Set<String>> cvsMap = new HashMap<Class<? extends CvObject>, Set<String>>();

        List<CvDagObject> children = new ArrayList<CvDagObject>();
        fillChildrenRecursively( parentCv, children );

        for ( CvDagObject child : children ) {
            addCvObjectToMap( cvsMap, child.getClass(), child.getShortLabel() );
        }

        return cvsMap;
    }

    private void addCvObjectToMap( Map<Class<? extends CvObject>, Set<String>> cvsMap, Class cvType, String label ) {
        if ( cvsMap.containsKey( cvType ) ) {
            cvsMap.get( cvType ).add( label );
        } else {
            Set<String> labels = new HashSet<String>();
            labels.add( label );
            cvsMap.put( cvType, labels );
        }
    }

    private void fillChildrenRecursively( CvDagObject parentCv, Collection<CvDagObject> children ) {
        Collection<CvDagObject> parentChildren = parentCv.getChildren();

        children.addAll( parentChildren );

        for ( CvDagObject child : parentChildren ) {
            fillChildrenRecursively( child, children );
        }
    }

    private String annotationProperty( Criteria criteria, String property ) {
        String aliasName = "annotation";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( "annotations", aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String xrefProperty( Criteria criteria, String property ) {
        String aliasName = "xref";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( "xrefs", aliasName, CriteriaSpecification.LEFT_JOIN );

            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String xrefCvXrefQualifierProperty( Criteria criteria, String property ) {
        String aliasName = "cvXrefQualifier";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( xrefProperty( criteria, "cvXrefQualifier" ), aliasName, CriteriaSpecification.LEFT_JOIN );

            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String xrefCvXrefQualifierXrefProperty( Criteria criteria, String property ) {
        String aliasName = "cvXrefQualifierXrefs";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( xrefCvXrefQualifierProperty( criteria, "xrefs" ), aliasName, CriteriaSpecification.LEFT_JOIN );

            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String annotationCvTopicProperty( Criteria criteria, String property ) {
        String aliasName = "annotCvTopic";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( annotationProperty( criteria, "cvTopic" ), aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String xrefCvDatabaseProperty( Criteria criteria, String property ) {
        String aliasName = "xrefCvDatabase";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( xrefProperty( criteria, "cvDatabase" ), aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String cvInteractionTypeProperty( Criteria criteria, String property ) {
        String aliasName = "cvInteractionType";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( "cvInteractionType", aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String cvIdentificationProperty( Criteria criteria, String property ) {
        String aliasName = "cvIdentification";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( "cvIdentification", aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private String cvInteractionProperty( Criteria criteria, String property ) {
        String aliasName = "cvInteraction";

        if ( !aliasesCreated.contains( aliasName ) ) {
            criteria.createAlias( "cvInteraction", aliasName, CriteriaSpecification.LEFT_JOIN );
            aliasesCreated.add( aliasName );
        }

        return aliasName + "." + property;
    }

    private static boolean isValueValid( QueryPhrase value ) {
        return value != null && !value.isOnlyWildcard();
    }


}
