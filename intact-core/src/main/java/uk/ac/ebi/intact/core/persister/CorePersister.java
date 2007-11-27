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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Persists intact object in the database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class CorePersister implements Persister {

    Map<Key, AnnotatedObject> annotatedObjectToPersist = new HashMap<Key, AnnotatedObject>();
    Map<Key, AnnotatedObject> annotatedObjectToMerge = new HashMap<Key, AnnotatedObject>();

    private Finder finder;
    private KeyBuilder keyBuilder;
    private TransientObjectHandler transientObjectHandler;
    private EntityStateCopier entityStateCopier;

    public CorePersister() {
        finder = new DefaultFinder();
        keyBuilder = new KeyBuilder();
        transientObjectHandler = new DefaultTransientObjectHandler();
        entityStateCopier = new DefaultEntityStateCopier();
    }

    ////////////////////////
    // Implement Persister

    public AnnotatedObject synchronize( AnnotatedObject ao ) {

        if ( ao == null ) {
            throw new IllegalArgumentException( "The given annotatedObject must not be null" );
        }

        final Key key = keyBuilder.keyFor( ao );

        if ( annotatedObjectToMerge.containsKey( key ) ) {
            return annotatedObjectToMerge.get( key );
        }

        if ( annotatedObjectToPersist.containsKey( key ) ) {
            return annotatedObjectToPersist.get( key );
        }

        AnnotatedObject synched = null;

        if ( ao.getAc() == null ) {

            // the object is new
            final String ac = finder.findAc( ao );

            if ( ac == null ) {

                // doesn't exist in the database, we will persist it
                annotatedObjectToPersist.put( key, ao );

                synchronizeChildren( ao );

            } else {

                // object exists in the database, we will update it
                final AnnotatedObjectDao<? extends AnnotatedObject> dao =
                        IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotatedObjectDao( ao.getClass() );
                final AnnotatedObject managedObject = dao.getByAc( ac );

                // updated the managed object based on ao's properties
                entityStateCopier.copy( ao, managedObject );
                annotatedObjectToMerge.put( key, ao );

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

    }

    //////////////////////
    // Private methods

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

        synchronizeAnnotatedObjectCommons( experiment );

        Collection<Interaction> interactions = new ArrayList<Interaction>( experiment.getInteractions().size() );
        for ( Interaction interaction : experiment.getInteractions() ) {
            interactions.add( (Interaction ) synchronize( interaction ) );
        }
        experiment.setInteractions( interactions );



    }

    private void synchronizeInteraction( Interaction interaction ) {
    }

    private void synchronizeInteractor( Interactor interactor ) {
    }

    private void synchronizeBioSource( BioSource bioSource ) {
    }

    private void synchronizeComponent( Component component ) {
    }

    private void synchronizeFeature( Feature feature ) {
    }

    private void synchronizeCvObject( CvObject cvObject ) {
    }

    private void synchronizePublication( Publication publication ) {
    }

    private void synchronizeInstitution( Institution institution ) {
    }

    private void synchronizeAnnotatedObjectCommons( AnnotatedObject ao ) {

        if( ! ( ao instanceof Institution ) ) {
            ao.setOwner( (Institution ) synchronize( ao.getOwner() ) );
        }



    }
}