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
public class ExperimentReader extends JpaPagingItemReader {

    private boolean excludeInferredByCurators = false;

    public ExperimentReader() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String query = "select e from Experiment e ";

        if (isExcludeInferredByCurators()) {
            query = query + " where e.cvInteraction.shortName <> 'inferred by curator' " +
                    "order by e.ac";
        }
        else {
            query += " order by e.ac";
        }

        setQueryString(query);

        super.afterPropertiesSet();
    }

    public boolean isExcludeInferredByCurators() {
        return this.excludeInferredByCurators;
    }

    public void setExcludeInferredByCurators(boolean excludeInferred) {
        this.excludeInferredByCurators = excludeInferred;
    }
}
