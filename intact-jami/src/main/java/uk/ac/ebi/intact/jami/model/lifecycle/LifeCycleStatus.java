/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.jami.model.lifecycle;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

/**
 * Enum of default lifeccyle status.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public enum LifeCycleStatus {

    PUB_STATUS("publication status", "PL:0001"),
    NEW("new", "PL:0004"),
    RESERVED("reserved", "PL:0005"),
    ASSIGNED("assigned", "PL:0006"),
    CURATION_IN_PROGRESS("curation in progress", "PL:0007"),
    READY_FOR_CHECKING("ready for checking", "PL:0008"),
    ACCEPTED("accepted", "PL:0009"),
    ACCEPTED_ON_HOLD("accepted on hold", "PL:0010"),
    READY_FOR_RELEASE("ready for release", "PL:0011"),
    RELEASED("released", "PL:0012"),
    DISCARDED("discarded", "PL:0013");

    private String shortLabel;
    private String identifier;

    private LifeCycleStatus(String shortLabel, String identifier) {
        this.shortLabel = shortLabel;
        this.identifier = identifier;
    }

    public String shortLabel() {
        return shortLabel;
    }

    public String identifier() {
        return identifier;
    }

    public CvTerm toCvTerm(){
        return IntactUtils.createLifecycleStatus(this.shortLabel);
    }

    public static LifeCycleStatus toLifeCycleStatus(CvTerm status){
        if (status.getShortName().equals(LifeCycleStatus.ACCEPTED.shortLabel())){
            return LifeCycleStatus.ACCEPTED;
        }
        else if (status.getShortName().equals(LifeCycleStatus.ASSIGNED.shortLabel())){
            return LifeCycleStatus.ASSIGNED;
        }
        else if (status.getShortName().equals(LifeCycleStatus.ACCEPTED_ON_HOLD.shortLabel())){
            return LifeCycleStatus.ACCEPTED_ON_HOLD;
        }
        else if (status.getShortName().equals(LifeCycleStatus.NEW.shortLabel())){
            return LifeCycleStatus.NEW;
        }
        else if (status.getShortName().equals(LifeCycleStatus.RESERVED.shortLabel())){
            return LifeCycleStatus.RESERVED;
        }
        else if (status.getShortName().equals(LifeCycleStatus.DISCARDED.shortLabel())){
            return LifeCycleStatus.DISCARDED;
        }
        else if (status.getShortName().equals(LifeCycleStatus.CURATION_IN_PROGRESS.shortLabel())){
            return LifeCycleStatus.CURATION_IN_PROGRESS;
        }
        else if (status.getShortName().equals(LifeCycleStatus.RELEASED.shortLabel())){
            return LifeCycleStatus.RELEASED;
        }
        else if (status.getShortName().equals(LifeCycleStatus.READY_FOR_CHECKING.shortLabel())){
            return LifeCycleStatus.READY_FOR_CHECKING;
        }
        else if (status.getShortName().equals(LifeCycleStatus.READY_FOR_RELEASE.shortLabel())){
            return LifeCycleStatus.READY_FOR_RELEASE;
        }
        else{
            return LifeCycleStatus.PUB_STATUS;
        }
    }
}
