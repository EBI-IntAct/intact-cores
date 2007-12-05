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
package uk.ac.ebi.intact.core.persister.stats;

import uk.ac.ebi.intact.model.AnnotatedObject;

import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;


/**
 * Gathers statistics during saveOrUpdate by the CorePersister
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterStatistics implements Serializable {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private Multimap<Class, StatUnit> persistedMap;
    private Multimap<Class, StatUnit> mergedMap;
    private Multimap<Class, StatUnit> duplicatesMap;
    private Multimap<Class, StatUnit> transientMap;


    public PersisterStatistics() {
        this.persistedMap = new ArrayListMultimap<Class, StatUnit>();
        this.mergedMap = new ArrayListMultimap<Class, StatUnit>();
        this.duplicatesMap = new ArrayListMultimap<Class, StatUnit>();
        this.transientMap = new ArrayListMultimap<Class, StatUnit>();
    }

    // persisted

    public void addPersisted(AnnotatedObject ao) {
        StatUnit su = new StatUnit(ao);
        persistedMap.put(su.getType(), su);
    }

    public Multimap<Class, StatUnit> getPersistedMap() {
        return persistedMap;
    }

    public Collection<StatUnit> getPersisted(Class type, boolean includeSubclasses) {
        return statsOfType(type, getPersistedMap(), includeSubclasses);
    }

    public int getPersistedCount(Class type, boolean includeSubclasses) {
        return getPersisted(type, includeSubclasses).size();
    }

    // merged

    public void addMerged(AnnotatedObject ao) {
        StatUnit su = new StatUnit(ao);
        mergedMap.put(su.getType(), su);
    }

    public Multimap<Class, StatUnit> getMergedMap() {
        return mergedMap;
    }

    public Collection<StatUnit> getMerged(Class type, boolean includeSubclasses) {
        return statsOfType(type, getMergedMap(), includeSubclasses);
    }

    public int getMergedCount(Class type, boolean includeSubclasses) {
        return getMerged(type, includeSubclasses).size();
    }

    // duplicates

    public void addDuplicate(AnnotatedObject ao) {
        StatUnit su = new StatUnit(ao);
        duplicatesMap.put(su.getType(), su);
    }

    public Multimap<Class, StatUnit> getDuplicatesMap() {
        return duplicatesMap;
    }

    public Collection<StatUnit> getDuplicates(Class type, boolean includeSubclasses) {
        return statsOfType(type, getDuplicatesMap(), includeSubclasses);
    }

    public int getDuplicatesCount(Class type, boolean includeSubclasses) {
        return getDuplicates(type, includeSubclasses).size();
    }

     // transient

    public void addTransient(AnnotatedObject ao) {
        StatUnit su = new StatUnit(ao);
        transientMap.put(su.getType(), su);
    }

    public Multimap<Class, StatUnit> getTransientMap() {
        return transientMap;
    }

    public Collection<StatUnit> getTransient(Class type, boolean includeSubclasses) {
        return statsOfType(type, getTransientMap(), includeSubclasses);
    }

    public int getTransientCount(Class type, boolean includeSubclasses) {
        return getTransient(type, includeSubclasses).size();
    }

    // Common methods
    /////////////////

    protected static Collection<StatUnit> statsOfType(Class type, Multimap<Class,StatUnit> multimap, boolean includeSubclasses) {
        if (!includeSubclasses) {
            return multimap.get(type);
        }

        Collection<StatUnit> stats = new ArrayList<StatUnit>();

        for (Class key : multimap.keySet()) {
            if (type.isAssignableFrom(key)) {
                stats.addAll(multimap.get(key));
            }
        }

        return stats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Persister Stats:").append(NEW_LINE);
        sb.append("----------------").append(NEW_LINE);
        sb.append("Persisted: ").append(multimapToString(getPersistedMap())).append(NEW_LINE);
        sb.append("Merged: ").append(multimapToString(getMergedMap())).append(NEW_LINE);
        sb.append("Duplicates: ").append(multimapToString(getDuplicatesMap())).append(NEW_LINE);
        sb.append("Transient: ").append(multimapToString(getTransientMap())).append(NEW_LINE);

        return sb.toString();
    }

    private String multimapToString(Multimap<Class,StatUnit> multimap) {
        StringBuilder sb = new StringBuilder();

        sb.append(multimap.size());

        if (!multimap.isEmpty()) {

            sb.append(" { ");

            for (Iterator<Class> iterator = multimap.keySet().iterator(); iterator.hasNext();) {
                Class key = iterator.next();

                sb.append(key.getSimpleName() + " (" + multimap.get(key).size() + ")");

                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append(" }");
        }

        return sb.toString();
    }
}