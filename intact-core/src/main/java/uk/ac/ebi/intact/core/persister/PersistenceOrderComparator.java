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

import uk.ac.ebi.intact.model.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Persisting order matters and we use this comparator to sort the objects to persist
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersistenceOrderComparator implements Comparator<AnnotatedObject> {

    private Map<Class<? extends AnnotatedObject>,Integer> annotatedObjectPriorities;

    public PersistenceOrderComparator() {
        annotatedObjectPriorities = new HashMap<Class<? extends AnnotatedObject>,Integer>();

        // the higher priority (int), the earlier it will be saved
        annotatedObjectPriorities.put(Institution.class, 200);
        annotatedObjectPriorities.put(CvObject.class, 150);
        annotatedObjectPriorities.put(BioSource.class, 110);
        annotatedObjectPriorities.put(Publication.class, 100);
        annotatedObjectPriorities.put(Experiment.class, 90);
        annotatedObjectPriorities.put(InteractorImpl.class, 85);
        annotatedObjectPriorities.put(Interactor.class, 80);
        annotatedObjectPriorities.put(Component.class, 50);
        annotatedObjectPriorities.put(Feature.class, 40);

    }

    public int compare(AnnotatedObject o1, AnnotatedObject o2) {
        int o1Priority = getPriorityForClass(o1.getClass());
        int o2Priority = getPriorityForClass(o2.getClass());

        return o2Priority - o1Priority;
    }

    protected int getPriorityForClass(Class<? extends AnnotatedObject> clazz) {
        for (Class<? extends AnnotatedObject> aoClass : annotatedObjectPriorities.keySet()) {
             if (aoClass.isAssignableFrom(clazz)) {
                 return annotatedObjectPriorities.get(aoClass);
             }
        }

        return 0;
    }

}