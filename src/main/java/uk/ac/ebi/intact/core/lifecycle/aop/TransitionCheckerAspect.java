/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.lifecycle.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.core.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.Publication;

/**
 * Checks if the transitions are legal for the passed publication.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Aspect
@Component
public class TransitionCheckerAspect {

    @Around("@annotation(lifecycleTransition)")
     public Object checkTransition(ProceedingJoinPoint pjp, LifecycleTransition lifecycleTransition) throws Throwable{

        // get the publication from the arguments
        for (Object obj : pjp.getArgs()) {
            if (obj instanceof Publication) {
                Publication publication = (Publication) obj;

                if (!publication.getStatus().getIdentifier().equals(lifecycleTransition.fromStatus().identifier())) {
                    throw new IllegalTransitionException("Transition '"+pjp.getSignature().getName()+"' cannot be applied to publication '"+ DebugUtil.annotatedObjectToString(publication, false)+
                            "' with state: '"+DebugUtil.cvObjectToSimpleString(publication.getStatus())+"'");
                }
            }
        }

        return pjp.proceed();
    }

}
