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
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
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

    private CvTerm cvStatus;

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
        if (this.cvStatus == null){
            this.cvStatus = IntactUtils.createLifecycleStatus(this.shortLabel);
            ((IntactCvTerm)this.cvStatus).setIdentifier(this.identifier);
        }
        return this.cvStatus;
    }

    public void initCvTerm(CvTerm cvTerm){
        this.cvStatus = cvTerm;
        if (!cvTerm.getShortName().equals(this.shortLabel)){
            this.cvStatus = null;
        }
    }
}
