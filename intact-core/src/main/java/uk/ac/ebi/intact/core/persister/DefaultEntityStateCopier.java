/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * Default implementation of the entity state copier.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultEntityStateCopier implements EntityStateCopier {

    private static final Log log = LogFactory.getLog( DefaultEntityStateCopier.class );

    public void copy( AnnotatedObject source, AnnotatedObject target ) {

        if ( source == null ) {
            throw new IllegalArgumentException( "You must give a non null source" );
        }

        if ( target == null ) {
            throw new IllegalArgumentException( "You must give a non null target" );
        }

        // here we use assigneable as hibernate is using CgLib proxies.
        if ( !( target.getClass().isAssignableFrom( source.getClass() ) ||
                source.getClass().isAssignableFrom( target.getClass() ) ) ) {

            throw new IllegalArgumentException( "You can only copy object of the same type [" +
                                                source.getClass().getSimpleName() + " -> " +
                                                target.getClass().getSimpleName() + "]" );
        }

        if ( source instanceof Institution ) {
            copyInstitution( ( Institution ) source, ( Institution ) target );
        } else if ( source instanceof Publication ) {
            copyPublication( ( Publication ) source, ( Publication ) target );
        } else if ( source instanceof CvObject ) {
            copyCvObject( ( CvObject ) source, ( CvObject ) target );
        } else if ( source instanceof Experiment ) {
            copyExperiment( ( Experiment ) source, ( Experiment ) target );
        } else if ( source instanceof Interaction ) {
            copyInteraction( ( Interaction ) source, ( Interaction ) target );
        } else if ( source instanceof Interactor ) {
            copyInteractor( ( Interactor ) source, ( Interactor ) target );
        } else if ( source instanceof BioSource ) {
            copyBioSource( ( BioSource ) source, ( BioSource ) target );
        } else if ( source instanceof Component ) {
            copyComponent( ( Component ) source, ( Component ) target );
        } else if ( source instanceof Feature ) {
            copyFeature( ( Feature ) source, ( Feature ) target );
        } else {
            throw new IllegalArgumentException( "DefaultEntityStateCopier doesn't copy " + source.getClass().getName() );
        }

        copyAnotatedObjectCommons( source, target );
    }

    protected void copyInstitution( Institution source, Institution target ) {
        target.setUrl( source.getUrl() );
        target.setPostalAddress( source.getPostalAddress() );
    }

    protected void copyPublication( Publication source, Publication target ) {
        copyCollection( source.getExperiments(), target.getExperiments() );
    }

    protected void copyExperiment( Experiment source, Experiment target ) {
        target.setBioSource( source.getBioSource() );
        target.setPublication( source.getPublication() );
        target.setCvIdentification( source.getCvIdentification() );
        target.setCvInteraction( source.getCvInteraction() );

        copyCollection( source.getInteractions(), target.getInteractions() );
    }

    protected void copyInteraction( Interaction source, Interaction target ) {
        target.setKD( target.getKD() );

        target.setCvInteractionType( source.getCvInteractionType() );

        copyCollection( source.getComponents(), target.getComponents() );
        copyCollection( source.getExperiments(), target.getExperiments() );

        copyInteractorCommons( source, target );

        // we have ommited CRC on purpose
    }

    protected void copyInteractor( Interactor source, Interactor target ) {
        copyCollection( source.getActiveInstances(), target.getActiveInstances() );

        copyInteractorCommons( source, target );
    }

    protected void copyInteractorCommons( Interactor source, Interactor target ) {

        if ( target.getBioSource() != null && source.getBioSource() != null &&
             !source.getBioSource().getTaxId().equals(target.getBioSource().getTaxId()) &&
             !source.getBioSource().getShortLabel().equals(target.getBioSource().getShortLabel())   ) {

            throw new PersisterException( "Operation not permitted: updating biosource of a " +
                                          target.getClass().getSimpleName() + " (" + target.getShortLabel() + ") " +
                                          " from " + target.getBioSource().getShortLabel() +
                                          " to " + source.getBioSource().getShortLabel() );

        } else if ( target.getBioSource() != null && source.getBioSource() == null ) {

            throw new PersisterException( "Operation not permitted: nullifying biosource of a " +
                                          target.getClass().getSimpleName() + " (" + target.getShortLabel() + ") - " +
                                          " current biosource is  " + target.getBioSource().getShortLabel() );
        }

        target.setBioSource( source.getBioSource() );
        target.setCvInteractorType( source.getCvInteractorType() );
    }

    protected void copyComponent( Component source, Component target ) {
        target.setStoichiometry( source.getStoichiometry() );

        target.setInteraction( source.getInteraction() );
        target.setInteractor( source.getInteractor() );
        target.setCvBiologicalRole( source.getCvBiologicalRole() );
        target.setCvExperimentalRole( source.getCvExperimentalRole() );
        target.setExpressedIn( source.getExpressedIn() );

        copyCollection( source.getBindingDomains(), target.getBindingDomains() );
        copyCollection( source.getExperimentalPreparations(), target.getExperimentalPreparations() );
        copyCollection( source.getParticipantDetectionMethods(), target.getParticipantDetectionMethods() );
    }

    protected void copyFeature( Feature source, Feature target ) {
        target.setComponent( source.getComponent() );
        target.setCvFeatureIdentification( source.getCvFeatureIdentification() );
        target.setCvFeatureType( source.getCvFeatureType() );
    }

    protected void copyBioSource( BioSource source, BioSource target ) {
        target.setTaxId( source.getTaxId() );
        target.setCvTissue( source.getCvTissue() );
        target.setCvCellType( source.getCvCellType() );
    }

    protected void copyCvObject( CvObject source, CvObject target ) {
        target.setMiIdentifier( source.getMiIdentifier() );
    }

    protected <X extends Xref> void copyAnotatedObjectCommons( AnnotatedObject<X, ?> source, AnnotatedObject<X, ?> target ) {
        target.setShortLabel( source.getShortLabel() );
        target.setFullName( source.getFullName() );

        copyCollection( source.getXrefs(), target.getXrefs() );
        copyCollection( source.getAliases(), target.getAliases() );
        copyCollection( source.getAnnotations(), target.getAnnotations() );
    }

    protected void copyCollection( Collection sourceCol, Collection targetCol ) {
        Collection elementsToAdd = CollectionUtils.subtract( sourceCol, targetCol );
        Collection elementsToRemove = CollectionUtils.subtract( sourceCol, targetCol );
        targetCol.removeAll( elementsToRemove );
        targetCol.addAll( elementsToAdd );
    }
}
