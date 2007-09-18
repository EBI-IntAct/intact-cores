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
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.interceptor.impl.ExperimentInterceptor;
import uk.ac.ebi.intact.core.persister.interceptor.impl.InteractionInterceptor;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


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

    public static PersisterContext getInstance() {
        return instance.get();
    }

    private PersisterContext() {
        this.cvObjectsToBePersisted = new HashMap<String,CvObject>();
        this.annotatedObjectsToBePersisted = new HashMap<String,AnnotatedObject>();
        this.institutionsToBePersisted = new HashMap<String,Institution>();

        this.annotatedObjectsToBeUpdated = new HashMap<String,AnnotatedObject>();
    }

    public void addToPersist(AnnotatedObject ao) {


        if (ao instanceof CvObject) {
            cvObjectsToBePersisted.put(keyFor(ao),(CvObject)ao);
        } else {
            annotatedObjectsToBePersisted.put(keyFor(ao), ao);
        }
    }

    public void addToUpdate(AnnotatedObject ao) {
        annotatedObjectsToBeUpdated.put(keyFor(ao), ao);
    }

//    public void addToPersistImmediately(AnnotatedObject ao) {
//        getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).saveOrUpdate(ao);
//    }

    public void addToPersist(Institution institution) {
        if ( !contains( institution ) ) {
            institutionsToBePersisted.put( institution.getShortLabel(), institution );
        }
        // TODO Bruno: seems to be a mismatch between the call to containsKey and put !!
//        if (!institutionsToBePersisted.containsKey(institution)) {
//            institutionsToBePersisted.put(institution.getShortLabel(), institution);
//        }
    }

    public boolean contains(AnnotatedObject ao) {
        final String key = keyFor(ao);

        if (ao instanceof CvObject) {
            return cvObjectsToBePersisted.containsKey(key);
        } else {
            return annotatedObjectsToBePersisted.containsKey(key);
        }
//        if (cvObjectsToBePersisted.containsKey(key)) {
//            return true;
//        }
//        return annotatedObjectsToBePersisted.containsKey(key);
    }

    public boolean contains(Institution institution) {
        final String key = institution.getShortLabel();

        return institutionsToBePersisted.containsKey(key);
    }

    public AnnotatedObject get(AnnotatedObject ao) {
        final String key = keyFor(ao);

        if (ao instanceof CvObject) {
            return cvObjectsToBePersisted.get(key);
        } else {
            return annotatedObjectsToBePersisted.get(key);
        }
//        if (cvObjectsToBePersisted.containsKey(key)) {
//            return cvObjectsToBePersisted.get(key);
//        }
//        return annotatedObjectsToBePersisted.get(key);
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

            // TODO Bruno: added replicate( i ) as I got org.hibernate.PersistentObjectException: detached entity passed to persist: uk.ac.ebi.intact.model.CvDatabase
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

                getDaoFactory().getCvObjectDao().replicate(cv);
            } else {
                // TODO (A) use of persist and save-or-update have to be harmonized (cf. B)
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tPersisting " + cv.getClass().getSimpleName() + ": " + cv.getShortLabel() );
                }
                getDaoFactory().getCvObjectDao().persist(cv);
            }

            logPersistence(cv);
        }

        getIntactContext().getDataContext().flushSession();

        if ( log.isDebugEnabled() ) {
            log.debug( "\tSaving AnnotatedObjects to be persisted: " + annotatedObjectsToBePersisted.size() );
            for ( String key : annotatedObjectsToBePersisted.keySet() ) {
                AnnotatedObject ao = annotatedObjectsToBePersisted.get( key );
                log.debug( " - " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
            }
        }

        ExperimentInterceptor experimentInterceptor = new ExperimentInterceptor();
        InteractionInterceptor interactionInterceptor = new InteractionInterceptor();

        for (AnnotatedObject ao : annotatedObjectsToBePersisted.values()) {
            if (ao instanceof Experiment) {
                experimentInterceptor.onPrePersist((Experiment)ao);
            } else if (ao instanceof Interaction) {
                interactionInterceptor.onPrePersist((Interaction)ao);
            }
            if (ao.getAc() != null) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tReplicating " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() + " (AC: "+ ao.getAc() +")" );
                }
                getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).replicate(ao);
            } else {
                if ( log.isDebugEnabled() ) {
                    log.debug( "\t\tPersisting " + ao.getClass().getSimpleName() + ": " + ao.getShortLabel() );
                }
                getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).persist(ao);
            }
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
            if (ao instanceof Experiment) {
                experimentInterceptor.onPrePersist((Experiment)ao);
            } else if (ao instanceof Interaction) {
                interactionInterceptor.onPrePersist((Interaction)ao);
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
            log.debug("\t\tPersisting "+ ao.getClass().getSimpleName() + ": " + ao.getShortLabel() + " (" + ao.getAc() + ")");

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