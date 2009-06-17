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
package uk.ac.ebi.intact.core.batch.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import uk.ac.ebi.intact.model.IntactObject;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactObjectLoggingItemWriter implements ItemWriter<IntactObject> {

    private static final Log log = LogFactory.getLog( IntactObjectLoggingItemWriter.class );

    private String logLevel;

    public void write(List<? extends IntactObject> items) throws Exception {
        for (IntactObject intactObject : items) {
            if ("fatal".equalsIgnoreCase(logLevel)) {
                if (log.isFatalEnabled()) log.fatal(intactObject);
            } else if ("error".equalsIgnoreCase(logLevel)) {
                if (log.isErrorEnabled()) log.error(intactObject);
            } else if ("info".equalsIgnoreCase(logLevel)) {
                if (log.isInfoEnabled()) log.info(intactObject);
            } else if ("debug".equalsIgnoreCase(logLevel)) {
                if (log.isDebugEnabled()) log.debug(intactObject);
            } else {
                if (log.isTraceEnabled()) log.trace(intactObject);
            }
        }
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}

