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
        Feature feature = null;

        if (intactObject.getAc() != null) {
            feature =  getIntactContext().getDataContext()
                    .getDaoFactory().getFeatureDao().getByAc(intactObject.getAc());
        }
        return feature;
    }

    @Override
    protected void saveOrUpdateAttributes(Feature intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate(intactObject.getCvFeatureType());

        if (intactObject.getCvFeatureIdentification() != null) {
            cvObjectPersister.saveOrUpdate(intactObject.getCvFeatureIdentification());
        }

        for (Range range : intactObject.getRanges()) {
            cvObjectPersister.saveOrUpdate(range.getFromCvFuzzyType());
            cvObjectPersister.saveOrUpdate(range.getToCvFuzzyType());
        }
    }

    @Override
    protected Feature syncAttributes(Feature intactObject) {
        Feature feature = super.syncAttributes(intactObject);

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        CvFeatureType syncedFeatureType = (CvFeatureType) cvObjectPersister.syncIfTransient(intactObject.getCvFeatureType());
        feature.setCvFeatureType(syncedFeatureType);

        if (intactObject.getCvFeatureIdentification() != null) {
            CvFeatureIdentification syncedFeatureDetMethod = (CvFeatureIdentification) cvObjectPersister.syncIfTransient(intactObject.getCvFeatureIdentification());
            feature.setCvFeatureIdentification(syncedFeatureDetMethod);
        }

        for (Range range : intactObject.getRanges()) {
            CvFuzzyType synchedFromFuzzyType = (CvFuzzyType) cvObjectPersister.syncIfTransient(range.getFromCvFuzzyType());
            range.setFromCvFuzzyType(synchedFromFuzzyType);

            CvFuzzyType syncedToCvFuzzyType = (CvFuzzyType) cvObjectPersister.syncIfTransient(range.getToCvFuzzyType());
            range.setToCvFuzzyType(syncedToCvFuzzyType);
        }

        return feature;
    }
}