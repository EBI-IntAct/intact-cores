package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_ComponentTest extends IntactBasicTestCase
{

    @Test
    public void persist_default() throws Exception {
        Component component = getMockBuilder().createComponentRandom();
        PersisterHelper.saveOrUpdate(component);

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertNotNull(newComponent.getCvBiologicalRole());

        Assert.assertFalse(newComponent.getParticipantDetectionMethods().isEmpty());
        Assert.assertFalse(newComponent.getExperimentalPreparations().isEmpty());

        assertFalse(newComponent.getCvExperimentalRole().getXrefs().isEmpty());
    }

    @Test
    public void persistComponent_detached() throws Exception {
        Component component = getMockBuilder().createDeterministicInteraction().getComponents().iterator().next();
        PersisterHelper.saveOrUpdate(component);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        getDaoFactory().getEntityManager().clear();
        getDaoFactory().getEntityManager().close();

        Feature feature = getMockBuilder().createFeatureRandom();
        component.addBindingDomain(feature);

        PersisterHelper.saveOrUpdate(component);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Component comp2 = reloadByAc(component);

        Assert.assertEquals(2, comp2.getBindingDomains().size());
    }

    private Component reloadByAc(Component interaction) {
        return getDaoFactory().getComponentDao().getByAc(interaction.getAc());
    }

    private void refresh(Component interaction) {
        getDaoFactory().getComponentDao().refresh(interaction);
    }

}