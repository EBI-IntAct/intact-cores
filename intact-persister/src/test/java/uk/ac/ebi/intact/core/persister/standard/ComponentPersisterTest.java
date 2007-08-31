package uk.ac.ebi.intact.core.persister.standard;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
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
        Component component = getMockBuilder().createComponentRandom();
        PersisterHelper.saveOrUpdate(component);

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);


        beginTransaction();

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertNotNull(newComponent.getCvBiologicalRole());

        Assert.assertFalse(newComponent.getParticipantDetectionMethods().isEmpty());
        Assert.assertFalse(newComponent.getExperimentalPreparations().isEmpty());

        assertFalse(newComponent.getCvExperimentalRole().getXrefs().isEmpty());
    }

}