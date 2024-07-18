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
package uk.ac.ebi.intact.core.batch.reader;

import org.springframework.batch.item.database.JpaPagingItemReader;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public class ComplexReader extends JpaPagingItemReader {

    public ComplexReader() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO: review where this ready is used.
        //  Do we need to remove the annotation check?
        String query = "select i from InteractionImpl i join i.annotations as annot " +
                "where annot.cvTopic.shortLabel = 'curated-complex' " +
                "order by i.ac";

        setQueryString(query);

        super.afterPropertiesSet();
    }
}
