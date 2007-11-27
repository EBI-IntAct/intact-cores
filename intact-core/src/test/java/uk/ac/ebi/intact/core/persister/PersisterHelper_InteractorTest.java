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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.persister.UndefinedCaseException;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.core.unit.mock.MockInteractorDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ProteinUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_InteractorTest extends IntactBasicTestCase {

    @Before
    public void before() throws Exception {
        beginTransaction();
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }

    @Test
    public void aliasPersisted() throws Exception {
        Interactor interactor = getMockBuilder().createProteinRandom();
        PersisterHelper.saveOrUpdate(interactor);

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test (expected = UndefinedCaseException.class)
    public void fetchFromDb_multipleIdXrefsToUniprot() throws Exception {
        Protein prot = getMockBuilder().createProtein("Q00112", "lalaProt");
        prot.getXrefs().clear();
        prot.addXref(getMockBuilder().createIdentityXrefChebi(prot, "CHEBI:1"));
        prot.addXref(getMockBuilder().createIdentityXrefChebi(prot, "CHEBI:2"));
        PersisterHelper.saveOrUpdate(prot);
    }

    @Test
    public void fetchFromDb_multipleIdXrefsMixed() throws Exception {
        Protein prot = getMockBuilder().createProtein("Q00112", "lalaProt1");
        PersisterHelper.saveOrUpdate(prot);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein prot2 = getMockBuilder().createProtein("Q00112", "lalaProt");
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        prot2.addXref(getMockBuilder().createIdentityXref(prot2, "EBI-12345", intact));
        PersisterHelper.saveOrUpdate(prot2);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void update_containsMoreXrefs() throws Exception {
        Protein prot = getMockBuilder().createProtein("Q00112", "lalaProt");
        PersisterHelper.saveOrUpdate(prot);

        beginTransaction();
        Protein protBeforeUpdate = getDaoFactory().getProteinDao().getByUniprotId("Q00112").iterator().next();
        Assert.assertNotNull(protBeforeUpdate);
        Assert.assertEquals(1, protBeforeUpdate.getXrefs().size());
        commitTransaction();

        Protein protUpdated = getMockBuilder().createProtein("Q00112", "lalaProt");
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        InteractorXref secondaryXref = getMockBuilder().createIdentityXrefUniprot(protUpdated, "A12345");
        secondaryXref.setCvXrefQualifier(secondaryAc);
        protUpdated.addXref(secondaryXref);
        PersisterHelper.saveOrUpdate(protUpdated);

        beginTransaction();
        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Protein protAfterUpdate = getDaoFactory().getProteinDao().getByUniprotId("Q00112").iterator().next();
        Assert.assertNotNull(protAfterUpdate);
        Assert.assertEquals(2, protAfterUpdate.getXrefs().size());
        commitTransaction();
    }

    @Test
    public void update_protein() throws Exception {
        // this test checks that a protein can be saved if it's CvInteractorType are already in the datatase.
        Protein protein = getMockBuilder().createProteinRandom();
        CvInteractorType type = protein.getCvInteractorType();
        protein.setCvInteractorType( null );

        Assert.assertNull( type.getAc() );
        PersisterHelper.saveOrUpdate(type);
        Assert.assertNotNull( type.getAc() );

        type = getDaoFactory().getCvObjectDao( CvInteractorType.class ).getByAc( type.getAc() );
        protein.setCvInteractorType( type );
        PersisterHelper.saveOrUpdate(protein);

    }

}