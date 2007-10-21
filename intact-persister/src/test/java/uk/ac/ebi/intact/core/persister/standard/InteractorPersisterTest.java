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
package uk.ac.ebi.intact.core.persister.standard;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.core.unit.mock.MockInteractorDao;
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
public class InteractorPersisterTest extends AbstractPersisterTest {

    @Test
    public void aliasPersisted() throws Exception {
        Interactor interactor = getMockBuilder().createProteinRandom();
        PersisterHelper.saveOrUpdate(interactor);

        beginTransaction();

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test
    public void fetchFromDb_duplicatedInteractorsInDb() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorDao<InteractorImpl>() {
            @Override
            public Collection<InteractorImpl> getColByPropertyName(String propertyName, String value) {
                if (propertyName.equals("shortLabel")) {
                    InteractorImpl interactor1 = (InteractorImpl) getMockBuilder().createProtein("P12345", "lala");
                    InteractorImpl interactor2 = (InteractorImpl) getMockBuilder().createProtein("P54321", "lala");

                    interactor1.setUpdated(new Date());
                    interactor2.setUpdated(new Date(1));

                    return Arrays.asList(interactor1, interactor2);
                }

                return null;
            }
        });

        Protein prot = getMockBuilder().createProteinRandom();
        prot.setShortLabel("lala");

        Protein lastProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(prot);

        Assert.assertEquals("P12345", ProteinUtils.getUniprotXref(lastProt).getPrimaryId());

        MockIntactContext.getCurrentInstance().close();
    }

    @Test
    public void fetchFromDb_duplicatedInteractorsInDb2() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorDao<InteractorImpl>() {
            @Override
            public Collection<InteractorImpl> getColByPropertyName(String propertyName, String value) {
                if (propertyName.equals("shortLabel")) {
                    InteractorImpl interactor1 = (InteractorImpl) getMockBuilder().createProtein("P12345", "lala");
                    InteractorImpl interactor2 = (InteractorImpl) getMockBuilder().createProtein("P54321", "lala");

                    interactor1.setUpdated(new Date(1));
                    interactor2.setUpdated(new Date());

                    return Arrays.asList(interactor1, interactor2);
                }

                return null;
            }
        });

        Protein prot = getMockBuilder().createProteinRandom();
        prot.setShortLabel("lala");

        Protein lastProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(prot);

        Assert.assertEquals("P54321", ProteinUtils.getUniprotXref(lastProt).getPrimaryId());

        MockIntactContext.getCurrentInstance().close();
    }

    @Test
    public void fetchFromDb_oneInteractor() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorDao<InteractorImpl>() {
            @Override
            public Collection<InteractorImpl> getColByPropertyName(String propertyName, String value) {
                if (propertyName.equals("shortLabel")) {
                    InteractorImpl interactor1 = (InteractorImpl) getMockBuilder().createProtein("P12345", "lala");

                    return Arrays.asList(interactor1);
                }

                return null;
            }
        });

        Protein prot = getMockBuilder().createProteinRandom();
        prot.setShortLabel("lala");

        Protein lastProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(prot);

        Assert.assertEquals("P12345", ProteinUtils.getUniprotXref(lastProt).getPrimaryId());

        MockIntactContext.getCurrentInstance().close();
    }

    @Test
    public void fetchFromDb_doesNotExit() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorDao<InteractorImpl>() {
            @Override
            public Collection<InteractorImpl> getColByPropertyName(String propertyName, String value) {
                if (propertyName.equals("shortLabel")) {
                    return new ArrayList<InteractorImpl>();
                }

                return null;
            }
        });

        Protein prot = getMockBuilder().createProteinRandom();
        prot.setShortLabel("lala");

        Assert.assertNull(InteractorPersister.getInstance().fetchFromDataSource(prot));

        MockIntactContext.getCurrentInstance().close();
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
        CvObjectPersister cvPersister = CvObjectPersister.getInstance();
        cvPersister.saveOrUpdate( type );
        cvPersister.commit();
        Assert.assertNotNull( type.getAc() );
        commitTransaction();

        beginTransaction();
        type = getDaoFactory().getCvObjectDao( CvInteractorType.class ).getByAc( type.getAc() );
        protein.setCvInteractorType( type );
        InteractorPersister interactorPersister = InteractorPersister.getInstance();
        interactorPersister.saveOrUpdate( protein );
        interactorPersister.commit();

        commitTransaction();
    }
}