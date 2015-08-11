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
import uk.ac.ebi.intact.model.MineInteraction;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Mockable
public interface MineInteractionDao {

    void persist( MineInteraction mineInteraction );

    int deleteAll();

    MineInteraction get( String proteinIntactAc1, String proteinIntactAc2 );

    int countAll();

    int countByProteinIntactAc( String proteinIntactAc );

    List<MineInteraction> getByProteinIntactAc( String proteinIntactAc, Integer firstResult, Integer maxResults );
}
