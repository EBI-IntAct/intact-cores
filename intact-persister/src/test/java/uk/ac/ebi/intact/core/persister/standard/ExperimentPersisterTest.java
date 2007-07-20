package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersisterTest extends IntactAbstractTestCase
{
    private ExperimentPersister persister;

    @Before
    public void prepare() {
        persister = ExperimentPersister.getInstance();
    }

    @After
    public void after() {
        persister = null;
        PersisterContext.getInstance().clear();
    }

    @Test
    @IntactUnitDataset(dataset = PsiTestDatasetProvider.INTACT_JUL_06, provider = PsiTestDatasetProvider.class)
    public void testPersistExperimentPublication() throws Exception {
        BioSource bioSource = getDaoFactory().getBioSourceDao().getByTaxonIdUnique("9606");
        CvInteraction cvInteraction = getIntactContext().getCvContext().getByMiRef(CvInteraction.class, "MI:0096");

        assertNotNull(bioSource);
        assertNotNull(cvInteraction);

        String expLabel = "expLabel";
        Experiment exp = new Experiment(getIntactContext().getInstitution(), expLabel, bioSource);
        exp.setCvInteraction(cvInteraction);

        String pubLabel = "1234567";
        Publication publication = new Publication(getIntactContext().getInstitution(), pubLabel);
        exp.setPublication(publication);

        persister.saveOrUpdate(exp);
        persister.commit();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel(expLabel);
        assertNotNull(refreshedExperiment);

        assertNotNull(refreshedExperiment.getPublication());
        assertEquals(pubLabel, refreshedExperiment.getPublication().getShortLabel());
    }
}
