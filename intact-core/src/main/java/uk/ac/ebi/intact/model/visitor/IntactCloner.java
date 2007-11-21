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
package uk.ac.ebi.intact.model.visitor;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCloner {

    public IntactObject clone( IntactObject intactObject ) {
        if ( intactObject instanceof AnnotatedObject ) {
            cloneAnnotatedObject( ( AnnotatedObject ) intactObject );
        } else if ( intactObject instanceof Annotation ) {
            cloneAnnotation( ( Annotation ) intactObject );
        } else if ( intactObject instanceof Alias ) {
            cloneAlias( ( Alias ) intactObject );
        } else if ( intactObject instanceof Xref ) {
            cloneXref( ( Xref ) intactObject );
        } else {
            throw new IllegalArgumentException( "Cannot clone objects of type: " + intactObject.getClass().getName() );
        }

        return null; // TODO update this
    }

    protected void cloneAnnotatedObject( AnnotatedObject annotatedObject ) {
        if ( annotatedObject instanceof Interaction ) {
            cloneInteraction( ( Interaction ) annotatedObject );
        } else if ( annotatedObject instanceof Interactor ) {
            cloneInteractor( ( Interactor ) annotatedObject );
        } else if ( annotatedObject instanceof CvObject ) {
            cloneCvObject( ( CvObject ) annotatedObject );
        } else if ( annotatedObject instanceof Experiment ) {
            cloneExperiment( ( Experiment ) annotatedObject );
        } else if ( annotatedObject instanceof Component ) {
            cloneComponent( ( Component ) annotatedObject );
        } else if ( annotatedObject instanceof BioSource ) {
            cloneBioSource( ( BioSource ) annotatedObject );
        } else if ( annotatedObject instanceof Feature ) {
            cloneFeature( ( Feature ) annotatedObject );
        } else if ( annotatedObject instanceof Publication ) {
            clonePublication( ( Publication ) annotatedObject );
        } else if ( annotatedObject instanceof Institution ) {
            cloneInstitution( ( Institution ) annotatedObject );
        } else {
            throw new IllegalArgumentException( "Cannot process annotated object of type: " + annotatedObject.getClass().getName() );
        }
    }

    ///////////////////////////////////////
    // IntactObject cloners

    protected void cloneAnnotation( Annotation annotation ) {
        if ( annotation == null ) return;

        cloneCvObject( annotation.getCvTopic() );
        cloneInstitution( annotation.getOwner() );
    }

    protected void cloneAlias( Alias alias ) {
        if ( alias == null ) return;

        //TODO clone

        cloneCvObject( alias.getCvAliasType() );
        cloneInstitution( alias.getOwner() );
    }

    protected void cloneXref( Xref xref ) {
        if ( xref == null ) return;

        //TODO clone


        cloneCvObject( xref.getCvXrefQualifier() );
        cloneCvObject( xref.getCvDatabase() );
        cloneInstitution( xref.getOwner() );
    }

    protected void cloneRange( Range range ) {
        if ( range == null ) return;

        //TODO clone


        cloneCvObject( range.getFromCvFuzzyType() );
        cloneCvObject( range.getToCvFuzzyType() );
    }

    ///////////////////////////////////////
    // AnnotatedObject cloners

    public void cloneExperiment( Experiment experiment ) {
        if ( experiment == null ) return;

        //TODO clone


        cloneCvObject( experiment.getCvIdentification() );
        cloneCvObject( experiment.getCvInteraction() );
        cloneBioSource( experiment.getBioSource() );
        clonePublication( experiment.getPublication() );

        //

        cloneAnnotatedObjectCommon( experiment );
    }

    public void cloneFeature( Feature feature ) {
        if ( feature == null ) return;

        //TODO clone


        cloneCvObject( feature.getCvFeatureType() );
        cloneCvObject( feature.getCvFeatureIdentification() );

        for ( Range range : feature.getRanges() ) {
            cloneRange( range );
        }

        cloneAnnotatedObjectCommon( feature );

        throw new UnsupportedOperationException();
    }

    public void cloneInstitution( Institution institution ) {
        if ( institution == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( institution );
    }

    public void cloneInteraction( Interaction interaction ) {
        if ( interaction == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( interaction );

        throw new UnsupportedOperationException();
    }

    public void cloneInteractor( Interactor interactor ) {
        if ( interactor == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( interactor );

        throw new UnsupportedOperationException();
    }

    public void cloneBioSource( BioSource bioSource ) {
        if ( bioSource == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( bioSource );

        throw new UnsupportedOperationException();
    }

    public void clonePublication( Publication publication ) {
        if ( publication == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( publication );

        throw new UnsupportedOperationException();
    }

    public void cloneComponent( Component component ) {
        if ( component == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( component );

        throw new UnsupportedOperationException();
    }

    public void cloneCvObject( CvObject cvObject ) {
        if ( cvObject == null ) return;

        //TODO clone


        cloneAnnotatedObjectCommon( cvObject );

        throw new UnsupportedOperationException();
    }

    protected void cloneAnnotatedObjectCommon( AnnotatedObject ao ) {
        for ( Annotation annotation : ao.getAnnotations() ) {
            //TODO clone
        }
        for ( Alias alias : ( Collection<Alias> ) ao.getAliases() ) {
            //TODO clone
        }
        for ( Xref xref : ( Collection<Xref> ) ao.getXrefs() ) {
            //TODO clone
        }

        //TODO clone institution
    }
}