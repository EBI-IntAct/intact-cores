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
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

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
        getPersisterHelper().save(interactor);

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test
    public void fetchFromDb_multipleIdXrefsMixed() throws Exception {
        Protein prot = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt1");
        getPersisterHelper().save(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein prot2 = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt1");
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        prot2.addXref(getMockBuilder().createIdentityXref(prot2, "EBI-12345", intact));
        getPersisterHelper().save(prot2);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void update_containsMoreXrefs() throws Exception {
        Protein prot = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt");
        getPersisterHelper().save(prot);

        Protein protBeforeUpdate = getDaoFactory().getProteinDao().getByUniprotId("Q00112").iterator().next();
        Assert.assertNotNull(protBeforeUpdate);
        Assert.assertEquals(1, protBeforeUpdate.getXrefs().size());
        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein protUpdated = getMockBuilder().createDeterministicProtein("Q00112", "lalaProt");
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        InteractorXref secondaryXref = getMockBuilder().createIdentityXrefUniprot(protUpdated, "A12345");
        secondaryXref.setCvXrefQualifier(secondaryAc);
        protUpdated.addXref(secondaryXref);
        getPersisterHelper().save(protUpdated);

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
        getPersisterHelper().save(type);
        Assert.assertNotNull( type.getAc() );

        type = getDaoFactory().getCvObjectDao( CvInteractorType.class ).getByAc( type.getAc() );
        protein.setCvInteractorType( type );
        getPersisterHelper().save(protein);

    }

    @Test
    public void protein_exists() throws Exception {
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        CvDatabase uniprotkb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);
        CvDatabase go = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);

        Protein prot = getMockBuilder().createDeterministicProtein("P12345", "lala");
        prot.addXref(getMockBuilder().createXref(prot, "Q88334", secondaryAc, uniprotkb));
        prot.addXref(getMockBuilder().createXref(prot, "GO:123456", null, go));
        getPersisterHelper().save(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein sameProt = getMockBuilder().createDeterministicProtein("P12345", "lala");
        Protein prot2 = getMockBuilder().createDeterministicProtein("Q99999", "koko");
        Interaction interaction = getMockBuilder().createInteraction(sameProt, prot2);
        
        getPersisterHelper().save(interaction);

        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }


    @Test
    public void updateSmallMolecule() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        final SmallMolecule smallMolecule = getMockBuilder().createSmallMolecule("CHEBI:18348", "noname");
        getPersisterHelper().save(smallMolecule);

        getEntityManager().clear();

        Assert.assertEquals(1, daoFactory.getInteractorDao(SmallMoleculeImpl.class).countAll());

        SmallMoleculeImpl byShortLabel = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("noname");
        Assert.assertEquals("noname", byShortLabel.getShortLabel());

        //update with new shortlabel and add new annotation persist
        smallMolecule.setShortLabel("newname");
        CvTopic inchiCvTopic = CvObjectUtils.createCvObject(smallMolecule.getOwner(), CvTopic.class, "MI:2010", "inchi id");
        Annotation annotation = new Annotation(smallMolecule.getOwner(), inchiCvTopic, "thisisinchiid");
        if (annotation != null) {
            if (!smallMolecule.getAnnotations().contains(annotation)) {
                smallMolecule.getAnnotations().add(annotation);
            }
        }
        getPersisterHelper().save(smallMolecule);

        byShortLabel = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("noname");
        Assert.assertNull(byShortLabel);

        byShortLabel = daoFactory.getInteractorDao(SmallMoleculeImpl.class).getByShortLabel("newname");
        Assert.assertNotNull(byShortLabel);

        Assert.assertEquals("CHEBI:18348", byShortLabel.getXrefs().iterator().next().getPrimaryId());
        Assert.assertEquals("inchi id", byShortLabel.getAnnotations().iterator().next().getCvTopic().getShortLabel());
    }

}