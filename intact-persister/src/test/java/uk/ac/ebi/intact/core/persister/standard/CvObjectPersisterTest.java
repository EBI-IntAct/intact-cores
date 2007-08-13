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

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectPersisterTest {

    @Before
    public void beforeTest() throws Exception {
        new IntactUnit().createSchema();
        beginTransaction();
    }

    @After
    public void afterTest() throws Exception {
        commitTransaction();
    }

    @Test
    public void persist_default() throws Exception {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier cvXrefQual = builder.createIdentityCvXrefQualifier(getIntactContext().getInstitution());
        CvDatabase cvDb = builder.createPsiMiCvDatabase(getIntactContext().getInstitution());

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole(getIntactContext().getInstitution(), expRoleLabel);
        CvObjectXref identityXref = new CvObjectXref(getIntactContext().getInstitution(), cvDb, "rolePrimaryId", cvXrefQual);

        expRole.addXref(identityXref);

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate(expRole);
        cvObjectPersister.commit();

        commitTransaction();
        beginTransaction();

        CvExperimentalRole newExpRole = getDaoFactory().getCvObjectDao(CvExperimentalRole.class).getByShortLabel(expRoleLabel);

        assertNotNull(newExpRole);
        assertFalse(newExpRole.getXrefs().isEmpty());

        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref(newExpRole);
        assertNotNull(cvObjectXref);
        assertEquals("rolePrimaryId",cvObjectXref.getPrimaryId());
    }

    @Test (expected = PersisterException.class)
    @Ignore
    public void persist_noXref() throws Exception {

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole(getIntactContext().getInstitution(), expRoleLabel);

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate(expRole);
    }

    protected DaoFactory getDaoFactory() {
         return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    protected IntactContext getIntactContext() {
         return IntactContext.getCurrentInstance();
    }

    protected void beginTransaction() {
         IntactContext.getCurrentInstance().getDataContext().beginTransaction();
    }

    protected void commitTransaction() throws Exception {
         IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }
}