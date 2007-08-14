package uk.ac.ebi.intact.core.persister.standard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentPersisterTest extends AbstractPersisterTest
{

    @Test
    public void testPersistComponent_default() throws Exception {

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        Component component = interaction.getComponents().iterator().next();

        beginTransaction();
        ComponentPersister.getInstance().saveOrUpdate(component);
        ComponentPersister.getInstance().commit();
        commitTransaction();

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);


        beginTransaction();

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertNotNull(newComponent.getCvBiologicalRole());

        assertFalse(newComponent.getCvExperimentalRole().getXrefs().isEmpty());
    }

    @Override
    protected IntactMockBuilder getMockBuilder() {
        return new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
    }
}