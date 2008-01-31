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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.persistence.dao.BaseDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.util.DebugUtil;

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

    private BiMap<Key, AnnotatedObject> annotatedObjectsToPersist;
    private Map<Key, AnnotatedObject> annotatedObjectsToMerge;
    private Map<Key, AnnotatedObject> synched;

    private Finder finder;
    private KeyBuilder keyBuilder;
    private EntityStateCopier entityStateCopier;

    /**
     * When true, if an annotated object that do not have an AC has an equivalent
     * with AC in the database, it will try to update the one in the database.
     * If false, it will ignore any difference and use the one from the database.     *
     */
    private boolean updateWithoutAcEnabled;

    private PersisterStatistics statistics;

    public CorePersister() {

        annotatedObjectsToPersist = new HashBiMap<Key, AnnotatedObject>();
        annotatedObjectsToMerge = new HashMap<Key, AnnotatedObject>();
        synched = new HashMap<Key, AnnotatedObject>();

        finder = new DefaultFinder();
        keyBuilder = new KeyBuilder();
        entityStateCopier = new DefaultEntityStateCopier();

        statistics = new PersisterStatistics();
    }

    ////////////////////////////
    // Strategy configuration

    public void setEntityStateCopier( EntityStateCopier entityStateCopier ) {
        if ( entityStateCopier == null ) {
            throw new IllegalArgumentException( "You must give a non null EntityStateCopier" );
        }
        this.entityStateCopier = entityStateCopier;
    }

    public void setFinder( Finder finder ) {
        if ( finder == null ) {
            throw new IllegalArgumentException( "You must give a non null Finder" );
        }
        this.finder = finder;
    }

    public void setKeyBuilder( KeyBuilder keyBuilder ) {
        if ( keyBuilder == null ) {
            throw new IllegalArgumentException( "You must give a non null KeyBuilder" );
        }
        this.keyBuilder = keyBuilder;
    }

    ////////////////////////
    // Implement Persister

    public void saveOrUpdate( AnnotatedObject ao ) {
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        boolean inTransaction = IntactContext.getCurrentInstance().getDataContext().isTransactionActive();

        if ( !inTransaction ) IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        boolean originalAutoFlush = dataContext.getDaoFactory().getDataConfig().isAutoFlush();
        dataContext.getDaoFactory().getDataConfig().setAutoFlush(false);

        try {
            synchronize( ao );
            commit();
        } finally {
           dataContext.getDaoFactory().getDataConfig().setAutoFlush(originalAutoFlush); 
        }

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

        Class<T> aoClass = ( Class<T> ) ao.getClass();

        final Key key = keyBuilder.keyFor( ao );

        if ( key == null ) {
            throw new IllegalArgumentException( "Cannot handle null key" );
        }

        if ( synched.containsKey( key ) ) {
            final T synchedAo = (T) synched.get(key);

            if (synchedAo == null) {
                throw new IllegalStateException("The synchronized cache was expected to return an non-null object for: "+ao);
            }

            // check if the synchronized AO and the provided AO are the same instance. If not, it should be
            // considered a duplicate (the provide AO has an equivalent synchronized AO).
            if (ao != synchedAo) {
                if (log.isDebugEnabled() && !(ao instanceof CvObject) && !(ao instanceof Institution)) {
                    log.debug("Duplicated "+ao.getClass().getSimpleName()+": [new:["+ao+"]] duplicates [synch:["+synchedAo+"]]");
                }

                statistics.addDuplicate(ao);
            }

            ao = synchedAo;

            verifyExpectedType(ao, aoClass);
            return ao;
        }

        synched.put( key, ao );

        if ( ao.getAc() == null ) {

            // the object is new
            final String ac = finder.findAc( ao );

            if ( ac == null ) {

                if (log.isTraceEnabled()) log.trace("New "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: PERSIST");

                // doesn't exist in the database, we will persist it
                annotatedObjectsToPersist.put( key, ao );

                synchronizeChildren( ao );

            } else {
                if (isUpdateWithoutAcEnabled()) {
                    if (log.isTraceEnabled()) log.trace("New (but found in database: "+ ac +") "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: UPDATE");

                    // object exists in the database, we will update it
                    final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                    final AnnotatedObjectDao<T> dao = daoFactory.getAnnotatedObjectDao( ( Class<T> ) ao.getClass() );
                    final T managedObject = dao.getByAc( ac );

                    if ( managedObject == null ) {
                        throw new IllegalStateException( "No managed object found with ac '" + ac + "' and type '" + ao.getClass() + "' and one was expected" );
                    }

                    // warn if an instance for this interaction is found in the database, as it could be a duplicate
                    warnIfInteractionDuplicate( ao, managedObject );

                    // updated the managed object based on ao's properties, but only add it to merge
                    // if something has been copied (it was not a duplicate)
                    boolean copied = entityStateCopier.copy( ao, managedObject );

                    // this will allow to reload the AO by its AC after flushing
                    ao.setAc(managedObject.getAc());

                    // traverse annotatedObject's properties and assign AC where appropriate
                    copyAnnotatedObjectAttributeAcs(managedObject, ao);

                    if (copied) {
                        statistics.addMerged(managedObject);

                        // synchronize the children
                        synchronizeChildren(managedObject);

                    } else {
                       statistics.addDuplicate(ao);
                    }
                } else {
                    if (log.isTraceEnabled()) log.trace("New (but found in database: "+ ac +") "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: IGNORE");
                    statistics.addDuplicate(ao);

                    ao.setAc(ac);
                }

            }

        } else {

            final BaseDao baseDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao();
            if ( baseDao.isTransient( ao )) {

                if (log.isTraceEnabled()) log.trace("Transient "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: SYNCHRONIZE-REFRESH");

                // transient object: that is not attached to the session
                //ao = transientObjectHandler.handle( ao );

                statistics.addTransient(ao);

                // object exists in the database, we will update it
                final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                final AnnotatedObjectDao<T> dao = daoFactory.getAnnotatedObjectDao( ( Class<T> ) ao.getClass() );
                final T managedObject = dao.getByAc( ao.getAc() );

                // updated the managed object based on ao's properties, but only add it to merge
                // if something has been copied (it was not a duplicate)
                try {
                    boolean copied = entityStateCopier.copy( ao, managedObject );

                    // this will allow to reload the AO by its AC after flushing
                    ao.setAc(managedObject.getAc());

                    // traverse annotatedObject's properties and assign AC where appropriate
                    copyAnnotatedObjectAttributeAcs(managedObject, ao);

                    // and the created info, so the merge does not fail due to missing created data
                    ao.setCreated(managedObject.getCreated());
                    ao.setCreator(managedObject.getCreator());

                    if (copied) {
                        statistics.addMerged(managedObject);
                        synchronizeChildren( managedObject );
                    } 
                } catch (LazyInitializationException e) {
                    log.warn("Could not copy the state from the annotated object to the transient object. Any modifications to the transient object will be lost: "+ao.getShortLabel()+" ("+ao.getAc()+")");
                    ao = managedObject;
                } catch (PersisterException e) {
                    log.warn("Could not copy the state from the annotated object to the transient object. Any modifications to the transient object will be lost: "+ao.getShortLabel()+" ("+ao.getAc()+")");
                    ao = managedObject;
                }

            } else {
                if (log.isTraceEnabled()) log.trace("Managed "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: IGNORE");

                // managed object
                // This has been commented out, because synchronizing the children might start a synchronization of the whole database
                //synchronizeChildren( ao );
            }
        }

        // check if the object class after synchronization is the same as in the beginning
        verifyExpectedType( ao, aoClass );

        // add the key after the synchronization to the synched map too
        // it the object now has an AC, the key is the AC
        Key keyAfter = keyBuilder.keyFor(ao);

        if (!key.equals(keyAfter)) {
            if (ao.getAc() == null) {
                log.warn(ao.getClass().getSimpleName()+" without AC changed its synchronization Key: "+key+" -> "+keyAfter);
            }

            synched.put(keyAfter, ao);
        }

        return ao;
    }

    private <T extends AnnotatedObject> void copyAnnotatedObjectAttributeAcs( T source, T target ) {

        Collection<Xref> xrefsToAdd = new ArrayList<Xref>( );
        for ( Iterator itXrefTarget = target.getXrefs().iterator(); itXrefTarget.hasNext(); ) {
            Xref targetXref = (Xref) itXrefTarget.next();

            for ( Iterator itXrefSrc = source.getXrefs().iterator(); itXrefSrc.hasNext(); ) {
                Xref sourceXref = ( Xref ) itXrefSrc.next();

                if( EqualsUtils.sameXref( sourceXref, targetXref ) ) {
                    // replace Xref of the target and store the managed one so it can be added later
                    itXrefTarget.remove();
                    xrefsToAdd.add( sourceXref );

                    // go to next target xref
                    break;
                }
            }
        }
        for ( Xref xref : xrefsToAdd ) {
            target.addXref( xref );
        }


        Collection<Alias> aliasesToAdd = new ArrayList<Alias>( );
        for ( Iterator itAliasTarget = target.getAliases().iterator(); itAliasTarget.hasNext(); ) {
            Alias targetAlias = (Alias) itAliasTarget.next();

            for ( Iterator itAliasSrc = source.getAliases().iterator(); itAliasSrc.hasNext(); ) {
                Alias sourceAlias = ( Alias ) itAliasSrc.next();

                if( EqualsUtils.sameAlias( sourceAlias, targetAlias ) ) {
                    // replaces Alias of the target and store the managed one so it can be added later
                    itAliasTarget.remove();
                    aliasesToAdd.add( sourceAlias );

                    // go to next target xref
                    break;
                }
            }
        }
        for ( Alias alias : aliasesToAdd ) {
            target.addAlias( alias );
        }


        Collection<Annotation> annotToAdd = new ArrayList<Annotation>( );
        for ( Iterator itAnnotTarget = target.getAnnotations().iterator(); itAnnotTarget.hasNext(); ) {
            Annotation targetAnnot = (Annotation) itAnnotTarget.next();

            for ( Iterator itAnnotSrc = source.getAnnotations().iterator(); itAnnotSrc.hasNext(); ) {
                Annotation sourceAnnot = ( Annotation ) itAnnotSrc.next();

                if( EqualsUtils.sameAnnotation( sourceAnnot, targetAnnot ) ) {
                    // replaces Alias of the target and store the managed one so it can be added later
                    itAnnotTarget.remove();
                    annotToAdd.add( sourceAnnot );

                    // go to next target xref
                    break;
                }
            }
        }
        for ( Annotation annotation : annotToAdd ) {
            target.addAnnotation( annotation );
        }
    }

    private <T extends AnnotatedObject> void warnIfInteractionDuplicate( T ao, T managedObject ) {
        if ( log.isWarnEnabled() && ao instanceof Interaction ) {
            Interaction newInteraction = ( Interaction ) ao;
            Interaction existingInteraction = ( Interaction ) managedObject;
            String newImexId = InteractionUtils.getImexIdentifier( newInteraction );
            String existingImexId = InteractionUtils.getImexIdentifier( existingInteraction );
            log.warn( "An AC already exists for this interaction. Possibly a duplicate? : Existing [" + managedObject.getAc() + ", " + managedObject.getShortLabel() + ", " + existingImexId + "] - " +
                      "New [-, " + ao.getShortLabel() + ", " + newImexId + "]. The existing interaction will be updated" );
        }
    }

    private <T extends AnnotatedObject> void verifyExpectedType( T ao, Class<T> aoClass ) {
        if ( !( aoClass.isAssignableFrom( ao.getClass() ) || ao.getClass().isAssignableFrom( aoClass ) ) ) {
            throw new IllegalArgumentException( "Wrong type returned after synchronization. Expected " + aoClass.getName() + " but found " +
                                                ao.getClass().getName() + ". The offender was: " + ao );
        }
    }

    /**
     * The reload method has the mission to syncronize the state of the passed annotated object
     * with the database. It is similar to the EntityManager.refresh() method but it can also
     * synchronize the state in non-managed entities by copying the state from the corresponding
     * entity from the database. If it has no AC (this happens if there are duplicates within the
     * same transaction), an AC will be found from the database.
     * @param ao The annotated object to refresh
     */
    protected void reload( AnnotatedObject ao ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        // otherwise, copy the state to the ao from the equivalent object in the db
        if (ao.getAc() == null) {
            if (log.isDebugEnabled()) {
                log.debug("Trying to reload " + ao.getClass().getSimpleName() + " without AC. Probably a duplicate: " + ao);
            }

            final String ac = finder.findAc(ao);

            if (ac == null) {
                throw new PersisterException(ao.getClass().getSimpleName() + " without AC couldn't be reloaded because " +
                        "no equivalent object was found in the database: " + ao);
            }

            if (log.isDebugEnabled()) log.debug("\tFound AC: " + ac);

            ao.setAc(ac);
        }

        AnnotatedObjectDao<?> dao = daoFactory.getAnnotatedObjectDao(ao.getClass());

        AnnotatedObject dbObject = dao.getByAc(ao.getAc());

        // copy the state from the managed object to the ao
        entityStateCopier.copy(dbObject, ao);

        if (ao instanceof InteractionImpl) {
            ((InteractionImpl)ao).calculateCrc();
        }
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
                log.debug( "\tAbout to persist " + DebugUtil.annotatedObjectToString(ao, true) +" - Key: "+annotatedObjectsToPersist.inverse().get(ao));
            }

            // this may happen if there is a cascade on this object from the parent
            // exception: features are persisted by cascade from the component, so they can be ignored
            if ( log.isWarnEnabled() && ao.getAc() != null && !(ao instanceof Feature) ) {
                log.warn( "Object to persist should NOT have an AC: " + DebugUtil.annotatedObjectToString(ao, true) );
            } else {

                daoFactory.getBaseDao().persist( ao );
                statistics.addPersisted(ao);
                logPersistence( ao );
            }
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Merging objects..." );
        }

        // Order the collection of objects to persist: institution, cvs, others
        List<AnnotatedObject> thingsToMerge = new ArrayList<AnnotatedObject>( annotatedObjectsToMerge.values() );
        Collections.sort( thingsToMerge, new PersistenceOrderComparator() );
        for ( AnnotatedObject ao : annotatedObjectsToMerge.values() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\tAbout to merge " + DebugUtil.annotatedObjectToString(ao, true) );
            }

            if ( ao.getAc() == null ) {
                throw new IllegalStateException( "Object to persist should have an AC: " + DebugUtil.annotatedObjectToString(ao, true));
            } else {
                daoFactory.getBaseDao().merge( ao );
                statistics.addMerged(ao);
                logPersistence( ao );
            }
        }

        try {
            log.debug( "Invoking an EntityManager flush..." );
            daoFactory.getEntityManager().flush();
        } catch ( Throwable t ) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception when flushing the Persister, which contained: \n");
            sb.append(statistics).append("\n");
            sb.append("Persisted entities: ").append(statistics.getPersistedMap().values()).append("\n");
            sb.append("Merged entities: ").append(statistics.getMergedMap().values()).append("\n");
            sb.append("Transient entities: ").append(statistics.getTransientMap().values()).append("\n");
            throw new PersisterException( sb.toString(), t );
        } finally {
            annotatedObjectsToMerge.clear();
            annotatedObjectsToPersist.clear();
            synched.clear();
        }
    }

    private static void logPersistence( AnnotatedObject<?, ?> ao ) {
        if ( log.isTraceEnabled() ) {
            log.trace( "\t\t\tPersisted with AC: " + ao.getAc() );

            if ( !ao.getXrefs().isEmpty() ) {
                log.trace( "\t\t\tXrefs: " + ao.getXrefs().size() );

                for ( Xref xref : ao.getXrefs() ) {
                    log.trace( "\t\t\t\t" + xref );
                }
            }

            if ( !ao.getAliases().isEmpty() ) {
                log.trace( "\t\t\tAliases: " + ao.getAliases().size() );

                for ( Alias alias : ao.getAliases() ) {
                    log.trace( "\t\t\t\t" + alias );
                }
            }

            if ( !ao.getAnnotations().isEmpty() ) {
                log.trace( "\t\t\tAnnotations: " + ao.getAnnotations().size() );

                for ( Annotation annot : ao.getAnnotations() ) {
                    log.trace( "\t\t\t\t" + annot );
                }
            }
        }
    }

    public PersisterStatistics getStatistics() {
        return statistics;
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
        interaction.setConfidences( synchronizeConfidences( interaction.getConfidences(), interaction ));

        synchronizeAnnotatedObjectCommons( interaction );
    }

    private Collection<Confidence> synchronizeConfidences( Collection<Confidence> confidencesToSynchronize, Interaction parentInteraction ) {
        List<Confidence> confidences = new ArrayList<Confidence>(confidencesToSynchronize.size());

        for ( Confidence confidence : confidencesToSynchronize ) {
             if (confidence.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getConfidenceDao().isTransient(confidence)) {
                  confidence = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getConfidenceDao().getByAc(confidence.getAc());
             }

            confidence.setCvConfidenceType( synchronize (confidence.getCvConfidenceType()));
            confidence.setInteraction((InteractionImpl)parentInteraction);

            synchronizeBasicObjectCommons(confidence);

            confidences.add(confidence);
        }

        return confidences;

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
        feature.setRanges( synchronizeRanges( feature.getRanges(), feature ));

        synchronizeAnnotatedObjectCommons( feature );
    }

    private Collection<Range> synchronizeRanges( Collection<Range> rangesToSychronize, Feature parentFeature ) {
        List<Range> ranges = new ArrayList<Range>(rangesToSychronize.size());

        for ( Range range : rangesToSychronize ) {
             if (range.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getRangeDao().isTransient(range)) {
                  range = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getRangeDao().getByAc(range.getAc());
             }

            range.setFromCvFuzzyType( synchronize( range.getFromCvFuzzyType() ) );
            range.setToCvFuzzyType( synchronize( range.getToCvFuzzyType() ) );

            synchronizeBasicObjectCommons(range);

            range.setFeature(parentFeature);

            ranges.add(range);
        }

        return ranges;

    }

    private void synchronizeCvObject( CvObject cvObject ) {
        if (cvObject instanceof CvDagObject) {
            CvDagObject cvDagObject = (CvDagObject)cvObject;
            cvDagObject.setChildren(synchronizeCollection(cvDagObject.getChildren()));
            cvDagObject.setParents(synchronizeCollection(cvDagObject.getParents()));
        }

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

        Collection synchedXrefs = new ArrayList( ao.getXrefs().size() );
        for ( Xref xref : ao.getXrefs() ) {
            synchedXrefs.add( synchronizeXref( xref, ao ) );
        }
        ao.setXrefs( synchedXrefs );

        Collection synchedAliases = new ArrayList( ao.getAliases().size() );
        for ( Alias alias : ao.getAliases() ) {
            synchedAliases.add( synchronizeAlias( alias, ao ) );
        }
        ao.setAliases( synchedAliases );

        Collection synchedAnnotations = new ArrayList( ao.getAnnotations().size() );
        for ( Annotation annotation : ao.getAnnotations() ) {
            synchedAnnotations.add( synchronizeAnnotation( annotation ) );
        }
        ao.setAnnotations( synchedAnnotations );

        synchronizeBasicObjectCommons(ao);
    }

    private void synchronizeBasicObjectCommons (BasicObject bo) {
        if ( !( bo instanceof Institution ) ) {
            bo.setOwner( synchronize( bo.getOwner() ) );
        }
    }

    private Xref synchronizeXref( Xref xref, AnnotatedObject parent ) {
        if (xref.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getXrefDao().getByAc(xref.getAc());
        }

        xref.setCvDatabase( synchronize( xref.getCvDatabase() ) );
        xref.setCvXrefQualifier( synchronize( xref.getCvXrefQualifier() ) );
        xref.setParent(parent);

        synchronizeBasicObjectCommons( xref );

        return xref;
    }

    private Alias synchronizeAlias( Alias alias, AnnotatedObject parent ) {
        if (alias.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getAliasDao().getByAc(alias.getAc());
        }

        alias.setCvAliasType( synchronize( alias.getCvAliasType() ) );
        alias.setParent(parent);

        synchronizeBasicObjectCommons( alias );

        return alias;
    }

    private Annotation synchronizeAnnotation( Annotation annotation ) {
        if (annotation.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getAnnotationDao().getByAc(annotation.getAc());
        }
        annotation.setCvTopic( synchronize( annotation.getCvTopic() ) );

        synchronizeBasicObjectCommons( annotation );

        return annotation;
    }

    public boolean isUpdateWithoutAcEnabled() {
        return updateWithoutAcEnabled;
    }

    public void setUpdateWithoutAcEnabled(boolean updateWithoutAcEnabled) {
        this.updateWithoutAcEnabled = updateWithoutAcEnabled;
    }

}