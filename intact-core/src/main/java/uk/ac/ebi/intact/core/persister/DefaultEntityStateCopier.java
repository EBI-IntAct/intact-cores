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
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyPublication( Publication source, Publication target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyExperiment( Experiment source, Experiment target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyInteraction( Interaction source, Interaction target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyInteractor( Interactor source, Interactor target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyComponent( Component source, Component target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyFeature( Feature source, Feature target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyBioSource( BioSource source, BioSource target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected void copyCvObject( CvObject source, CvObject target ) {
        log.warn( "Method to be implemented: " + source.getClass() );
    }

    protected <X extends Xref> void copyAnotatedObjectCommons( AnnotatedObject<X, ?> source, AnnotatedObject<X, ?> target ) {
        target.setShortLabel( source.getShortLabel() );
        target.setFullName( source.getFullName() );

        Collection xrefToAdd = CollectionUtils.subtract( source.getXrefs(), target.getXrefs() );
        Collection xrefToRemove = CollectionUtils.subtract( target.getXrefs(), source.getXrefs() );
        target.getXrefs().removeAll( xrefToRemove );
        target.getXrefs().addAll( xrefToAdd );

        Collection aliasToAdd = CollectionUtils.subtract( source.getAliases(), target.getAliases() );
        Collection aliasToRemove = CollectionUtils.subtract( target.getAliases(), source.getAliases() );
        target.getAliases().removeAll( aliasToRemove );
        target.getAliases().addAll( aliasToAdd );

        Collection annotToAdd = CollectionUtils.subtract( source.getAnnotations(), target.getAnnotations() );
        Collection annotToRemove = CollectionUtils.subtract( target.getAnnotations(), source.getAnnotations() );
        target.getAnnotations().removeAll( annotToRemove );
        target.getAnnotations().addAll( annotToAdd );
    }
}
