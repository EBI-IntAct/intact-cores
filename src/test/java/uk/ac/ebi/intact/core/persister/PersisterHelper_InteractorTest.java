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
package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.ProteinUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_InteractorTest extends IntactBasicTestCase {

    @Test
    public void aliasPersisted() throws Exception {
        Interactor interactor = getMockBuilder().createProteinRandom();
        getCorePersister().saveOrUpdate(interactor);

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test
    public void fetchFromDb_multipleIdXrefsMixed() throws Exception {
        Protein prot = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt1");
        getCorePersister().saveOrUpdate(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein prot2 = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt1");
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        prot2.addXref(getMockBuilder().createIdentityXref(prot2, "EBI-12345", intact));
        getCorePersister().saveOrUpdate(prot2);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void update_containsMoreXrefs() throws Exception {
        Protein prot = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt");
        getCorePersister().saveOrUpdate(prot);

        Protein protBeforeUpdate = getDaoFactory().getProteinDao().getByUniprotId("Q00112").iterator().next();
        Assert.assertNotNull(protBeforeUpdate);
        Assert.assertEquals(1, protBeforeUpdate.getXrefs().size());
        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein protUpdated = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt");
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        InteractorXref secondaryXref = getMockBuilder().createIdentityXrefUniprot(protUpdated, "A12345");
        secondaryXref.setCvXrefQualifier(secondaryAc);
        protUpdated.addXref(secondaryXref);
        getCorePersister().saveOrUpdate(protUpdated);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Protein protAfterUpdate = getDaoFactory().getProteinDao().getByUniprotId("Q00112").iterator().next();
        Assert.assertNotNull(protAfterUpdate);
        Assert.assertEquals(1, protAfterUpdate.getXrefs().size());
    }

    @Test
    public void update_protein() throws Exception {
        // this test checks that a protein can be saved if it's CvInteractorType are already in the datatase.
        Protein protein = getMockBuilder().createProteinRandom();
        CvInteractorType type = protein.getCvInteractorType();
        protein.setCvInteractorType( null );

        Assert.assertNull( type.getAc() );
        getCorePersister().saveOrUpdate(type);
        Assert.assertNotNull( type.getAc() );

        type = getDaoFactory().getCvObjectDao( CvInteractorType.class ).getByAc( type.getAc() );
        protein.setCvInteractorType( type );
        getCorePersister().saveOrUpdate(protein);

    }

    @Test
    public void protein_exists() throws Exception {
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        CvDatabase uniprotkb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);
        CvDatabase go = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);

        Protein prot = getMockBuilder().createDeterministicProtein("P12345", "lala");
        prot.addXref(getMockBuilder().createXref(prot, "Q88334", secondaryAc, uniprotkb));
        prot.addXref(getMockBuilder().createXref(prot, "GO:123456", null, go));
        getCorePersister().saveOrUpdate(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein sameProt = getMockBuilder().createDeterministicProtein("P12345", "lala");
        Protein prot2 = getMockBuilder().createDeterministicProtein("Q99999", "koko");
        Interaction interaction = getMockBuilder().createInteraction(sameProt, prot2);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }


    @Test
    public void updateSmallMolecule() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        final SmallMolecule smallMolecule = getMockBuilder().createSmallMolecule("CHEBI:18348", "noname");
        smallMolecule.getAnnotations().clear();

        getCorePersister().saveOrUpdate(smallMolecule);

        getEntityManager().clear();

        Assert.assertEquals(1, daoFactory.getInteractorDao(SmallMoleculeImpl.class).countAll());

        SmallMoleculeImpl noNameMolecule = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("noname");

        Assert.assertEquals(0, noNameMolecule.getAnnotations().size());
        Assert.assertEquals("noname", noNameMolecule.getShortLabel());

        //update with new shortlabel and add new annotation persist
        smallMolecule.setShortLabel("newname");

        CvTopic inchiCvTopic = CvObjectUtils.createCvObject(smallMolecule.getOwner(), CvTopic.class, "MI:2010", "inchi id");

        Annotation annotation = new Annotation(smallMolecule.getOwner(), inchiCvTopic, "thisisinchiid");
        smallMolecule.addAnnotation(annotation);

        Assert.assertEquals(1, smallMolecule.getAnnotations().size());

        getCorePersister().saveOrUpdate(smallMolecule);

        SmallMolecule refreshedNoName = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("noname");
        Assert.assertNull(refreshedNoName);

        SmallMolecule refreshedNewName = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("newname");
        Assert.assertNotNull(refreshedNewName);

        Assert.assertEquals("CHEBI:18348", refreshedNewName.getXrefs().iterator().next().getPrimaryId());
        Assert.assertEquals(1, refreshedNewName.getAnnotations().size());
        Assert.assertEquals("inchi id", refreshedNewName.getAnnotations().iterator().next().getCvTopic().getShortLabel());
    }

    @Test
    @DirtiesContext
    @Transactional(propagation = Propagation.NEVER)
    public void test_move_all_components_no_features(){
        DataContext context = getDataContext();
        TransactionStatus status = context.beginTransaction();

        Protein source = getMockBuilder().createProtein("P12345", "source");

        Protein destination = getMockBuilder().createProtein("P12346", "destination");

        Protein random = getMockBuilder().createProteinRandom();
        getCorePersister().saveOrUpdate(source, destination, random);

        Interaction i = getMockBuilder().createInteraction(source, random);
        getCorePersister().saveOrUpdate(i);

        Assert.assertEquals(3, IntactContext.getCurrentInstance().getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, IntactContext.getCurrentInstance().getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, source.getActiveInstances().size());
        Assert.assertEquals(0, destination.getActiveInstances().size());

        context.commitTransaction(status);

        DataContext context2 = getDataContext();
        TransactionStatus status2 = context2.beginTransaction();

        Protein sourceReloaded = context2.getDaoFactory().getProteinDao().getByAc(source.getAc());
        Protein destinationReloaded = context2.getDaoFactory().getProteinDao().getByAc(destination.getAc());

        Collection<Component> componentsToMove = new ArrayList<Component>();
        componentsToMove.addAll(sourceReloaded.getActiveInstances());

        for (Component c : componentsToMove){
            // The following initializations are not necessary, since we run Hibernate with an assertion removed
            // in the CollectionEntry class.

            //Hibernate.initialize(c.getBindingDomains());
            //Hibernate.initialize(c.getExperimentalRoles());
            //Hibernate.initialize(c.getExperimentalPreparations());

            c.setInteractor(destinationReloaded);
            context2.getDaoFactory().getComponentDao().update(c);
        }

        context2.commitTransaction(status2);

        DataContext context3 = getDataContext();
        TransactionStatus status3 = context3.beginTransaction();

        Protein sourceReloaded2 = context2.getDaoFactory().getProteinDao().getByAc(source.getAc());
        Protein destinationReloaded2 = context2.getDaoFactory().getProteinDao().getByAc(destination.getAc());

        Assert.assertEquals(1, destinationReloaded2.getActiveInstances().size());
        Assert.assertEquals(0, sourceReloaded2.getActiveInstances().size());

        context3.commitTransaction(status3);
    }

    @Test
    @DirtiesContext
    @Transactional(propagation = Propagation.NEVER)
    public void test_move_interactorXRef(){
        DataContext context = getDataContext();
        TransactionStatus status = context.beginTransaction();

        Protein source = getMockBuilder().createProtein("P12345", "source");

        Protein destination = getMockBuilder().createProtein("P12346", "destination");

        getCorePersister().saveOrUpdate(source, destination);

        Assert.assertEquals(2, IntactContext.getCurrentInstance().getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, source.getXrefs().size());
        Assert.assertEquals(1, destination.getXrefs().size());

        context.commitTransaction(status);

        DataContext context2 = getDataContext();
        TransactionStatus status2 = context2.beginTransaction();

        Protein sourceReloaded = context2.getDaoFactory().getProteinDao().getByAc(source.getAc());
        Protein destinationReloaded = context2.getDaoFactory().getProteinDao().getByAc(destination.getAc());

        Collection<InteractorXref> refsToMove = new ArrayList(sourceReloaded.getXrefs());

        for (InteractorXref x : refsToMove){

            x.setParentAc(destinationReloaded.getAc());
            sourceReloaded.removeXref(x);
            destinationReloaded.addXref(x);

            context2.getDaoFactory().getXrefDao(InteractorXref.class).update(x);
        }

        getDaoFactory().getProteinDao().update((ProteinImpl) destinationReloaded);
        getDaoFactory().getProteinDao().update((ProteinImpl) sourceReloaded);

        context2.commitTransaction(status2);

        DataContext context3 = getDataContext();
        TransactionStatus status3 = context3.beginTransaction();

        Protein sourceReloaded2 = context2.getDaoFactory().getProteinDao().getByAc(source.getAc());
        Protein destinationReloaded2 = context2.getDaoFactory().getProteinDao().getByAc(destination.getAc());

        Assert.assertEquals(2, destinationReloaded2.getXrefs().size());
        Assert.assertEquals(1, sourceReloaded2.getXrefs().size());

        context3.commitTransaction(status3);
    }

    @Test
    @DirtiesContext
    @Transactional(propagation = Propagation.NEVER)
    public void update_protein_delete_xref() throws Exception {
        TransactionStatus transaction = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Protein protein = getMockBuilder().createProteinRandom();

        Assert.assertEquals(1, protein.getXrefs().size());

        CvDatabase go = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);
        CvXrefQualifier qual = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.PROCESS_MI_REF, CvXrefQualifier.PROCESS);

        protein.addXref(getMockBuilder().createXref(protein, "lala", qual, go));

        getCorePersister().saveOrUpdate(protein);

        Protein refreshedProt = getDaoFactory().getProteinDao().getByAc(protein.getAc());

        Assert.assertEquals(2, refreshedProt.getXrefs().size());

        Iterator<InteractorXref> xrefIter = refreshedProt.getXrefs().iterator();

        while (xrefIter.hasNext()) {
            InteractorXref next = xrefIter.next();

            if ("lala".equals(next.getPrimaryId())) {
                xrefIter.remove();
            }
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transaction);

        Assert.assertEquals(1, refreshedProt.getXrefs().size());

        TransactionStatus transaction2 = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        getCorePersister().saveOrUpdate(refreshedProt);

        Protein refreshedProt2 = getDaoFactory().getProteinDao().getByAc(protein.getAc());

        Assert.assertEquals(1, refreshedProt2.getXrefs().size());


        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transaction2);

    }

    @Test
    @DirtiesContext
    @Transactional(propagation = Propagation.NEVER)
    public void update_protein_delete_xref_2() throws Exception {
        TransactionStatus transaction = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Protein protein = getMockBuilder().createProteinRandom();

        Assert.assertEquals(1, protein.getXrefs().size());

        CvDatabase go = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);
        CvXrefQualifier qual = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.PROCESS_MI_REF, CvXrefQualifier.PROCESS);

        protein.addXref(getMockBuilder().createXref(protein, "lala", qual, go));

        getCorePersister().saveOrUpdate(protein);

        Protein refreshedProt = getDaoFactory().getProteinDao().getByAc(protein.getAc());

        Assert.assertEquals(2, refreshedProt.getXrefs().size());

        Iterator<InteractorXref> xrefIter = refreshedProt.getXrefs().iterator();

        while (xrefIter.hasNext()) {
            InteractorXref next = xrefIter.next();

            if ("lala".equals(next.getPrimaryId())) {
                xrefIter.remove();
            }
        }

        getCorePersister().saveOrUpdate(refreshedProt);

        getDataContext().commitTransaction(transaction);

        // retrieve the protein again and check number of xrefs - should be 1
        TransactionStatus transaction2 = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Protein refreshedProt2 = getDaoFactory().getProteinDao().getByAc(protein.getAc());

        Assert.assertEquals(1, refreshedProt2.getXrefs().size());

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transaction2);




    }
}