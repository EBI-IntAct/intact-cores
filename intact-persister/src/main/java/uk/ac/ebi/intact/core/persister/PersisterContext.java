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

    public static PersisterContext getInstance() {
        return instance.get();
    }

    private PersisterContext() {
        this.cvObjectsToBePersisted = new HashMap<String,CvObject>();
        this.annotatedObjectsToBePersisted = new HashMap<String,AnnotatedObject>();
        this.institutionsToBePersisted = new HashMap<String,Institution>();
    }

    public void addToPersist(AnnotatedObject ao) {
        if (ao instanceof CvObject) {
            cvObjectsToBePersisted.put(keyFor(ao),(CvObject)ao);
        } else {
            annotatedObjectsToBePersisted.put(keyFor(ao), ao);
        }
    }

    public void addToPersist(Institution institution) {
        if (!institutionsToBePersisted.containsKey(institution)) {
            institutionsToBePersisted.put(institution.getShortLabel(), institution);
        }
    }

    public boolean contains(AnnotatedObject ao) {
        final String key = keyFor(ao);

        if (cvObjectsToBePersisted.containsKey(key)) {
            return true;
        }
        return annotatedObjectsToBePersisted.containsKey(key);
    }

    public boolean contains(Institution institution) {
        final String key = institution.getShortLabel();

        return institutionsToBePersisted.containsKey(key);
    }

    public AnnotatedObject get(AnnotatedObject ao) {
        final String key = keyFor(ao);

        if (cvObjectsToBePersisted.containsKey(key)) {
            return cvObjectsToBePersisted.get(key);
        }

        return annotatedObjectsToBePersisted.get(key);
    }

    public Institution get(Institution institution) {
       final String key = institution.getShortLabel(); 

        return institutionsToBePersisted.get(key);
    }

    public void persistAll() {
        if (log.isDebugEnabled()) {
            log.debug("Persisting all"+ (isDryRun()? " - DRY RUN" : ""));
            log.debug("\tCvObjects: "+cvObjectsToBePersisted.size());

        }

        for (Institution institution : institutionsToBePersisted.values()) {
            getDaoFactory().getInstitutionDao().persist(institution);
        }

        getIntactContext().getDataContext().flushSession();

        for (CvObject cv : cvObjectsToBePersisted.values()) {
            getDaoFactory().getCvObjectDao().persist(cv);
            logPersistence(cv);
        }

        getIntactContext().getDataContext().flushSession();

        log.debug("\tOther AnnotatedObjects: "+annotatedObjectsToBePersisted.size());

        ExperimentInterceptor experimentInterceptor = new ExperimentInterceptor();
        InteractionInterceptor interactionInterceptor = new InteractionInterceptor();

        for (AnnotatedObject ao : annotatedObjectsToBePersisted.values()) {
            if (ao instanceof Experiment) {
                experimentInterceptor.onPrePersist((Experiment)ao);
            } else if (ao instanceof Interaction) {
                interactionInterceptor.onPrePersist((Interaction)ao);
            }
            getDaoFactory().getAnnotatedObjectDao((Class<AnnotatedObject>)ao.getClass()).persist(ao);
            logPersistence(ao);
        }

        clear();
        SyncContext.getInstance().clear();
        getIntactContext().getDataContext().flushSession();
    }

    private static void logPersistence(AnnotatedObject<?,?> ao) {
        if (log.isDebugEnabled()) {
            log.debug("\t\tPersisting: " + ao.getShortLabel() + " (" + ao.getAc() + ")");

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
        }
    }

    public void clear() {
        if (log.isDebugEnabled()) log.debug("Clearing PersistenceContext");
        institutionsToBePersisted.clear();
        cvObjectsToBePersisted.clear();
        annotatedObjectsToBePersisted.clear();
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