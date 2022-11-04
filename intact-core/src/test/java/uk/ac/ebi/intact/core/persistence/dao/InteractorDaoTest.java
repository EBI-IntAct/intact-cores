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
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorDaoTest extends IntactBasicTestCase {

    @Test
    public void countAllInteractors() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        getCorePersister().saveOrUpdate(getMockBuilder().createDeterministicInteraction());
        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAllInteractors());
    }

    @Test
    public void getInteractors() throws Exception {
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countAllInteractors());
        getCorePersister().saveOrUpdate(getMockBuilder().createDeterministicInteraction());
        Assert.assertEquals(2, getDaoFactory().getInteractorDao().getInteractors(0, 5).size());
    }

    @Test
    public void getPartnersWithInteractionAcsByInteractorAc() throws Exception {
        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("B", "prot2");
        Protein prot3 = getMockBuilder().createProtein("C", "prot3");
        Interaction interaction = getMockBuilder().createInteraction(prot1, prot2, prot3);

        getCorePersister().saveOrUpdate(interaction);

        final Map<String,List<String>> partnersMap = getDaoFactory().getInteractorDao()
                .getPartnersWithInteractionAcsByInteractorAc(prot1.getAc());

        Assert.assertEquals(2, partnersMap.size());
        Assert.assertTrue(partnersMap.containsKey(prot2.getAc()));
        Assert.assertEquals(interaction.getAc(), partnersMap.get(prot2.getAc()).get(0));
        Assert.assertTrue(partnersMap.containsKey(prot3.getAc()));
        Assert.assertEquals(interaction.getAc(), partnersMap.get(prot3.getAc()).get(0));
    }

    @Test
    public void countByInteractorType1() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        SmallMolecule sm = getMockBuilder().createSmallMoleculeRandom();

        getCorePersister().saveOrUpdate(prot, sm);

        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countByInteractorType(CvInteractorType.PROTEIN_MI_REF, false));
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countByInteractorType(CvInteractorType.SMALL_MOLECULE_MI_REF, false));
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countByInteractorType("MI:lalala", false));
    }

    @Test
    public void countByInteractorType2() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        final CvInteractorType nucAcidType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, CvInteractorType.NUCLEIC_ACID);
        final CvInteractorType dnaType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.DNA_MI_REF, CvInteractorType.DNA);
        dnaType.addParent(nucAcidType);

        InteractorImpl interactor = new InteractorImpl("interactor", new Institution("lalaInst"), dnaType);

        getCorePersister().saveOrUpdate(prot, interactor);

        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countByInteractorType(CvInteractorType.NUCLEIC_ACID_MI_REF, true));
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().countByInteractorType("MI:lalala", false));
    }

    @Test
    public void getByInteractorType1() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        SmallMolecule sm = getMockBuilder().createSmallMoleculeRandom();

        getCorePersister().saveOrUpdate(prot, sm);

        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType(CvInteractorType.PROTEIN_MI_REF, false).size());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType(CvInteractorType.SMALL_MOLECULE_MI_REF, false).size());
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().getByInteractorType("MI:lalala", false).size());
    }

    @Test
    public void getByInteractorType2() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        final CvInteractorType nucAcidType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, CvInteractorType.NUCLEIC_ACID);
        final CvInteractorType dnaType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.DNA_MI_REF, CvInteractorType.DNA);
        dnaType.addParent(nucAcidType);

        InteractorImpl interactor = new InteractorImpl("interactor", new Institution("lalaInst"), dnaType);

        getCorePersister().saveOrUpdate(prot, interactor);

        Assert.assertEquals(2, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType(CvInteractorType.NUCLEIC_ACID_MI_REF, true).size());
        Assert.assertEquals(0, getDaoFactory().getInteractorDao().getByInteractorType("MI:lalala", false).size());
    }

    @Test
    public void getProtein_usingInteractorType_protein() throws Exception {
        CvInteractorType protType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);
        Interactor interactor = new InteractorImpl("protA", getIntactContext().getInstitution(), protType);

        getCorePersister().saveOrUpdate(interactor);

        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());

        final InteractorImpl returnedInteractor = getDaoFactory().getInteractorDao().getAll().get(0);
        Assert.assertEquals("uk.ac.ebi.intact.model.ProteinImpl", returnedInteractor.getObjClass());
    }

    @Test
    public void getProtein_usingInteractorType_smallMol() throws Exception {
        CvInteractorType type = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.SMALL_MOLECULE_MI_REF, CvInteractorType.SMALL_MOLECULE);
        Interactor interactor = new InteractorImpl("sm1", getIntactContext().getInstitution(), type);

        getCorePersister().saveOrUpdate(interactor);

        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType(CvInteractorType.SMALL_MOLECULE_MI_REF, false).size());
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());

        final InteractorImpl returnedInteractor = getDaoFactory().getInteractorDao().getAll().get(0);
        Assert.assertEquals("uk.ac.ebi.intact.model.SmallMoleculeImpl", returnedInteractor.getObjClass());
    }

    @Test
    public void getProtein_usingInteractorType_new() throws Exception {
        CvInteractorType type = getMockBuilder().createCvObject(CvInteractorType.class, "IAX:24314", "super interactor");
        Interactor interactor = new InteractorImpl("si50", getIntactContext().getInstitution(), type);

        getCorePersister().saveOrUpdate(interactor);

        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType("IAX:24314", false).size());
        Assert.assertEquals(0, getDaoFactory().getInteractionDao().countAll());

        final InteractorImpl returnedInteractor = getDaoFactory().getInteractorDao().getAll().get(0);
        Assert.assertEquals("uk.ac.ebi.intact.model.InteractorImpl", returnedInteractor.getObjClass());
    }

    @Test
    public void getProtein_usingInteractorType_interaction() throws Exception {
        CvInteractorType type = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF, CvInteractorType.INTERACTION);
        Interactor interactor = new InteractorImpl("interaction1", getIntactContext().getInstitution(), type);

        getCorePersister().saveOrUpdate(interactor);

        Assert.assertEquals(1, getDaoFactory().getInteractorDao().countAll());
        Assert.assertEquals(0, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractorDao().getByInteractorType(CvInteractorType.INTERACTION_MI_REF, false).size());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        final InteractorImpl returnedInteractor = getDaoFactory().getInteractorDao().getAll().get(0);
        Assert.assertEquals("uk.ac.ebi.intact.model.InteractionImpl", returnedInteractor.getObjClass());
    }

    @Test
    public void countInteractionsForInteractorWithAc(){
        Protein prot1 = getMockBuilder().createProtein("A", "prot1");
        Protein prot2 = getMockBuilder().createProtein("B", "prot2");
        Protein prot3 = getMockBuilder().createProtein("C", "prot3");
        Protein prot4 = getMockBuilder().createProtein("D", "prot4");
        Interaction interaction = getMockBuilder().createInteraction(prot1, prot2, prot3);

        getCorePersister().saveOrUpdate(interaction);
        getCorePersister().saveOrUpdate(prot4);

        String ac = prot1.getAc();
        String ac2 = prot4.getAc();

        int countInteraction = getIntactContext().getDaoFactory().getProteinDao().countInteractionsForInteractorWithAc(ac);

        Assert.assertEquals(1, countInteraction);

        int countInteraction2 = getIntactContext().getDaoFactory().getProteinDao().countInteractionsForInteractorWithAc(ac2);

        Assert.assertEquals(0, countInteraction2);
    }
}