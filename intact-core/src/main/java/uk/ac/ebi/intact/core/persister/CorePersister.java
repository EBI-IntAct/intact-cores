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

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.persistence.dao.BaseDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.*;

/**
 * Persists intact object in the database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class CorePersister implements Persister {

    private Map<Key, AnnotatedObject> annotatedObjectsToPersist;
    private Map<Key, AnnotatedObject> annotatedObjectsToMerge;

    private Finder finder;
    private KeyBuilder keyBuilder;
    private TransientObjectHandler transientObjectHandler;
    private EntityStateCopier entityStateCopier;

    public CorePersister() {

        annotatedObjectsToPersist = new HashMap<Key, AnnotatedObject>();
        annotatedObjectsToMerge = new HashMap<Key, AnnotatedObject>();

        finder = new DefaultFinder();
        keyBuilder = new KeyBuilder();
        transientObjectHandler = new DefaultTransientObjectHandler();
        entityStateCopier = new DefaultEntityStateCopier();
    }

    ////////////////////////
    // Implement Persister

    public AnnotatedObject synchronize( AnnotatedObject ao ) {

        if ( ao == null ) {
            return null;
        }

        final Key key = keyBuilder.keyFor( ao );

        if ( key == null ) {
            throw new IllegalArgumentException( "Cannot handle null key" );
        }

        if ( annotatedObjectsToMerge.containsKey( key ) ) {
            return annotatedObjectsToMerge.get( key );
        }

        if ( annotatedObjectsToPersist.containsKey( key ) ) {
            return annotatedObjectsToPersist.get( key );
        }

        AnnotatedObject synched = null;

        if ( ao.getAc() == null ) {

            // the object is new
            final String ac = finder.findAc( ao );

            if ( ac == null ) {

                // doesn't exist in the database, we will persist it
                annotatedObjectsToPersist.put( key, ao );

                synchronizeChildren( ao );

            } else {

                // object exists in the database, we will update it
                final AnnotatedObjectDao<? extends AnnotatedObject> dao =
                        IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotatedObjectDao( ao.getClass() );
                final AnnotatedObject managedObject = dao.getByAc( ac );

                // updated the managed object based on ao's properties
                entityStateCopier.copy( ao, managedObject );
                annotatedObjectsToMerge.put( key, ao );

                synchronizeChildren( ao );

            }

            synched = ao;

        } else {

            final BaseDao baseDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao();
            if ( baseDao.isTransient( ao ) ) {

                // transient object: that is not attached to the session
                synched = transientObjectHandler.handle( ao );

            } else {

                // managed object
                synchronizeChildren( ao );
            }
        }

        return synched;
    }

    public void commit() {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        // order the collection of objects to persist: institution, cvs, others
        List<AnnotatedObject> thingsToMerge = new ArrayList<AnnotatedObject>( annotatedObjectsToMerge.values() );
        Collections.sort( thingsToMerge, new PersistenceOrderComparator() );
        for ( AnnotatedObject ao : annotatedObjectsToMerge.values() ) {
            daoFactory.getBaseDao().merge( ao );
        }

        SortedMap m = new TreeMap();

        // order the collection of objects to persist: institution, cvs, others
        List<AnnotatedObject> thingsToPersist = new ArrayList<AnnotatedObject>( annotatedObjectsToPersist.values() );
        Collections.sort( thingsToPersist, new PersistenceOrderComparator() );
        for ( AnnotatedObject ao : thingsToPersist ) {
            daoFactory.getBaseDao().persist( ao );
        }

        daoFactory.commitTransaction();
    }

    /////////////////////////////////////////////
    // Private methods - synchronize children

    private void synchronizeChildren( AnnotatedObject ao ) {
        if ( ao instanceof Institution ) {
            synchronizeInstitution( ( Institution ) ao );
        } else if ( ao instanceof Publication ) {
            synchronizePublication( ( Publication ) ao );
        } else if ( ao instanceof CvObject ) {
            synchronizeCvObject( ( CvObject ) ao );
        } else if ( ao instanceof Experiment ) {
            synchronizeExperiment( ( Experiment ) ao );
        } else if ( ao instanceof Interaction ) {
            synchronizeInteraction( ( Interaction ) ao );
        } else if ( ao instanceof Interactor ) {
            synchronizeInteractor( ( Interactor ) ao );
        } else if ( ao instanceof BioSource ) {
            synchronizeBioSource( ( BioSource ) ao );
        } else if ( ao instanceof Component ) {
            synchronizeComponent( ( Component ) ao );
        } else if ( ao instanceof Feature ) {
            synchronizeFeature( ( Feature ) ao );
        } else {
            throw new IllegalArgumentException( "synchronizeChildren doesn't handle : " + ao.getClass().getName() );
        }
    }

    private void synchronizeExperiment( Experiment experiment ) {
        experiment.setPublication( ( Publication ) synchronize( experiment.getPublication() ) );
        experiment.setInteractions( synchronizeCollection( experiment.getInteractions() ) );
        experiment.setCvIdentification( ( CvIdentification ) synchronize( experiment.getCvIdentification() ) );
        experiment.setCvInteraction( ( CvInteraction ) synchronize( experiment.getCvInteraction() ) );
        experiment.setBioSource( ( BioSource ) synchronize( experiment.getBioSource() ) );
        synchronizeAnnotatedObjectCommons( experiment );
    }

    private void synchronizeInteraction( Interaction interaction ) {
        interaction.setCvInteractionType( ( CvInteractionType ) synchronize( interaction.getCvInteractionType() ) );
        interaction.setCvInteractorType( ( CvInteractorType ) synchronize( interaction.getCvInteractorType() ) );
        interaction.setComponents( synchronizeCollection( interaction.getComponents() ) );
        interaction.setBioSource( ( BioSource ) synchronize( interaction.getBioSource() ) );
        interaction.setExperiments( synchronizeCollection( interaction.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( interaction );
    }

    private void synchronizeInteractor( Interactor interactor ) {
        interactor.setActiveInstances( synchronizeCollection( interactor.getActiveInstances() ) );
        interactor.setBioSource( ( BioSource ) synchronize( interactor.getBioSource() ) );
        interactor.setCvInteractorType( ( CvInteractorType ) synchronize( interactor.getCvInteractorType() ) );
        synchronizeAnnotatedObjectCommons( interactor );
    }

    private void synchronizeBioSource( BioSource bioSource ) {
        bioSource.setCvCellType( ( CvCellType ) synchronize( bioSource.getCvCellType() ) );
        bioSource.setCvTissue( ( CvTissue ) synchronize( bioSource.getCvTissue() ) );
        synchronizeAnnotatedObjectCommons( bioSource );
    }

    private void synchronizeComponent( Component component ) {
        component.setBindingDomains( synchronizeCollection( component.getBindingDomains() ) );
        component.setCvBiologicalRole( ( CvBiologicalRole ) synchronize( component.getCvBiologicalRole() ) );
        component.setCvExperimentalRole( ( CvExperimentalRole ) synchronize( component.getCvExperimentalRole() ) );
        component.setExperimentalPreparations( synchronizeCollection( component.getExperimentalPreparations() ) );
        component.setExpressedIn( ( BioSource ) synchronize( component.getExpressedIn() ) );
        component.setInteraction( ( Interaction ) synchronize( component.getInteraction() ) );
        component.setInteractor( ( Interactor ) synchronize( component.getInteractor() ) );
        component.setParticipantDetectionMethods( synchronizeCollection( component.getParticipantDetectionMethods() ) );
        synchronizeAnnotatedObjectCommons( component );
    }

    private void synchronizeFeature( Feature feature ) {
        feature.setBoundDomain( ( Feature ) synchronize( feature.getBoundDomain() ) );
        feature.setComponent( ( Component ) synchronize( feature.getComponent() ) );
        feature.setCvFeatureIdentification( ( CvFeatureIdentification ) synchronize( feature.getCvFeatureIdentification() ) );
        feature.setCvFeatureType( ( CvFeatureType ) synchronize( feature.getCvFeatureType() ) );
        feature.setRanges( synchronizeCollection( feature.getRanges() ) );
        synchronizeAnnotatedObjectCommons( feature );
    }

    private void synchronizeCvObject( CvObject cvObject ) {
        // no sub-object, do nothing
        // TODO handle parents and children in case the instance is a CvDagObject
        synchronizeAnnotatedObjectCommons( cvObject );
    }

    private void synchronizePublication( Publication publication ) {
        publication.setExperiments( synchronizeCollection( publication.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( publication );
    }

    private void synchronizeInstitution( Institution institution ) {
        // no sub-object, do nothing
        synchronizeAnnotatedObjectCommons( institution );
    }

    private Collection synchronizeCollection( Collection collection ) {
        Collection synchedCollection = new ArrayList( collection.size() );
        for ( Object o : collection ) {
            synchedCollection.add( synchronize( ( AnnotatedObject ) o ) );
        }
        return synchedCollection;
    }

    private void synchronizeAnnotatedObjectCommons( AnnotatedObject<? extends Xref, ? extends Alias> ao ) {

        if ( !( ao instanceof Institution ) ) {
            ao.setOwner( ( Institution ) synchronize( ao.getOwner() ) );
        }

        Collection synchedXrefs = new ArrayList( ao.getXrefs().size() );
        for ( Xref xref : ao.getXrefs() ) {
            synchedXrefs.add( synchronizeXrefs( xref ) );
        }
        ao.setXrefs( synchedXrefs );

        Collection synchedAliases = new ArrayList( ao.getAliases().size() );
        for ( Alias alias : ao.getAliases() ) {
            synchedAliases.add( synchronizeAlias( alias ) );
        }
        ao.setAliases( synchedAliases );

        Collection synchedAnnotations = new ArrayList( ao.getAnnotations().size() );
        for ( Annotation annotation : ao.getAnnotations() ) {
            synchedAnnotations.add( synchronizeAnnotation( annotation ) );
        }
        ao.setAnnotations( synchedAnnotations );
    }

    private Xref synchronizeXrefs( Xref xref ) {
        xref.setCvDatabase( ( CvDatabase ) synchronize( xref.getCvDatabase() ) );
        xref.setCvXrefQualifier( ( CvXrefQualifier ) synchronize( xref.getCvXrefQualifier() ) );
        return xref;
    }

    private Alias synchronizeAlias( Alias alias ) {
        alias.setCvAliasType( ( CvAliasType ) synchronize( alias.getCvAliasType() ) );
        return alias;
    }

    private Annotation synchronizeAnnotation( Annotation annotation ) {
        annotation.setCvTopic( ( CvTopic ) synchronize( annotation.getCvTopic() ) );
        return annotation;
    }
}