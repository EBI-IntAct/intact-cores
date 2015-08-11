/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util.filter;

import org.apache.commons.collections.Predicate;
import uk.ac.ebi.intact.model.IntactObject;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactObjectFilterPredicate implements Predicate {

    private IntactObjectFilter filter;

    public IntactObjectFilterPredicate(IntactObjectFilter filter) {
        this.filter = filter;
    }

    /**
     * Use the specified parameter to perform a test that returns true or false.
     *
     * @param object  the object to evaluate, should not be changed
     * @return true or false
     * @throws ClassCastException (runtime) if the input is the wrong class
     */
    public boolean evaluate(Object object) {
        if (object == null) return false;
        
        if (!(object instanceof IntactObject)) {
            throw new ClassCastException("Expecting IntactObject");
        }

        return filter.accept((IntactObject)object);
    }
}
