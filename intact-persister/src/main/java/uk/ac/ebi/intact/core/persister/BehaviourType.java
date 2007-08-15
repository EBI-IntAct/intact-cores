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
package uk.ac.ebi.intact.core.persister;

/**
 * Defines the behaviour of a syncing method. For instance, when checking if an entity exists in the database
 * we can have different behaviours depending on the outcome.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public enum BehaviourType {

    /**
     * The candidate entity is a new one and must be persisted.
     */
    NEW,

    /**
     * The candidate entity is the same than the one in the database, but should be updated.
     */
    UPDATE,

    /**
     * The candidate entity already exists, so no action needs to be taken.
     */
    IGNORE

}