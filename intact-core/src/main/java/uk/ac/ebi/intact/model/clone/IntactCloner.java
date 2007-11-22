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

import uk.ac.ebi.intact.model.*;

import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * IntAct Object cloner.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCloner {

    public IntactObject clone( IntactObject intactObject ) {
        IntactObject io = null;
        if ( intactObject instanceof AnnotatedObject ) {
            io = cloneAnnotatedObject( ( AnnotatedObject ) intactObject );
        } else if ( intactObject instanceof Annotation ) {
            io = cloneAnnotation( ( Annotation ) intactObject );
        } else if ( intactObject instanceof Alias ) {
            io = cloneAlias( ( Alias ) intactObject );
        } else if ( intactObject instanceof Xref ) {
            io = cloneXref( ( Xref ) intactObject );
        } else {
            throw new IllegalArgumentException( "Cannot clone objects of type: " + intactObject.getClass().getName() );
        }
        return io;
    }

    protected AnnotatedObject cloneAnnotatedObject( AnnotatedObject annotatedObject ) {
        AnnotatedObject ao = null;
        if ( annotatedObject instanceof Interaction ) {
            ao = cloneInteraction( ( Interaction ) annotatedObject );
        } else if ( annotatedObject instanceof Interactor ) {
            ao = cloneInteractor( ( Interactor ) annotatedObject );
        } else if ( annotatedObject instanceof CvObject ) {
            ao = cloneCvObject( ( CvObject ) annotatedObject );
        } else if ( annotatedObject instanceof Experiment ) {
            ao = cloneExperiment( ( Experiment ) annotatedObject );
        } else if ( annotatedObject instanceof Component ) {
            ao = cloneComponent( ( Component ) annotatedObject );
        } else if ( annotatedObject instanceof BioSource ) {
            ao = cloneBioSource( ( BioSource ) annotatedObject );
        } else if ( annotatedObject instanceof Feature ) {
            ao = cloneFeature( ( Feature ) annotatedObject );
        } else if ( annotatedObject instanceof Publication ) {
            ao = clonePublication( ( Publication ) annotatedObject );
        } else if ( annotatedObject instanceof Institution ) {
            ao = cloneInstitution( ( Institution ) annotatedObject );
        } else {
            throw new IllegalArgumentException( "Cannot process annotated object of type: " + annotatedObject.getClass().getName() );
        }
        return ao;
    }

    ///////////////////////////////////////
    // IntactObject cloners

    protected Annotation cloneAnnotation( Annotation annotation ) throws IntactClonerException {
        if ( annotation == null ) return null;
        return new Annotation( cloneInstitution( annotation.getOwner() ),
                               ( CvTopic ) cloneCvObject( annotation.getCvTopic() ),
                               annotation.getAnnotationText() );
    }

    protected Alias cloneAlias( AnnotatedObject clonedParent, Alias alias ) throws IntactClonerException {
        if ( alias == null ) return null;
        if ( clonedParent == null ) {
            throw new IllegalArgumentException( "Parent AnnotatedObject must not be null" );
        }

        Class clazz = alias.getClass();
        Alias clonedAlias = null;
        try {
            final Constructor constructor = clazz.getConstructor( Institution.class,
                                                                  AnnotatedObject.class,
                                                                  CvTopic.class,
                                                                  String.class );
            clonedAlias = ( Alias ) constructor.newInstance( cloneInstitution( alias.getOwner() ),
                                                             clonedParent,
                                                             ( CvTopic ) cloneCvObject( alias.getCvAliasType() ),
                                                             alias.getName() );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }
        return clonedAlias;
    }

    protected Xref cloneXref( AnnotatedObject clonedParent, Xref xref ) throws IntactClonerException {
        if ( xref == null ) return null;
        if ( clonedParent == null ) {
            throw new IllegalArgumentException( "Parent AnnotatedObject must not be null" );
        }

        Class clazz = xref.getClass();
        Xref clonedXref = null;
        try {
            final Constructor constructor = clazz.getConstructor( Institution.class,
                                                                  CvDatabase.class,
                                                                  String.class,
                                                                  String.class,
                                                                  String.class,
                                                                  CvXrefQualifier.class );
            clonedXref = ( Xref ) constructor.newInstance( cloneInstitution( xref.getOwner() ),
                                                           ( CvDatabase ) cloneCvObject( xref.getCvDatabase() ),
                                                           xref.getPrimaryId(),
                                                           xref.getSecondaryId(),
                                                           xref.getDbRelease(),
                                                           ( CvXrefQualifier ) cloneCvObject( xref.getCvXrefQualifier() ) );
            clonedParent.addXref( xref );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }

        return clonedXref;
    }

    protected Range cloneRange( Range range ) {
        if ( range == null ) {
            throw new IllegalArgumentException( "You must give a non null range" );
        }

        Range clonedRange = new Range( cloneInstitution( range.getOwner() ),
                                       range.getFromIntervalStart(),
                                       range.getFromIntervalEnd(),
                                       range.getToIntervalStart(),
                                       range.getToIntervalEnd(),
                                       range.getSequence() );

        clonedRange.setFromCvFuzzyType( ( CvFuzzyType ) cloneCvObject( range.getFromCvFuzzyType() ) );
        clonedRange.setToCvFuzzyType( ( CvFuzzyType ) cloneCvObject( range.getToCvFuzzyType() ) );

        return clonedRange;
    }

    ///////////////////////////////////////
    // AnnotatedObject cloners

    public Experiment cloneExperiment( Experiment experiment ) {
        if ( experiment == null ) return null;
        Experiment clone = new Experiment( cloneInstitution( experiment.getOwner() ),
                                           experiment.getShortLabel(),
                                           cloneBioSource( experiment.getBioSource() ) );

        //TODO clone


        cloneCvObject( experiment.getCvIdentification() );
        cloneCvObject( experiment.getCvInteraction() );
        cloneBioSource( experiment.getBioSource() );
        clonePublication( experiment.getPublication() );

        //

        cloneAnnotatedObjectCommon( experiment );
    }

    public Feature cloneFeature( Feature feature ) throws IntactClonerException {
        AnnotatedObject ao = null;
        if ( feature == null ) return null;

        //TODO clone


        cloneCvObject( feature.getCvFeatureType() );
        cloneCvObject( feature.getCvFeatureIdentification() );

        for ( Range range : feature.getRanges() ) {
            cloneRange( range );
        }

        cloneAnnotatedObjectCommon( feature );

        throw new UnsupportedOperationException();
    }

    public Institution cloneInstitution( Institution institution ) {
        AnnotatedObject ao = null;
        if ( institution == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( institution );
    }

    public Interaction cloneInteraction( Interaction interaction ) {
        AnnotatedObject ao = null;
        if ( interaction == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( interaction );

        throw new UnsupportedOperationException();
    }

    public AnnotatedObject cloneInteractor( Interactor interactor ) {
        AnnotatedObject ao = null;
        if ( interactor == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( interactor );

        throw new UnsupportedOperationException();
    }

    public BioSource cloneBioSource( BioSource bioSource ) {
        AnnotatedObject ao = null;
        if ( bioSource == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( bioSource );

        throw new UnsupportedOperationException();
    }

    public Publication clonePublication( Publication publication ) {
        AnnotatedObject ao = null;
        if ( publication == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( publication );

        throw new UnsupportedOperationException();
    }

    public Component cloneComponent( Component component ) {
        AnnotatedObject ao = null;
        if ( component == null ) return null;

        //TODO clone


        cloneAnnotatedObjectCommon( component );

        throw new UnsupportedOperationException();
    }

    public CvObject cloneCvObject( CvObject cvObject ) throws IntactClonerException {
        if ( cvObject == null ) return null;

        Class clazz = cvObject.getClass();
        CvObject clone = null;
        try {
            final Constructor constructor = clazz.getConstructor( Institution.class, String.class );
            clone = ( CvObject ) constructor.newInstance( cloneInstitution( cvObject.getOwner() ),
                                                          cvObject.getShortLabel() );
            clone.setFullName( cvObject.getFullName() );
            cloneAnnotatedObjectCommon( cvObject, clone );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected AnnotatedObject cloneAnnotatedObjectCommon( AnnotatedObject ao, AnnotatedObject clone ) throws IntactClonerException {
        for ( Annotation annotation : ao.getAnnotations() ) {
            clone.addAnnotation( cloneAnnotation( annotation ) );
        }
        for ( Alias alias : ( Collection<Alias> ) ao.getAliases() ) {
            clone.addAlias( cloneAlias( clone, alias ) );
        }
        for ( Xref xref : ( Collection<Xref> ) ao.getXrefs() ) {
            clone.addXref( cloneXref( clone, xref ) );
        }
        return clone;
    }
}