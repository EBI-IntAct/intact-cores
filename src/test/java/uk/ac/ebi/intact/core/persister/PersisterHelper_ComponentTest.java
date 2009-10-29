package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.Feature;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


/**
 * Tests component after Persisting
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_ComponentTest extends IntactBasicTestCase
{

    private static final Log log = LogFactory.getLog( PersisterHelper_ComponentTest.class );


    @Test
    public void persist_default() throws Exception {
        Component component = getMockBuilder().createComponentRandom();
        getCorePersister().saveOrUpdate(component);

        String newComponentAc = component.getAc();
        assertNotNull(newComponentAc);

        Component newComponent = getDaoFactory().getComponentDao().getByAc(newComponentAc);

        assertNotNull(newComponent);
        assertNotNull(newComponent.getCvExperimentalRole());
        assertNotNull(newComponent.getCvBiologicalRole());

        Assert.assertFalse(newComponent.getParticipantDetectionMethods().isEmpty());
        Assert.assertFalse(newComponent.getExperimentalPreparations().isEmpty());
        Assert.assertFalse(newComponent.getParameters().isEmpty());

        assertFalse(newComponent.getCvExperimentalRole().getXrefs().isEmpty());
    }

    @Test
    public void persistComponent_detached() throws Exception {
        Component component = getMockBuilder().createDeterministicInteraction().getComponents().iterator().next();
        getCorePersister().saveOrUpdate(component);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(1, component.getBindingDomains().size());
        Assert.assertEquals(1, component.getParameters().size());

        getDaoFactory().getEntityManager().clear();
        getDaoFactory().getEntityManager().close();

        Feature feature = getMockBuilder().createFeatureRandom();
        component.addBindingDomain(feature);

        Assert.assertEquals(2, component.getBindingDomains().size());

        Assert.assertTrue(getDaoFactory().getBaseDao().isTransient(component));

        getCorePersister().saveOrUpdate(component);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Component comp2 = reloadByAc(component);

        Assert.assertEquals(2, comp2.getBindingDomains().size());
    }

    private Component reloadByAc(Component component) {
        return getDaoFactory().getComponentDao().getByAc(component.getAc());
    }

    @Test
    public void persistComponent_ExperimentalRoles() {

        CvExperimentalRole baitExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole neutralExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        Collection<CvExperimentalRole> baitNeutralExperimentalRoles = new ArrayList<CvExperimentalRole>();
        baitNeutralExperimentalRoles.add( baitExperimentalRole );
        baitNeutralExperimentalRoles.add( neutralExperimentalRole );

        Component baitNeutralComponent = getMockBuilder().createComponentBait( getMockBuilder().createDeterministicProtein( "P1", "baaa" ) );
        baitNeutralComponent.setExperimentalRoles(baitNeutralExperimentalRoles);
       
        getCorePersister().saveOrUpdate( baitNeutralComponent );

        Component reloadedComponent = reloadByAc(baitNeutralComponent);
        Assert.assertNotNull( reloadedComponent.getExperimentalRoles());
        Assert.assertEquals(2, reloadedComponent.getExperimentalRoles().size());

    }





}
