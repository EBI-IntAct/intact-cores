package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Entity;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactAliasSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactAnnotationSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactXrefSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactCausalRelationchipSynchronizer extends AbstractIntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> implements CausalRelationshipDbSynchronizer{

    private CvTermDbSynchronizer typeSynchronizer;
    private IntActEntitySynchronizer entitySynchronizer;

    public IntactCausalRelationchipSynchronizer(EntityManager entityManager){
        super(entityManager, IntactCausalRelationship.class);
    }

    public IntactCausalRelationship find(CausalRelationship object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCausalRelationship object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize relation type
        CvTerm type = object.getRelationType();
        object.setRelationType(getTypeSynchronizer().synchronize(type, true));

        // synchronize target
        Entity target = object.getTarget();
        object.setTarget(getEntitySynchronizer().synchronize(target, false));
    }

    public void clearCache() {
        getTypeSynchronizer().clearCache();
        clearCache(entitySynchronizer);
        if (typeSynchronizer != null){
            if (entitySynchronizer == null || entitySynchronizer.getCvAnnotationSynchronizer().getTopicSynchronizer() != typeSynchronizer){
                this.typeSynchronizer.clearCache();
            }
        }
    }

    public CvTermDbSynchronizer getTypeSynchronizer() {
        if (this.typeSynchronizer == null){
            if (this.entitySynchronizer == null){
               initialiseDefaultParticipantAndRelationTypeSynchronizer();
            }
            else{
                initialiseDefaultTypeSynchronizer();
            }
        }
        return typeSynchronizer;
    }

    public IntactCausalRelationchipSynchronizer setTypeSynchronizer(CvTermDbSynchronizer typeSynchronizer) {
        this.typeSynchronizer = typeSynchronizer;
        return this;
    }

    public IntActEntitySynchronizer getEntitySynchronizer() {
        if (this.entitySynchronizer == null){
            if (this.typeSynchronizer == null){
                initialiseDefaultParticipantAndRelationTypeSynchronizer();
            }
            else{
                initialiseDefaultParticipantSynchronizer();
            }
        }
        return entitySynchronizer;
    }

    public IntactCausalRelationchipSynchronizer setEntitySynchronizer(IntActEntitySynchronizer entitySynchronizer) {
        this.entitySynchronizer = entitySynchronizer;
        return this;
    }

    public AliasDbSynchronizer<CvTermAlias> getCvAliasSynchronizer() {
        return getTypeSynchronizer().getAliasSynchronizer();
    }

    public IntactCausalRelationchipSynchronizer setCvAliasSynchronizer(AliasDbSynchronizer<CvTermAlias> aliasSynchronizer) {
        getTypeSynchronizer().setAliasSynchronizer(aliasSynchronizer);
        getEntitySynchronizer().setCvAliasSynchronizer(aliasSynchronizer);
        return this;
    }

    public AnnotationDbSynchronizer<CvTermAnnotation> getCvAnnotationSynchronizer() {
        return getTypeSynchronizer().getAnnotationSynchronizer();
    }

    public IntactCausalRelationchipSynchronizer setCvAnnotationSynchronizer(AnnotationDbSynchronizer<CvTermAnnotation> annotationSynchronizer) {
        getTypeSynchronizer().setAnnotationSynchronizer(annotationSynchronizer);
        getEntitySynchronizer().setCvAnnotationSynchronizer(annotationSynchronizer);
        return this;
    }

    public XrefDbSynchronizer<CvTermXref> getCvXrefSynchronizer() {
        return getTypeSynchronizer().getXrefSynchronizer();
    }

    public IntactCausalRelationchipSynchronizer setCvXrefSynchronizer(XrefDbSynchronizer<CvTermXref> xrefSynchronizer) {
        getTypeSynchronizer().setXrefSynchronizer(xrefSynchronizer);
        getEntitySynchronizer().setCvXrefSynchronizer(xrefSynchronizer);
        return this;
    }

    protected void initialiseDefaultParticipantAndRelationTypeSynchronizer() {
        // create new type synchronizer
        this.typeSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);

        // basic cv synchronizers to initialise
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = new IntactAliasSynchronizer<CvTermAlias>(getEntityManager(), CvTermAlias.class);
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = new IntactAnnotationSynchronizer<CvTermAnnotation>(getEntityManager(), CvTermAnnotation.class);
        cvAnnotationSynchronizer.setTopicSynchronizer(this.typeSynchronizer);
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = new IntactXrefSynchronizer<CvTermXref>(getEntityManager(), CvTermXref.class);
        IntactUtils.initialiseBasicSynchronizers(cvAliasSynchronizer, cvXrefSynchronizer, cvAnnotationSynchronizer);

        // initialise basic synchronizers for type synchronizer
        IntactUtils.initialiseBasicSynchronizers(this.typeSynchronizer, cvAliasSynchronizer, cvXrefSynchronizer, cvAnnotationSynchronizer);

        // generate participant synchronizer and set basic types
        IntActEntitySynchronizer pSynchronizer = new IntActEntitySynchronizer(getEntityManager());
        this.entitySynchronizer = pSynchronizer;
        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultParticipantSynchronizer() {
        // basic cv synchronizers
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = this.typeSynchronizer.getAliasSynchronizer();
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = this.typeSynchronizer.getAnnotationSynchronizer();
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = this.typeSynchronizer.getXrefSynchronizer();

        // generate participant synchronizer and set basic types
        IntActEntitySynchronizer pSynchronizer = new IntActEntitySynchronizer(getEntityManager());
        this.entitySynchronizer = pSynchronizer;
        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultTypeSynchronizer() {

        // generate type synchronizer from entity synchronizer
        this.typeSynchronizer = this.entitySynchronizer.getCvAnnotationSynchronizer().getTopicSynchronizer();
    }

    @Override
    protected Object extractIdentifier(IntactCausalRelationship object) {
        return object.getId();
    }

    @Override
    protected IntactCausalRelationship instantiateNewPersistentInstance(CausalRelationship object, Class<? extends IntactCausalRelationship> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactCausalRelationship(object.getRelationType(), object.getTarget());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<CausalRelationship, IntactCausalRelationship>(this));
    }
}
