/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.apt.mockable;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitors;

import java.io.File;
import java.util.Collection;
import java.util.Set;


/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-Oct-2006</pre>
 */
public class MockableProcessor implements AnnotationProcessor {

    private final AnnotationProcessorEnvironment env;
    private final Set<AnnotationTypeDeclaration> atds;

    public MockableProcessor(Set<AnnotationTypeDeclaration> atds,
                             AnnotationProcessorEnvironment env
    ) {
        this.atds = atds;
        this.env = env;
        this.env.getMessager().printNotice("Starting annotation process");

    }

    public void process() {
        File targetDir = createTargetDir();

        for (AnnotationTypeDeclaration atd : atds) {
            env.getMessager().printNotice("Processing annotation " + atd);
            Collection<Declaration> decls = env.getDeclarationsAnnotatedWith(atd);
            for (Declaration decl : decls) {
                MockableVisitor visitor = new MockableVisitor(env, targetDir);

                decl.accept(DeclarationVisitors.getDeclarationScanner(visitor, DeclarationVisitors.NO_OP));

                try {
                    visitor.generateMock(decl.getSimpleName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File createTargetDir() {
        String s = env.getOptions().get("-s");

        // little hack to get the output dir target/classes, as the getOptions does not seem to work
        File outputDir = new File(s).getParentFile();
        File targetDir = new File(outputDir, "classes");

        System.out.println("Created dir: " + targetDir);

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        return targetDir;
    }
}