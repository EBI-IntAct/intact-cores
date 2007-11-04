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
import uk.ac.ebi.intact.core.persister.UndefinedCaseException;
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

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test
    public void fetchFromDb_existingAc() throws Exception {
        commitTransaction();

        Protein prot = getMockBuilder().createProteinRandom();
        prot.setShortLabel("IDoExist");

        PersisterHelper.saveOrUpdate(prot);

        Protein newProt = getMockBuilder().createProteinRandom();
        newProt.setAc(prot.getAc());
        newProt.setShortLabel("doIExist?");

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein fetchedProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(newProt);

        Assert.assertEquals("IDoExist", fetchedProt.getShortLabel());
    }

    @Test
    public void fetchFromDb_existingPrimaryId() throws Exception {
        commitTransaction();

        Protein prot = getMockBuilder().createProtein("P12345", "IDoExist");

        PersisterHelper.saveOrUpdate(prot);

        Protein newProt = getMockBuilder().createProtein("P12345", "doIExist?");

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein fetchedProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(newProt);

        Assert.assertEquals("IDoExist", fetchedProt.getShortLabel());
    }

    @Test
    public void fetchFromDb_existingPrimaryIdAsAc() throws Exception {
        commitTransaction();

        Protein prot = getMockBuilder().createProtein("aPrimaryId", "IDoExist");

        PersisterHelper.saveOrUpdate(prot);

        Protein newProt = getMockBuilder().createProtein(prot.getAc(), "doIExist?");

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein fetchedProt = (Protein) InteractorPersister.getInstance().fetchFromDataSource(newProt);

        Assert.assertEquals("IDoExist", fetchedProt.getShortLabel());
    }

    @Test
    public void fetchFromDb_duplicatedInteractorsInDb() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorFetchDao() {
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
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorFetchDao() {
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
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorFetchDao() {
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

    @Test (expected = UndefinedCaseException.class)
    public void fetchFromDb_multipleIdXrefsToUniprot() throws Exception {
        Protein prot = getMockBuilder().createProtein("Q00112", "lalaProt");
        prot.addXref(getMockBuilder().createIdentityXrefUniprot(prot, "Q00113"));
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
    public void fetchFromDb_doesNotExit() throws Exception {
        MockIntactContext.initMockContext();
        MockIntactContext.configureMockDaoFactory().setMockInteractorDao(new MockInteractorFetchDao() {
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

    private class MockInteractorFetchDao extends MockInteractorDao<InteractorImpl> {

        @Override
        public InteractorImpl getByAc(String ac) {
            return null;
        }

        @Override
        public InteractorImpl getByXref(String primaryId) {
            return null;
        }

        @Override
        public Collection<InteractorImpl> getColByPropertyName(String propertyName, String value) {
            return null;
        }

    }
}