package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
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
public class PersisterHelper_ComponentTest extends IntactBasicTestCase {

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
        TransactionStatus transactionStatus1 = getDataContext().beginTransaction();

        Component component = getMockBuilder().createDeterministicInteraction().getComponents().iterator().next();
        getCorePersister().saveOrUpdate(component);

        getDataContext().commitTransaction(transactionStatus1);
        TransactionStatus transactionStatus2 = getDataContext().beginTransaction();

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

        getDataContext().commitTransaction(transactionStatus2);

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

    @Test
    public void persistComponent_updateExpRole() {
        CvExperimentalRole baitExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );

        Component comp = getMockBuilder().createComponentBait( getMockBuilder().createDeterministicProtein( "P1", "baaa" ) );
        comp.getExperimentalRoles().clear();
        comp.getExperimentalRoles().add(baitExperimentalRole);

        getCorePersister().saveOrUpdate(comp);

        Component refreshed = getDaoFactory().getComponentDao().getByAc(comp.getAc());

        CvExperimentalRole neutralExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        refreshed.getExperimentalRoles().clear();
        refreshed.addExperimentalRole(neutralExperimentalRole);

        getCorePersister().saveOrUpdate(neutralExperimentalRole, refreshed);

        Component refreshed2 = getDaoFactory().getComponentDao().getByAc(comp.getAc());

        Assert.assertEquals(1, refreshed2.getExperimentalRoles().size());
        Assert.assertEquals(CvExperimentalRole.NEUTRAL_PSI_REF, refreshed2.getExperimentalRoles().iterator().next().getIdentifier());
    }

    @Test
    public void featurePersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Component component = builder.createComponentRandom();
        component.getFeatures().clear();
        getCorePersister().saveOrUpdate(component);

        Assert.assertEquals(0, getDaoFactory().getFeatureDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        component.addFeature(getMockBuilder().createFeatureRandom());

        getCorePersister().saveOrUpdate(component);
        Assert.assertEquals(1, getDaoFactory().getFeatureDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

}
