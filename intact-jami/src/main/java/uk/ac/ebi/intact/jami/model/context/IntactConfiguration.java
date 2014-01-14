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
package uk.ac.ebi.intact.jami.model.context;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.annotations.PersistentConfiguration;
import uk.ac.ebi.intact.core.annotations.PersistentProperty;
import uk.ac.ebi.intact.model.Institution;

/**
 * Intact Configuration.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
@Component
public class IntactConfiguration {

    private String acPrefix;
    private Institution defaultInstitution;
    private String localCvPrefix;
    private boolean autoUpdateExperimentLabel;
    private boolean autoUpdateInteractionLabel;
    private boolean skipSchemaCheck;

    public IntactConfiguration() {
        acPrefix = "UNK";
        localCvPrefix = "IA";
    }

    public String getAcPrefix() {
        return acPrefix;
    }

    public void setAcPrefix(String acPrefix) {
        this.acPrefix = acPrefix;
    }

    public Institution getDefaultInstitution() {
        return defaultInstitution;
    }

    public void setDefaultInstitution(Institution defaultInstitution) {
        this.defaultInstitution = defaultInstitution;
    }

    public String getLocalCvPrefix() {
        return localCvPrefix;
    }

    public void setLocalCvPrefix(String localCvPrefix) {
        this.localCvPrefix = localCvPrefix;
    }

    public boolean isAutoUpdateExperimentLabel() {
        return autoUpdateExperimentLabel;
    }

    public void setAutoUpdateExperimentLabel(boolean autoUpdateExperimentLabel) {
        this.autoUpdateExperimentLabel = autoUpdateExperimentLabel;
    }

    public boolean isAutoUpdateInteractionLabel() {
        return autoUpdateInteractionLabel;
    }

    public void setAutoUpdateInteractionLabel(boolean autoUpdateInteractionLabel) {
        this.autoUpdateInteractionLabel = autoUpdateInteractionLabel;
    }

    public boolean isSkipSchemaCheck() {
        return skipSchemaCheck;
    }

    public void setSkipSchemaCheck(boolean skipSchemaCheck) {
        this.skipSchemaCheck = skipSchemaCheck;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( 256 );
        sb.append( "IntactConfiguration" );
        sb.append( "{acPrefix='" ).append( acPrefix ).append( '\'' );
        sb.append( ", defaultInstitution=" ).append( defaultInstitution );
        sb.append( ", localCvPrefix='" ).append( localCvPrefix ).append( '\'' );
        sb.append( ", autoUpdateExperimentLabel=" ).append( autoUpdateExperimentLabel );
        sb.append( ", autoUpdateInteractionLabel=" ).append( autoUpdateInteractionLabel );
        sb.append( ", skipSchemaCheck=" ).append( skipSchemaCheck );
        sb.append( '}' );
        return sb.toString();
    }
}