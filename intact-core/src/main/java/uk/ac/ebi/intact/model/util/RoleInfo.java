/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.CvBiologicalRole;
import uk.ac.ebi.intact.model.CvExperimentalRole;

/**
 * Wrapper for both Biological and Experimental roles, that can decide which is the
 * relevant name to show in an interface
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RoleInfo {

    public static final String RELEVANT_SEPARATOR = "/";

    private CvExperimentalRole experimentalRole;
    private CvBiologicalRole biologicalRole;
    private String experimentalRoleMi;
    private String biologicalRoleMi;
    private String relevantName;
    private String relevantNameMi;

    public RoleInfo(CvBiologicalRole biologicalRole, CvExperimentalRole experimentalRole) {
        this.biologicalRole = biologicalRole;
        this.experimentalRole = experimentalRole;
    }

    public CvBiologicalRole getBiologicalRole() {
        return biologicalRole;
    }

    public CvExperimentalRole getExperimentalRole() {
        return experimentalRole;
    }

    public String getBiologicalRoleMi() {
        if (biologicalRoleMi != null) {
            return biologicalRoleMi;
        }

        biologicalRoleMi = CvObjectUtils.getPsiMiIdentityXref(biologicalRole).getPrimaryId();

        return biologicalRoleMi;
    }

    public String getExperimentalRoleMi() {
        if (experimentalRoleMi != null) {
            return experimentalRoleMi;
        }

        experimentalRoleMi = CvObjectUtils.getPsiMiIdentityXref(experimentalRole).getPrimaryId();

        return experimentalRoleMi;
    }

    public String getRelevantName() {
        if (relevantName != null) {
            return relevantName;
        }

        if (isExperimentalRoleUnspecified() && isBiologicalRoleUnspecified()) {
            relevantName = CvExperimentalRole.UNSPECIFIED;
        } else if (isExperimentalRoleUnspecified() && !isBiologicalRoleUnspecified()) {
            relevantName = getBiologicalRole().getShortLabel();
        } else if (!isExperimentalRoleUnspecified() && isBiologicalRoleUnspecified()) {
            relevantName = getExperimentalRole().getShortLabel();
        } else if (!isExperimentalRoleUnspecified() && !isBiologicalRoleUnspecified()) {
            relevantName = getExperimentalRole().getShortLabel()+ RELEVANT_SEPARATOR +getBiologicalRole().getShortLabel();
        }

        return relevantName;
    }

    public String getRelevantMi() {
        if (relevantNameMi != null) {
            return relevantNameMi;
        }

        if (isExperimentalRoleUnspecified() && isBiologicalRoleUnspecified()) {
            relevantNameMi = CvExperimentalRole.UNSPECIFIED_PSI_REF;
        } else if (isExperimentalRoleUnspecified() && !isBiologicalRoleUnspecified()) {
            relevantNameMi = getBiologicalRoleMi();
        } else if (!isExperimentalRoleUnspecified() && isBiologicalRoleUnspecified()) {
            relevantNameMi = getExperimentalRoleMi();
        } else if (!isExperimentalRoleUnspecified() && !isBiologicalRoleUnspecified()) {
            relevantNameMi = getExperimentalRoleMi()+ RELEVANT_SEPARATOR +getBiologicalRoleMi();
        }

        return relevantNameMi;
    }

    private boolean isExperimentalRoleUnspecified() {
        return getExperimentalRoleMi().equals(CvExperimentalRole.UNSPECIFIED_PSI_REF);
    }

    private boolean isBiologicalRoleUnspecified() {
        return getBiologicalRoleMi().equals(CvBiologicalRole.UNSPECIFIED_PSI_REF);
    }
}