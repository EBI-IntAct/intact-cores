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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class FeaturePersisterTest extends AbstractPersisterTest{

    @Test
    public void persistFeature() throws Exception {
        Feature feature = getMockBuilder().createFeatureRandom();
        PersisterHelper.saveOrUpdate(feature);

        Assert.assertNotNull(feature.getCvFeatureType());
    }

    @Test
    public void persistFeature_sameLabelDifferentComponents() throws Exception {
        Feature feature1 = getMockBuilder().createFeatureRandom();
        Feature feature2 = getMockBuilder().createFeatureRandom();
        feature2.setShortLabel(feature1.getShortLabel());

        FeaturePersister.getInstance().saveOrUpdate(feature1);
        FeaturePersister.getInstance().saveOrUpdate(feature2);

        FeaturePersister.getInstance().commit();

        Assert.assertEquals(2, getDaoFactory().getFeatureDao().countAll());
    }

    @Test
    public void persistFeatureWithRangeWithoutFuzzyType() throws Exception {
        Feature feature = getMockBuilder().createFeatureRandom();

        final Range range = getMockBuilder().createRange( 1, 1, 2, 2 );
        range.setFromCvFuzzyType( null );
        range.setToCvFuzzyType( null );
        feature.addRange( range );

        PersisterHelper.saveOrUpdate(feature);
    }
}