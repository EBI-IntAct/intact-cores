package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CooperativityEvidence;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.CooperativityEvidenceCloner;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCooperativityEvidence;
import uk.ac.ebi.intact.jami.synchronizer.impl.AliasSynchronizerTemplate;
import uk.ac.ebi.intact.jami.synchronizer.impl.AnnotationSynchronizerTemplate;
import uk.ac.ebi.intact.jami.synchronizer.impl.CvTermSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.XrefSynchronizerTemplate;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Default finder/synchronizer for cooperativity evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactCooperativityEvidenceSynchronizer extends AbstractIntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence>
implements CooperativityEvidenceDbSynchronizer{
    private CvTermDbSynchronizer methodSynchronizer;
    private PublicationDbSynchronizer publicationSynchronizer;

    public IntactCooperativityEvidenceSynchronizer(EntityManager entityManager){
        super(entityManager, IntactCooperativityEvidence.class);
    }

    public IntactCooperativityEvidence find(CooperativityEvidence object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCooperativityEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // publication first
        Publication pub = object.getPublication();
        object.setPublication(getPublicationSynchronizer().synchronize(pub, true));

        // check evidence methodse
        prepareEvidenceMethods(object);
    }

    public void clearCache() {
        clearCache(this.publicationSynchronizer);
        if (this.methodSynchronizer != null){
            if (publicationSynchronizer == null || publicationSynchronizer.getCvAnnotationSynchronizer().getTopicSynchronizer() != methodSynchronizer){
                this.methodSynchronizer.clearCache();
            }
        }
    }

    public CvTermDbSynchronizer getMethodSynchronizer() {
        if (this.methodSynchronizer == null){
            if (this.publicationSynchronizer == null){
                initialiseDefaultPublicationAndEvidenceSynchronizer();
            }
            else{
                initialiseDefaultMethodSynchronizer();
            }
        }
        return methodSynchronizer;
    }

    public IntactCooperativityEvidenceSynchronizer setMethodSynchronizer(CvTermDbSynchronizer methodSynchronizer) {
        this.methodSynchronizer = methodSynchronizer;
        return this;
    }

    public PublicationDbSynchronizer getPublicationSynchronizer() {
        if (this.publicationSynchronizer == null){
            if (this.methodSynchronizer == null){
                initialiseDefaultPublicationAndEvidenceSynchronizer();
            }
            else{
                initialiseDefaultPublicationSynchronizer();
            }
        }
        return publicationSynchronizer;
    }

    public IntactCooperativityEvidenceSynchronizer setPublicationSynchronizer(PublicationDbSynchronizer publicationSynchronizer) {
        this.publicationSynchronizer = publicationSynchronizer;
        return this;
    }

    public AliasDbSynchronizer<CvTermAlias> getCvAliasSynchronizer() {
        return getMethodSynchronizer().getAliasSynchronizer();
    }

    public IntactCooperativityEvidenceSynchronizer setCvAliasSynchronizer(AliasDbSynchronizer<CvTermAlias> aliasSynchronizer) {
        getMethodSynchronizer().setAliasSynchronizer(aliasSynchronizer);
        getPublicationSynchronizer().setCvAliasSynchronizer(aliasSynchronizer);
        return this;
    }

    public AnnotationDbSynchronizer<CvTermAnnotation> getCvAnnotationSynchronizer() {
        return getMethodSynchronizer().getAnnotationSynchronizer();
    }

    public IntactCooperativityEvidenceSynchronizer setCvAnnotationSynchronizer(AnnotationDbSynchronizer<CvTermAnnotation> annotationSynchronizer) {
        getMethodSynchronizer().setAnnotationSynchronizer(annotationSynchronizer);
        getPublicationSynchronizer().setCvAnnotationSynchronizer(annotationSynchronizer);
        return this;
    }

    public XrefDbSynchronizer<CvTermXref> getCvXrefSynchronizer() {
        return getMethodSynchronizer().getXrefSynchronizer();
    }

    public IntactCooperativityEvidenceSynchronizer setCvXrefSynchronizer(XrefDbSynchronizer<CvTermXref> xrefSynchronizer) {
        getMethodSynchronizer().setXrefSynchronizer(xrefSynchronizer);
        getPublicationSynchronizer().setCvXrefSynchronizer(xrefSynchronizer);
        return this;
    }

    protected void initialiseDefaultPublicationAndEvidenceSynchronizer() {
        // create new type synchronizer
        this.methodSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);

        // basic cv synchronizers to initialise
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = new AliasSynchronizerTemplate<CvTermAlias>(getEntityManager(), CvTermAlias.class);
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = new AnnotationSynchronizerTemplate<CvTermAnnotation>(getEntityManager(), CvTermAnnotation.class);
        cvAnnotationSynchronizer.setTopicSynchronizer(this.methodSynchronizer);
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = new XrefSynchronizerTemplate<CvTermXref>(getEntityManager(), CvTermXref.class);
        IntactUtils.initialiseBasicSynchronizers(cvAliasSynchronizer, cvXrefSynchronizer, cvAnnotationSynchronizer);

        // initialise basic synchronizers for type synchronizer
        IntactUtils.initialiseBasicSynchronizers(this.methodSynchronizer, cvAliasSynchronizer, cvXrefSynchronizer, cvAnnotationSynchronizer);

        // generate publication synchronizer and set basic types
        IntactPublicationSynchronizer pSynchronizer = new IntactPublicationSynchronizer(getEntityManager());
        this.publicationSynchronizer = pSynchronizer;
        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultPublicationSynchronizer() {
        // basic cv synchronizers
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = this.methodSynchronizer.getAliasSynchronizer();
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = this.methodSynchronizer.getAnnotationSynchronizer();
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = this.methodSynchronizer.getXrefSynchronizer();

        // generate publication synchronizer and set basic types
        IntactPublicationSynchronizer pSynchronizer = new IntactPublicationSynchronizer(getEntityManager());
        this.publicationSynchronizer = pSynchronizer;
        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultMethodSynchronizer() {

        // generate type synchronizer from publication synchronizer
        this.methodSynchronizer = this.publicationSynchronizer.getCvAnnotationSynchronizer().getTopicSynchronizer();
    }

    protected void prepareEvidenceMethods(IntactCooperativityEvidence object) throws PersisterException, FinderException, SynchronizerException {

        if (object.areEvidenceMethodsInitialized()){
            Collection<CvTerm> parametersToPersist = new ArrayList<CvTerm>(object.getEvidenceMethods());
            for (CvTerm param : parametersToPersist){
                CvTerm expParam = getMethodSynchronizer().synchronize(param, true);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getEvidenceMethods().remove(param);
                    object.getEvidenceMethods().add(expParam);
                }
            }
        }
    }

    @Override
    protected Object extractIdentifier(IntactCooperativityEvidence object) {
        return object.getId();
    }

    @Override
    protected IntactCooperativityEvidence instantiateNewPersistentInstance(CooperativityEvidence object, Class<? extends IntactCooperativityEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCooperativityEvidence ev = new IntactCooperativityEvidence(object.getPublication());
        CooperativityEvidenceCloner.copyAndOverrideCooperativityEvidenceProperties(object, ev);
        return ev;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CooperativityEvidence, IntactCooperativityEvidence>(this));
    }
}
