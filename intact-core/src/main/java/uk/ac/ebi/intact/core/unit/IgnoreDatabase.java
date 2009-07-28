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
package uk.ac.ebi.intact.core.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>When using this annotation, no action is taken to prepare the database by default. If this is applied
 * at type level, but a method uses an @IntactUnitDataset, the database will be prepared acording to the dataset</p>
 * <p>this annotation is used for those types/methods which do not need to use the database, but that
 * extend IntactAbstractTestCase</p>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Target(value = {ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface IgnoreDatabase {

}