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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
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
public class CorePersister implements Persister<AnnotatedObject> {

    private static final Log log = LogFactory.getLog( CorePersister.class );

    private Map<Key, AnnotatedObject> annotatedObjectsToPersist;
    private Map<Key, AnnotatedObject> annotatedObjectsToMerge;
    private Map<Key, AnnotatedObject> synched;

    private Finder finder;
    private KeyBuilder keyBuilder;
    private EntityStateCopier entityStateCopier;

    public CorePersister() {

        annotatedObjectsToPersist = new HashMap<Key, AnnotatedObject>();
        annotatedObjectsToMerge = new HashMap<Key, AnnotatedObject>();
        synched = new HashMap<Key, AnnotatedObject>();

        finder = new DefaultFinder();
        keyBuilder = new KeyBuilder();
        entityStateCopier = new DefaultEntityStateCopier();
    }

    public void setEntityStateCopier( EntityStateCopier entityStateCopier ) {
        if ( entityStateCopier == null ) {
            throw new IllegalArgumentException();
        }
        this.entityStateCopier = entityStateCopier;
    }

    ////////////////////////
    // Implement Persister

    public void saveOrUpdate( AnnotatedObject ao ) {

        boolean inTransaction = IntactContext.getCurrentInstance().getDataContext().isTransactionActive();

        if ( !inTransaction ) IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        synchronize( ao );
        commit();

        reload( ao );

        if ( !inTransaction ) commitTransactionAndRollbackIfNecessary();
    }

    protected void commitTransactionAndRollbackIfNecessary() throws PersisterException {
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
        catch ( IntactTransactionException e ) {
            try {
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCurrentTransaction().rollback();
            }
            catch ( IntactTransactionException e1 ) {
                throw new PersisterException( e1 );
            }
        }
    }

    protected <T extends AnnotatedObject> T synchronize( T ao ) {

        if ( ao == null ) {
            return null;
        }

        Class<T> aoClass = (Class<T>) ao.getClass();

        final Key key = keyBuilder.keyFor( ao );

        if ( key == null ) {
            throw new IllegalArgumentException( "Cannot handle null key" );
        }

        if ( synched.containsKey( key ) ) {
            ao = (T) synched.get( key );
            verifyExpectedType(ao, aoClass);
            return ao;
        }
        synched.put( key, ao );

        if ( ao.getAc() == null ) {

            // the object is new
            final String ac = finder.findAc( ao );

            if ( ac == null ) {

                // doesn't exist in the database, we will persist it
                annotatedObjectsToPersist.put( key, ao );

                synchronizeChildren( ao );

            } else {

                // object exists in the database, we will update it
                final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                final AnnotatedObjectDao<T> dao = daoFactory.getAnnotatedObjectDao( (Class<T>)ao.getClass() );
                final T managedObject = dao.getByAc( ac );

                if (managedObject == null) {
                    throw new IllegalStateException("No managed object found with ac '"+ac+"' and type '"+ao.getClass()+"' and one was expected");
                }

                // updated the managed object based on ao's properties
                entityStateCopier.copy( ao, managedObject );

                // this will allow to reload the AO by its AC after flushing
                ao.setAc( managedObject.getAc() );

                // and the created info, so the merge does not fail due to missing created data
                ao.setCreated( managedObject.getCreated() );
                ao.setCreator( managedObject.getCreator() );

                // synchronize the children
                synchronizeChildren( managedObject );
            }

        } else {

            final BaseDao baseDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao();
            if ( baseDao.isTransient( ao ) ) {

                // transient object: that is not attached to the session
                //ao = transientObjectHandler.handle( ao );
                annotatedObjectsToMerge.put(key, ao);
                synchronizeChildren(ao);

            } else {

                // managed object
                // annotatedObjectsToMerge.put( key, ao );
                synchronizeChildren( ao );
            }
        }

        // check if the object class after synchronization is the same as in the beginning
        verifyExpectedType(ao, aoClass);

        return ao;
    }

    private <T extends AnnotatedObject> void verifyExpectedType(T ao, Class<T> aoClass) {
        if (!(aoClass.isAssignableFrom(ao.getClass()) || ao.getClass().isAssignableFrom(aoClass))) {
            throw new IllegalArgumentException("Wrong type returned after synchronization. Expected "+aoClass.getName()+" but found "+
            ao.getClass().getName()+". The offender was: "+ao);
        }
    }

    protected void reload( AnnotatedObject ao ) {
        if ( ao.getAc() == null ) {
            throw new IllegalStateException( "Annotated object without ac, cannot be reloaded: " + ao );
        }
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        daoFactory.getAnnotatedObjectDao( ao.getClass() ).getByAc( ao.getAc() );
    }

    protected void commit() {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        if ( log.isDebugEnabled() ) {
            log.debug( "Persisting objects..." );
        }

        // Order the collection of objects to persist: institution, cvs, others
        List<AnnotatedObject> thingsToPersist = new ArrayList<AnnotatedObject>( annotatedObjectsToPersist.values() );
        Collections.sort( thingsToPersist, new PersistenceOrderComparator() );

        for ( AnnotatedObject ao : thingsToPersist ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\tAbout to persist " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }

            // this may happen if there is a cascade on this object from the parent
            if ( ao.getAc() != null ) {
                log.warn( "Object to persist should NOT have an AC: " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }

            daoFactory.getBaseDao().persist( ao );
            logPersistence( ao );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Merging objects..." );
        }

        // Order the collection of objects to persist: institution, cvs, others
        List<AnnotatedObject> thingsToMerge = new ArrayList<AnnotatedObject>( annotatedObjectsToMerge.values() );
        Collections.sort( thingsToMerge, new PersistenceOrderComparator() );
        for ( AnnotatedObject ao : annotatedObjectsToMerge.values() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\tAbout to merge " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }

            if ( ao.getAc() == null ) {
                throw new IllegalStateException( "Object to persist should have an AC: " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }

            daoFactory.getBaseDao().merge( ao );
            logPersistence( ao );
        }

        try {
            daoFactory.getEntityManager().flush();
        } catch (Throwable t) {
            throw new PersisterException("Exception when flushing the Persister, which contained " +
                                         annotatedObjectsToPersist.size()+" objects to persist and " +
                                         annotatedObjectsToMerge.size()+" objects to merge.", t);
        } finally {
            annotatedObjectsToMerge.clear();
            annotatedObjectsToPersist.clear();
            synched.clear();
        }
    }

    private static void logPersistence( AnnotatedObject<?, ?> ao ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "\t\t\tPersisted with AC: " + ao.getAc() );

            if ( !ao.getXrefs().isEmpty() ) {
                log.debug( "\t\t\tXrefs: " + ao.getXrefs().size() );

                for ( Xref xref : ao.getXrefs() ) {
                    log.debug( "\t\t\t\t" + xref );
                }
            }

            if ( !ao.getAliases().isEmpty() ) {
                log.debug( "\t\t\tAliases: " + ao.getAliases().size() );

                for ( Alias alias : ao.getAliases() ) {
                    log.debug( "\t\t\t\t" + alias );
                }
            }

            if ( !ao.getAnnotations().isEmpty() ) {
                log.debug( "\t\t\tAnnotations: " + ao.getAnnotations().size() );

                for ( Annotation annot : ao.getAnnotations() ) {
                    log.debug( "\t\t\t\t" + annot );
                }
            }
        }
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

        experiment.setPublication( synchronize( experiment.getPublication() ) );
        experiment.setInteractions( synchronizeCollection( experiment.getInteractions() ) );
        experiment.setCvIdentification( synchronize( experiment.getCvIdentification() ) );
        experiment.setCvInteraction( synchronize( experiment.getCvInteraction() ) );
        experiment.setBioSource( synchronize( experiment.getBioSource() ) );
        synchronizeAnnotatedObjectCommons( experiment );
    }

    private void synchronizeInteraction( Interaction interaction ) {

        interaction.setCvInteractionType( synchronize( interaction.getCvInteractionType() ) );
        interaction.setCvInteractorType( synchronize( interaction.getCvInteractorType() ) );
        interaction.setComponents( synchronizeCollection( interaction.getComponents() ) );
        interaction.setBioSource( synchronize( interaction.getBioSource() ) );
        interaction.setExperiments( synchronizeCollection( interaction.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( interaction );
    }

    private void synchronizeInteractor( Interactor interactor ) {

        interactor.setActiveInstances( synchronizeCollection( interactor.getActiveInstances() ) );
        interactor.setBioSource( synchronize( interactor.getBioSource() ) );
        interactor.setCvInteractorType( synchronize( interactor.getCvInteractorType() ) );
        synchronizeAnnotatedObjectCommons( interactor );
    }

    private void synchronizeBioSource( BioSource bioSource ) {

        bioSource.setCvCellType( synchronize( bioSource.getCvCellType() ) );
        bioSource.setCvTissue( synchronize( bioSource.getCvTissue() ) );
        synchronizeAnnotatedObjectCommons( bioSource );
    }

    private void synchronizeComponent( Component component ) {

        component.setBindingDomains( synchronizeCollection( component.getBindingDomains() ) );
        component.setCvBiologicalRole( synchronize( component.getCvBiologicalRole() ) );
        component.setCvExperimentalRole( synchronize( component.getCvExperimentalRole() ) );
        component.setExpressedIn( synchronize( component.getExpressedIn() ) );
        component.setInteraction( synchronize( component.getInteraction() ) );
        component.setInteractor( synchronize( component.getInteractor() ) );
        component.setParticipantDetectionMethods( synchronizeCollection( component.getParticipantDetectionMethods() ) );
        component.setExperimentalPreparations( synchronizeCollection( component.getExperimentalPreparations() ) );
        synchronizeAnnotatedObjectCommons( component );
    }

    private void synchronizeFeature( Feature feature ) {

        feature.setBoundDomain( synchronize( feature.getBoundDomain() ) );
        feature.setComponent( synchronize( feature.getComponent() ) );
        feature.setCvFeatureIdentification( synchronize( feature.getCvFeatureIdentification() ) );
        feature.setCvFeatureType( synchronize( feature.getCvFeatureType() ) );
        for ( Range range : feature.getRanges() ) {
            synchronizeRange( range );
        }
        synchronizeAnnotatedObjectCommons( feature );
    }

    private void synchronizeRange( Range range ) {

        range.setFromCvFuzzyType(synchronize( range.getFromCvFuzzyType() ) );
        range.setToCvFuzzyType(synchronize( range.getToCvFuzzyType() ) );
    }

    private void synchronizeCvObject( CvObject cvObject ) {

        // TODO handle parents and children in case the instance is a CvDagObject

        synchronizeAnnotatedObjectCommons( cvObject );
    }

    private void synchronizePublication( Publication publication ) {

        publication.setExperiments( synchronizeCollection( publication.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( publication );
    }

    private void synchronizeInstitution( Institution institution ) {

        synchronizeAnnotatedObjectCommons( institution );
    }

    private <X extends AnnotatedObject> Collection<X> synchronizeCollection( Collection<X> collection ) {
        Collection<X> synchedCollection = new ArrayList<X>( collection.size() );
        for ( X ao : collection ) {
            synchedCollection.add( synchronize( ao ) );
        }
        return synchedCollection;
    }

    private void synchronizeAnnotatedObjectCommons( AnnotatedObject<? extends Xref, ? extends Alias> ao ) {

        if ( !( ao instanceof Institution ) ) {
            ao.setOwner(synchronize(ao.getOwner() ) );
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

        xref.setOwner( synchronize( xref.getOwner() ) );
        xref.setCvDatabase( synchronize( xref.getCvDatabase() ) );
        xref.setCvXrefQualifier( synchronize( xref.getCvXrefQualifier() ) );
        return xref;
    }

    private Alias synchronizeAlias( Alias alias ) {

        alias.setOwner( synchronize( alias.getOwner() ) );
        alias.setCvAliasType( synchronize( alias.getCvAliasType() ) );
        return alias;
    }

    private Annotation synchronizeAnnotation( Annotation annotation ) {

        annotation.setOwner( synchronize( annotation.getOwner() ) );
        annotation.setCvTopic( synchronize( annotation.getCvTopic() ) );
        return annotation;
    }

}