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

import org.junit.*;
import static org.junit.Assert.*;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
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
public class PersisterHelper_CvObjectTest extends IntactBasicTestCase {

    @Test
    public void persist_recursive_object() throws Exception {
        // Note: CvDatabase( psi-mi ) has an Xref to psi-mi (that is itself)
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase psimi = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        PersisterHelper.saveOrUpdate(psimi);

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
        CvObjectXref identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "roleMi", cvXrefQual );

        expRole.addXref( identityXref );

        PersisterHelper.saveOrUpdate(expRole);

        CvExperimentalRole newExpRole = getDaoFactory().getCvObjectDao( CvExperimentalRole.class ).getByShortLabel( expRoleLabel );

        assertNotNull( newExpRole );
        assertFalse( newExpRole.getXrefs().isEmpty() );

        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref( newExpRole );
        assertNotNull( cvObjectXref );
        assertEquals( "roleMi", cvObjectXref.getPrimaryId() );
    }

    @Test
    public void persist_existing_object() throws Exception {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier cvXrefQual = builder.createIdentityCvXrefQualifier( getIntactContext().getInstitution() );
        CvDatabase cvDb = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        final String expRoleLabel = "EXP_ROLE";
        CvExperimentalRole expRole = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );
        CvObjectXref identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "roleMi", cvXrefQual );

        expRole.addXref( identityXref );

        PersisterHelper.saveOrUpdate(expRole);

        // re-create the same object and check that it gets assigned the AC.
        cvXrefQual = builder.createIdentityCvXrefQualifier( getIntactContext().getInstitution() );
        cvDb = builder.createPsiMiCvDatabase( getIntactContext().getInstitution() );

        CvExperimentalRole expRole2 = new CvExperimentalRole( getIntactContext().getInstitution(), expRoleLabel );
        identityXref = new CvObjectXref( getIntactContext().getInstitution(), cvDb, "rolePrimaryId", cvXrefQual );

        expRole2.addXref( identityXref );

        PersisterHelper.saveOrUpdate(expRole2);

        Assert.assertNotSame( expRole, expRole2 );
        Assert.assertEquals( expRole.getAc(), expRole2.getAc() );
    }

    @Test
    public void persist_prepareMi() throws Exception {
        CvObject cv = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPARC_MI_REF, CvDatabase.UNIPARC);
        Assert.assertNotNull(cv.getMiIdentifier());

        cv.setMiIdentifier(null);

        PersisterHelper.saveOrUpdate(cv);

        Assert.assertNotNull(cv.getMiIdentifier());
        Assert.assertEquals(CvDatabase.UNIPARC_MI_REF, cv.getMiIdentifier());
    }

}