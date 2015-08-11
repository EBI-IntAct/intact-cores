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
 * Enum of default CvLifecycleEvents.
 *
 */
public enum LifeCycleEventType {

    LIFECYCLE_EVENT("lifecycle event", "PL:0002"),
    DISCARDED("discarded", "PL:0014"),
    OWNER_CHANGED("owner changed", "PL:0015"),
    CREATED("created", "PL:0016"),
    RESERVED("reserved", "PL:0017"),
    SELF_ASSIGNED("self assigned", "PL:0018"),
    ASSIGNED("assigned", "PL:0019"),
    CURATION_STARTED("curation started", "PL:0020"),
    ASSIGNMENT_DECLINED("assignment declined", "PL:0021"),
    READY_FOR_CHECKING("ready for checking", "PL:0022"),
    SANITY_CHECK_FAILED("sanity check failed", "PL:0023"),
    ACCEPTED("accepted", "PL:0024"),
    REJECTED("rejected", "PL:0025"),
    READY_FOR_RELEASE("ready for release", "PL:0027"),
    RELEASED("released", "PL:0028"),
    REVIEWER_CHANGED("reviewer changed", "PL:0029"),
    PUT_ON_HOLD("put on hold", "PL:0030");

    private String shortLabel;
    private String identifier;

    private LifeCycleEventType(String shortLabel, String identifier) {
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
        return  IntactUtils.createLifecycleEvent(this.shortLabel);
    }

    public static LifeCycleEventType toLifeCycleEventType(CvTerm event){
        if (event.getShortName() == null){
            return LifeCycleEventType.LIFECYCLE_EVENT;
        }
        else if (event.getShortName().equals(LifeCycleEventType.ACCEPTED.shortLabel())){
            return LifeCycleEventType.ACCEPTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.ASSIGNED.shortLabel())){
            return LifeCycleEventType.ASSIGNED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.ASSIGNMENT_DECLINED.shortLabel())){
            return LifeCycleEventType.ASSIGNMENT_DECLINED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.CREATED.shortLabel())){
            return LifeCycleEventType.CREATED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.CURATION_STARTED.shortLabel())){
            return LifeCycleEventType.CURATION_STARTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.DISCARDED.shortLabel())){
            return LifeCycleEventType.DISCARDED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.OWNER_CHANGED.shortLabel())){
            return LifeCycleEventType.OWNER_CHANGED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.PUT_ON_HOLD.shortLabel())){
            return LifeCycleEventType.PUT_ON_HOLD;
        }
        else if (event.getShortName().equals(LifeCycleEventType.READY_FOR_CHECKING.shortLabel())){
            return LifeCycleEventType.READY_FOR_CHECKING;
        }
        else if (event.getShortName().equals(LifeCycleEventType.READY_FOR_RELEASE.shortLabel())){
            return LifeCycleEventType.READY_FOR_RELEASE;
        }
        else if (event.getShortName().equals(LifeCycleEventType.REJECTED.shortLabel())){
            return LifeCycleEventType.REJECTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.RELEASED.shortLabel())){
            return LifeCycleEventType.RELEASED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.RESERVED.shortLabel())){
            return LifeCycleEventType.RESERVED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.REVIEWER_CHANGED.shortLabel())){
            return LifeCycleEventType.REVIEWER_CHANGED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.SANITY_CHECK_FAILED.shortLabel())){
            return LifeCycleEventType.SANITY_CHECK_FAILED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.SELF_ASSIGNED.shortLabel())){
            return LifeCycleEventType.SELF_ASSIGNED;
        }
        else{
            return LifeCycleEventType.LIFECYCLE_EVENT;
        }
    }
}
