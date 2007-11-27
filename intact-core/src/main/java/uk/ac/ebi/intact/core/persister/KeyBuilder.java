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
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Builds string that allow to identify an intact object.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda
 * @version $Id$
 * @since 1.8.0
 */
class KeyBuilder {

    public Key keyFor( AnnotatedObject ao ) {
        Key key = null;
        if ( ao instanceof Institution ) {
            key = keyFor( ao );
        } else if ( ao instanceof Institution ) {
            key = keyFor( ao );
        } else if ( ao instanceof Publication ) {
            key = keyFor( ao );
        } else if ( ao instanceof Experiment ) {
            key = keyFor( ao );
        } else if ( ao instanceof Interactor ) {
            key = keyFor( ao );
        } else if ( ao instanceof Component ) {
            key = keyFor( ao );
        } else if ( ao instanceof Feature ) {
            key = keyFor( ao );
        } else if ( ao instanceof Range ) {
            key = keyFor( ao );
        } else {
            throw new UnsupportedOperationException( "KeyBuilder doesn't build key for: " + ao.getClass().getName() );
        }
        return key;
    }

    public Key keyFor( Institution institution ) {
        final Collection<InstitutionXref> institutionXrefs = XrefUtils.getIdentityXrefs( institution );

        Key key;

        if ( institutionXrefs.isEmpty() ) {
            key = keyForAnnotatedObject( institution );
        } else {
            key = new Key( "Institution:" + concatPrimaryIds( institutionXrefs ) );
        }

        return key;
    }

    public Key keyFor( Publication publication ) {
        return keyForAnnotatedObject( publication );
    }

    public Key keyFor( Experiment experiment ) {
        return keyForAnnotatedObject( experiment );
    }

    public Key keyFor( Interaction interaction ) {
        return new Key( new CrcCalculator().crc64( interaction ) );
    }

    public Key keyFor( Interactor interactor ) {
        final Collection<InteractorXref> interactorXrefs = XrefUtils.getIdentityXrefs( interactor );

        Key key;

        if ( interactorXrefs.isEmpty() ) {
            key = keyForAnnotatedObject( interactor );
        } else {
            Class normalizedClass = CgLibUtil.removeCglibEnhanced( interactor.getClass() );
            key = new Key( normalizedClass.getSimpleName() + ":" + concatPrimaryIds( interactorXrefs ) );
        }

        return key;
    }

    public Key keyFor( Component component ) {
        if ( component.getInteraction() == null ) {
            throw new IllegalArgumentException( "Cannot create a component key for component without interaction: " + component );
        }
        if ( component.getInteractor() == null ) {
            throw new IllegalArgumentException( "Cannot create a component key for component without interactor: " + component );
        }

        String label = "Component:" + component.getInteraction().getShortLabel() + "_" + component.getInteractor().getShortLabel();
        return new Key( label );
    }

    public Key keyFor( Feature feature ) {
        if ( feature.getComponent() == null ) {
            throw new IllegalArgumentException( "Cannot create a feature key for feature without component: " + feature );
        }

        Key componentKey = keyFor( feature.getComponent() );

        return new Key( keyForAnnotatedObject( feature ).getUniqueString() + "___" + componentKey.getUniqueString() );
    }

    public Key keyFor( Range range ) {
        Key featureKey = keyFor( range.getFeature() );

        Key fromFuzzyKey = keyFor( range.getFromCvFuzzyType() );
        Key toFuzzyKey = keyFor( range.getToCvFuzzyType() );

        String uniqueString = "Range:" + range.getFromIntervalStart() + "_" + range.getFromIntervalEnd() + "-" +
                              range.getToIntervalStart() + "_" + range.getToIntervalEnd() + "___" +
                              fromFuzzyKey.getUniqueString() + "___" + toFuzzyKey.getUniqueString() + "___" +
                              featureKey.getUniqueString();

        return new Key( uniqueString );
    }

    public Key keyFor( CvObject cvObject ) {
        return new Key( cvObject.getMiIdentifier() );
    }

    protected Key keyForAnnotatedObject( AnnotatedObject annotatedObject ) {
        Class normalizedClass = CgLibUtil.removeCglibEnhanced( annotatedObject.getClass() );
        return new Key( normalizedClass.getSimpleName() + ":" + annotatedObject.getShortLabel() );
    }

    protected String concatPrimaryIds( Collection<? extends Xref> xrefs ) {
        List<String> primaryIds = new ArrayList<String>( xrefs.size() );

        for ( Xref xref : xrefs ) {
            primaryIds.add( xref.getPrimaryId() );
        }

        Collections.sort( primaryIds );

        StringBuilder sb = new StringBuilder();

        for ( String primaryId : primaryIds ) {
            sb.append( primaryId ).append( "___" );
        }

        return sb.toString();
    }
}
