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
import java.util.HashMap;
import java.util.Map;

/**
 * IntAct Object cloner.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCloner {

    private class ClonerManager {

        Map<IntactObject, IntactObject> register = new HashMap<IntactObject, IntactObject>();

        public ClonerManager() {
        }

        public IntactObject getClonedObject( IntactObject io ) {
            return register.get( io );
        }

        public boolean isAlreadyCloned( IntactObject io ) {
            return register.containsKey( io );
        }
    }

    private ClonerManager clonerManager;

    public IntactCloner() {
        clonerManager = new ClonerManager();
    }

    public IntactObject clone( IntactObject intactObject ) throws IntactClonerException {
        IntactObject io = null;

        if ( clonerManager.isAlreadyCloned( intactObject ) ) {
            return clonerManager.getClonedObject( intactObject );
        }

        if ( intactObject instanceof AnnotatedObject ) {
            io = cloneAnnotatedObject( ( AnnotatedObject ) intactObject );
        } else if ( intactObject instanceof Annotation ) {
            io = cloneAnnotation( ( Annotation ) intactObject );
        } else if ( intactObject instanceof Alias ) {
            io = cloneAlias( ( Alias ) intactObject );
        } else if ( intactObject instanceof Xref ) {
            io = cloneXref( ( Xref ) intactObject );
        } else if ( intactObject instanceof Range ) {
            io = cloneRange( ( Range ) intactObject );
        } else {
            throw new IllegalArgumentException( "Cannot clone objects of type: " + intactObject.getClass().getName() );
        }

        cloneIntactObjectCommon( intactObject, io );

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

        cloneAnnotatedObjectCommon( annotatedObject, ao );

        return ao;
    }

    ///////////////////////////////////////
    // IntactObject cloners

    protected Annotation cloneAnnotation( Annotation annotation ) throws IntactClonerException {
        if ( annotation == null ) return null;
        return new Annotation( ( Institution ) clone( annotation.getOwner() ),
                               ( CvTopic ) clone( annotation.getCvTopic() ),
                               annotation.getAnnotationText() );
    }

    protected Alias cloneAlias( Alias alias ) throws IntactClonerException {
        if ( alias == null ) return null;

        Class clazz = alias.getClass();
        Alias clone = null;
        try {
            final Constructor constructor = clazz.getConstructor( );
            clone = ( Alias ) constructor.newInstance( );

            clone.setCvAliasType( (CvAliasType) clone( alias.getCvAliasType() ));
            clone.setName( alias.getName() );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }
        return clone;
    }

    /**
     * Note: this does not clone the parent.
     * @param xref
     * @return
     * @throws IntactClonerException
     */
    protected Xref cloneXref( Xref xref ) throws IntactClonerException {
        if ( xref == null ) return null;

        Class clazz = xref.getClass();
        Xref clone = null;

        try {
            final Constructor constructor = clazz.getConstructor( Institution.class,
                                                                  CvDatabase.class,
                                                                  String.class,
                                                                  String.class,
                                                                  String.class,
                                                                  CvXrefQualifier.class );
            clone = ( Xref ) constructor.newInstance( clone( xref.getOwner() ),
                                                           ( CvDatabase ) clone( xref.getCvDatabase() ),
                                                           xref.getPrimaryId(),
                                                           xref.getSecondaryId(),
                                                           xref.getDbRelease(),
                                                           ( CvXrefQualifier ) clone( xref.getCvXrefQualifier() ) );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected Range cloneRange( Range range ) throws IntactClonerException {
        if ( range == null ) {
            throw new IllegalArgumentException( "You must give a non null range" );
        }

        Range clonedRange = new Range( ( Institution ) clone( range.getOwner() ),
                                       range.getFromIntervalStart(),
                                       range.getFromIntervalEnd(),
                                       range.getToIntervalStart(),
                                       range.getToIntervalEnd(),
                                       range.getSequence() );

        clonedRange.setFromCvFuzzyType( ( CvFuzzyType ) clone( range.getFromCvFuzzyType() ) );
        clonedRange.setToCvFuzzyType( ( CvFuzzyType ) clone( range.getToCvFuzzyType() ) );

        return clonedRange;
    }

    ///////////////////////////////////////
    // AnnotatedObject cloners

    public Experiment cloneExperiment( Experiment experiment ) throws IntactClonerException {
        if ( experiment == null ) return null;
        Experiment clone = new Experiment( ( Institution ) clone( experiment.getOwner() ),
                                           experiment.getShortLabel(),
                                           cloneBioSource( experiment.getBioSource() ) );

        experiment.setCvIdentification( ( CvIdentification ) clone( experiment.getCvIdentification() ) );
        experiment.setCvInteraction( ( CvInteraction ) clone( experiment.getCvInteraction() ) );
        experiment.setBioSource( ( BioSource ) clone( experiment.getBioSource() ) );
        experiment.setPublication( ( Publication ) clone( experiment.getPublication() ) );

        for ( Interaction i : experiment.getInteractions() ) {
            clone.addInteraction( ( Interaction ) clone( i ) );
        }

        return clone;
    }

    public Feature cloneFeature( Feature feature ) throws IntactClonerException {
        if ( feature == null ) return null;
        Feature clone = new Feature();

        clone.setOwner( ( Institution ) clone( feature.getOwner() ) );
        clone.setShortLabel( feature.getShortLabel() );
        clone.setCvFeatureType( ( CvFeatureType ) clone( feature.getCvFeatureType() ) );
        clone.setCvFeatureIdentification( ( CvFeatureIdentification ) clone( feature.getCvFeatureIdentification() ) );

        for ( Range range : feature.getRanges() ) {
            clone.addRange( ( Range ) clone( range ) );
        }

        return clone;
    }

    public Institution cloneInstitution( Institution institution ) throws IntactClonerException {
        if ( institution == null ) return null;

        Institution clone = new Institution( institution.getShortLabel() );

        clone.setUrl( institution.getUrl() );
        clone.setPostalAddress( institution.getPostalAddress() );

        return clone;
    }

    public Interaction cloneInteraction( Interaction interaction ) throws IntactClonerException {
        if ( interaction == null ) return null;
        Interaction clone = new InteractionImpl( );

        for ( Component component : interaction.getComponents() ) {
            clone.addComponent( ( Component ) clone( component ) );
        }

        clone.setCvInteractionType( ( CvInteractionType ) clone( interaction.getCvInteractionType() ));
        clone.setCvInteractorType( ( CvInteractorType ) clone( interaction.getCvInteractorType() ));

        for ( Experiment experiment : interaction.getExperiments() ) {
            clone.addExperiment( ( Experiment ) clone( experiment ));
        }

        clone.setKD( interaction.getKD());
        clone.setCrc( interaction.getCrc() );

        return clone;
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
                                                                clone( interactor.getOwner() ),
                                                                ( CvInteractorType ) clone( interactor.getCvInteractorType() ) );
            } else if ( interactor instanceof Polymer ) {

                constructor = clazz.getConstructor( Institution.class,
                                                    BioSource.class,
                                                    String.class,
                                                    CvInteractorType.class );
                clone = ( Interactor ) constructor.newInstance( clone( interactor.getOwner() ),
                                                                cloneBioSource( interactor.getBioSource() ),
                                                                interactor.getShortLabel(),
                                                                ( CvInteractorType ) clone( interactor.getCvInteractorType() ) );
                Polymer p = ( Polymer ) clone;
                p.setSequence( ( ( Polymer ) interactor ).getSequence() );
                p.setCrc64( ( ( Polymer ) interactor ).getCrc64() );

            } else {

                throw new IllegalArgumentException( clazz.getSimpleName() + " cloning is not supported." );
            }
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    public BioSource cloneBioSource( BioSource bioSource ) throws IntactClonerException {
        if ( bioSource == null ) return null;

        BioSource clone = new BioSource( ( Institution ) clone( bioSource.getOwner() ),
                                         bioSource.getShortLabel(),
                                         bioSource.getTaxId() );

        clone.setCvCellType( ( CvCellType ) clone( bioSource.getCvCellType() ) );
        clone.setCvTissue( ( CvTissue ) clone( bioSource.getCvTissue() ) );

        return bioSource;
    }

    public Publication clonePublication( Publication publication ) throws IntactClonerException {
        if ( publication == null ) return null;

        Publication clone = new Publication( ( Institution ) clone( publication.getOwner() ),
                                             publication.getShortLabel() );
        for ( Experiment e : publication.getExperiments() ) {
            clone.addExperiment( cloneExperiment( e ) );
        }

        return clone;
    }

    public Component cloneComponent( Component component ) throws IntactClonerException {
        if ( component == null ) return null;

        Component clone = new Component();

        clone.setInteractor( ( Interactor ) clone( component.getInteractor() ) );
        clone.setInteraction( ( Interaction ) clone( component.getInteraction() ) );
        clone.setCvExperimentalRole( ( CvExperimentalRole ) clone( component.getCvExperimentalRole() ) );
        clone.setCvBiologicalRole( ( CvBiologicalRole ) clone( component.getCvBiologicalRole() ) );

        clone.setStoichiometry( component.getStoichiometry() );
        clone.setExpressedIn( ( BioSource ) clone( component.getExpressedIn() ) );

        for ( Feature feature : component.getBindingDomains() ) {
            clone.addBindingDomain( ( Feature ) clone( feature ) );
        }

        for ( CvExperimentalPreparation preparation : component.getExperimentalPreparations() ) {
            clone.getExperimentalPreparations().add( ( CvExperimentalPreparation ) clone( preparation ) );
        }

        for ( CvIdentification method : component.getParticipantDetectionMethods() ) {
            clone.getParticipantDetectionMethods().add( ( CvIdentification ) clone( method ) );
        }

        return clone;
    }

    public CvObject cloneCvObject( CvObject cvObject ) throws IntactClonerException {
        if ( cvObject == null ) return null;

        Class clazz = cvObject.getClass();
        CvObject clone = null;
        try {
            final Constructor constructor = clazz.getConstructor( Institution.class, String.class );
            clone = ( CvObject ) constructor.newInstance( clone( cvObject.getOwner() ),
                                                          cvObject.getShortLabel() );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected AnnotatedObject cloneAnnotatedObjectCommon( AnnotatedObject ao, AnnotatedObject clone ) throws IntactClonerException {

        clone.setOwner( ( Institution ) clone( ao.getOwner() ) );

        clone.setShortLabel( ao.getShortLabel() );
        clone.setFullName( ao.getFullName() );

        for ( Annotation annotation : ao.getAnnotations() ) {
            clone.addAnnotation( ( Annotation ) clone( annotation ) );
        }
        for ( Alias alias : ( Collection<Alias> ) ao.getAliases() ) {
            clone.addAlias( ( Alias ) clone( alias ) );
        }
        for ( Xref xref : ( Collection<Xref> ) ao.getXrefs() ) {
            clone.addXref( ( Xref ) clone( xref ) );
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