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
package uk.ac.ebi.intact.core.persister.standard;

import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class FeaturePersister extends AbstractAnnotatedObjectPersister<Feature>{

    private static ThreadLocal<FeaturePersister> instance = new ThreadLocal<FeaturePersister>() {
        @Override
        protected FeaturePersister initialValue() {
            return new FeaturePersister();
        }
    };

    public static FeaturePersister getInstance() {
        return instance.get();
    }

    public FeaturePersister() {
        super();
    }

    protected Feature fetchFromDataSource(Feature intactObject) {
       // Note: we do not try to reconnect to the database, we create a new one every time.
        return null;
    }

    @Override
    protected void saveOrUpdateAttributes(Feature intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        ComponentPersister.getInstance().saveOrUpdate(intactObject.getComponent());

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        if (intactObject.getCvFeatureIdentification() != null) {
            cvObjectPersister.saveOrUpdate(intactObject.getCvFeatureIdentification());
        }

        if (intactObject.getCvFeatureType() != null) {
            cvObjectPersister.saveOrUpdate(intactObject.getCvFeatureType());
        }

        for (Range range : intactObject.getRanges()) {
            if ( range.getFromCvFuzzyType() != null ) {
                cvObjectPersister.saveOrUpdate(range.getFromCvFuzzyType());
            }
            if ( range.getToCvFuzzyType() != null ) {
                cvObjectPersister.saveOrUpdate(range.getToCvFuzzyType());
            }
        }
    }

    @Override
    protected Feature syncAttributes(Feature intactObject) {

        ComponentPersister compPersister = ComponentPersister.getInstance();
        Component component = compPersister.syncIfTransient(intactObject.getComponent());
        intactObject.setComponent(component);

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        if (intactObject.getCvFeatureIdentification() != null) {
            CvFeatureIdentification syncedFeatureDetMethod = (CvFeatureIdentification) cvObjectPersister.syncIfTransient(intactObject.getCvFeatureIdentification());
            intactObject.setCvFeatureIdentification(syncedFeatureDetMethod);
        }

        if (intactObject.getCvFeatureType() != null) {
            CvFeatureType syncedFeatureType = (CvFeatureType) cvObjectPersister.syncIfTransient(intactObject.getCvFeatureType());
            intactObject.setCvFeatureType(syncedFeatureType);
        }
        
        // Note: Ranges are never synced from the database as they are always new

        for (Range range : intactObject.getRanges()) {
            if ( range.getFromCvFuzzyType() != null ) {
                CvFuzzyType synchedFromFuzzyType = (CvFuzzyType) cvObjectPersister.syncIfTransient(range.getFromCvFuzzyType());
                range.setFromCvFuzzyType(synchedFromFuzzyType);
            }

            if ( range.getToCvFuzzyType() != null ) {
                CvFuzzyType syncedToCvFuzzyType = (CvFuzzyType) cvObjectPersister.syncIfTransient(range.getToCvFuzzyType());
                range.setToCvFuzzyType(syncedToCvFuzzyType);
            }

            range.setOwner(InstitutionPersister.getInstance().syncIfTransient(range.getOwner()));
        }

        return super.syncAttributes(intactObject);
    }

    /**
     * Returs true if the features provided contain the same ranges
     */
    protected static boolean haveSameRanges(Feature feature1, Feature feature2) {
        final Collection<Range> ranges1 = feature1.getRanges();
        final Collection<Range> ranges2 = feature2.getRanges();

        if (ranges1.size() != ranges2.size()) {
            return false;
        }

        for (Range r1 : ranges1) {
            boolean found = false;

            String fromFuzzyType1 = CvObjectUtils.getPsiMiIdentityXref(r1.getFromCvFuzzyType()).getPrimaryId();
            String toFuzzyType1 = CvObjectUtils.getPsiMiIdentityXref(r1.getToCvFuzzyType()).getPrimaryId();
            int fromIntervalStart1 = r1.getFromIntervalStart();
            int fromIntervalEnd1 = r1.getFromIntervalEnd();
            int toIntervalStart1 = r1.getToIntervalStart();
            int toIntervalEnd1 = r1.getToIntervalEnd();


            for (Range r2 : ranges2) {
                String fromFuzzyType2 = CvObjectUtils.getPsiMiIdentityXref(r2.getFromCvFuzzyType()).getPrimaryId();
                String toFuzzyType2 = CvObjectUtils.getPsiMiIdentityXref(r2.getToCvFuzzyType()).getPrimaryId();
                int fromIntervalStart2 = r2.getFromIntervalStart();
                int fromIntervalEnd2 = r2.getFromIntervalEnd();
                int toIntervalStart2 = r2.getToIntervalStart();
                int toIntervalEnd2 = r2.getToIntervalEnd();

                if (fromFuzzyType1.equals(fromFuzzyType2) &&
                    toFuzzyType1.equals(toFuzzyType2) &&
                        fromIntervalStart1 == fromIntervalStart2 &&
                        fromIntervalEnd1 == fromIntervalEnd2 &&
                        toIntervalStart1 == toIntervalStart2 &&
                        toIntervalEnd1 == toIntervalEnd2) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }
}