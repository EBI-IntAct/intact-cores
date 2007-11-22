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
import java.util.Date;

/**
 * IntAct Object cloner.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCloner {

    public IntactObject clone( IntactObject intactObject ) throws IntactClonerException {
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

    protected AnnotatedObject cloneAnnotatedObject( AnnotatedObject annotatedObject ) throws IntactClonerException {
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

    protected Range cloneRange( Range range ) throws IntactClonerException {
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

    public Experiment cloneExperiment( Experiment experiment ) throws IntactClonerException {
        if ( experiment == null ) return null;
        Experiment clone = new Experiment( cloneInstitution( experiment.getOwner() ),
                                           experiment.getShortLabel(),
                                           cloneBioSource( experiment.getBioSource() ) );

        cloneCvObject( experiment.getCvIdentification() );
        cloneCvObject( experiment.getCvInteraction() );
        cloneBioSource( experiment.getBioSource() );
        clonePublication( experiment.getPublication() );

        for ( Interaction i : experiment.getInteractions() ) {
            clone.addInteraction( cloneInteraction(  i ) );
        }

        cloneAnnotatedObjectCommon( experiment, clone );
        cloneIntactObjectCommon( experiment, clone );

        return clone;
    }

    public Feature cloneFeature( Feature feature, Component clonedComponent ) throws IntactClonerException {
        if ( feature == null ) return null;
        Feature clone = new Feature( cloneInstitution( feature.getOwner() ),
                                     feature.getShortLabel(),
                                     clonedComponent,
                                     ( CvFeatureType ) cloneCvObject( feature.getCvFeatureType() ) );

        clone.setCvFeatureIdentification( ( CvFeatureIdentification ) cloneCvObject( feature.getCvFeatureIdentification() ) );

        for ( Range range : feature.getRanges() ) {
            clone.addRange( cloneRange( range ) );
        }

        cloneAnnotatedObjectCommon( feature, clone );
        cloneIntactObjectCommon( feature, clone );

        return clone;
    }

    public Institution cloneInstitution( Institution institution ) throws IntactClonerException {
        if ( institution == null ) return null;

        Institution clone = new Institution( institution.getShortLabel() );

        clone.setUrl( institution.getUrl() );
        clone.setPostalAddress( institution.getPostalAddress() );
        
        cloneAnnotatedObjectCommon( institution, clone );
        cloneIntactObjectCommon( institution, clone );

        return clone;
    }

    public Interaction cloneInteraction( Interaction interaction ) throws IntactClonerException {
        if ( interaction == null ) return null;
        Interaction clone = null;


        //TODO clone


        cloneAnnotatedObjectCommon( interaction, clone );
        cloneIntactObjectCommon( interaction, clone );

        throw new UnsupportedOperationException();
    }

    public Interactor cloneInteractor( Interactor interactor ) throws IntactClonerException {
        if ( interactor == null ) return null;

        if ( interactor instanceof Interaction ) {
            return cloneInteraction( ( Interaction ) interactor );
        }

        Interactor clone = null;

        final Class clazz = interactor.getClass();
        Constructor constructor = null;

        try {
            if ( interactor instanceof SmallMolecule ) {
                constructor = clazz.getConstructor( String.class,
                                                    Institution.class,
                                                    CvInteractorType.class );
                clone = ( Interactor ) constructor.newInstance( interactor.getShortLabel(),
                                                                cloneInstitution( interactor.getOwner() ),
                                                                ( CvInteractorType ) cloneCvObject( interactor.getCvInteractorType() ) );
            } else if ( interactor instanceof Polymer ) {

                constructor = clazz.getConstructor( Institution.class,
                                                    BioSource.class,
                                                    String.class,
                                                    CvInteractorType.class );
                clone = ( Interactor ) constructor.newInstance( cloneInstitution( interactor.getOwner() ),
                                                                cloneBioSource( interactor.getBioSource() ),
                                                                interactor.getShortLabel(),
                                                                ( CvInteractorType ) cloneCvObject( interactor.getCvInteractorType() ) );
                Polymer p = ( Polymer ) clone;
                p.setSequence( ( ( Polymer ) interactor ).getSequence() );
                p.setCrc64( ( ( Polymer ) interactor ).getCrc64() );

            } else {

                throw new IllegalArgumentException( clazz.getSimpleName() + " cloning is not supported." );
            }
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        cloneAnnotatedObjectCommon( interactor, clone );
        cloneIntactObjectCommon( interactor, clone );

        return clone;
    }

    public BioSource cloneBioSource( BioSource bioSource ) throws IntactClonerException {
        if ( bioSource == null ) return null;

        BioSource clone = new BioSource( cloneInstitution( bioSource.getOwner() ),
                                         bioSource.getShortLabel(),
                                         bioSource.getTaxId() );

        clone.setCvCellType( ( CvCellType ) cloneCvObject( bioSource.getCvCellType() ) );
        clone.setCvTissue( ( CvTissue ) cloneCvObject( bioSource.getCvTissue() ) );

        cloneAnnotatedObjectCommon( bioSource, clone );
        cloneIntactObjectCommon( bioSource, clone );

        return bioSource;
    }

    public Publication clonePublication( Publication publication ) throws IntactClonerException {
        if ( publication == null ) return null;

        Publication clone = new Publication( cloneInstitution( publication.getOwner() ),
                                             publication.getShortLabel() );
        for ( Experiment e : publication.getExperiments() ) {
            clone.addExperiment( cloneExperiment( e ) );
        }

        cloneAnnotatedObjectCommon( publication, clone );
        cloneIntactObjectCommon( publication, clone );

        return clone;
    }

    public Component cloneComponent( Component component, Interaction interaction ) throws IntactClonerException {
        if ( component == null ) return null;
        Component clone = new Component( cloneInstitution( component.getOwner() ),
                                         interaction,
                                         cloneInteractor( component.getInteractor() ),
                                         ( CvExperimentalRole ) cloneCvObject( component.getCvExperimentalRole() ),
                                         ( CvBiologicalRole ) cloneCvObject( component.getCvBiologicalRole() ) );

        clone.setStoichiometry( component.getStoichiometry() );
        clone.setExpressedIn( cloneBioSource( component.getExpressedIn() ) );

        for ( Feature feature : component.getBindingDomains() ) {
            clone.addBindingDomain( cloneFeature( feature, clone ) );
        }

        for ( CvExperimentalPreparation preparation : component.getExperimentalPreparations() ) {
            clone.getExperimentalPreparations().add( ( CvExperimentalPreparation ) cloneCvObject( preparation ) );
        }

        for ( CvIdentification method : component.getParticipantDetectionMethods() ) {
            clone.getParticipantDetectionMethods().add( ( CvIdentification ) cloneCvObject( method ) );
        }

        cloneAnnotatedObjectCommon( component, clone );
        cloneIntactObjectCommon( component, clone );

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
            cloneAnnotatedObjectCommon( cvObject, clone );
            cloneIntactObjectCommon( cvObject, clone );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected AnnotatedObject cloneAnnotatedObjectCommon( AnnotatedObject ao, AnnotatedObject clone ) throws IntactClonerException {

        clone.setFullName( ao.getFullName() );
        
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

    protected IntactObject cloneIntactObjectCommon( IntactObject ao, IntactObject clone ) throws IntactClonerException {
        clone.setAc( ao.getAc() );
        clone.setCreated( new Date( ao.getCreated().getTime() ) );
        clone.setUpdated( new Date( ao.getUpdated().getTime() ) );
        clone.setCreator( ao.getCreator() );
        clone.setUpdator( ao.getUpdator() );
        return clone;
    }
}