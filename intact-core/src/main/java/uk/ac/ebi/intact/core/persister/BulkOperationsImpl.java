/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilities to do bulk operations on the database.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class BulkOperationsImpl implements BulkOperations {

    @Autowired
    @Qualifier("lazyCloner")
    private IntactCloner intactCloner;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Adds an annotation to all the passed annotated object ACs. The annotation is not shared and it is basically
     * a clone from the passed annotation.
     * @param annotation the annotation to copy to the annotated objects
     * @param acs array of ACs to modify
     * @param aoClass type of class of the ACs
     * @return the accessions that have been modified
     */
    @Override
    @Transactional
    public String[] addAnnotation(Annotation annotation, String[] acs, Class<? extends AnnotatedObject> aoClass) {
        Collection<String> updatedAcs = new ArrayList<String>(acs.length);

        for (String ac : acs) {
            Annotation clonedAnnotation = clone(annotation);

            AnnotatedObject ao = entityManager.find(aoClass, ac);

            if (ao != null) {
                updatedAcs.add(ac);

                ao.addAnnotation(clonedAnnotation);

                entityManager.persist(clonedAnnotation);
            }
        }

        return updatedAcs.toArray(new String[updatedAcs.size()]);

    }

    private <T extends IntactObject> T clone(T intactObject) {
        T clone = null;
        try {
            clone = (T) BeanUtils.cloneBean(intactObject);
            clone.setAc(null);
        } catch (Exception e) {
            throw new IntactException("Problem creating intact object clone: "+intactObject, e);
        }

        return clone;
    }

}
