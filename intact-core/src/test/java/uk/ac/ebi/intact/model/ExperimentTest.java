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
package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentTest extends IntactBasicTestCase{

    @Test
    public void equals_differentCvInteraction() throws Exception {
        Experiment exp1 = getMockBuilder().createDeterministicExperiment();
        exp1.getBioSource().setTaxId("9606");
        exp1.setCvInteraction(getMockBuilder().createCvObject(CvInteraction.class, "MI:0028", "unknown"));

        Experiment exp2 = new IntactCloner().clone(exp1);
        exp2.setCvInteraction(getMockBuilder().createCvObject(CvInteraction.class, "MI:0808", "unknown"));

        Assert.assertFalse(exp1.equals(exp2));
    }

    @Test
    public void synchShortlabelDisabled2() throws Exception {
        final Experiment e1 = getMockBuilder().createExperimentRandom( 1 );
        e1.setShortLabel( "nana-2008" );
        Assert.assertEquals( 1, e1.getXrefs().size() );
        String pmid = e1.getXrefs().iterator().next().getPrimaryId();
        e1.getPublication().setShortLabel( pmid );

        final Experiment e2 = getMockBuilder().createExperimentRandom( 2 );
        e2.setShortLabel( "nana-2008" );
        Assert.assertEquals( 1, e2.getXrefs().size() );
        e2.getXrefs().iterator().next().setPrimaryId( pmid );
        e2.getPublication().setShortLabel( pmid );

        getIntactContext().getConfig().setAutoUpdateExperimentLabel( false );

        // Careful here, we have to persist the experiment one after the other to ensure that label WOULD are synchronized.
        getCorePersister().saveOrUpdate( e1 );
        getCorePersister().saveOrUpdate( e2 );

        Assert.assertEquals( 2, getDaoFactory().getExperimentDao().countAll() );

        final Collection<Experiment> exps = getDaoFactory().getExperimentDao().getByShortLabelLike( "nana-2008%" );
        Assert.assertEquals( 2, exps.size() );
        for ( Experiment exp : exps ) {
            Assert.assertEquals("nana-2008", exp.getShortLabel());
        }

        getIntactContext().getConfig().setAutoUpdateExperimentLabel( true );

    }

    @Test
    public void synchShortlabelEnabled() throws Exception {
        final Experiment e1 = getMockBuilder().createExperimentRandom( 1 );
        e1.setShortLabel( "nana-2008" );
        Assert.assertEquals( 1, e1.getXrefs().size() );
        String pmid = e1.getXrefs().iterator().next().getPrimaryId();
        e1.getPublication().setShortLabel( pmid );

        final Experiment e2 = getMockBuilder().createExperimentRandom( 2 );
        e2.setShortLabel( "nana-2008" );
        Assert.assertEquals( 1, e2.getXrefs().size() );
        e2.getXrefs().iterator().next().setPrimaryId( pmid );
        e2.getPublication().setShortLabel( pmid );

        getIntactContext().getConfig().setAutoUpdateExperimentLabel( true );

        // Careful here, we have to persist the experiment one after the other to ensure that label are synchronized.
        getCorePersister().saveOrUpdate( e1 );
        getCorePersister().saveOrUpdate( e2 );

        Assert.assertEquals( 2, getDaoFactory().getExperimentDao().countAll() );

        final Experiment exp1 = getDaoFactory().getExperimentDao().getByShortLabel( "nana-2008-1" );
        Assert.assertNotNull( exp1 );

        final Experiment exp2 = getDaoFactory().getExperimentDao().getByShortLabel( "nana-2008-2" );
        Assert.assertNotNull( exp2 );
    }
}