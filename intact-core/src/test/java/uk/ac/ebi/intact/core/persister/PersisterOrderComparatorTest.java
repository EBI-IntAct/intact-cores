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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.InteractionImpl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterOrderComparatorTest extends IntactBasicTestCase {

    @Test
    public void compare() throws Exception {

        List<AnnotatedObject> annotatedObjects = new LinkedList<AnnotatedObject>();
        annotatedObjects.add(getMockBuilder().createFeatureRandom());
        annotatedObjects.add(getMockBuilder().createInteractionRandomBinary());
        annotatedObjects.add(getMockBuilder().createExperimentRandom(1));

        Collections.sort(annotatedObjects, new PersistenceOrderComparator());

        Assert.assertEquals(Experiment.class, annotatedObjects.get(0).getClass());
        Assert.assertEquals(InteractionImpl.class, annotatedObjects.get(1).getClass());
        Assert.assertEquals(Feature.class, annotatedObjects.get(2).getClass());
    }

}