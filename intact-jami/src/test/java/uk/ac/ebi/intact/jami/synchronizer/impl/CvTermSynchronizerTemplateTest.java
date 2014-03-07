package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for AliasSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional
@TransactionConfiguration
public class CvTermSynchronizerTemplateTest {

    @Autowired
    private ApplicationContext applicationContext;
    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private CvTermSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new CvTermSynchronizer(this.context);

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactUtils.createMIAliasType(Alias.GENE_NAME+ " ", Alias.GENE_NAME_MI);
        // cvs with parent/children
        IntactCvTerm annotationTopic = IntactUtils.createMITopic("teST", null);
        IntactCvTerm annotationTopicParent = IntactUtils.createMITopic(Annotation.CAUTION, Annotation.CAUTION_MI);
        annotationTopic.addParent(annotationTopicParent);
        // cvs with annotations and aliases
        IntactCvTerm cvDatabase = IntactUtils.createMIDatabase("teST", null);
        cvDatabase.getAnnotations().add(new CvTermAnnotation(annotationTopicParent));
        cvDatabase.getSynonyms().add(new CvTermAlias(aliasType, "test synonym"));
        // cvs with fullname and definition
        IntactCvTerm cvConfidenceType = IntactUtils.createMIConfidenceType("test3", null);
        cvConfidenceType.setFullName("Test Confidence");
        cvConfidenceType.setDefinition("Test Definition");
        cvConfidenceType.setObjClass(null);

        this.synchronizer.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.synchronizer.persist(aliasType);

        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(Alias.GENE_NAME, aliasType.getShortName());
        Assert.assertNull(aliasType.getFullName());
        Assert.assertNull(aliasType.getDefinition());
        Assert.assertTrue(aliasType.getAnnotations().isEmpty());
        Assert.assertTrue(aliasType.getSynonyms().isEmpty());
        Assert.assertTrue(aliasType.getParents().isEmpty());
        Assert.assertTrue(aliasType.getChildren().isEmpty());
        Assert.assertEquals(1, aliasType.getPersistentXrefs().size());
        Assert.assertEquals(IntactUtils.ALIAS_TYPE_OBJCLASS, aliasType.getObjClass());
        CvTermXref ref = (CvTermXref) aliasType.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals(Alias.GENE_NAME_MI, ref.getId());

        Assert.assertEquals(2, aliasType.getIdentifiers().size());
        Assert.assertEquals(aliasType.getAc(), XrefUtils.collectFirstIdentifierWithDatabase(aliasType.getIdentifiers(), null, "intact").getId());

        this.synchronizer.setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.synchronizer.persist(annotationTopicParent);

        Assert.assertNotNull(annotationTopic.getAc());
        Assert.assertEquals("test", annotationTopic.getShortName());
        Assert.assertNull(annotationTopic.getFullName());
        Assert.assertNull(annotationTopic.getDefinition());
        Assert.assertTrue(annotationTopic.getAnnotations().isEmpty());
        Assert.assertTrue(annotationTopic.getSynonyms().isEmpty());
        Assert.assertEquals(1, annotationTopic.getPersistentXrefs().size());
        Assert.assertTrue(annotationTopic.getChildren().isEmpty());
        Assert.assertEquals(1, annotationTopic.getParents().size());
        Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, annotationTopic.getObjClass());
        Assert.assertTrue(annotationTopicParent == annotationTopic.getParents().iterator().next());
        ref = (CvTermXref) annotationTopic.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));

        Assert.assertNotNull(annotationTopicParent.getAc());
        Assert.assertEquals(Annotation.CAUTION, annotationTopicParent.getShortName());
        Assert.assertNull(annotationTopicParent.getFullName());
        Assert.assertNull(annotationTopicParent.getDefinition());
        Assert.assertTrue(annotationTopicParent.getAnnotations().isEmpty());
        Assert.assertTrue(annotationTopicParent.getSynonyms().isEmpty());
        Assert.assertEquals(1, annotationTopicParent.getPersistentXrefs().size());
        Assert.assertTrue(annotationTopicParent.getParents().isEmpty());
        Assert.assertEquals(1, annotationTopicParent.getChildren().size());
        Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, annotationTopicParent.getObjClass());
        ref = (CvTermXref) annotationTopicParent.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals(Annotation.CAUTION_MI, ref.getId());
        Assert.assertTrue(annotationTopic == annotationTopicParent.getChildren().iterator().next());

        this.synchronizer.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.synchronizer.persist(cvDatabase);

        Assert.assertNotNull(cvDatabase.getAc());
        Assert.assertEquals("test", cvDatabase.getShortName());
        Assert.assertNull(cvDatabase.getFullName());
        Assert.assertNull(cvDatabase.getDefinition());
        Assert.assertEquals(1, cvDatabase.getAnnotations().size());
        Assert.assertTrue(cvDatabase.getParents().isEmpty());
        Assert.assertEquals(1, cvDatabase.getPersistentXrefs().size());
        Assert.assertTrue(cvDatabase.getChildren().isEmpty());
        Assert.assertEquals(1, cvDatabase.getSynonyms().size());
        Assert.assertEquals(IntactUtils.DATABASE_OBJCLASS, cvDatabase.getObjClass());
        CvTermAnnotation annot = (CvTermAnnotation) cvDatabase.getAnnotations().iterator().next();
        Assert.assertNull(annot.getAc());
        Assert.assertTrue(annotationTopicParent == annot.getTopic());
        CvTermAlias alias = (CvTermAlias) cvDatabase.getSynonyms().iterator().next();
        Assert.assertNull(alias.getAc());
        Assert.assertTrue(aliasType == alias.getType());
        Assert.assertEquals("test synonym", alias.getName());
        ref = (CvTermXref) cvDatabase.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));

        this.synchronizer.setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.synchronizer.persist(cvConfidenceType);

        Assert.assertNotNull(cvConfidenceType.getAc());
        Assert.assertEquals("test3", cvConfidenceType.getShortName());
        Assert.assertEquals("Test Confidence", cvConfidenceType.getFullName());
        Assert.assertEquals("Test Definition", cvConfidenceType.getDefinition());
        Assert.assertEquals(1, cvConfidenceType.getAnnotations().size());
        Assert.assertTrue(cvConfidenceType.getSynonyms().isEmpty());
        Assert.assertEquals(1, cvConfidenceType.getPersistentXrefs().size());
        Assert.assertTrue(cvConfidenceType.getChildren().isEmpty());
        Assert.assertTrue(cvConfidenceType.getParents().isEmpty());
        Assert.assertEquals(IntactUtils.CONFIDENCE_TYPE_OBJCLASS, cvConfidenceType.getObjClass());
        ref = (CvTermXref) cvConfidenceType.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));
        annot = (CvTermAnnotation) cvConfidenceType.getAnnotations().iterator().next();
        Assert.assertNull(annot.getAc());
        Assert.assertEquals(annot.getValue(), cvConfidenceType.getDefinition());
        Assert.assertEquals("definition", annot.getTopic().getShortName());

        entityManager.flush();

        System.out.println("flush");
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_with_existing_cv() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new CvTermSynchronizer(this.context);

        IntactCvTerm existingType = createExistingType();

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactUtils.createMIAliasType(Alias.GENE_NAME+ " ", Alias.GENE_NAME_MI);

        this.synchronizer.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        IntactCvTerm newAliasType = this.synchronizer.synchronize(aliasType, true);

        Assert.assertEquals(newAliasType.getAc(), existingType.getAc());

    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new CvTermSynchronizer(this.context);

        IntactCvTerm existingType = createExistingType();

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactUtils.createMIAliasType(Alias.GENE_NAME+ " ", Alias.GENE_NAME_MI);

        this.synchronizer.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        IntactCvTerm newAliasType = this.synchronizer.find(aliasType);

        Assert.assertEquals(newAliasType.getAc(), existingType.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new CvTermSynchronizer(this.context);

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactUtils.createMIAliasType(Alias.GENE_NAME+ " ", Alias.GENE_NAME_MI);
        // cvs with parent/children
        IntactCvTerm annotationTopic = IntactUtils.createMITopic("teST", null);
        IntactCvTerm annotationTopicParent = IntactUtils.createMITopic(Annotation.CAUTION, Annotation.CAUTION_MI);
        annotationTopic.addParent(annotationTopicParent);
        // cvs with annotations and aliases
        IntactCvTerm cvDatabase = IntactUtils.createMIDatabase("teST", null);
        cvDatabase.getAnnotations().add(new CvTermAnnotation(annotationTopicParent));
        cvDatabase.getSynonyms().add(new CvTermAlias(aliasType, "test synonym"));
        // cvs with fullname and definition
        IntactCvTerm cvConfidenceType = IntactUtils.createMIConfidenceType("test3", null);
        cvConfidenceType.setFullName("Test Confidence");
        cvConfidenceType.setDefinition("Test Definition");
        cvConfidenceType.setObjClass(null);

        this.synchronizer.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.synchronizer.synchronizeProperties(aliasType);

        Assert.assertNull(aliasType.getAc());
        Assert.assertEquals(Alias.GENE_NAME, aliasType.getShortName());
        Assert.assertNull(aliasType.getFullName());
        Assert.assertNull(aliasType.getDefinition());
        Assert.assertTrue(aliasType.getAnnotations().isEmpty());
        Assert.assertTrue(aliasType.getSynonyms().isEmpty());
        Assert.assertTrue(aliasType.getParents().isEmpty());
        Assert.assertTrue(aliasType.getChildren().isEmpty());
        Assert.assertEquals(1, aliasType.getPersistentXrefs().size());
        Assert.assertEquals(IntactUtils.ALIAS_TYPE_OBJCLASS, aliasType.getObjClass());
        CvTermXref ref = (CvTermXref) aliasType.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals(Alias.GENE_NAME_MI, ref.getId());

        Assert.assertEquals(1, aliasType.getIdentifiers().size());
        Assert.assertNull(XrefUtils.collectFirstIdentifierWithDatabase(aliasType.getIdentifiers(), null, "intact"));

        this.synchronizer.setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.synchronizer.synchronizeProperties(annotationTopic);

        Assert.assertNull(annotationTopic.getAc());
        Assert.assertEquals("test", annotationTopic.getShortName());
        Assert.assertNull(annotationTopic.getFullName());
        Assert.assertNull(annotationTopic.getDefinition());
        Assert.assertTrue(annotationTopic.getAnnotations().isEmpty());
        Assert.assertTrue(annotationTopic.getSynonyms().isEmpty());
        Assert.assertEquals(1, annotationTopic.getPersistentXrefs().size());
        Assert.assertTrue(annotationTopic.getChildren().isEmpty());
        Assert.assertEquals(1, annotationTopic.getParents().size());
        Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, annotationTopic.getObjClass());
        Assert.assertTrue(annotationTopicParent == annotationTopic.getParents().iterator().next());
        ref = (CvTermXref) annotationTopic.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));

        Assert.assertNull(annotationTopicParent.getAc());
        Assert.assertEquals(Annotation.CAUTION, annotationTopicParent.getShortName());
        Assert.assertNull(annotationTopicParent.getFullName());
        Assert.assertNull(annotationTopicParent.getDefinition());
        Assert.assertTrue(annotationTopicParent.getAnnotations().isEmpty());
        Assert.assertTrue(annotationTopicParent.getSynonyms().isEmpty());
        Assert.assertEquals(1, annotationTopicParent.getPersistentXrefs().size());
        Assert.assertTrue(annotationTopicParent.getParents().isEmpty());
        Assert.assertEquals(1, annotationTopicParent.getChildren().size());
        Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, annotationTopicParent.getObjClass());
        ref = (CvTermXref) annotationTopicParent.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals(Annotation.CAUTION_MI, ref.getId());
        Assert.assertTrue(annotationTopic == annotationTopicParent.getChildren().iterator().next());

        this.synchronizer.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.synchronizer.synchronizeProperties(cvDatabase);

        Assert.assertNull(cvDatabase.getAc());
        Assert.assertEquals("test", cvDatabase.getShortName());
        Assert.assertNull(cvDatabase.getFullName());
        Assert.assertNull(cvDatabase.getDefinition());
        Assert.assertEquals(1, cvDatabase.getAnnotations().size());
        Assert.assertTrue(cvDatabase.getParents().isEmpty());
        Assert.assertEquals(1, cvDatabase.getPersistentXrefs().size());
        Assert.assertTrue(cvDatabase.getChildren().isEmpty());
        Assert.assertEquals(1, cvDatabase.getSynonyms().size());
        Assert.assertEquals(IntactUtils.DATABASE_OBJCLASS, cvDatabase.getObjClass());
        CvTermAnnotation annot = (CvTermAnnotation) cvDatabase.getAnnotations().iterator().next();
        Assert.assertNull(annot.getAc());
        Assert.assertTrue(annotationTopicParent == annot.getTopic());
        CvTermAlias alias = (CvTermAlias) cvDatabase.getSynonyms().iterator().next();
        Assert.assertNull(alias.getAc());
        Assert.assertTrue(aliasType == alias.getType());
        Assert.assertEquals("test synonym", alias.getName());
        ref = (CvTermXref) cvDatabase.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));

        this.synchronizer.setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.synchronizer.synchronizeProperties(cvConfidenceType);

        Assert.assertNull(cvConfidenceType.getAc());
        Assert.assertEquals("test3", cvConfidenceType.getShortName());
        Assert.assertEquals("Test Confidence", cvConfidenceType.getFullName());
        Assert.assertEquals("Test Definition", cvConfidenceType.getDefinition());
        Assert.assertEquals(1, cvConfidenceType.getAnnotations().size());
        Assert.assertTrue(cvConfidenceType.getSynonyms().isEmpty());
        Assert.assertEquals(1, cvConfidenceType.getPersistentXrefs().size());
        Assert.assertTrue(cvConfidenceType.getChildren().isEmpty());
        Assert.assertTrue(cvConfidenceType.getParents().isEmpty());
        Assert.assertEquals(IntactUtils.CONFIDENCE_TYPE_OBJCLASS, cvConfidenceType.getObjClass());
        ref = (CvTermXref) cvConfidenceType.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));
        annot = (CvTermAnnotation) cvConfidenceType.getAnnotations().iterator().next();
        Assert.assertNull(annot.getAc());
        Assert.assertEquals(annot.getValue(), cvConfidenceType.getDefinition());
        Assert.assertEquals("definition", annot.getTopic().getShortName());

        entityManager.flush();

        System.out.println("flush");
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new CvTermSynchronizer(this.context);

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactUtils.createMIAliasType(Alias.GENE_NAME+ " ", Alias.GENE_NAME_MI);
        // cvs with parent/children
        IntactCvTerm annotationTopic = IntactUtils.createMITopic("teST", null);
        IntactCvTerm annotationTopicParent = IntactUtils.createMITopic(Annotation.CAUTION, Annotation.CAUTION_MI);
        annotationTopic.addParent(annotationTopicParent);
        // cvs with annotations and aliases
        IntactCvTerm cvDatabase = IntactUtils.createMIDatabase("teST", null);
        cvDatabase.getAnnotations().add(new CvTermAnnotation(annotationTopicParent));
        cvDatabase.getSynonyms().add(new CvTermAlias(aliasType, "test synonym"));
        // cvs with fullname and definition
        IntactCvTerm cvConfidenceType = IntactUtils.createMIConfidenceType("test3", null);
        cvConfidenceType.setFullName("Test Confidence");
        cvConfidenceType.setDefinition("Test Definition");
        cvConfidenceType.setObjClass(null);

        this.synchronizer.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        IntactCvTerm newCv = this.synchronizer.synchronize(aliasType, false);

        Assert.assertNull(newCv.getAc());
        Assert.assertEquals(Alias.GENE_NAME, newCv.getShortName());
        Assert.assertNull(newCv.getFullName());
        Assert.assertNull(newCv.getDefinition());
        Assert.assertTrue(newCv.getAnnotations().isEmpty());
        Assert.assertTrue(newCv.getSynonyms().isEmpty());
        Assert.assertTrue(newCv.getParents().isEmpty());
        Assert.assertTrue(newCv.getChildren().isEmpty());
        Assert.assertEquals(1, newCv.getPersistentXrefs().size());
        Assert.assertEquals(IntactUtils.ALIAS_TYPE_OBJCLASS, newCv.getObjClass());
        CvTermXref ref = (CvTermXref) newCv.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals(Alias.GENE_NAME_MI, ref.getId());

        Assert.assertEquals(1, newCv.getIdentifiers().size());
        Assert.assertNull(XrefUtils.collectFirstIdentifierWithDatabase(newCv.getIdentifiers(), null, "intact"));

        this.synchronizer.setObjClass(IntactUtils.TOPIC_OBJCLASS);
        IntactCvTerm newCv2 = this.synchronizer.synchronize(annotationTopic, false);

        Assert.assertNull(newCv2.getAc());
        Assert.assertEquals("test", newCv2.getShortName());
        Assert.assertNull(newCv2.getFullName());
        Assert.assertNull(newCv2.getDefinition());
        Assert.assertTrue(newCv2.getAnnotations().isEmpty());
        Assert.assertTrue(newCv2.getSynonyms().isEmpty());
        Assert.assertEquals(1, newCv2.getPersistentXrefs().size());
        Assert.assertTrue(newCv2.getChildren().isEmpty());
        Assert.assertEquals(1, newCv2.getParents().size());
        Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, newCv2.getObjClass());
        Assert.assertTrue(annotationTopicParent == newCv2.getParents().iterator().next());
        ref = (CvTermXref) newCv2.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));

        this.synchronizer.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        IntactCvTerm newCv3 = this.synchronizer.synchronize(cvDatabase, false);

        Assert.assertNull(newCv3.getAc());
        Assert.assertEquals("test", newCv3.getShortName());
        Assert.assertNull(newCv3.getFullName());
        Assert.assertNull(newCv3.getDefinition());
        Assert.assertEquals(1, newCv3.getAnnotations().size());
        Assert.assertTrue(newCv3.getParents().isEmpty());
        Assert.assertEquals(1, newCv3.getPersistentXrefs().size());
        Assert.assertTrue(newCv3.getChildren().isEmpty());
        Assert.assertEquals(1, newCv3.getSynonyms().size());
        Assert.assertEquals(IntactUtils.DATABASE_OBJCLASS, newCv3.getObjClass());

        this.synchronizer.setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        IntactCvTerm newCv4 = this.synchronizer.synchronize(cvConfidenceType, false);

        Assert.assertNull(newCv4.getAc());
        Assert.assertEquals("test3", newCv4.getShortName());
        Assert.assertEquals("Test Confidence", newCv4.getFullName());
        Assert.assertEquals("Test Definition", newCv4.getDefinition());
        Assert.assertEquals(1, newCv4.getAnnotations().size());
        Assert.assertTrue(newCv4.getSynonyms().isEmpty());
        Assert.assertEquals(1, newCv4.getPersistentXrefs().size());
        Assert.assertTrue(newCv4.getChildren().isEmpty());
        Assert.assertTrue(newCv4.getParents().isEmpty());
        Assert.assertEquals(IntactUtils.CONFIDENCE_TYPE_OBJCLASS, newCv4.getObjClass());
        ref = (CvTermXref) newCv4.getPersistentXrefs().iterator().next();
        Assert.assertNull(ref.getAc());
        Assert.assertEquals("intact", ref.getDatabase().getShortName());
        Assert.assertTrue(ref.getId().startsWith("IA:"));
        CvTermAnnotation annot = (CvTermAnnotation) newCv4.getAnnotations().iterator().next();
        Assert.assertNull(annot.getAc());
        Assert.assertEquals(annot.getValue(), newCv4.getDefinition());
        Assert.assertEquals("definition", annot.getTopic().getShortName());

        entityManager.flush();

        System.out.println("flush");
    }

    /*@Transactional
    @Test
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.synchronize(cvAliasWithType, true);

        Assert.assertNotNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.synchronize(organismAlias, true);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.synchronize(interactorAlias, true);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());

        entityManager.flush();
    }

    @Transactional
    @Test
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        Alias cvAliasWithType = new DefaultAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        Alias organismAlias = new DefaultAlias("test synonym 2");

        Alias interactorAlias = new DefaultAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        CvTermAlias newAlias = (CvTermAlias)this.synchronizer.synchronize(cvAliasWithType, true);

        Assert.assertNotNull(newAlias.getAc());
        Assert.assertNotNull(newAlias.getType());
        IntactCvTerm aliasType = (IntactCvTerm)newAlias.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        OrganismAlias newAlias2 = (OrganismAlias)this.synchronizer.synchronize(organismAlias, true);

        Assert.assertNotNull(newAlias2.getAc());
        Assert.assertNull(newAlias2.getType());
        Assert.assertEquals("test synonym 2", newAlias2.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        InteractorAlias newAlias3 = (InteractorAlias)this.synchronizer.synchronize(interactorAlias, true);

        Assert.assertNotNull(newAlias3.getAc());
        Assert.assertNotNull(newAlias3.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)newAlias3.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());

        entityManager.flush();
    } */

    private IntactCvTerm createExistingType() {
        // pre persist alias synonym
        IntactCvTerm aliasSynonym = new IntactCvTerm(Alias.GENE_NAME);
        aliasSynonym.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        entityManager.persist(aliasSynonym);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Alias.GENE_NAME_MI, identity);
        aliasSynonym.getPersistentXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getPersistentXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getPersistentXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return aliasSynonym;
    }
}
