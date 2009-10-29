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

import org.apache.commons.collections.map.LRUMap;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.XrefUtils;

import java.util.*;


/**
 * Builds string that allow to identify an intact object.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda
 * @version $Id$
 * @since 1.8.0
 */
public class KeyBuilder {

    private Map<String,Key> keyCache = new LRUMap(10000);

    public Key keyFor( AnnotatedObject ao ) {
        Key key;

        String cacheKey = ao.getClass().getSimpleName()+":"+System.identityHashCode(ao);

        if (ao.getAc() != null) {
            key = new Key(ao.getAc());
        } else if (keyCache.containsKey(cacheKey)) {
            return keyCache.get(cacheKey);
        } else if ( ao instanceof Institution ) {
            key = keyForInstitution( ( Institution ) ao );
        } else if ( ao instanceof Publication ) {
            key = keyForPublication( ( Publication ) ao );
        } else if ( ao instanceof CvObject ) {
            key = keyForCvObject( ( CvObject ) ao );
        } else if ( ao instanceof Experiment ) {
            key = keyForExperiment( ( Experiment ) ao );
        } else if ( ao instanceof Interaction ) {
            key = keyForInteraction( ( Interaction ) ao );
        } else if ( ao instanceof Interactor ) {
            key = keyForInteractor( ( Interactor ) ao );
        } else if ( ao instanceof BioSource ) {
            key = keyForBioSource( ( BioSource ) ao );
        } else if ( ao instanceof Component ) {
            key = keyForComponent( ( Component ) ao );
        } else if ( ao instanceof Feature ) {
            key = keyForFeature( ( Feature ) ao );
        } else {
            throw new IllegalArgumentException( "KeyBuilder doesn't build key for: " + ao.getClass().getName() );
        }

        keyCache.put(cacheKey, key);

        return key;
    }

    protected Key keyForInstitution( Institution institution ) {
        final Collection<InstitutionXref> institutionXrefs = XrefUtils.getIdentityXrefs( institution );

        Key key;

        if ( institutionXrefs.isEmpty() ) {
            key = keyForAnnotatedObject( institution );
        } else {
            key = new Key( "Institution:" + concatPrimaryIds( institutionXrefs ) );
        }

        return key;
    }

    protected Key keyForPublication( Publication publication ) {
        return keyForAnnotatedObject( publication );
    }

    protected Key keyForExperiment( Experiment experiment ) {

        return new Key(new ExperimentKeyCalculator().calculateExperimentKey(experiment));
    }

    protected Key keyForInteraction( Interaction interaction ) {
        final Key key = new Key(new CrcCalculator().crc64(interaction));

        // pre-calculate the keys for the components here and put them in a map
        int n = 0;

        for (Component component : interaction.getComponents()) {
            Key compKey = new Key(key.getUniqueString()+":"+component.getShortLabel()+"["+n+"]");
            keyCache.put(Component.class.getSimpleName()+":"+System.identityHashCode(component), compKey);
            n++;
        }

        return key;
    }

    protected Key keyForInteractor( Interactor interactor ) {
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

    protected Key keyForBioSource( BioSource bioSource ) {
        return new Key( "BioSource:" + bioSource.getTaxId() );
    }

    protected Key keyForComponent( Component component ) {
        final String cacheKey = Component.class.getSimpleName()+":"+System.identityHashCode(component);

        if (!keyCache.containsKey(cacheKey)) {
            keyForInteraction(component.getInteraction());
        }

        if (keyCache.containsKey(cacheKey)) {
            return keyCache.get(cacheKey);
        }

        throw new IllegalStateException("This component should already have already a key, generated when the interaction key is generated: "+component);
    }

    protected Key keyForFeature( Feature feature ) {


        if ( feature.getComponent() == null ) {
            throw new IllegalArgumentException( "Cannot create a feature key for feature without component: " + feature );
        }

        Key componentKey = keyFor( feature.getComponent() );
        return new Key( new FeatureKeyCalculator().calculateFeatureKey(feature) + "___" + componentKey.getUniqueString() );

//        return new Key( keyForAnnotatedObject( feature ).getUniqueString() + "___" + componentKey.getUniqueString() );
    }

    protected Key keyForCvObject( CvObject cvObject ) {
        String key = cvObject.getIdentifier();
        if ( key == null ) {
            // search for identity
            final Collection<CvObjectXref> xrefs = XrefUtils.getIdentityXrefs( cvObject );
            if ( !xrefs.isEmpty() ) {
                    key = concatPrimaryIds( xrefs );
            } else {
                key = cvObject.getShortLabel();
            }
        }

        key = cvObject.getClass().getSimpleName()+"__"+key;

        return new Key( key );
    }

    public Key keyForXref(Xref xref) {
        return new Key(keyFor(xref.getParent()).getUniqueString()+"::"+xref.getPrimaryId());
    }

     public Key keyForAlias(Alias alias) {
        return new Key(keyFor(alias.getParent()).getUniqueString()+"::"+alias.getName());
    }

    public Key keyForAnnotation(Annotation annotation, AnnotatedObject parent) {
        return new Key(keyFor(parent).getUniqueString()+"::"+annotation.getCvTopic()+"_"+annotation.getAnnotationText());
    }

    protected Key keyForAnnotatedObject( AnnotatedObject annotatedObject ) {
        Class normalizedClass = CgLibUtil.removeCglibEnhanced( annotatedObject.getClass() );
        return new Key( normalizedClass.getSimpleName() + ":" + annotatedObject.getShortLabel() );
    }

    protected String concatPrimaryIds( Collection<? extends Xref> xrefs ) {
        if ( xrefs.isEmpty() ) {
            throw new IllegalArgumentException( "Expecting a non empty collection of Xrefs" );
        }

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

    private class ExperimentKeyCalculator extends CrcCalculator {
        public String calculateExperimentKey(Experiment exp) {
            return super.createUniquenessString(exp).toString();
        }
    }

    private class FeatureKeyCalculator extends CrcCalculator {
        public String calculateFeatureKey(Feature feature) {
            return super.createUniquenessString(feature).toString();
        }
    }
}
