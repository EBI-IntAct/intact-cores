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
package uk.ac.ebi.intact.annotation.util;

import static junit.framework.Assert.*;
import org.junit.Test;
import uk.ac.ebi.intact.annotation.EditorTopic;
import uk.ac.ebi.intact.annotation.Mockable;

import java.io.File;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationUtilTest {

    @Test
    public void getClassesWithAnnotationFromDir() throws Exception {
        File dir = new File(AnnotationUtilTest.class.getResource("/").getFile());
        dir = new File(dir.getParentFile(), "test-classes");
        Collection<Class> classes = AnnotationUtil.getClassesWithAnnotationFromDir(EditorTopic.class, dir);

        assertEquals("1 class has to be found in test dir: (dir used "+dir+")", 1, classes.size());
    }

    @Test
    public void getClassesWithAnnotationFromClasspathDirs() throws Exception {
        Collection<Class> classes = AnnotationUtil.getClassesWithAnnotationFromClasspathDirs(EditorTopic.class);

        assertEquals(1, classes.size());
    }

    @Test
    public void getClassesWithAnnotationFromClasspath() throws Exception {
        Collection<Class> classes = AnnotationUtil.getClassesWithAnnotationFromClasspath(EditorTopic.class);

        assertEquals(1, classes.size());
    }

    @Test
    public void isAnnotationPresent() throws Exception {
        assertTrue(AnnotationUtil.isAnnotationPresent(AnnotatedClass.class, EditorTopic.class));
        assertFalse(AnnotationUtil.isAnnotationPresent(AnnotatedClass.class, Mockable.class));
        assertTrue(AnnotationUtil.isAnnotationPresent(AnnotatedInterface.class, Mockable.class));
        assertFalse(AnnotationUtil.isAnnotationPresent(AnnotatedSubClass.class, EditorTopic.class));
    }

    @EditorTopic(name = "name")
    private class AnnotatedClass {}

    private class AnnotatedSubClass extends AnnotatedClass {}

    @Mockable
    private interface AnnotatedInterface {}
}