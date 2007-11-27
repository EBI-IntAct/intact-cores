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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        for ( AnnotatedObject ao : annotatedObjectsToMerge.values() ) {
            daoFactory.getBaseDao().merge( ao );
        }

        for ( AnnotatedObject ao : annotatedObjectsToPersist.values() ) {
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
    }

    private void synchronizeInteraction( Interaction interaction ) {
        interaction.setCvInteractionType( ( CvInteractionType ) synchronize( interaction.getCvInteractionType() ) );
        interaction.setComponents( synchronizeCollection( interaction.getComponents() ) );
        interaction.setBioSource( ( BioSource ) synchronize( interaction.getBioSource() ) );
        interaction.setExperiments( synchronizeCollection( interaction.getExperiments() ) );
    }

    private void synchronizeInteractor( Interactor interactor ) {
        interactor.setActiveInstances( synchronizeCollection( interactor.getActiveInstances() ) );
        interactor.setBioSource( ( BioSource ) synchronize( interactor.getBioSource() ) );
        interactor.setCvInteractorType( ( CvInteractorType ) synchronize( interactor.getCvInteractorType() ) );
    }

    private void synchronizeBioSource( BioSource bioSource ) {
        bioSource.setCvCellType( ( CvCellType ) synchronize( bioSource.getCvCellType() ) );
        bioSource.setCvTissue( ( CvTissue ) synchronize( bioSource.getCvTissue() ) );
    }

    private void synchronizeComponent( Component component ) {
        component.setBindingDomains( synchronizeCollection( component.getBindingDomains() ) );
        component.setCvBiologicalRole( ( CvBiologicalRole ) synchronize( component.getCvBiologicalRole() ) );
        component.setCvExperimentalRole( ( CvExperimentalRole ) synchronize( component.getCvExperimentalRole() ) );
        component.setExpressedIn( ( BioSource ) synchronize( component.getExpressedIn() ) );
        component.setInteraction( ( Interaction ) synchronize( component.getInteraction() ) );
        component.setInteractor( ( Interactor ) synchronize( component.getInteractor() ) );
        component.setParticipantDetectionMethods( synchronizeCollection( component.getParticipantDetectionMethods() ) );
    }

    private void synchronizeFeature( Feature feature ) {
        feature.setBoundDomain( ( Feature ) synchronize( feature.getBoundDomain() ) );
        feature.setComponent( ( Component ) synchronize( feature.getComponent() ) );
        feature.setCvFeatureIdentification( ( CvFeatureIdentification ) synchronize( feature.getCvFeatureIdentification() ) );
        feature.setCvFeatureType( ( CvFeatureType ) synchronize( feature.getCvFeatureType() ) );
        feature.setRanges( synchronizeCollection( feature.getRanges() ) );
    }

    private void synchronizeCvObject( CvObject cvObject ) {
        // no sub-object, do nothing
        // TODO handle parents and children in case the instance is a CvDagObject
    }

    private void synchronizePublication( Publication publication ) {
        publication.setExperiments( synchronizeCollection( publication.getExperiments() ) );
    }

    private void synchronizeInstitution( Institution institution ) {
        // no sub-object, do nothing
    }

    private Collection synchronizeCollection( Collection collection ) {
        Collection synchedCollection = new ArrayList( collection.size() );
        for ( Object o : collection ) {
            synchedCollection.add( synchronize( ( AnnotatedObject ) o ) );
        }
        return synchedCollection;
    }
}