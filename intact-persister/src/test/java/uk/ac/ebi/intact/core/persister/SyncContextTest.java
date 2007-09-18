package uk.ac.ebi.intact.core.persister;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SyncContextTest extends IntactBasicTestCase
{
    @Before
    public void prepare() throws Exception {
        SyncContext.getInstance().clear();
    }

    @Test
    public void clear_annotatedObject() throws Exception {
        AnnotatedObject ao = createNiceMock(AnnotatedObject.class);

        expect(ao.getShortLabel()).andReturn("aoLabel").anyTimes();
        replay(ao);

        SyncContext.getInstance().addToSynced(ao);

        verify(ao);

        assertEquals(1, SyncContext.getInstance().getSyncedAnnotatedObjects().size());
        assertEquals(0, SyncContext.getInstance().getSyncedCvObjects().size());

        SyncContext.getInstance().clear();
        assertEquals(0, SyncContext.getInstance().getSyncedAnnotatedObjects().size());
        assertEquals(0, SyncContext.getInstance().getSyncedCvObjects().size());
    }

    @Test
    public void clear_cvObject() throws Exception {
        CvObject ao = getMockBuilder().createCvObject(CvExperimentalRole.class, CvExperimentalRole.COFACTOR_MI_REF, CvExperimentalRole.COFACTOR);

        SyncContext.getInstance().addToSynced(ao);

        assertEquals(1, SyncContext.getInstance().getSyncedCvObjects().size());
        assertEquals(0, SyncContext.getInstance().getSyncedAnnotatedObjects().size());

        SyncContext.getInstance().clear();
        assertEquals(0, SyncContext.getInstance().getSyncedCvObjects().size());
        assertEquals(0, SyncContext.getInstance().getSyncedAnnotatedObjects().size());
    }


}