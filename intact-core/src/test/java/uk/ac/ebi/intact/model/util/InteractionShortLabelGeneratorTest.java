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
package uk.ac.ebi.intact.model.util;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.context.CvContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IgnoreDatabase;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.unitdataset.LegacyPsiTestDatasetProvider;

import java.util.Arrays;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IgnoreDatabase
public class InteractionShortLabelGeneratorTest extends IntactAbstractTestCase {

    @Test
    public void createCandidateShortLabel() throws Exception {
        String baitLabel = "bait";
        String preyLabel ="prey";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("bait-prey", candLabel);
    }

    @Test
    public void createCandidateShortLabel_truncate() throws Exception {
        String baitLabel = "IAmAHappyBayt";
        String preyLabel ="AndIAmAHappyPrey";

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(baitLabel, preyLabel);

        assertNotNull(candLabel);
        assertEquals("iamahappyb-andiamaha", candLabel);
    }

    @Test
    @IntactUnitDataset(dataset = LegacyPsiTestDatasetProvider.INTACT_CORE, provider = LegacyPsiTestDatasetProvider.class)
    public void createCandidateShortLabel_fromInteraction() throws Exception {
        Interaction interaction = getDaoFactory().getInteractionDao().getByShortLabel("cara-carb-4");

        assertNotNull(interaction);

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        assertEquals("cara-carb", candLabel);
    }

    @Test
     public void selfInteractionShortLabel() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext context = (MockIntactContext) IntactContext.getCurrentInstance();
        context.setCvContext(new MyCvContext());
        //MockIntactContext.configureMockDaoFactory().setMockCvObjectDao(new MyCvObjectDao());


        Interaction interaction = createNiceMock(InteractionImpl.class);
        Component component = createNiceMock(Component.class);
        Interactor interactor = createNiceMock(Interactor.class);

        CvExperimentalRole neutralRole = new CvExperimentalRole(context.getInstitution(), CvExperimentalRole.NEUTRAL);
        addMiXrefToCvObject(neutralRole, CvExperimentalRole.NEUTRAL_PSI_REF, context);

        expect(interactor.getShortLabel()).andReturn("lalateractor");
        expect(component.getInteractor()).andReturn(interactor).anyTimes();
        expect(component.getCvExperimentalRole()).andReturn(neutralRole);
        expect(interaction.getComponents()).andReturn(Arrays.asList(component)).anyTimes();

        replay(interaction);
        replay(component);
        replay(interactor);

        String candLabel = InteractionShortLabelGenerator.createCandidateShortLabel(interaction);

        assertNotNull(candLabel);
        assertEquals("lalateractor", candLabel);
    }

    private void addMiXrefToCvObject(CvObject cv, String mi, IntactContext context) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvObjectXref xref = new CvObjectXref(context.getInstitution(), builder.createPsiMiCvDatabase(context), mi, builder.createIdentityCvXrefQualifier(context));
        cv.addXref(xref);
    }

    private class MyCvContext extends CvContext {

        private MyCvContext() {
            super(IntactContext.getCurrentInstance().getSession());
        }

        @Override
        public <T extends CvObject> T getByMiRef(Class<T> cvType, String miRef, boolean forceReload) {
            return null;
        }
    }
}