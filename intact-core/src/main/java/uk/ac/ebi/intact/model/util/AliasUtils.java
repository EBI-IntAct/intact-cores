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
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.util.ClassUtils;

/**
 * Utils with xrefs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AliasUtils {

    public static <A extends Alias> A createAliasGeneName(AnnotatedObject<?,A> parent, String name) {
        CvAliasType cvGeneName = CvObjectUtils.createCvObject(parent.getOwner(), CvAliasType.class, CvAliasType.GENE_NAME_MI_REF, CvAliasType.GENE_NAME);

        A alias = (A) newAliasInstanceFor(parent.getClass());
        alias.setOwner(parent.getOwner());
        alias.setParent(parent);
        alias.setCvAliasType(cvGeneName);
        alias.setName(name);

        return alias;
    }

    public static <A extends Alias> A newAliasInstanceFor(Class<? extends AnnotatedObject> aoClass) {
        Class<A> aliasClass = (Class<A>) AnnotatedObjectUtils.getAliasClassType(aoClass);
        return ClassUtils.newInstance(aliasClass);
    }
}