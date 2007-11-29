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
public class CorePersister implements Persister {

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

    protected AnnotatedObject synchronize( AnnotatedObject ao ) {

        if ( ao == null ) {
            return null;
        }

        final Key key = keyBuilder.keyFor( ao );

        if ( key == null ) {
            throw new IllegalArgumentException( "Cannot handle null key" );
        }

        if ( synched.containsKey( key ) ) {
            return synched.get( key );
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
                final AnnotatedObjectDao<? extends AnnotatedObject> dao = daoFactory.getAnnotatedObjectDao( ao.getClass() );
                final AnnotatedObject managedObject = dao.getByAc( ac );

                // updated the managed object based on ao's properties
                entityStateCopier.copy( ao, managedObject );

                // this will allow to reload the AO by its AC after flushing
                ao.setAc( managedObject.getAc() );

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

        return ao;
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

        annotatedObjectsToMerge.clear();
        annotatedObjectsToPersist.clear();
        daoFactory.getEntityManager().flush();

        synched.clear();
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
        verifyObjectToSynchronize(Experiment.class, experiment);

        experiment.setPublication( ( Publication ) synchronize( experiment.getPublication() ) );
        experiment.setInteractions( synchronizeCollection( experiment.getInteractions() ) );
        experiment.setCvIdentification( ( CvIdentification ) synchronize( experiment.getCvIdentification() ) );
        experiment.setCvInteraction( ( CvInteraction ) synchronize( experiment.getCvInteraction() ) );
        experiment.setBioSource( ( BioSource ) synchronize( experiment.getBioSource() ) );
        synchronizeAnnotatedObjectCommons( experiment );
    }

    private void synchronizeInteraction( Interaction interaction ) {
        verifyObjectToSynchronize(Interaction.class, interaction);

        interaction.setCvInteractionType( ( CvInteractionType ) synchronize( interaction.getCvInteractionType() ) );
        interaction.setCvInteractorType( ( CvInteractorType ) synchronize( interaction.getCvInteractorType() ) );
        interaction.setComponents( synchronizeCollection( interaction.getComponents() ) );
        interaction.setBioSource( ( BioSource ) synchronize( interaction.getBioSource() ) );
        interaction.setExperiments( synchronizeCollection( interaction.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( interaction );
    }

    private void synchronizeInteractor( Interactor interactor ) {
        verifyObjectToSynchronize(Interactor.class, interactor);

        interactor.setActiveInstances( synchronizeCollection( interactor.getActiveInstances() ) );
        interactor.setBioSource( ( BioSource ) synchronize( interactor.getBioSource() ) );
        interactor.setCvInteractorType( ( CvInteractorType ) synchronize( interactor.getCvInteractorType() ) );
        synchronizeAnnotatedObjectCommons( interactor );
    }

    private void synchronizeBioSource( BioSource bioSource ) {
        verifyObjectToSynchronize(BioSource.class, bioSource);

        bioSource.setCvCellType( ( CvCellType ) synchronize( bioSource.getCvCellType() ) );
        bioSource.setCvTissue( ( CvTissue ) synchronize( bioSource.getCvTissue() ) );
        synchronizeAnnotatedObjectCommons( bioSource );
    }

    private void synchronizeComponent( Component component ) {
        verifyObjectToSynchronize(Component.class, component);

        component.setBindingDomains( synchronizeCollection( component.getBindingDomains() ) );
        component.setCvBiologicalRole( ( CvBiologicalRole ) synchronize( component.getCvBiologicalRole() ) );
        component.setCvExperimentalRole( ( CvExperimentalRole ) synchronize( component.getCvExperimentalRole() ) );
        component.setExpressedIn( ( BioSource ) synchronize( component.getExpressedIn() ) );
        component.setInteraction( ( Interaction ) synchronize( component.getInteraction() ) );
        component.setInteractor( ( Interactor ) synchronize( component.getInteractor() ) );
        component.setParticipantDetectionMethods( synchronizeCollection( component.getParticipantDetectionMethods() ) );
        component.setExperimentalPreparations( synchronizeCollection( component.getExperimentalPreparations() ) );
        synchronizeAnnotatedObjectCommons( component );
    }

    private void synchronizeFeature( Feature feature ) {
        verifyObjectToSynchronize(Feature.class, feature);

        feature.setBoundDomain( ( Feature ) synchronize( feature.getBoundDomain() ) );
        feature.setComponent( ( Component ) synchronize( feature.getComponent() ) );
        feature.setCvFeatureIdentification( ( CvFeatureIdentification ) synchronize( feature.getCvFeatureIdentification() ) );
        feature.setCvFeatureType( ( CvFeatureType ) synchronize( feature.getCvFeatureType() ) );
        for ( Range range : feature.getRanges() ) {
            synchronizeRange( range );
        }
        synchronizeAnnotatedObjectCommons( feature );
    }

    private void synchronizeRange( Range range ) {
        verifyObjectToSynchronize(Range.class, range);

        range.setFromCvFuzzyType( ( CvFuzzyType ) synchronize( range.getFromCvFuzzyType() ) );
        range.setToCvFuzzyType( ( CvFuzzyType ) synchronize( range.getToCvFuzzyType() ) );
    }

    private void synchronizeCvObject( CvObject cvObject ) {
        verifyObjectToSynchronize(CvObject.class, cvObject);

        // TODO handle parents and children in case the instance is a CvDagObject

        synchronizeAnnotatedObjectCommons( cvObject );
    }

    private void synchronizePublication( Publication publication ) {
        verifyObjectToSynchronize(Publication.class, publication);

        publication.setExperiments( synchronizeCollection( publication.getExperiments() ) );
        synchronizeAnnotatedObjectCommons( publication );
    }

    private void synchronizeInstitution( Institution institution ) {
        verifyObjectToSynchronize(Institution.class, institution);

        synchronizeAnnotatedObjectCommons( institution );
    }

    private <X extends AnnotatedObject> Collection<X> synchronizeCollection( Collection<X> collection ) {
        Collection<X> synchedCollection = new ArrayList<X>( collection.size() );
        for ( X ao : collection ) {
            synchedCollection.add( ( X ) synchronize( ao ) );
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
        verifyObjectToSynchronize(Xref.class, xref);

        xref.setOwner( ( Institution ) synchronize( xref.getOwner() ) );
        xref.setCvDatabase( ( CvDatabase ) synchronize( xref.getCvDatabase() ) );
        xref.setCvXrefQualifier( ( CvXrefQualifier ) synchronize( xref.getCvXrefQualifier() ) );
        return xref;
    }

    private Alias synchronizeAlias( Alias alias ) {
        verifyObjectToSynchronize(Alias.class, alias);

        alias.setOwner( ( Institution ) synchronize( alias.getOwner() ) );
        alias.setCvAliasType( ( CvAliasType ) synchronize( alias.getCvAliasType() ) );
        return alias;
    }

    private Annotation synchronizeAnnotation( Annotation annotation ) {
        verifyObjectToSynchronize(Annotation.class, annotation);

        annotation.setOwner( ( Institution ) synchronize( annotation.getOwner() ) );
        annotation.setCvTopic( ( CvTopic ) synchronize( annotation.getCvTopic() ) );
        return annotation;
    }

    private <T extends IntactObject> void verifyObjectToSynchronize(Class<T> expectedType, T objToSynchronize) {
        if (!expectedType.isAssignableFrom(objToSynchronize.getClass())) {
            throw new IllegalArgumentException("Wrong type passed to synchronize. Expected "+expectedType.getName()+" but found "+
            objToSynchronize.getClass().getName()+". The offender was: "+objToSynchronize);
        }
    }
}