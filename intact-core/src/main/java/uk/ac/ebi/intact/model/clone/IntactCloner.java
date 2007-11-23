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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log log = LogFactory.getLog( IntactCloner.class );

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

        public void addClone( IntactObject io, IntactObject clone ) {
            register.put( io, clone );
        }
    }

    private ClonerManager clonerManager;

    public IntactCloner() {
        clonerManager = new ClonerManager();
    }

    public IntactObject clone( IntactObject intactObject ) throws IntactClonerException {

        if ( intactObject == null ) return null;

        System.out.println( "Cloning " + intactObject.getClass().getSimpleName() + ": " +
                            ( intactObject instanceof AnnotatedObject ?
                              ( ( AnnotatedObject ) intactObject ).getShortLabel() : intactObject.getAc() ) );

        IntactObject clone = null;

        if ( clonerManager.isAlreadyCloned( intactObject ) ) {
            System.out.println( "(!) we already have it in cache" );
            return clonerManager.getClonedObject( intactObject );
        }

        if ( intactObject instanceof AnnotatedObject ) {
            clone = cloneAnnotatedObject( ( AnnotatedObject ) intactObject );
        } else if ( intactObject instanceof Annotation ) {
            clone = cloneAnnotation( ( Annotation ) intactObject );
        } else if ( intactObject instanceof Alias ) {
            clone = cloneAlias( ( Alias ) intactObject );
        } else if ( intactObject instanceof Xref ) {
            clone = cloneXref( ( Xref ) intactObject );
        } else if ( intactObject instanceof Range ) {
            clone = cloneRange( ( Range ) intactObject );
        } else {
            throw new IllegalArgumentException( "Cannot clone objects of type: " + intactObject.getClass().getName() );
        }

        cloneIntactObjectCommon( intactObject, clone );

        return clone;
    }

    protected AnnotatedObject cloneAnnotatedObject( AnnotatedObject annotatedObject ) throws IntactClonerException {

        if ( annotatedObject == null ) return null;

        AnnotatedObject clone = null;
        if ( annotatedObject instanceof Interaction ) {
            clone = cloneInteraction( ( Interaction ) annotatedObject );
        } else if ( annotatedObject instanceof Interactor ) {
            clone = cloneInteractor( ( Interactor ) annotatedObject );
        } else if ( annotatedObject instanceof CvObject ) {
            clone = cloneCvObject( ( CvObject ) annotatedObject );
        } else if ( annotatedObject instanceof Experiment ) {
            clone = cloneExperiment( ( Experiment ) annotatedObject );
        } else if ( annotatedObject instanceof Component ) {
            clone = cloneComponent( ( Component ) annotatedObject );
        } else if ( annotatedObject instanceof BioSource ) {
            clone = cloneBioSource( ( BioSource ) annotatedObject );
        } else if ( annotatedObject instanceof Feature ) {
            clone = cloneFeature( ( Feature ) annotatedObject );
        } else if ( annotatedObject instanceof Publication ) {
            clone = clonePublication( ( Publication ) annotatedObject );
        } else if ( annotatedObject instanceof Institution ) {
            clone = cloneInstitution( ( Institution ) annotatedObject );
        } else {
            throw new IllegalArgumentException( "Cannot process annotated object of type: " + annotatedObject.getClass().getName() );
        }

        cloneAnnotatedObjectCommon( annotatedObject, clone );

        return clone;
    }

    ///////////////////////////////////////
    // IntactObject cloners

    protected Annotation cloneAnnotation( Annotation annotation ) throws IntactClonerException {
        if ( annotation == null ) return null;
        Annotation clone = new Annotation();

        clonerManager.addClone( annotation, clone );

        clone.setCvTopic( ( CvTopic ) clone( annotation.getCvTopic() ) );
        clone.setAnnotationText( annotation.getAnnotationText() );
        return annotation;
    }

    protected Alias cloneAlias( Alias alias ) throws IntactClonerException {
        if ( alias == null ) return null;

        Class clazz = alias.getClass();
        Alias clone = null;
        try {
            final Constructor constructor = clazz.getConstructor();
            clone = ( Alias ) constructor.newInstance();

            clonerManager.addClone( alias, clone );

            clone.setCvAliasType( ( CvAliasType ) clone( alias.getCvAliasType() ) );
            clone.setName( alias.getName() );
        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }
        return clone;
    }

    /**
     * Note: this does not clone the parent.
     *
     * @param xref
     * @return
     * @throws IntactClonerException
     */
    protected Xref cloneXref( Xref xref ) throws IntactClonerException {
        if ( xref == null ) return null;

        Class clazz = xref.getClass();
        Xref clone = null;

        try {
            final Constructor constructor = clazz.getConstructor();
            clone = ( Xref ) constructor.newInstance();

            clonerManager.addClone( xref, clone );

            clone.setPrimaryId( xref.getPrimaryId() );
            clone.setSecondaryId( xref.getSecondaryId() );
            clone.setDbRelease( xref.getDbRelease() );
            clone.setCvDatabase( ( CvDatabase ) clone( xref.getCvDatabase() ) );
            clone.setCvXrefQualifier( ( CvXrefQualifier ) clone( xref.getCvXrefQualifier() ) );

        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon building a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected Range cloneRange( Range range ) throws IntactClonerException {
        if ( range == null ) {
            throw new IllegalArgumentException( "You must give a non null range" );
        }

        Range clone = new Range();

        clonerManager.addClone( range, clone );

        clone.setFromIntervalStart( range.getFromIntervalStart() );
        clone.setFromIntervalEnd( range.getFromIntervalEnd() );
        clone.setToIntervalStart( range.getToIntervalStart() );
        clone.setToIntervalEnd( range.getToIntervalEnd() );
        clone.setSequence( range.getSequence() );

        clone.setFromCvFuzzyType( ( CvFuzzyType ) clone( range.getFromCvFuzzyType() ) );
        clone.setToCvFuzzyType( ( CvFuzzyType ) clone( range.getToCvFuzzyType() ) );

        return clone;
    }

    ///////////////////////////////////////
    // AnnotatedObject cloners

    public Experiment cloneExperiment( Experiment experiment ) throws IntactClonerException {
        if ( experiment == null ) return null;
        Experiment clone = new Experiment();

        clonerManager.addClone( experiment, clone );

        clone.setCvIdentification( ( CvIdentification ) clone( experiment.getCvIdentification() ) );
        clone.setCvInteraction( ( CvInteraction ) clone( experiment.getCvInteraction() ) );
        System.out.println( "Cloning Biosource..." );
        clone.setBioSource( ( BioSource ) clone( experiment.getBioSource() ) );
        clone.setPublication( ( Publication ) clone( experiment.getPublication() ) );

        for ( Interaction i : experiment.getInteractions() ) {
            clone.addInteraction( ( Interaction ) clone( i ) );
        }

        return clone;
    }

    public Feature cloneFeature( Feature feature ) throws IntactClonerException {
        if ( feature == null ) return null;
        Feature clone = new Feature();

        clonerManager.addClone( feature, clone );

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

        if( clonerManager.isAlreadyCloned( institution ) ) {
            return (Institution ) clonerManager.getClonedObject( institution );
        }

        Institution clone = new Institution();
        
        clonerManager.addClone( institution, clone );

        clone.setUrl( institution.getUrl() );
        clone.setPostalAddress( institution.getPostalAddress() );

        return clone;
    }

    public Interaction cloneInteraction( Interaction interaction ) throws IntactClonerException {
        if ( interaction == null ) return null;
        Interaction clone = new InteractionImpl();

        clonerManager.addClone( interaction, clone );

        for ( Component component : interaction.getComponents() ) {
            clone.addComponent( ( Component ) clone( component ) );
        }

        clone.setCvInteractionType( ( CvInteractionType ) clone( interaction.getCvInteractionType() ) );
        clone.setCvInteractorType( ( CvInteractorType ) clone( interaction.getCvInteractorType() ) );

        for ( Experiment experiment : interaction.getExperiments() ) {
            System.out.println( "Adding cloned experiment" );
            clone.addExperiment( ( Experiment ) clone( experiment ) );
        }

        clone.setKD( interaction.getKD() );
        clone.setCrc( interaction.getCrc() );

        return clone;
    }

    public Interactor cloneInteractor( Interactor interactor ) throws IntactClonerException {
        if ( interactor == null ) return null;

        Interactor clone = null;

        final Class clazz = interactor.getClass();

        try {
            final Constructor constructor = clazz.getConstructor();
            clone = ( Interactor ) constructor.newInstance();

            clonerManager.addClone( interactor, clone );

        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        if ( interactor instanceof Polymer ) {
            Polymer p = ( Polymer ) clone;
            p.setSequence( ( ( Polymer ) interactor ).getSequence() );
            p.setCrc64( ( ( Polymer ) interactor ).getCrc64() );
        }

        clone.setBioSource( ( BioSource ) clone( interactor.getBioSource() ) );
        clone.setCvInteractorType( ( CvInteractorType ) clone( interactor.getCvInteractorType() ) );

        return clone;
    }

    public BioSource cloneBioSource( BioSource bioSource ) throws IntactClonerException {
        if ( bioSource == null ) return null;

        BioSource clone = new BioSource();

        clonerManager.addClone( bioSource, clone );

        clone.setTaxId( bioSource.getTaxId() );
        clone.setCvCellType( ( CvCellType ) clone( bioSource.getCvCellType() ) );
        clone.setCvTissue( ( CvTissue ) clone( bioSource.getCvTissue() ) );

        return clone;
    }

    public Publication clonePublication( Publication publication ) throws IntactClonerException {
        if ( publication == null ) return null;

        Publication clone = new Publication();

        clonerManager.addClone( publication, clone );

        for ( Experiment e : publication.getExperiments() ) {
            clone.addExperiment( cloneExperiment( e ) );
        }

        return clone;
    }

    public Component cloneComponent( Component component ) throws IntactClonerException {
        if ( component == null ) return null;

        Component clone = new Component();

        clonerManager.addClone( component, clone );

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
            final Constructor constructor = clazz.getConstructor();
            clone = ( CvObject ) constructor.newInstance();

            clonerManager.addClone( cvObject, clone );

        } catch ( Exception e ) {
            throw new IntactClonerException( "An error occured upon cloning a " + clazz.getSimpleName(), e );
        }

        return clone;
    }

    protected AnnotatedObject cloneAnnotatedObjectCommon( AnnotatedObject ao, AnnotatedObject clone ) throws IntactClonerException {

        if( ao == clone ) {
            throw new IllegalStateException( ao.getClass().getSimpleName() + " are the same instance!!" );
        }

        if ( !( ao instanceof Institution ) ) {
            clone.setOwner( ( Institution ) clone( ao.getOwner() ) );
        }

        clone.setShortLabel( ao.getShortLabel() );
        clone.setFullName( ao.getFullName() );

        for ( Annotation annotation : ao.getAnnotations() ) {
            clone.addAnnotation( ( Annotation ) clone( annotation ) );
        }
        for ( Alias alias : ( Collection<Alias> ) ao.getAliases() ) {
            System.out.println( "Cloning Alias" );
            clone.addAlias( ( Alias ) clone( alias ) );
        }
        for ( Xref xref : ( Collection<Xref> ) ao.getXrefs() ) {
            System.out.println( "Cloning Xref" );
            clone.addXref( ( Xref ) clone( xref ) );
        }
        return clone;
    }

    protected IntactObject cloneIntactObjectCommon( IntactObject ao, IntactObject clone ) throws IntactClonerException {
        clone.setAc( ao.getAc() );
        if ( ao.getCreated() != null ) {
            clone.setCreated( new Date( ao.getCreated().getTime() ) );
        }
        if ( ao.getUpdated() != null ) {
            clone.setUpdated( new Date( ao.getUpdated().getTime() ) );
        }
        clone.setCreator( ao.getCreator() );
        clone.setUpdator( ao.getUpdator() );


        if ( !( ao instanceof Institution ) ) {
            if( ao instanceof BasicObject ) {
                final BasicObject boc = ( BasicObject ) clone;
                final BasicObject bo = ( BasicObject ) ao;
                boc.setOwner( ( Institution ) clone( bo.getOwner() ));
            }
        }

        return clone;
    }
}