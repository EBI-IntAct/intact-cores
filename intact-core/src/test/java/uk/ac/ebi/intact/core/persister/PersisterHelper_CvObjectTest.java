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
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.InMemoryDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;

import java.util.Properties;

/**
 * PersisterHelper tester.
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

        Assert.assertFalse( getDaoFactory().isTransactionActive() );
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

        String miIdentifier = newExpRole.getIdentifier();
        assertNotNull( miIdentifier );
        assertEquals( "roleMi", miIdentifier );
    }

    @Test
    public void persist_existing_object() throws Exception {
        final String expRoleLabel = "EXP_ROLE";

        CvExperimentalRole expRole = getMockBuilder().createCvObject( CvExperimentalRole.class, "MI:xxxx", expRoleLabel);
        PersisterHelper.saveOrUpdate(expRole);

        CvExperimentalRole expRole2 = getMockBuilder().createCvObject( CvExperimentalRole.class, "MI:xxxx", expRoleLabel);
        PersisterHelper.saveOrUpdate(expRole2);



        Assert.assertNotSame( expRole, expRole2 );
        Assert.assertEquals( expRole.getAc(), expRole2.getAc() );
    }

    @Test (expected = PersisterException.class)
    public void add_annotation_on_existing_cv() throws Exception {
        final String expRoleLabel = "EXP_ROLE";

        CvExperimentalRole expRole = getMockBuilder().createCvObject( CvExperimentalRole.class, "MI:xxxx", expRoleLabel);
        Assert.assertEquals( 0, expRole.getAnnotations().size());
        PersisterHelper.saveOrUpdate(expRole);

        final CvExperimentalRole role = getDaoFactory().getCvObjectDao( CvExperimentalRole.class ).getByShortLabel( expRoleLabel );
        Assert.assertNotNull( role );
        Annotation annotation = getMockBuilder().createAnnotation( "text", null, "topic" );
        role.addAnnotation( annotation );
        PersisterHelper.saveOrUpdate(role);
    }

    @Test
    public void add_annotation_on_existing_cv_transientsPersisted() throws Exception {
        final String expRoleLabel = "EXP_ROLE";

        CvExperimentalRole expRole = getMockBuilder().createCvObject( CvExperimentalRole.class, "MI:xxxx", expRoleLabel);
        Assert.assertEquals( 0, expRole.getAnnotations().size());
        PersisterHelper.saveOrUpdate(expRole);
        commitTransaction();

        beginTransaction();
        final CvExperimentalRole role = getDaoFactory().getCvObjectDao( CvExperimentalRole.class ).getByShortLabel( expRoleLabel );
        Assert.assertNotNull( role );
        Annotation annotation = getMockBuilder().createAnnotation( "text", null, "topic" );
        role.addAnnotation( annotation );

        // if we pass the cvTopic to the saveOrUpdate, the PersisterException won't happen
        PersisterHelper.saveOrUpdate(annotation.getCvTopic(), role);

        final CvExperimentalRole role2 = getDaoFactory().getCvObjectDao( CvExperimentalRole.class ).getByShortLabel( expRoleLabel );
        Assert.assertNotNull( role2 );
        Assert.assertEquals( 1, role2.getAnnotations().size());
        Assert.assertEquals( "topic", role2.getAnnotations().iterator().next().getCvTopic().getShortLabel() );
    }

    @Test
    public void persist_prepareMi() throws Exception {
        CvObject cv = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPARC_MI_REF, CvDatabase.UNIPARC);
        Assert.assertNotNull(cv.getIdentifier());

        cv.setIdentifier(null);

        PersisterHelper.saveOrUpdate(cv);

        Assert.assertNotNull(cv.getIdentifier());
        Assert.assertEquals(CvDatabase.UNIPARC_MI_REF, cv.getIdentifier());
    }

    @Test
    public void persist_dagObject() throws Exception {
        CvInteractorType proteinType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);
        CvInteractorType megaProteinType = getMockBuilder().createCvObject(CvInteractorType.class, "MI:xxx5", "mega-protein");

        proteinType.addChild(megaProteinType);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(proteinType);

        Assert.assertEquals(5, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(2, stats.getPersistedCount(CvInteractorType.class, false));
    }

    @Test
    public void persist_dagObject_duplicatedInvokation() throws Exception {
        CvInteractorType proteinType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);
        CvInteractorType megaProteinType = getMockBuilder().createCvObject(CvInteractorType.class, "MI:xxx5", "mega-protein");

        proteinType.addChild(megaProteinType);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(proteinType, megaProteinType);

        Assert.assertEquals(5, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(2, stats.getPersistedCount(CvInteractorType.class, false));
    }

    @Test
    public void persist_dagObject_saveParent() throws Exception {
        CvInteractorType proteinType = getMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN_MI_REF, CvInteractorType.PROTEIN);
        CvInteractorType megaProteinType = getMockBuilder().createCvObject(CvInteractorType.class, "MI:xxx5", "mega-protein");

        proteinType.addChild(megaProteinType);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(megaProteinType);

        Assert.assertEquals(5, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(2, stats.getPersistedCount(CvInteractorType.class, false));
    }

    @Test
    public void persist_duplicatedInHierarchy() throws Exception {
        CvDatabase citation = getMockBuilder().createCvObject(CvDatabase.class, "MI:0444", "database citation");
        CvDatabase psiMi = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PSI_MI_MI_REF, CvDatabase.PSI_MI);
        CvDatabase sourceDb = getMockBuilder().createCvObject(CvDatabase.class, "MI:0489", "source database");
        CvDatabase interactionXref = getMockBuilder().createCvObject(CvDatabase.class, "MI:0461", "interaction xref");
        CvDatabase bind = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.BIND_MI_REF, CvDatabase.BIND);
        CvDatabase bind_duplicate = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.BIND_MI_REF, CvDatabase.BIND);

        citation.addChild(psiMi);
        citation.addChild(sourceDb);
        citation.addChild(interactionXref);

        sourceDb.addChild(bind);
        interactionXref.addChild(bind_duplicate);
        
        PersisterStatistics stats = PersisterHelper.saveOrUpdate(citation);

        Assert.assertEquals(7, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(4, stats.getPersistedCount(CvDatabase.class, false));
    }

    @Test
    public void persist_parameterType_duplicated() throws Exception {
        CvParameterType paramType1 = getMockBuilder().createCvObject(CvParameterType.class, "MI:0835", "koff");
        PersisterHelper.saveOrUpdate(paramType1);

        Assert.assertEquals(1, getDaoFactory().getCvObjectDao(CvParameterType.class).countAll());

        paramType1 = getMockBuilder().createCvObject(CvParameterType.class, "MI:0835", "koff");
        PersisterHelper.saveOrUpdate(paramType1);

        Assert.assertEquals(1, getDaoFactory().getCvObjectDao(CvParameterType.class).countAll());
    }

    @Test
    @Ignore // if the EntityStateCopier copies the tree of CVDagObjects, it messes up the synchronization
    // as it copies transient versions of instances already synchronized
    public void persist_linkParentChildrenOnUpdate() throws Exception {
        CvDatabase citation = getMockBuilder().createCvObject(CvDatabase.class, "MI:0444", "database citation");
        CvDatabase psiMi = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PSI_MI_MI_REF, CvDatabase.PSI_MI);
        CvDatabase psiMiChild = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PSI_MI_MI_REF+"_C", CvDatabase.PSI_MI+"_C");

        PersisterHelper.saveOrUpdate(citation, psiMi, psiMiChild);

        CvDatabase refreshedCitation = reloadByAc(citation);
        CvDatabase refreshedPsiMi = reloadByAc(psiMi);

        Assert.assertEquals(0, refreshedCitation.getParents().size());
        Assert.assertEquals(0, refreshedCitation.getChildren().size());
        Assert.assertEquals(0, refreshedPsiMi.getParents().size());
        Assert.assertEquals(0, refreshedPsiMi.getChildren().size());

        // re-create same objects, but linked
        citation = getMockBuilder().createCvObject(CvDatabase.class, "MI:0444", "database citation");
        psiMi = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PSI_MI_MI_REF, CvDatabase.PSI_MI);

        citation.addChild(psiMi);
        //psiMi.addChild(psiMiChild);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(citation);
        
        Assert.assertEquals(0, stats.getPersistedCount(CvDatabase.class, false));
        Assert.assertEquals(2, stats.getMergedCount(CvDatabase.class, false));

        refreshedCitation = getDaoFactory().getCvObjectDao(CvDatabase.class).getByPsiMiRef("MI:0444");
        refreshedPsiMi = getDaoFactory().getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.PSI_MI_MI_REF);

        Assert.assertEquals(0, refreshedCitation.getParents().size());
        Assert.assertEquals(1, refreshedCitation.getChildren().size());
        Assert.assertEquals(1, refreshedPsiMi.getParents().size());
        Assert.assertEquals(0, refreshedPsiMi.getChildren().size());

    }

    private <T extends CvObject> T reloadByAc(T cv) {
        return (T) getDaoFactory().getCvObjectDao(cv.getClass()).getByAc(cv.getAc());
    }

    @Test
    public void persist_cvNoMi() throws Exception {
        CvTopic postalAddress = getMockBuilder().createCvObject(CvTopic.class, "removed", "postalAddress");
        postalAddress.setIdentifier(null);
        postalAddress.getXrefs().clear();

        PersisterHelper.saveOrUpdate(postalAddress);

        Assert.assertEquals(1, getDaoFactory().getCvObjectDao(CvTopic.class).countAll());
        Assert.assertEquals("postaladdress", getDaoFactory().getCvObjectDao(CvTopic.class).getAll().get(0).getShortLabel());

        CvTopic repeatedPostalAddress = getMockBuilder().createCvObject(CvTopic.class, "removed", "postalAddress");
        repeatedPostalAddress.setIdentifier(null);
        repeatedPostalAddress.getXrefs().clear();

        PersisterHelper.saveOrUpdate(repeatedPostalAddress);

        Assert.assertEquals(1, getDaoFactory().getCvObjectDao(CvTopic.class).countAll());
    }

    @Test
    public void prePersist_prepareIdentifier() throws Exception {
        CvDatabase premDb = getMockBuilder().createCvObject(CvDatabase.class,  null, "prem-db");
        CvDatabase brunoDb = getMockBuilder().createCvObject(CvDatabase.class,  null, "bruno-db");
        premDb.getXrefs().clear();
        brunoDb.getXrefs().clear();

        Assert.assertNull(premDb.getIdentifier());
        Assert.assertNull(brunoDb.getIdentifier());

        PersisterHelper.saveOrUpdate(premDb, brunoDb);

        CvDatabase refreshedPremDb = getDaoFactory().getCvObjectDao().getByShortLabel(CvDatabase.class, "prem-db");
        CvDatabase refreshedBrunoDb = getDaoFactory().getCvObjectDao().getByShortLabel(CvDatabase.class, "bruno-db");

        Assert.assertNotNull(refreshedPremDb);
        Assert.assertNotNull(refreshedPremDb.getIdentifier());
        Assert.assertEquals("IA:0001", refreshedPremDb.getIdentifier());

        Assert.assertEquals(1, refreshedPremDb.getXrefs().size());

        Assert.assertNotSame(refreshedPremDb.getIdentifier(), refreshedBrunoDb.getIdentifier());

        Assert.assertNotNull(brunoDb);
        Assert.assertNotNull(brunoDb.getIdentifier());
        Assert.assertEquals("IA:0002", brunoDb.getIdentifier());
        
        Assert.assertEquals(1, brunoDb.getXrefs().size());
    }

    @Test
    public void updateCvObjectAnnotation_updateWithoutAcDisabled() throws Exception {
        // setup the database
        int initialCvCount = getDaoFactory().getCvObjectDao().countAll();

        CvTopic topic = getMockBuilder().createCvObject(CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
        topic.addAnnotation(getMockBuilder().createAnnotation("Interaction", null, CvTopic.USED_IN_CLASS));
        PersisterHelper.saveOrUpdate(topic);

        Assert.assertEquals(initialCvCount+2, getDaoFactory().getCvObjectDao().countAll());

        // perform the test
        CvTopic sameTopic = getMockBuilder().createCvObject(CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
        sameTopic.addAnnotation(getMockBuilder().createAnnotation("Experiment", null, CvTopic.USED_IN_CLASS));

        Assert.assertNull(sameTopic.getAc());

        PersisterHelper.saveOrUpdate(sameTopic);  // by default, updated without ac is disabled

        // final assertions
        CvTopic reloadedTopic = getDaoFactory().getCvObjectDao(CvTopic.class).getByPsiMiRef(CvTopic.COMMENT_MI_REF);
        Assert.assertFalse(reloadedTopic.getAnnotations().isEmpty());

        Annotation usedInClassAnnot = reloadedTopic.getAnnotations().iterator().next();

        Assert.assertEquals(CvTopic.USED_IN_CLASS, usedInClassAnnot.getCvTopic().getShortLabel());

        // as updating without cv is disabled, we do not expect the annotation text to be updated
        Assert.assertEquals("Interaction", usedInClassAnnot.getAnnotationText());
    }

    @Test
    public void updateCvObjectAnnotation_updateWithoutAcEnabled() throws Exception {
        // setup the database
        int initialCvCount = getDaoFactory().getCvObjectDao().countAll();

        CvTopic topic = getMockBuilder().createCvObject(CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
        topic.addAnnotation(getMockBuilder().createAnnotation("Interaction", null, CvTopic.USED_IN_CLASS));
        PersisterHelper.saveOrUpdate(topic);

        Assert.assertEquals(initialCvCount+2, getDaoFactory().getCvObjectDao().countAll());

        // perform the test
        CvTopic sameTopic = getMockBuilder().createCvObject(CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
        sameTopic.addAnnotation(getMockBuilder().createAnnotation("Experiment", null, CvTopic.USED_IN_CLASS));

        Assert.assertNull(sameTopic.getAc());

        CorePersister corePersister = new CorePersister();
        corePersister.setUpdateWithoutAcEnabled(true);   // <----- update without AC enabled

        PersisterHelper.saveOrUpdate(corePersister, sameTopic);

        // final assertions
        CvTopic reloadedTopic = getDaoFactory().getCvObjectDao(CvTopic.class).getByPsiMiRef(CvTopic.COMMENT_MI_REF);
        Assert.assertFalse(reloadedTopic.getAnnotations().isEmpty());

        Annotation usedInClassAnnot = reloadedTopic.getAnnotations().iterator().next();

        Assert.assertEquals(CvTopic.USED_IN_CLASS, usedInClassAnnot.getCvTopic().getShortLabel());

        // as updating without cv is enabled, we expect the annotation text to be updated
        Assert.assertEquals("Experiment", usedInClassAnnot.getAnnotationText());
    }
    
    @Test
    public void usingInstitutionIntact() throws Exception {
        Properties properties = new Properties();
        properties.put(IntactEnvironment.INSTITUTION_LABEL.getFqn(), "intact");

        IntactSession session = new StandaloneSession(properties);
        DataConfig dataConfig = new InMemoryDataConfig(session);

        IntactContext.initContext(dataConfig, session);

        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getConfig().getInstitution());

        CvTopic mySuperHappyTopic = mockBuilder.createCvObject(CvTopic.class, "MY:super", "happyTopic");
        PersisterHelper.saveOrUpdate(mySuperHappyTopic);

        Assert.assertTrue(getDaoFactory().getCvObjectDao().getByAc(mySuperHappyTopic.getAc()) != null);
    }
}