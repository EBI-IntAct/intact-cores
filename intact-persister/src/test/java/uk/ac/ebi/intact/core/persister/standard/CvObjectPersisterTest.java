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
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectPersisterTest extends AbstractPersisterTest {

    @Test
    public void persist_recursive_object() throws Exception {
        // Note: CvDatabase( psi-mi ) has an Xref to psi-mi (that is itself)
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase psimi = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );
        Protein protein = getMockBuilder().createProteinRandom();
        CvObjectPersister persister = new CvObjectPersister();
        persister.saveOrUpdate( psimi );
        persister.commit();

        Xref xref = AnnotatedObjectUtils.searchXrefs( psimi, psimi ).iterator().next();
        Assert.assertEquals( psimi, xref.getCvDatabase() );
        Assert.assertSame( psimi, xref.getCvDatabase() );
    }

    @Test
    public void persist_default() throws Exception {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier cvXrefQual = builder.createIdentityCvXrefQualifier( getIntactContext().getInstitution() );
        CvDatabase cvDb = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );
        CvObjectXref identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "rolePrimaryId", cvXrefQual );

        expRole.addXref( identityXref );

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate( expRole );
        cvObjectPersister.commit();

        commitTransaction();
        beginTransaction();

        CvExperimentalRole newExpRole = getDaoFactory().getCvObjectDao( CvExperimentalRole.class ).getByShortLabel( expRoleLabel );

        assertNotNull( newExpRole );
        assertFalse( newExpRole.getXrefs().isEmpty() );

        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref( newExpRole );
        assertNotNull( cvObjectXref );
        assertEquals( "rolePrimaryId", cvObjectXref.getPrimaryId() );
    }

    @Test
    public void persist_existing_object() throws Exception {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier cvXrefQual = builder.createIdentityCvXrefQualifier( getIntactContext().getInstitution() );
        CvDatabase cvDb = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );
        CvObjectXref identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "rolePrimaryId", cvXrefQual );

        expRole.addXref( identityXref );

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate( expRole );
        cvObjectPersister.commit();

        // re-create the same object and check that it gets assigned the AC.
        cvXrefQual = builder.createIdentityCvXrefQualifier( getIntactContext().getInstitution() );
        cvDb = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        CvExperimentalRole expRole2 = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );
        identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "rolePrimaryId", cvXrefQual );

        expRole2.addXref( identityXref );

        cvObjectPersister.saveOrUpdate( expRole2 );
        cvObjectPersister.commit();

        Assert.assertNotSame( expRole, expRole2 );
        Assert.assertEquals( expRole.getAc(), expRole2.getAc() );
    }

    @Test
    public void persist_sameMi_differentLabel() throws Exception {
        CvObject cv1 = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalRole.class, "MI:0123", "label");
        PersisterHelper.saveOrUpdate(cv1);

        commitTransaction();

        beginTransaction();
        CvObject cv = getDaoFactory().getCvObjectDao(CvExperimentalRole.class).getByPsiMiRef("MI:0123");
        Assert.assertNotNull(cv);
        commitTransaction();

        beginTransaction();
        CvObject cv1Copy = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalRole.class, "MI:0123", "label");
        CvObject cv1Fetched = CvObjectPersister.getInstance().fetchFromDataSource(cv1Copy);
        Assert.assertNotNull(cv1Fetched);
        Assert.assertNotNull(cv1Fetched.getAc());
        commitTransaction();

        CvObject cv2 = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalRole.class, "MI:0123", "another-label");
        PersisterHelper.saveOrUpdate(cv2);

        beginTransaction();

        Assert.assertEquals(1, getDaoFactory().getCvObjectDao(CvExperimentalRole.class).countAll());

        //CvObject cv = getDaoFactory().getCvObjectDao(CvExperimentalRole.class).getByPsiMiRef("MI:0123");


        commitTransaction();
    }

    @Test( expected = PersisterException.class )
    @Ignore
    public void persist_noXref() throws Exception {

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );

        CvObjectPersister cvObjectPersister = CvObjectPersister.getInstance();

        cvObjectPersister.saveOrUpdate( expRole );
    }

}