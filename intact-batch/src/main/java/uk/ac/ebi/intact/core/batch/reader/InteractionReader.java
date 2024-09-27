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
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionReader extends JpaPagingItemReader {

    private boolean excludeNegative = false;


    public InteractionReader() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String query = "select i " +
                "from InteractionImpl i " +
                "where not exists " +
                "(select i2 " +
                "from InteractionImpl i2 " +
                "left join i2.annotations as a1 " +
                "where i2.ac = i.ac " +
                "and (a1.cvTopic.shortLabel = 'curated-complex' or i2.predictedComplex is true))";

        if (isExcludeNegative()) {
            query = query + " and not exists " +
                    "(select i3 from InteractionImpl i3 join i3.annotations as annot where i3.ac = i.ac and annot.cvTopic.shortLabel = 'negative') " +
                    "order by i.ac";
        }
        else {
            query += " order by i.ac";
        }

        setQueryString(query);

        super.afterPropertiesSet();
    }

    public boolean isExcludeNegative() {
        return excludeNegative;
    }

    public void setExcludeNegative(boolean excludeNegative) {
        this.excludeNegative = excludeNegative;
    }
}
