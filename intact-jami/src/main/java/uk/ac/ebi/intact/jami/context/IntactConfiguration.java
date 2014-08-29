/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.jami.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

/**
 * Intact Configuration.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
@Component(value = "intactJamiConfiguration")
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class IntactConfiguration {

    private String acPrefix;
    private IntactSource defaultInstitution;
    private String localCvPrefix;

    public IntactConfiguration() {
    }

    public String getAcPrefix() {
        if (acPrefix == null){
            acPrefix = "UNK";
        }
        return acPrefix;
    }

    public void setAcPrefix(String acPrefix) {
        this.acPrefix = acPrefix;
    }

    public IntactSource getDefaultInstitution() {
        if (defaultInstitution == null){
            defaultInstitution = new IntactSource("unknown");
        }
        return defaultInstitution;
    }

    public void setDefaultInstitution(IntactSource defaultInstitution) {
        this.defaultInstitution = defaultInstitution;
    }

    public String getLocalCvPrefix() {
        if (localCvPrefix == null){
            localCvPrefix = "IA";
        }
        return localCvPrefix;
    }

    public void setLocalCvPrefix(String localCvPrefix) {
        this.localCvPrefix = localCvPrefix;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( 256 );
        sb.append( "IntactConfiguration" );
        sb.append( "{acPrefix='" ).append( acPrefix ).append( '\'' );
        sb.append( ", defaultInstitution=" ).append( defaultInstitution );
        sb.append( ", localCvPrefix='" ).append( localCvPrefix ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
