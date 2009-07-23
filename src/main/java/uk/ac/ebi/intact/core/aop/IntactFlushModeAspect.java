/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.core.annotations.IntactFlushMode;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Aspect
@Component
public class IntactFlushModeAspect {

    private static final Log log = LogFactory.getLog( IntactFlushModeAspect.class );

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager entityManager;

    @Around("@annotation(intactFlushMode)")
    public Object switchFlushMode(ProceedingJoinPoint pjp, IntactFlushMode intactFlushMode) throws Throwable{
        FlushModeType flushMode = entityManager.getFlushMode();
        FlushModeType newFlushMode = intactFlushMode.value();

        boolean switchedMode = newFlushMode != flushMode;

        if (switchedMode) {
            if (log.isDebugEnabled()) log.debug("Setting flush mode to: "+ newFlushMode);
            entityManager.setFlushMode(newFlushMode);
        }

        Object obj = null;
        try {
            obj = pjp.proceed();
        } finally {
            if (switchedMode) {
                if (log.isDebugEnabled()) log.debug("Resetting back flush mode to: " + flushMode);
                entityManager.setFlushMode(flushMode);
            }
        }

        return obj;
    }

}
