package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.*;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelperTest extends IntactBasicTestCase {

    @Test
    public void saveOrUpdate_default() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        PersisterHelper.saveOrUpdate(experiment);

        beginTransaction();
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        commitTransaction();
    }

    @Test
    public void saveOrUpdate_transactionAlreadyOpened() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        beginTransaction();
        PersisterHelper.saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        commitTransaction();
    }

    @Test
    public void persisterFor() throws Exception {
        Assert.assertEquals(BioSourcePersister.class, PersisterHelper.persisterFor(BioSource.class).getClass());
        Assert.assertEquals(ComponentPersister.class, PersisterHelper.persisterFor(Component.class).getClass());
        Assert.assertEquals(CvObjectPersister.class, PersisterHelper.persisterFor(CvObject.class).getClass());
        Assert.assertEquals(ExperimentPersister.class, PersisterHelper.persisterFor(Experiment.class).getClass());
        Assert.assertEquals(FeaturePersister.class, PersisterHelper.persisterFor(Feature.class).getClass());
        Assert.assertEquals(InstitutionPersister.class, PersisterHelper.persisterFor(Institution.class).getClass());
        Assert.assertEquals(InteractionPersister.class, PersisterHelper.persisterFor(Interaction.class).getClass());
        Assert.assertEquals(InteractorPersister.class, PersisterHelper.persisterFor(Interactor.class).getClass());
        Assert.assertEquals(PublicationPersister.class, PersisterHelper.persisterFor(Publication.class).getClass());
    }
}
