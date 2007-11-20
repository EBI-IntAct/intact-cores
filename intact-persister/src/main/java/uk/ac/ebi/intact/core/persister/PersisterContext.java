/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.hibernate.TransientObjectException;
import org.hibernate.LazyInitializationException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.interceptor.impl.ExperimentInterceptor;
import uk.ac.ebi.intact.core.persister.interceptor.impl.InteractionInterceptor;
import uk.ac.ebi.intact.core.persister.util.PersistenceOrderComparator;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.*;


/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterContext {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(PersisterContext.class);

    private static ThreadLocal<PersisterContext> instance = new ThreadLocal<PersisterContext>() {
        @Override
        protected PersisterContext initialValue() {
            return new PersisterContext();
        }
    };

    private boolean dryRun;

    private Map<String, CvObject> cvObjectsToBePersisted;
    private Map<String, AnnotatedObject> annotatedObjectsToBePersisted;
    private Map<String, Institution> institutionsToBePersisted;
    private Map<String, AnnotatedObject> annotatedObjectsToBeUpdated;

    boolean originalAutoFlush;

    public static PersisterContext getInstance() {
        IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().setAutoFlush(false);
        return instance.get();
    }

    private PersisterContext() {
        this.cvObjectsToBePersisted = new HashMap<String,CvObject>();
        this.annotatedObjectsToBePersisted = new HashMap<String,AnnotatedObject>();
        this.institutionsToBePersisted = new HashMap<String,Institution>();

        this.annotatedObjectsToBeUpdated = new HashMap<String,AnnotatedObject>();

        originalAutoFlush = IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().isAutoFlush();
    }

    public void addToPersist(AnnotatedObject ao) {
        if (log.isDebugEnabled()) log.debug("\t\t\tAdding to PersisterContext: "+keyFor(ao));

        if (ao instanceof CvObject) {
            cvObjectsToBePersisted.put(keyFor(ao),(CvObject)ao);
        } else {
            annotatedObjectsToBePersisted.put(keyFor(ao), ao);
        }
    }

    public void addToUpdate(AnnotatedObject ao) {
        annotatedObjectsToBeUpdated.put(keyFor(ao), ao);
    }

    public void addToPersist(Institution institution) {
        if ( !contains( institution ) ) {
            institutionsToBePersisted.put( institution.getShortLabel(), institution );
        }
    }

    public boolean contains(AnnotatedObject ao) {
        final String key;
        try {
            key = keyFor(ao);
        } catch (LazyInitializationException e) {
            log.debug("Trying to retrieve for the PersisterContext using a transient object (provokes a LazyInitializationException): " + ao.getShortLabel() + " - Will refetch from database");
            return false;
        }

        if (ao instanceof CvObject) {
            return cvObjectsToBePersisted.containsKey(key);
        } else {
            return annotatedObjectsToBePersisted.containsKey(key);
        }
    }

    public boolean contains(Institution institution) {
        final String key = institution.getShortLabel();

        return institutionsToBePersisted.containsKey(key);
    }

    public AnnotatedObject get(AnnotatedObject ao) {
        final String key;
        try {
            key = keyFor(ao);
        } catch (LazyInitializationException e) {
            log.debug("Trying to retrieve for the PersisterContext using a transient object (provokes a LazyInitializationException): " + ao.getShortLabel() + " - Will refetch from database");
            return null;
        }

        if (ao instanceof CvObject) {
            return cvObjectsToBePersisted.get(key);
        } else {
            return annotatedObjectsToBePersisted.get(key);
        }
    }

    public Institution get(Institution institution) {
       final String key = institution.getShortLabel(); 

        return institutionsToBePersisted.get(key);
    }

    public void persistAll() {
        if (log.isDebugEnabled()) {
            log.debug("Persisting all"+ (isDryRun()? " - DRY RUN" : ""));
            log.debug("\tCvObjects: "+cvObjectsToBePersisted.size());
            for ( String key : cvObjectsToBePersisted.keySet() ) {
                CvObject cv = cvObjectsToBePersisted.get( key );
                log.debug( " - " + cv.getClass().getSimpleName() + ": " + cv.getShortLabel() );
            }
        }

        for (Institution institution : institutionsToBePersisted.values()) {

            if( institution.getAc() != null ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tReplicating " + institution.getClass().getSimpleName() + ": " + institution.getShortLabel()+ " (AC:"+ institution.getAc() +")" );
                }
                getDaoFactory().getInstitutionDao().replicate(institution);
            } else {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tPersisting " + institution.getClass().getSimpleName() + ": " + institution.getShortLabel() );
                }
                getDaoFactory().getInstitutionDao().persist(institution);
            }
        }

        for (CvObject cv : cvObjectsToBePersisted.values()) {
            if (cv.getAc() != null) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tReplicating " + cv.getClass().getSimpleName() + ": " + cv.getShortLabel()+ " (AC:"+ cv.getAc() +")" );
                }

                getDaoFactory().getCvObjectDao().merge(cv);
            } else {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tPersisting " + cv.getClass().getSimpleName() + ": " + cv.getShortLabel() );
                }
                getDaoFactory().getCvObjectDao().persist(cv);
            }

            logPersistence(cv);
        }

        //getIntactContext().getDataContext().flushSession();

        if (log.isDebugEnabled()) log.debug("\tExecuting AnnotatedObject Interceptors.");

        ExperimentInterceptor experimentInterceptor = new ExperimentInterceptor();
        InteractionInterceptor interactionInterceptor = new InteractionInterceptor();

        for (AnnotatedObject ao : annotatedObjectsToBePersisted.values()) {
            if (ao instanceof Experiment) {
                experimentInterceptor.onPrePersist((Experiment)ao);
            } else if (ao instanceof Interaction) {
                interactionInterceptor.onPrePersist((Interaction)ao);
            }
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "\tSaving AnnotatedObjects to be persisted: " + annotatedObjectsToBePersisted.size() );
            log.debug( "\t-------------------------------------------- " );
            for ( AnnotatedObject ao : annotatedObjectsToBePersisted.values() ) {
                String ac = (ao.getAc() == null? "" : " (AC: "+ao.getAc()+")");
                log.debug( "\t - " + ao.getClass().getSimpleName() + ac+": " + ao.getShortLabel() + " (Context Key: "+ AnnotKeyGenerator.createKey(ao)+")");
            }
            log.debug( "\t-------------------------------------------- " );
        }

        List<AnnotatedObject> annotObjects = new ArrayList<AnnotatedObject>(annotatedObjectsToBePersisted.values());
        Collections.sort(annotObjects, new PersistenceOrderComparator());
        
        for (AnnotatedObject ao : annotObjects) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tPersisting " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() + " (Context Key: "+ AnnotKeyGenerator.createKey(ao)+")");

                    if (ao instanceof Component) {
                        Component c = (Component)ao;
                        log.debug("\t\t\tInteractor: "+c.getInteractor().getShortLabel()+" - Interaction: "+c.getInteraction().getShortLabel());
                        log.debug("\t\t\tFeatures: ");
                        for (Feature f : c.getBindingDomains()) {
                            log.debug("\t\t\t\tFeature: "+f.getShortLabel());
                        }
                    }

                    if (ao instanceof Feature) {
                        Feature f = (Feature)ao;
                        log.debug("\t\t\tComponent ("+f.getComponent().getAc()+") - Interactor: "+f.getComponent().getInteractor().getShortLabel()+" - Interaction: "+f.getComponent().getInteraction().getShortLabel());
                    }
                }
                
            getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).persist(ao);

            logPersistence(ao);
        }

        getIntactContext().getDataContext().flushSession();

        if ( log.isDebugEnabled() ) {
            log.debug( "\tSaving AnnotatedObjects to be updated: " + annotatedObjectsToBeUpdated.size() );
            for ( String key : annotatedObjectsToBeUpdated.keySet() ) {
                AnnotatedObject ao = annotatedObjectsToBeUpdated.get( key );
                log.debug( " - " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }
        }

        for (AnnotatedObject ao : annotatedObjectsToBeUpdated.values()) {
             if (ao instanceof Interaction) {
                interactionInterceptor.onPreUpdate((Interaction)ao);
            }
            if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tMerging " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel()+ " (AC: "+ ao.getAc() +")" );
            }
            getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).merge(ao);
            logPersistence(ao);
        }

        clear();

        getIntactContext().getDataContext().flushSession();
    }

    private static void logPersistence(AnnotatedObject<?,?> ao) {
        if (log.isDebugEnabled()) {
            log.debug("\t\t\tPersisted with AC: " + ao.getAc());

            if (!ao.getXrefs().isEmpty()) {
                log.debug("\t\t\tXrefs: " + ao.getXrefs().size());

                for (Xref xref : ao.getXrefs()) {
                    log.debug("\t\t\t\t"+xref);
                }
            }

            if (!ao.getAliases().isEmpty()) {
                log.debug("\t\t\tAliases: " + ao.getAliases().size());

                for (Alias alias: ao.getAliases()) {
                    log.debug("\t\t\t\t"+alias);
                }
            }

            if (!ao.getAnnotations().isEmpty()) {
                log.debug("\t\t\tAnnotations: " + ao.getAnnotations().size());

                for (Annotation annot: ao.getAnnotations()) {
                    log.debug("\t\t\t\t"+annot);
                }
            }
        }
    }

    public void clear() {
        if (log.isDebugEnabled()) log.debug("Clearing PersistenceContext");
        SyncContext.getInstance().clear();
        
        institutionsToBePersisted.clear();
        cvObjectsToBePersisted.clear();
        annotatedObjectsToBePersisted.clear();
        annotatedObjectsToBeUpdated.clear();

        IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().setAutoFlush(originalAutoFlush);
    }

    private String keyFor(AnnotatedObject ao) {
        return AnnotKeyGenerator.createKey(ao);
    }

    private IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    private DaoFactory getDaoFactory() {
        return getIntactContext().getDataContext().getDaoFactory();
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public Collection<AnnotatedObject> getAnnotatedObjectsToBePersisted() {
        return annotatedObjectsToBePersisted.values();
    }

    public Collection<CvObject> getCvObjectsToBePersisted() {
        return cvObjectsToBePersisted.values();
    }
}