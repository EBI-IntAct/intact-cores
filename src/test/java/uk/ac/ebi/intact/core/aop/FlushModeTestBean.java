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

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.IntactFlushMode;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

/**
* @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
@Component
public class FlushModeTestBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @IntactFlushMode(FlushModeType.COMMIT)
    public FlushModeType modeCommit() throws Exception {
        return entityManager.getFlushMode();
    }

    @Transactional
    @IntactFlushMode(FlushModeType.AUTO)
    public FlushModeType modeAuto() throws Exception {
        return entityManager.getFlushMode();
    }

    @Transactional
    public FlushModeType modeDefault() throws Exception {
        return entityManager.getFlushMode();
    }

}
