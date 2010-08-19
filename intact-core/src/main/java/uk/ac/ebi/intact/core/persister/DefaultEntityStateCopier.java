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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import uk.ac.ebi.intact.model.clone.IntactClonerException;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

    private boolean copiedProperty;


    public boolean copy( AnnotatedObject source, AnnotatedObject target ) {
        copiedProperty = false;

        if ( source == null ) {
            throw new IllegalArgumentException( "You must give a non null source" );
        }

        if ( target == null ) {
            throw new IllegalArgumentException( "You must give a non null target" );
        }

        if (source.getAc() != null && target.getAc() != null && !source.getAc().equals(target.getAc())) {
            throw new IllegalArgumentException("Source and target do not have the same AC, so they cannot be copied");
        }

        // here we use assigneable as hibernate is using CgLib proxies.
        if ( !( target.getClass().isAssignableFrom( source.getClass() ) ||
                source.getClass().isAssignableFrom( target.getClass() ) ) ) {

            throw new IllegalArgumentException( "You can only copy object of the same type [" +
                                                source.getClass().getSimpleName() + " -> " +
                                                target.getClass().getSimpleName() + "]" );
        }

        // if the objects are considered to be the same object, proceed. Otherwise, return
        // false and don't copy anything.
        // This statement acts as a filter, so during an update (from a source with null AC),
        // there are no undesired updates in the database. This could be the case, for instance,
        // when an interaction is provided that is a duplicated of the existing one in the database.
        // Supposing the case they had two different shortlabels, for instance, we do NOT want to
        // update the shortLabel in the database.
        // Of couse, we would do it if source and target have the same AC, as it is considered
        // an update on purpose.
        if (source.getAc() == null && areEqual(source, target)) {
            return false;
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

        return copiedProperty;
    }

    private <T extends AnnotatedObject> T clone(T objToClone) throws IntactClonerException {
        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        return cloner.clone(objToClone);
    }

    protected void copyInstitution( Institution source, Institution target ) {
        copyProperty(source, "url", target);
        copyProperty(source, "postalAddress", target);
    }

    protected void copyPublication( Publication source, Publication target ) {
        copyCollection( source.getExperiments(), target.getExperiments() );
    }

    protected void copyExperiment( Experiment source, Experiment target ) {
        copyProperty(source, "bioSource", target);
        copyProperty(source, "publication", target);
        copyProperty(source, "cvIdentification", target);
        copyProperty(source, "cvInteraction", target);

        copyCollection( source.getInteractions(), target.getInteractions() );
    }

    protected void copyInteraction( Interaction source, Interaction target ) {
        copyProperty(source, "KD", target);
        copyProperty(source, "crc", target);
        copyProperty(source, "cvInteractionType", target);

        copyCollection( source.getComponents(), target.getComponents() );
        copyCollection( source.getExperiments(), target.getExperiments() );

        copyCollection( source.getConfidences(), target.getConfidences() );
        
        copyCollection( source.getParameters(), target.getParameters() );

        copyInteractorCommons( source, target );

        // we have ommited CRC on purpose
    }

    protected void copyInteractor( Interactor source, Interactor target ) {
        copyCollection( source.getActiveInstances(), target.getActiveInstances() );

        copyInteractorCommons( source, target );
    }

    protected void copyInteractorCommons( Interactor source, Interactor target ) {

        if ( target.getBioSource() != null && source.getBioSource() == null ) {

            throw new PersisterException( "Operation not permitted: nullifying biosource of a " +
                                          target.getClass().getSimpleName() + " (" + target.getShortLabel() + ") - " +
                                          " current biosource is  " + target.getBioSource().getShortLabel() );
        }

        copyProperty(source, "bioSource", target);
        copyProperty(source, "cvInteractorType", target);
    }

    protected void copyComponent( Component source, Component target ) {
        copyProperty(source, "stoichiometry", target);

        copyProperty(source, "interaction", target);
        copyProperty(source, "interactor", target);
        copyProperty(source, "cvBiologicalRole", target);
        copyProperty(source, "cvExperimentalRole", target);
        copyProperty(source, "expressedIn", target);

        copyCollection( source.getBindingDomains(), target.getBindingDomains() );

        for (Feature bindingDomain : target.getBindingDomains()) {
            bindingDomain.setComponent(target);
        }

        copyCollection( source.getExperimentalPreparations(), target.getExperimentalPreparations() );
        copyCollection( source.getParticipantDetectionMethods(), target.getParticipantDetectionMethods() );
        copyCollection( source.getParameters(), target.getParameters() );
    }

    protected void copyFeature( Feature source, Feature target ) {
        //copyProperty(source, "component", target);
        copyProperty(source, "cvFeatureIdentification", target);
        copyProperty(source, "cvFeatureType", target);

        copyCollection( source.getRanges(), target.getRanges() );
    }

    protected void copyBioSource( BioSource source, BioSource target ) {
        copyProperty(source, "taxId", target);
        copyProperty(source, "cvTissue", target);
        copyProperty(source, "cvCellType", target);
    }

    protected void copyCvObject( CvObject source, CvObject target ) {
        // nothing copied
    }

    protected <X extends Xref, A extends Alias> void copyAnotatedObjectCommons( AnnotatedObject<X, A> source,
                                                                                AnnotatedObject<X, A> target ) {

        // if the source does not have an AC, we should not update the target shortLabel, fullName and owner
        // as it does not make much sense
        //if (source.getAc() != null) {
            copyProperty(source, "shortLabel", target);
            copyProperty(source, "fullName", target);
            copyProperty(source, "owner", target);
        //}

        copyXrefCollection( source.getXrefs(), target.getXrefs() );
        copyAliasCollection( source.getAliases(), target.getAliases() );
        copyAnnotationCollection( source.getAnnotations(), target.getAnnotations() );
    }

    private void copyAnnotationCollection( Collection<Annotation> sourceCol, Collection<Annotation> targetCol ) {
        if (!Hibernate.isInitialized(sourceCol)) {
            return;
        }
        
        if (!CollectionUtils.isEqualCollection(sourceCol, targetCol)) {
            copiedProperty = true;
        }
        
        Collection elementsToAdd = subtractAnnotations( sourceCol, targetCol );
        Collection elementsToRemove = subtractAnnotations( targetCol, sourceCol );
        targetCol.removeAll( elementsToRemove );
        targetCol.addAll( elementsToAdd );
    }

    private Collection<Annotation> subtractAnnotations( Collection<Annotation> sourceCol, Collection<Annotation> targetCol ) {
        Collection<Annotation> annotations = new ArrayList<Annotation>( Math.max( sourceCol.size(), targetCol.size() ) );

        for ( Annotation source : sourceCol ) {
            boolean found = false;
            for ( Iterator<Annotation> iterator = targetCol.iterator(); iterator.hasNext() && !found; ) {
                Annotation target = iterator.next();
                if ( EqualsUtils.sameAnnotation( source, target ) ) {
                    // found it, we do not copy if to the resulting collection
                    found = true;
                }
            }

            if ( !found ) {
                annotations.add( source );
            }
        }

        return annotations;
    }

    private <X extends Xref> void copyXrefCollection( Collection<X> sourceCol, Collection<X> targetCol ) {
        if (!Hibernate.isInitialized(sourceCol)) {
            return;
        }

        if (!CollectionUtils.isEqualCollection(sourceCol, targetCol)) {
            copiedProperty = true;
        }

        Collection elementsToAdd = subtractXrefs( sourceCol, targetCol );
        Collection elementsToRemove = subtractXrefs( targetCol, sourceCol );
        targetCol.removeAll( elementsToRemove );
        targetCol.addAll( elementsToAdd );
    }

    private <A extends Alias> void copyAliasCollection( Collection<A> sourceCol, Collection<A> targetCol ) {
        if (!Hibernate.isInitialized(sourceCol)) {
            return;
        }

        if (!CollectionUtils.isEqualCollection(sourceCol, targetCol)) {
            copiedProperty = true;
        }

        Collection elementsToAdd = subtractAliases( sourceCol, targetCol );
        Collection elementsToRemove = subtractAliases( targetCol, sourceCol );
        targetCol.removeAll( elementsToRemove );
        targetCol.addAll( elementsToAdd );
    }

    private <X extends Xref> Collection<X> subtractXrefs( Collection<X> sourceCol, Collection<X> targetCol ) {
        Collection<X> xrefs = new ArrayList<X>( Math.max( sourceCol.size(), targetCol.size() ) );

        for ( X source : sourceCol ) {
            boolean found = false;
            for ( Iterator<X> iterator = targetCol.iterator(); iterator.hasNext() && !found; ) {
                X target = iterator.next();
                if ( EqualsUtils.sameXref( source, target ) ) {
                    // found it, we do not copy if to the resulting collection
                    found = true;
                }
            }

            if ( !found ) {
                xrefs.add( source );
            }
        }

        return xrefs;
    }

    private <A extends Alias> Collection<A> subtractAliases( Collection<A> sourceCol, Collection<A> targetCol ) {
        Collection<A> aliases = new ArrayList<A>( Math.max( sourceCol.size(), targetCol.size() ) );

        for ( A source : sourceCol ) {
            boolean found = false;
            for ( Iterator<A> iterator = targetCol.iterator(); iterator.hasNext() && !found; ) {
                A target = iterator.next();
                if ( EqualsUtils.sameAlias( source, target ) ) {
                    // found it, we do not copy if to the resulting collection
                    found = true;
                }
            }

            if ( !found ) {
                aliases.add( source );
            }
        }

        return aliases;
    }

    protected void copyCollection( Collection sourceCol, Collection targetCol ) {
        if (!Hibernate.isInitialized(sourceCol) || !Hibernate.isInitialized(targetCol)) {
            return;
        }

        if (!CollectionUtils.isEqualCollection(sourceCol, targetCol)) {
            copiedProperty = true;
        }
        
        Collection elementsToAdd = CollectionUtils.subtract( sourceCol, targetCol );
        Collection elementsToRemove = CollectionUtils.subtract( targetCol, sourceCol );
        targetCol.removeAll( elementsToRemove );
        targetCol.addAll( elementsToAdd );
    }

    /**
     * <p>Returs true if two annotated objects are equal. The generic way to do the check
     * is:</p>
     * a) If they have the same AC, consider them equal<br/>
     * b) Otherwise, clone both objects (excluding the ACs) and invoke equals() on them
     */
    protected boolean areEqual(AnnotatedObject source, AnnotatedObject target) {
        if (source instanceof CvObject && areCvObjectsEqual((CvObject)source, (CvObject)target)) {
            return true;
        } else if (source instanceof Interaction && areInteractionsEqual((Interaction)source, (Interaction)target)) {
            return true;
        }

        // clone both source and target to try a perfect equals on them
        try {
            if (clone(source).equals(clone(target))) {
                return true;
            }
        } catch (IntactClonerException e) {
            throw new PersisterException("Problem cloning source or target, to check if they are equal", e);
        }

        return false;
    }

    protected boolean areCvObjectsEqual(CvObject source, CvObject target) {
        return CvObjectUtils.areEqual(source, target, true);
    }

    protected boolean areInteractionsEqual(Interaction source, Interaction target) {
        CrcCalculator calculator = new CrcCalculator();
        return calculator.crc64(source).equals(calculator.crc64(target));
    }

    protected boolean copyProperty(Object source, String propertyName, Object target) {
        try {
            Object sourceProperty = PropertyUtils.getProperty(source, propertyName);
            Object targetProperty = PropertyUtils.getProperty(target, propertyName);

            if (sourceProperty == null && targetProperty == null) {
                return false;
            }

            if (sourceProperty instanceof AnnotatedObject) {
                if (areEqual((AnnotatedObject)sourceProperty, (AnnotatedObject)targetProperty)) {
                    return false;
                }
            } else {
                if (sourceProperty != null && sourceProperty.equals(targetProperty)) {
                    return false;
                }
            }
            
            if (log.isTraceEnabled()) log.trace("Copying "+propertyName+" from "+source.getClass().getSimpleName()+
                                                " ["+source+"] to "+target.getClass().getSimpleName()+" ["+target+"]");

            // copy the value
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, target.getClass());
            propertyDescriptor.getWriteMethod().invoke(target, sourceProperty);

            copiedProperty = true;

        } catch (Throwable e) {
            String sourceInfo = "";

            if (source instanceof AnnotatedObject) {
                sourceInfo = DebugUtil.annotatedObjectToString((AnnotatedObject) source, true);
            }

            throw new PersisterException("Problem copying property '"+propertyName+"' from "+source.getClass().getSimpleName()+" "+sourceInfo, e);
        }

        return true;
    }
}
