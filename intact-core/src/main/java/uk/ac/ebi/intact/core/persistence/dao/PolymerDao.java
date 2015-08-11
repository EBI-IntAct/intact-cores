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
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.PolymerImpl;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Mockable
public interface PolymerDao<T extends PolymerImpl> extends InteractorDao<T> {

    String getSequenceByPolymerAc( String polymerAc );

    /**
     * Gets all proteins with the passed CRC and tax id
     *
     * @since 1.9.0
     */
    List<T> getByCrcAndTaxId(String crc, String taxId);

    /**
     * Gets all proteins with the passed CRC
     *
     * @since 1.9.0
     */
    List<T> getByCrc(String crc);

}
