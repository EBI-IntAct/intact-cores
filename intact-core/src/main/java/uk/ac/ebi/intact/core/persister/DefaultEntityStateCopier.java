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

import uk.ac.ebi.intact.model.*;

/**
 * Default implementation of the entity state copier.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultEntityStateCopier implements EntityStateCopier {

    public void copy( AnnotatedObject source, AnnotatedObject target ) {

        if ( source == null ) {
            throw new IllegalArgumentException( "You must give a non null source" );
        }

        if ( target == null ) {
            throw new IllegalArgumentException( "You must give a non null target" );
        }

        if ( !target.getClass().equals( source.getClass() ) ) {
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
    }

    protected void copyInstitution( Institution from, Institution to ) {

    }

    protected void copyPublication( Publication from, Publication to ) {

    }

    protected void copyExperiment( Experiment from, Experiment to ) {

    }

    protected void copyInteraction( Interaction from, Interaction to ) {

    }

    protected void copyInteractor( Interactor from, Interactor to ) {

    }

    protected void copyComponent( Component from, Component to ) {

    }

    protected void copyFeature( Feature from, Feature to ) {

    }

    protected void copyBioSource( BioSource from, BioSource to ) {

    }

    protected void copyCvObject( CvObject from, CvObject to ) {

    }
}
