/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.*;

/**
 * Builds string that allow to identify an intact object.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 1.8.0
 */
class KeyBuilder {

    public String keyFor( Institution institution ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Publication publication ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Experiment experiment ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Interaction interaction ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Interactor interactor ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Component component ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Feature feature ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( Range range ) {
        throw new UnsupportedOperationException( );
    }

    public String keyFor( CvObject cvObject ) {
        throw new UnsupportedOperationException( );
    }
}
