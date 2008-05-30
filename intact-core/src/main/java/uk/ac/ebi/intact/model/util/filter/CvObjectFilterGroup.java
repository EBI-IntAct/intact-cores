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
package uk.ac.ebi.intact.model.util.filter;

import uk.ac.ebi.intact.model.CvObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a set of inclusion/exclusions of CvObjects, by identifier or label
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectFilterGroup {

    private List<String> includedIdentifiers;
    private List<String> excludedIdentifiers;
    private List<String> includedLabels;
    private List<String> excludedLabels;

    private boolean includeByDefault;
    private boolean excludeByDefault;

    public CvObjectFilterGroup() {
        this(true, false);
    }

    public CvObjectFilterGroup(boolean includeByDefault, boolean excludeByDefault) {
        this.includeByDefault = includeByDefault;
        this.excludeByDefault = excludeByDefault;

        if (includeByDefault && excludeByDefault) {
            throw new IllegalArgumentException("It is not possible to include and exclude by default");
        }

        includedIdentifiers = new ArrayList<String>();
        excludedIdentifiers = new ArrayList<String>();
        includedLabels = new ArrayList<String>();
        excludedLabels = new ArrayList<String>();
    }

    public boolean isAccepted(String element) {
        if (includeByDefault) {
            if (!isAcceptedIdentifier(element)) return false;
            if (!isAcceptedLabel(element)) return false;
        }
        return isAcceptedIdentifier(element) || isAcceptedLabel(element);
    }

    public boolean isAcceptedIdentifier(String id) {
        return isAcceptedInLists(id, includedIdentifiers, excludedIdentifiers);
    }

    public boolean isAcceptedLabel(String label) {
        return isAcceptedInLists(label, includedLabels, excludedLabels);
    }

    protected boolean isAcceptedInLists(String element, List<String> inclusionList, List<String> exclusionList) {
        if (inclusionList.contains(element)) {
            return true;
        } else if (exclusionList.contains(element)) {
            return false;
        } else if (includeByDefault && !exclusionList.contains(element)) {
            return true;
        } 
        return includeByDefault;
    }

    public void addIncludedIdentifier(String id) {
        if (id != null) {
            includeByDefault = false;
            includedIdentifiers.add(id);
        }
    }

    public void addExcludedIdentifier(String id) {
        if (id != null) {
            excludeByDefault = false;
            excludedIdentifiers.add(id);
        }
    }

    public void addIncludedLabel(String label) {
        if (label != null) {
            includeByDefault = false;
            includedLabels.add(label);
        }
    }

    public void addExcludedLabel(String label) {
        if (label != null) {
            excludeByDefault = false;
            excludedLabels.add(label);
        }
    }

    public void addIncludedCvObject(CvObject cvObject) {
        if (cvObject != null) {
            includedIdentifiers.add(cvObject.getMiIdentifier());
            includedLabels.add(cvObject.getShortLabel());
        }
    }

    public void addExcludedCvObject(CvObject cvObject) {
        if (cvObject != null) {
            excludedIdentifiers.add(cvObject.getMiIdentifier());
            excludedLabels.add(cvObject.getShortLabel());
        }
    }

    public boolean isIncludeByDefault() {
        return includeByDefault;
    }

    public void setIncludeByDefault(boolean includeByDefault) {
        this.includeByDefault = includeByDefault;
    }

    public boolean isExcludeByDefault() {
        return excludeByDefault;
    }

    public void setExcludeByDefault(boolean excludeByDefault) {
        this.excludeByDefault = excludeByDefault;
    }
}
