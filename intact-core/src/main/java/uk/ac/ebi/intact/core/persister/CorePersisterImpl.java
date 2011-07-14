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

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.hibernate.TransientObjectException;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.Status;
import org.hibernate.impl.SessionImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.IntactFlushMode;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.BaseDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import javax.persistence.FlushModeType;
import java.util.*;

/**
 * Persists intact object in the database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class CorePersisterImpl implements CorePersister {

    private static final Log log = LogFactory.getLog( CorePersisterImpl.class );

    private DataContext dataContext;
    private Finder finder;

    private Map<Key, IntactObject> annotatedObjectsToPersist;
    private Map<Key, AnnotatedObject> annotatedObjectsToMerge;
    private Map<Key, IntactObject> synched;

    private KeyBuilder keyBuilder;
    private EntityStateCopier entityStateCopier;


    /**
     * When true, if an annotated object that do not have an AC has an equivalent
     * with AC in the database, it will try to update the one in the database.
     * If false, it will ignore any difference and use the one from the database.     *
     */
    private boolean updateWithoutAcEnabled;

    /**
     * If false, no statistics are gathered (recommended for production or massive persistences
     */
    private boolean statisticsEnabled = true;

    private PersisterStatistics statistics;

    public CorePersisterImpl() {
        this(IntactContext.getCurrentInstance(), (Finder)
                IntactContext.getCurrentInstance().getSpringContext().getBean("finder"));
    }

    public CorePersisterImpl(IntactContext intactContext, Finder finder) {
        this.dataContext = intactContext.getDataContext();
        this.finder = finder;

        annotatedObjectsToPersist = Maps.newHashMap();
        annotatedObjectsToMerge = Maps.newHashMap();
        synched = Maps.newHashMap();

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

    @Transactional
    @IntactFlushMode(FlushModeType.COMMIT)
    public PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException {
        for (AnnotatedObject ao : annotatedObjects) {
            if (log.isDebugEnabled()) log.debug("Saving: "+DebugUtil.annotatedObjectToString(ao, false));
            synchronize(ao);
        }

        commit();

        // we reload the annotated objects by its AC
        // note: if an object does not have one, it is probably a duplicate
        for ( AnnotatedObject ao : annotatedObjects ) {
            reload( ao );
        }

        if (log.isDebugEnabled()) log.debug(statistics);

        return statistics;
    }

    @Transactional
    public PersisterStatistics saveOrUpdate( AnnotatedObject ao ) {
        if (log.isDebugEnabled()) log.debug("Saving: "+DebugUtil.annotatedObjectToString( ao, false ));

        dataContext.getDaoFactory().getEntityManager().setFlushMode(FlushModeType.COMMIT);
        //dataContext.getDaoFactory().getDataConfig().setAutoFlush(false);

        try {
            synchronize( ao );
            commit();
        } finally {
            dataContext.getDaoFactory().getEntityManager().setFlushMode(FlushModeType.AUTO);
        }

        reload( ao );

        return statistics;
    }

    public PersisterStatistics saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException {
        for ( IntactEntry intactEntry : intactEntries ) {
            // Very important to not rely on the collection of interaction of the intactEntry. The core persister is updating the list of interactions
            // of an experiment so if the collection of interactions of this intactEntry is the collection of interactions of the experiment, we can have
            // concurrent modification. Indeed, this problem didn't exist when setting the list of experiments in the core persister instead of clear and addAll.
            Collection<Interaction> interactionsToSave = new ArrayList<Interaction>(intactEntry.getInteractions());

            for ( Interaction interaction : interactionsToSave ) {
                saveOrUpdate( interaction );
            }
        }

        return statistics;
    }

    @Transactional
    public void saveOrUpdate( User... users ) {
        try {
            dataContext.getDaoFactory().getEntityManager().setFlushMode(FlushModeType.COMMIT);
            for ( User user : users ) {
                // roles
                Collection<Role> rolesToRemove = new ArrayList<Role>();
                Collection<Role> rolesToAdd = new ArrayList<Role>();
                for ( Role role : user.getRoles() ) {
                    if( role.getAc() == null ) {
                        final String ac = finder.findAc( role );
                        if( ac != null ) {
                            Role reloadedRole = dataContext.getDaoFactory().getRoleDao().getByAc( ac );
                            rolesToRemove.add( role );
                            rolesToAdd.add( reloadedRole );
                        } else {
                            dataContext.getDaoFactory().getRoleDao().persist( role );
                        }
                    } else {
                        // if the object is not managed, replace it
                        Role reloadedRole = dataContext.getDaoFactory().getRoleDao().getByAc( role.getAc() );
                        rolesToRemove.add( role );
                        rolesToAdd.add( reloadedRole );
                    }
                }
                user.getRoles().removeAll( rolesToRemove );
                user.getRoles().addAll( rolesToAdd );

                // preferences
                for ( Preference preference : user.getPreferences() ) {
                    if( preference.getAc() == null ) {
                        dataContext.getDaoFactory().getPreferenceDao().persist( preference );
                    }
                }

                // user
                final UserDao userDao = dataContext.getDaoFactory().getUserDao();
                if( user.getAc() == null ) {
                    userDao.persist( user );
                } else {
                    userDao.update( user );
                }
            }
        } finally {
            dataContext.getDaoFactory().getEntityManager().setFlushMode(FlushModeType.AUTO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PersisterStatistics saveOrUpdateInNewTransaction(AnnotatedObject... annotatedObjects ) throws PersisterException {
        return saveOrUpdate(annotatedObjects);
    }

    public <T extends AnnotatedObject> T synchronize( T ao ) {

        if ( ao == null ) {
            return null;
        }

        if (!IntactCore.isInitialized(ao)) {
            return ao;
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
            if (ao != synchedAo &&
                    !(ao instanceof Component) &&
                    !(ao instanceof CvObject) &&
                    !(ao instanceof Institution)) {
                if (log.isDebugEnabled()) {
                    log.debug("Duplicated "+ao.getClass().getSimpleName()+": [new:["+ao+"]] duplicates [synch:["+synchedAo+"]]");
                }

                if (statisticsEnabled) statistics.addDuplicate(ao);
            }

            ao = synchedAo;

            verifyExpectedType(ao, aoClass);
            return ao;
        }

        synched.put( key, ao );

        if ( ao.getAc() == null || ao.getAc().length() == 0) {

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

                    // initialize collections for the managedObject object
                    initializeCommonsIfNecessary(ao, managedObject);

                    // updated the managed object based on ao's properties, but only add it to merge
                    // if something has been copied (it was not a duplicate)
                    boolean copied = entityStateCopier.copy( ao, managedObject);

                    // this will allow to reload the AO by its AC after flushing
                    ao.setAc(managedObject.getAc());

                    // traverse annotatedObject's properties and assign AC where appropriate
                    copyAnnotatedObjectAttributeAcs(managedObject, ao);

                    if (copied) {
                        if (statisticsEnabled) statistics.addMerged(managedObject);

                        annotatedObjectsToMerge.put(key, managedObject);

                        // synchronize the children
                        synchronizeChildren(managedObject);

                    } else {
                        if (statisticsEnabled) statistics.addDuplicate(ao);
                    }
                } else {
                    if (log.isTraceEnabled()) log.trace("New (but found in database: "+ ac +") "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: IGNORE");
                    if (statisticsEnabled) statistics.addDuplicate(ao);

                    ao.setAc(ac);
                }

            }

        } else {

            final BaseDao baseDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao();

            if ( baseDao.isTransient( ao )) {

                if (!(ao instanceof Institution)) {  // ignore transient insititutions

                    if (log.isTraceEnabled()) log.trace("Transient "+ao.getClass().getSimpleName()+": "+ao.getAc()+" "+ao.getShortLabel()+" - Decision: SYNCHRONIZE-REFRESH");

                    if (statisticsEnabled) statistics.addTransient(ao);

                    // object exists in the database, we will update it
                    final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                    final AnnotatedObjectDao<T> dao = daoFactory.getAnnotatedObjectDao( ( Class<T> ) ao.getClass() );

                    String ac = ao.getAc();

                    final T managedObject = dao.getByAc(ac);

                    if (managedObject == null) {
                        throw new IllegalStateException(ao.getClass().getSimpleName()+" is transient, but no object with the same AC could be found in database: "+ao.getAc()+" ("+ao.getShortLabel()+")");
                    }

                    // initialize collections for managed object
                    initializeCommonsIfNecessary(ao, managedObject);

                    // updated the managed object based on ao's properties, but only add it to merge
                    // if something has been copied (it was not a duplicate)
                    try {
                        boolean copied = false;
                        try {
                            copied = entityStateCopier.copy( ao, managedObject);
                        } catch (Exception e) {
                            throw new PersisterException("Problem copying state to managed object of type "+ao.getClass().getSimpleName()+" and AC "+ao.getAc()+", from object: "+ao, e);
                        }

                        // this will allow to reload the AO by its AC after flushing
                        ao.setAc(ac);

                        // traverse annotatedObject's properties and assign AC where appropriate
                        try {
                            copyAnnotatedObjectAttributeAcs(managedObject, ao);
                        } catch (Exception e) {
                            throw new PersisterException("Problem copying ACs from managed object of type "+ao.getClass().getSimpleName()+" and AC "+ao.getAc()+", to object: "+ao, e);
                        }

                        // and the created info, so the merge does not fail due to missing created data
                        ao.setCreated(managedObject.getCreated());
                        ao.setCreator(managedObject.getCreator());


                        if (copied) {
                            statistics.addMerged(managedObject);
                            annotatedObjectsToMerge.put(keyBuilder.keyFor(ao),ao);
                        }

                        // synchronize aliases, xrefs, annotations...
                        synchronizeChildren( ao );


                    } catch (LazyInitializationException e) {
                        log.warn("Could not copy the state from the annotated object to the transient object. Any modifications to the transient object will be lost: "+ao.getShortLabel()+" ("+ao.getAc()+")");
                        ao = managedObject;
                    } catch (Throwable e) {
                        log.error("Could not copy the state from the annotated object to the transient object. Any modifications to the transient object will be lost: "+ao.getShortLabel()+" ("+ao.getAc()+").", e);
                        ao = managedObject;
                    }
                }

            } else {
                if (log.isTraceEnabled()) log.trace("Managed "+ao.getClass().getSimpleName()+": "+ao.getShortLabel()+" - Decision: IGNORE");

                // managed object but can update/persist children if necessary and initialized. Create any children of this object if necessary but do not synchronize annotated attributes as it will be done by hibernate
                synchronizeChildrenButNotAnnotatedAttributes(ao);
            }
        }

        if (ao == null) {
            throw new IllegalStateException("Annotated object is null");
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

    private <T extends AnnotatedObject> void initializeCommonsIfNecessary(T ao, T managedObject) {
        if (IntactCore.isInitialized(ao.getXrefs()))
            IntactCore.initialize(managedObject.getXrefs());

        if (IntactCore.isInitialized(ao.getAnnotations()))
            IntactCore.initialize(managedObject.getAnnotations());

        if (IntactCore.isInitialized(ao.getAliases())) {
            IntactCore.initialize(managedObject.getAliases());
        }
    }

    private <T extends AnnotatedObject> void copyAnnotatedObjectAttributeAcs( T source, T target ) {
        if (IntactCore.isInitialized(target.getXrefs())) {

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

        }

        if (IntactCore.isInitialized(target.getAliases())) {

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

        }

        if (IntactCore.isInitialized(target.getAnnotations())) {

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
    public void reload( AnnotatedObject ao ) {
        if (ao == null) return;

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
        if (dbObject != null) {
            entityStateCopier.copy(dbObject, ao);
        }

        if (ao instanceof InteractionImpl) {
            ((InteractionImpl)ao).calculateCrc();
        }
    }

    public void commit() {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        if ( log.isTraceEnabled() ) {
            log.trace( "Committing..." );
        }

        // Order the collection of objects to persist: institution, cvs, others
        List<IntactObject> thingsToPersist = new ArrayList<IntactObject>( annotatedObjectsToPersist.values() );
        Collections.sort( thingsToPersist, new PersistenceOrderComparator() );

        for ( IntactObject ao : thingsToPersist ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\tAbout to persist " + DebugUtil.intactObjectToString(ao, true) +" - Key: "+ getKeyForValue( annotatedObjectsToPersist, ao ));
            }

            // this may happen if there is a cascade on this object from the parent
            // exception: features are persisted by cascade from the component, so they can be ignored
            if ( log.isWarnEnabled() && ao.getAc() != null && !(ao instanceof Feature) ) {
                log.warn( "Object to persist should NOT have an AC: " + DebugUtil.intactObjectToString(ao, true) );
            } else {

                try {
                    daoFactory.getBaseDao().persist( ao );
                } catch (Exception e) {
                    throw new PersisterException("Problem persisting: "+ao, e);
                }

                if (ao instanceof AnnotatedObject) {
                    if (statisticsEnabled) statistics.addPersisted((AnnotatedObject) ao);
                }
            }
        }

        if ( log.isTraceEnabled() ) {
            log.trace( "Merging objects..." );
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
                if (statisticsEnabled)  statistics.addMerged(ao);
            }
        }

        try {
            log.trace( "Invoking an EntityManager flush..." );
            daoFactory.getEntityManager().flush();
        } catch (IllegalStateException ise) {
            if (ise.getCause() instanceof TransientObjectException) {

                final SessionImpl session = (SessionImpl) ((HibernateEntityManager) daoFactory.getEntityManager()).getSession();
                final Map<?, EntityEntry> entityEntries = session.getPersistenceContext().getEntityEntries();

                for (Map.Entry<?, EntityEntry> entry : entityEntries.entrySet()) {
                    EntityEntry entityEntry = entry.getValue();
                    Status entityStatus = entityEntry.getStatus();

                    Object ac = entityEntry.getId();

                    if (entityStatus == Status.SAVING) {
                        throw new PersisterException("Problem persisting this entity because it contains transient members (invoke saveOrUpdate(transientObject) on those first): ac:"+ac+" - "+entry.getKey(), ise);
                    }
                }

                throw new PersisterException(ise);
            } else {
                throw new PersisterException("Problem flushing the entity manager", ise);
            }
        } catch ( Exception t ) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception when flushing the Persister");

            if (statisticsEnabled) {
                sb.append(", which contained: \n");
                sb.append(statistics).append("\n");
                sb.append("Persisted entities: ").append(statistics.getPersistedMap().values()).append("\n");
                sb.append("Merged entities: ").append(statistics.getMergedMap().values()).append("\n");
                sb.append("Transient entities: ").append(statistics.getTransientMap().values()).append("\n");
            } else {
                sb.append(" - No data about the entities (statistics are disabled)");
            }
            throw new PersisterException( sb.toString(), t );
        } finally {
            annotatedObjectsToMerge.clear();
            annotatedObjectsToPersist.clear();
            synched.clear();
        }
    }

    private Object getKeyForValue( Map map, Object value ) {
        for ( Object x : map.entrySet() ) {
            Map.Entry entry = ( Map.Entry ) x;
            if( entry.getValue() == value ) {
                return entry.getKey();
            }
        }
        return null;
    }

    public PersisterStatistics getStatistics() {
        return statistics;
    }

    /////////////////////////////////////////////
    // Private methods - synchronize children

    private void synchronizeChildren( AnnotatedObject ao ) {
        if ( ao instanceof Institution ) {
            synchronizeInstitution( ( Institution ) ao, true );
        } else if ( ao instanceof Publication ) {
            synchronizePublication( ( Publication ) ao, true );
        } else if ( ao instanceof CvObject ) {
            synchronizeCvObject( ( CvObject ) ao, true );
        } else if ( ao instanceof Experiment ) {
            synchronizeExperiment( ( Experiment ) ao, true );
        } else if ( ao instanceof Interaction ) {
            synchronizeInteraction( ( Interaction ) ao, true );
        } else if ( ao instanceof Interactor ) {
            synchronizeInteractor( ( Interactor ) ao, true );
        } else if ( ao instanceof BioSource ) {
            synchronizeBioSource( ( BioSource ) ao, true );
        } else if ( ao instanceof Component ) {
            synchronizeComponent( ( Component ) ao, true );
        } else if ( ao instanceof Feature ) {
            synchronizeFeature( ( Feature ) ao, true );
        } else {
            throw new IllegalArgumentException( "synchronizeChildren doesn't handle : " + ao.getClass().getName() );
        }
    }

    private void synchronizeChildrenButNotAnnotatedAttributes( AnnotatedObject ao ) {
        if ( ao instanceof Institution ) {
            synchronizeInstitution( ( Institution ) ao, false );
        } else if ( ao instanceof Publication ) {
            synchronizePublication( ( Publication ) ao, false );
        } else if ( ao instanceof CvObject ) {
            synchronizeCvObject( ( CvObject ) ao, false );
        } else if ( ao instanceof Experiment ) {
            synchronizeExperiment( ( Experiment ) ao, false );
        } else if ( ao instanceof Interaction ) {
            synchronizeInteraction( ( Interaction ) ao, false );
        } else if ( ao instanceof Interactor ) {
            synchronizeInteractor( ( Interactor ) ao, false );
        } else if ( ao instanceof BioSource ) {
            synchronizeBioSource( ( BioSource ) ao, false );
        } else if ( ao instanceof Component ) {
            synchronizeComponent( ( Component ) ao, false );
        } else if ( ao instanceof Feature ) {
            synchronizeFeature( ( Feature ) ao, false );
        } else {
            throw new IllegalArgumentException( "synchronizeChildren doesn't handle : " + ao.getClass().getName() );
        }
    }


    private void synchronizeExperiment( Experiment experiment, boolean synchronizeAnnotatedAttributes ) {

        experiment.setPublication( synchronize( experiment.getPublication() ) );
        if (IntactCore.isInitializedAndDirty(experiment.getInteractions())){
            Collection<Interaction> interactions = synchronizeCollection(experiment.getInteractions());
            experiment.getInteractions().clear();
            experiment.getInteractions().addAll(interactions);
        }

        experiment.setCvIdentification(synchronize(experiment.getCvIdentification()));
        experiment.setCvInteraction( synchronize( experiment.getCvInteraction() ) );
        experiment.setBioSource( synchronize( experiment.getBioSource() ) );

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( experiment );
        }
    }

    private void synchronizeInteraction( Interaction interaction, boolean synchronizeAnnotatedAttributes ) {

        interaction.setCvInteractionType( synchronize( interaction.getCvInteractionType() ) );
        interaction.setCvInteractorType( synchronize( interaction.getCvInteractorType() ) );
        if (IntactCore.isInitializedAndDirty(interaction.getComponents())){
            Collection<Component> components = synchronizeCollection(interaction.getComponents());
            interaction.getComponents().clear();
            interaction.getComponents().addAll(components);
        }
        interaction.setBioSource( synchronize( interaction.getBioSource() ) );
        if (IntactCore.isInitializedAndDirty(interaction.getExperiments())){
            Collection<Experiment> experiment = synchronizeCollection(interaction.getExperiments());
            interaction.getExperiments().clear();
            interaction.getExperiments().addAll(experiment);
        }

        // cannot call setConfidences in interaction because of orphan relationship limitation
        if (IntactCore.isInitializedAndDirty(interaction.getConfidences())){
            Collection<Confidence> confidences = synchronizeConfidences(interaction.getConfidences(), interaction);
            interaction.getConfidences().clear();
            interaction.getConfidences().addAll(confidences);
        }

        // cannot call setParameters in interaction because of orphan relationship limitation
        if (IntactCore.isInitializedAndDirty(interaction.getParameters())){

            Collection<InteractionParameter> interactionParameters = synchronizeInteractionParameters(interaction.getParameters(), interaction);
            interaction.getParameters().clear();
            interaction.getParameters().addAll(interactionParameters);
        }

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( interaction );
        }
    }

    private Collection<Confidence> synchronizeConfidences( Collection<Confidence> confidencesToSynchronize, Interaction parentInteraction ) {
        List<Confidence> confidences = new ArrayList<Confidence>(confidencesToSynchronize.size());

        for ( Confidence confidence : confidencesToSynchronize ) {
            if (confidence.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getConfidenceDao().isTransient(confidence)) {
                confidence = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getConfidenceDao().getByAc(confidence.getAc());
            }

            confidence.setCvConfidenceType( synchronize (confidence.getCvConfidenceType()));
            confidence.setInteraction((InteractionImpl)parentInteraction);

            confidences.add(confidence);
        }

        return confidences;

    }

    private Collection<InteractionParameter> synchronizeInteractionParameters( Collection<InteractionParameter> interactionParametersToSynchronize, Interaction parentInteraction ) {
        List<InteractionParameter> interactionParameters = new ArrayList<InteractionParameter>(interactionParametersToSynchronize.size());

        for ( InteractionParameter interactionParameter : interactionParametersToSynchronize ) {
            if (interactionParameter.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionParameterDao().isTransient(interactionParameter)) {
                interactionParameter = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionParameterDao().getByAc(interactionParameter.getAc());
            }
            interactionParameter.setCvParameterType( synchronize (interactionParameter.getCvParameterType()));
            interactionParameter.setCvParameterUnit( synchronize (interactionParameter.getCvParameterUnit()));
            interactionParameter.setInteraction((InteractionImpl)parentInteraction);

            interactionParameters.add(interactionParameter);
        }

        return interactionParameters;

    }

    private void synchronizeInteractor( Interactor interactor, boolean synchronizeAnnotatedAtributes ) {

        if (IntactCore.isInitializedAndDirty(interactor.getActiveInstances())){
            Collection<Component> activeInstances = synchronizeCollection(interactor.getActiveInstances());
            interactor.getActiveInstances().clear();
            interactor.getActiveInstances().addAll(activeInstances);
        }

        interactor.setBioSource( synchronize( interactor.getBioSource() ) );
        interactor.setCvInteractorType( synchronize( interactor.getCvInteractorType() ) );

        if (synchronizeAnnotatedAtributes){
            synchronizeAnnotatedObjectCommons( interactor );
        }
    }

    private void synchronizeBioSource( BioSource bioSource, boolean synchronizeAnnotatedAttributes ) {

        bioSource.setCvCellType( synchronize( bioSource.getCvCellType() ) );
        bioSource.setCvTissue( synchronize( bioSource.getCvTissue() ) );

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( bioSource );
        }
    }

    private void synchronizeComponent( Component component, boolean synchronizeAnnotatedAttributes ) {
        // cannot call setFeatures in interaction because of orphan relationship limitation
        if (IntactCore.isInitializedAndDirty(component.getFeatures())) {
            Collection<Feature> features = synchronizeCollection(component.getFeatures());
            component.getFeatures().clear();
            component.getFeatures().addAll(features);
        }

        component.setCvBiologicalRole( synchronize( component.getCvBiologicalRole() ) );

        if (IntactCore.isInitializedAndDirty(component.getExperimentalRoles())) {
            Collection<CvExperimentalRole> roles = synchronizeCollection( component.getExperimentalRoles() );
            component.getExperimentalRoles().clear();
            component.getExperimentalRoles().addAll(roles);
        }

        component.setExpressedIn( synchronize( component.getExpressedIn() ) );
        component.setInteraction( synchronize( component.getInteraction() ) );
        component.setInteractor( synchronize( component.getInteractor() ) );

        if (IntactCore.isInitializedAndDirty(component.getParticipantDetectionMethods())) {
            Collection<CvIdentification> partDet = synchronizeCollection( component.getParticipantDetectionMethods());
            component.getParticipantDetectionMethods().clear();
            component.getParticipantDetectionMethods().addAll(partDet);
        }

        if (IntactCore.isInitializedAndDirty(component.getExperimentalPreparations())) {
            Collection<CvExperimentalPreparation> expPrep = synchronizeCollection( component.getExperimentalPreparations() );
            component.getExperimentalPreparations().clear();
            component.getExperimentalPreparations().addAll(expPrep);
        }

        if (IntactCore.isInitializedAndDirty(component.getParameters())) {
            Collection<ComponentParameter> componentParameters = synchronizeComponentParameters(component.getParameters(), component);
            component.getParameters().clear();
            component.getParameters().addAll(componentParameters);
        }

        if (IntactCore.isInitializedAndDirty(component.getConfidences())) {
            Collection<ComponentConfidence> componentConfidences = synchronizeComponentConfidences(component.getConfidences(), component);
            component.getConfidences().clear();
            component.getConfidences().addAll(componentConfidences);
        }

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( component );
        }
    }

    private Collection<ComponentParameter> synchronizeComponentParameters( Collection<ComponentParameter> componentParametersToSynchronize, Component parentComponent ) {
        List<ComponentParameter> componentParameters = new ArrayList<ComponentParameter>(componentParametersToSynchronize.size());

        for ( ComponentParameter componentParameter : componentParametersToSynchronize ) {
            if (componentParameter.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getComponentParameterDao().isTransient(componentParameter)) {
                componentParameter = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getComponentParameterDao().getByAc(componentParameter.getAc());
            }

            componentParameter.setCvParameterType( synchronize (componentParameter.getCvParameterType()));
            componentParameter.setCvParameterUnit( synchronize (componentParameter.getCvParameterUnit()));
            componentParameter.setComponent(parentComponent);

            componentParameters.add(componentParameter);
        }

        return componentParameters;

    }

    private Collection<ComponentConfidence> synchronizeComponentConfidences( Collection<ComponentConfidence> confidencesToSynchronize, Component parentComponent ) {
        List<ComponentConfidence> confidences = new ArrayList<ComponentConfidence>(confidencesToSynchronize.size());

        for ( ComponentConfidence confidence : confidencesToSynchronize ) {
            if (confidence.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getComponentConfidenceDao().isTransient(confidence)) {
                confidence = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getComponentConfidenceDao().getByAc(confidence.getAc());
            }

            confidence.setCvConfidenceType( synchronize (confidence.getCvConfidenceType()));
            confidence.setComponent(parentComponent);

            confidences.add(confidence);
        }

        return confidences;

    }


    private void synchronizeFeature( Feature feature, boolean synchronizeAnnotatedAttributes ) {

        feature.setBoundDomain( synchronize( feature.getBoundDomain() ) );
        feature.setComponent( synchronize( feature.getComponent() ) );
        feature.setCvFeatureIdentification( synchronize( feature.getCvFeatureIdentification() ) );
        feature.setCvFeatureType( synchronize( feature.getCvFeatureType() ) );

        // cannot call setRanges in interaction because of orphan relationship limitation
        if (IntactCore.isInitializedAndDirty(feature.getRanges())){
            Collection<Range> ranges = synchronizeRanges(feature.getRanges(), feature);
            feature.getRanges().clear();
            feature.getRanges().addAll(ranges);
        }

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( feature );
        }
    }

    private Collection<Range> synchronizeRanges( Collection<Range> rangesToSychronize, Feature parentFeature ) {
        List<Range> ranges = new ArrayList<Range>(rangesToSychronize.size());

        for ( Range range : rangesToSychronize ) {
            if (range.getAc() != null && IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getRangeDao().isTransient(range)) {
                range = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getRangeDao().getByAc(range.getAc());
            }

            range.setFromCvFuzzyType( synchronize( range.getFromCvFuzzyType() ) );
            range.setToCvFuzzyType( synchronize( range.getToCvFuzzyType() ) );

            range.setFeature(parentFeature);

            ranges.add(range);
        }

        return ranges;

    }

    private void synchronizeCvObject( CvObject cvObject, boolean synchronizeAnnotatedAttributes ) {
        if (cvObject instanceof CvDagObject) {
            CvDagObject cvDagObject = (CvDagObject)cvObject;

            if (IntactCore.isInitializedAndDirty(cvDagObject.getChildren())){
                Collection<CvDagObject> children = synchronizeCollection(cvDagObject.getChildren());
                cvDagObject.getChildren().clear();
                cvDagObject.getChildren().addAll(children);
            }
            if (IntactCore.isInitializedAndDirty(cvDagObject.getParents())){
                Collection<CvDagObject> parents = synchronizeCollection(cvDagObject.getParents());
                cvDagObject.getParents().clear();
                cvDagObject.getParents().addAll(parents);
            }
        }

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( cvObject );
        }
    }

    private void synchronizePublication( Publication publication, boolean synchronizeAnnotatedAttributes ) {

        if (IntactCore.isInitializedAndDirty(publication.getExperiments())){
            Collection<Experiment> experiments = synchronizeCollection(publication.getExperiments());
            publication.getExperiments().clear();
            publication.getExperiments().addAll(experiments);
        }

        publication.setStatus( synchronize( publication.getStatus() ) );
        publication.setCurrentOwner( synchronizeUser(publication.getCurrentOwner()) );
        publication.setCurrentReviewer( synchronizeUser(publication.getCurrentReviewer()) );

        final List<LifecycleEvent> events = publication.getLifecycleEvents();
        if (IntactCore.isInitializedAndDirty( events )){
            Collection<LifecycleEvent> synchedEvents = new ArrayList<LifecycleEvent>( events.size() );
            for ( LifecycleEvent event : events ) {
                synchedEvents.add( synchronize( event ) );
            }
            events.clear();
            events.addAll( synchedEvents );
        }

        if (synchronizeAnnotatedAttributes){
            synchronizeAnnotatedObjectCommons( publication );
        }
    }


    private Role synchronizeRole( Role role ) {
        Key key = new Key("role:"+role.getName());

        if (synched.containsKey(key)) {
            return (Role) synched.get(key);
        }

        synched.put(key, role);

        return role;
    }

    private Preference synchronizePreference( Preference preference ) {
        preference.setUser(  synchronizeUser(preference.getUser()) );
        return preference;
    }

    private User synchronizeUser( User user ) {
        if (user == null) return null;

        Key key;

        if (user.getAc() != null) {
            key = new Key("user:"+user.getAc());
        } else {
            key = new Key("user:"+user.getLogin());
        }

        if (synched.containsKey(key)) {
            return (User) synched.get(key);
        }

        final Set<Role> roles = user.getRoles();
        if (IntactCore.isInitializedAndDirty( roles )){
           Collection<Role> synchedRoles = new ArrayList<Role>( roles.size() );
            for ( Role role : roles ) {
                synchedRoles.add( synchronizeRole( role ) );
            }
            roles.clear();
            roles.addAll( synchedRoles );
        }

        final Collection<Preference> prefs = user.getPreferences();
        if (IntactCore.isInitializedAndDirty( prefs )){
           Collection<Preference> synchedPrefs = new ArrayList<Preference>( prefs.size() );
            for ( Preference p : prefs ) {
                synchedPrefs.add( synchronizePreference( p ) );
            }
            prefs.clear();
            prefs.addAll( synchedPrefs );
        }

        synched.put(key, user);

        return user;
    }

    private LifecycleEvent synchronize( LifecycleEvent event ) {
        event.setWho( synchronizeUser(event.getWho()) );
        event.setEvent( synchronize( event.getEvent() ) );

        return event;
    }

    private void synchronizeInstitution( Institution institution, boolean synchronyzeCommonAttributes ) {

        if (synchronyzeCommonAttributes){
            synchronizeAnnotatedObjectCommons( institution );
        }
    }

    private <X extends AnnotatedObject> Collection<X> synchronizeCollection( Collection<X> collection ) {
        if (!IntactCore.isInitializedAndDirty(collection)) {
            return collection;
        }

        Collection<X> synchedCollection = new ArrayList<X>( collection.size() );
        for ( X ao : collection ) {
            synchedCollection.add( synchronize( ao ) );
        }
        return synchedCollection;
    }

    private void synchronizeAnnotatedObjectCommons( AnnotatedObject<? extends Xref, ? extends Alias> ao ) {

        Collection synchedXrefs = new ArrayList();

        if (IntactCore.isInitializedAndDirty(ao.getXrefs())) {
            for ( Xref xref : ao.getXrefs() ) {
                synchedXrefs.add( synchronizeXref( xref, ao ) );
            }

            ao.getXrefs().clear();
            ao.getXrefs().addAll(synchedXrefs);
        } else {
            synchedXrefs = ao.getXrefs();
        }

        Collection synchedAliases = new ArrayList();

        if (IntactCore.isInitializedAndDirty(ao.getAliases())) {
            for ( Alias alias : ao.getAliases() ) {
                synchedAliases.add( synchronizeAlias( alias, ao ) );
            }

            ao.getAliases().clear();
            ao.getAliases().addAll(synchedAliases);
        } else {
            synchedAliases = ao.getAliases();
        }

        Collection synchedAnnotations = new ArrayList();

        if (IntactCore.isInitializedAndDirty(ao.getAnnotations())) {
            for ( Annotation annotation : ao.getAnnotations() ) {
                synchedAnnotations.add( synchronizeAnnotation( annotation, ao ) );
            }

            ao.getAnnotations().clear();
            ao.getAnnotations().addAll(synchedAnnotations);
        } else {
            synchedAnnotations = ao.getAnnotations();
        }

        if (ao instanceof OwnedObject) {
            OwnedObject ownedObject = (OwnedObject) ao;
            synchronizeOwnedObjectCommons(ownedObject);
        }
    }

    private void synchronizeOwnedObjectCommons (OwnedObject bo) {
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

        if (xref.getAc() == null && xref.getAc() != null) {
            annotatedObjectsToPersist.put(keyBuilder.keyForXref(xref), xref);
        }

        return xref;
    }

    private Alias synchronizeAlias( Alias alias, AnnotatedObject parent ) {
        if (alias.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getAliasDao().getByAc(alias.getAc());
        }

        alias.setCvAliasType( synchronize( alias.getCvAliasType() ) );
        alias.setParent(parent);

        if (alias.getAc() == null && parent.getAc() != null) {
            annotatedObjectsToPersist.put(keyBuilder.keyForAlias(alias), alias);
        }

        return alias;
    }

    private Annotation synchronizeAnnotation( Annotation annotation, AnnotatedObject parent ) {
        if (annotation.getAc() != null) {
            return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getAnnotationDao().getByAc(annotation.getAc());
        }
        else {

        }
        annotation.setCvTopic( synchronize( annotation.getCvTopic() ) );

        if (annotation.getAc() == null && parent.getAc() != null) {
            annotatedObjectsToPersist.put(keyBuilder.keyForAnnotation(annotation, parent), annotation);
        }

        return annotation;
    }

    public boolean isUpdateWithoutAcEnabled() {
        return updateWithoutAcEnabled;
    }

    public void setUpdateWithoutAcEnabled(boolean updateWithoutAcEnabled) {
        this.updateWithoutAcEnabled = updateWithoutAcEnabled;
    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }
}
