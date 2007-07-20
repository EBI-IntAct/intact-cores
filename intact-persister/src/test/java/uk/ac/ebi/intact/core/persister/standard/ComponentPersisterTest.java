package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersistenceContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentPersisterTest extends IntactAbstractTestCase
{
    private ExperimentPersister persister;

    @Before
    public void prepare() {
        persister = ExperimentPersister.getInstance();
    }

    @After
    public void after() {
        persister = null;
        PersistenceContext.getInstance().clear();
    }

    @Test
    @IntactUnitDataset(dataset = PsiTestDatasetProvider.INTACT_JUL_06, provider = PsiTestDatasetProvider.class)
    public void testPersistComponent_existingSources() throws Exception {
        Interaction interaction = getDaoFactory().getInteractionDao().getAll(0,1).iterator().next();
        Interactor interactor = getDaoFactory().getInteractorDao().getAll(0,1).iterator().next();
        CvExperimentalRole expRole = getDaoFactory().getCvObjectDao(CvExperimentalRole.class).getAll(0,1).iterator().next();
        CvBiologicalRole bioRole = getDaoFactory().getCvObjectDao(CvBiologicalRole.class).getAll(0,1).iterator().next();

        Component component = new Component(IntactContext.getCurrentInstance().getInstitution(), interaction, interactor, expRole, bioRole);

        ComponentPersister.getInstance().saveOrUpdate(component);
        ComponentPersister.getInstance().commit();

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);

        commitTransaction();
        beginTransaction();

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertNotNull(newComponent.getCvBiologicalRole());
    }

    @Test
    @IntactUnitDataset(dataset = PsiTestDatasetProvider.INTACT_JUL_06, provider = PsiTestDatasetProvider.class)
    public void testPersistComponent_newExpRole() throws Exception {
        Interaction interaction = getDaoFactory().getInteractionDao().getAll(0,1).iterator().next();
        Interactor interactor = getDaoFactory().getInteractorDao().getAll(0,1).iterator().next();

        final String newExpRoleLabel = "NEW_EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole(getIntactContext().getInstitution(), newExpRoleLabel);

        CvBiologicalRole bioRole = getDaoFactory().getCvObjectDao(CvBiologicalRole.class).getAll(0,1).iterator().next();

        Component component = new Component(IntactContext.getCurrentInstance().getInstitution(), interaction, interactor, expRole, bioRole);

        ComponentPersister.getInstance().saveOrUpdate(component);
        ComponentPersister.getInstance().commit();

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);

        commitTransaction();
        beginTransaction();

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertEquals(newExpRoleLabel, newComponent.getCvExperimentalRole().getShortLabel());
        assertNotNull(newComponent.getCvBiologicalRole());
    }
}