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
package uk.ac.ebi.intact.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Contains the basic audit methods
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Sep-2006</pre>
 */
public interface Auditable extends Serializable {

    Date getCreated();

    void setCreated( java.util.Date created );

    Date getUpdated();

    void setUpdated( java.util.Date updated );

    String getCreator();

    void setCreator( String createdUser );

    String getUpdator();

    void setUpdator( String userStamp );

    long getVersion();

    void setVersion( long version );
}
