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
package uk.ac.ebi.intact.context.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Sep-2006</pre>
 */
public class WebServiceSession extends StandaloneSession {

    private static final Log log = LogFactory.getLog( WebServiceSession.class );


    public WebServiceSession() {
        super();
    }

    public WebServiceSession( Properties properties ) {
        super( properties );
    }

    @Override
    public boolean isWebapp() {
        return true;
    }

    @Override
    public boolean isRequestAvailable() {
        return false;
    }
}
