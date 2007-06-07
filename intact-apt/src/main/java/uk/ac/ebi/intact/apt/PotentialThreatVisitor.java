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
package uk.ac.ebi.intact.apt;

import com.sun.mirror.declaration.*;
import com.sun.tools.apt.mirror.declaration.EnumConstantDeclarationImpl;

import java.util.Collection;
import java.util.Map;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-Oct-2006</pre>
 */
public class PotentialThreatVisitor extends AnnotationDeclarationVisitorCollector {

    int counter = 1;

    public void print()
    {
        System.out.println("\nPotential Threats:");
        System.out.println("--------------------");

        for (MethodDeclaration decl : this.getCollectedMethodDeclations())
        {
            printMethodPotentialThread(decl);
        }

        System.out.println("--------------------");
        System.out.println(this.getCollectedMethodDeclations().size()+" methods with potential threats\n");
    }

    private void printMethodPotentialThread(MethodDeclaration d)
    {
        System.out.println("[PT-"+(counter++)+"] "+d.getDeclaringType() + "." + d.getSimpleName() +
                " (" + d.getPosition().line() + ")");

        for (AnnotationMirror annot : d.getAnnotationMirrors())
        {
            for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : annot.getElementValues().entrySet())
            {
                System.out.println("\t"+entry.getKey().getSimpleName()+": "+entry.getValue().getValue());
            }
        }
        /*
       System.out.println("Method simpleName    " + d.getSimpleName());
       System.out.println("Method docComment    " + d.getDocComment());
       System.out.println("Method returnType    " + d.getReturnType());
       System.out.println("Method parameter     " + d.getParameters());
       System.out.println("Method declaringType " + d.getDeclaringType());
       printAnnotationMirrors(d.getAnnotationMirrors()); */
    }
}
