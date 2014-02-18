package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for basic allostery effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactAllosterySynchronizer extends IntactCooperativeEffectBaseSynchronizer<Allostery, IntactAllostery> implements AllosteryDbSynchronizer{
    private IntActEntitySynchronizer participantSynchronizer;
    private FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> featureSynchronizer;

    private static final Log log = LogFactory.getLog(IntactAllosterySynchronizer.class);

    public IntactAllosterySynchronizer(EntityManager entityManager) {
        super(entityManager, IntactAllostery.class);
    }

    public void synchronizeProperties(IntactAllostery object) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(object);

        // prepare mechanism
        prepareMechanism(object);
        // prepare allostery type
        prepareAllosteryType(object);
        // prepare allosteric effector
        prepareAllostericEffector(object);
        // prepare allostericMolecule
        prepareAllostericMolecule(object);

    }

    public void clearCache() {
        super.clearCache();
        clearCache(participantSynchronizer);
        if (featureSynchronizer != null){
            if (participantSynchronizer == null || participantSynchronizer.getModelledFeatureSynchronizer() != featureSynchronizer){
                this.featureSynchronizer.clearCache();
            }
        }
    }

    public IntActEntitySynchronizer getParticipantSynchronizer() {
        if (this.participantSynchronizer == null){
            if (featureSynchronizer == null){
                initialiseDefaultParticipantAndFeatureSynchronizer();
            }
            else{
                initialiseDefaultParticipantSynchronizer();
            }
        }
        return participantSynchronizer;
    }

    public IntactAllosterySynchronizer setParticipantSynchronizer(IntActEntitySynchronizer participantSynchronizer) {
        this.participantSynchronizer = participantSynchronizer;
        return this;
    }

    public FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> getFeatureSynchronizer() {
        if (this.featureSynchronizer == null){
            if (participantSynchronizer == null){
                initialiseDefaultParticipantAndFeatureSynchronizer();
            }
            else{
                initialiseDefaultFeatureSynchronizer();
            }
        }
        return featureSynchronizer;
    }

    public IntactAllosterySynchronizer setFeatureSynchronizer(FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> featureSynchronizer) {
        this.featureSynchronizer = featureSynchronizer;
        return this;
    }

    protected void initialiseDefaultParticipantAndFeatureSynchronizer() {
        // basic cv synchronizers to initialise
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = new IntactAliasSynchronizer<CvTermAlias>(getEntityManager(), CvTermAlias.class);
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = new IntactAnnotationSynchronizer<CvTermAnnotation>(getEntityManager(), CvTermAnnotation.class);
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = new IntactXrefSynchronizer<CvTermXref>(getEntityManager(), CvTermXref.class);
        IntactUtils.initialiseBasicSynchronizers(cvAliasSynchronizer, cvXrefSynchronizer, cvAnnotationSynchronizer);

        // generate participant synchronizer and set basic types
        IntActEntitySynchronizer pSynchronizer = new IntActEntitySynchronizer(getEntityManager());
        IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature> fSynchronizer = new IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature>(getEntityManager(), IntactModelledFeature.class);
        this.participantSynchronizer = pSynchronizer;
        this.featureSynchronizer = fSynchronizer;

        pSynchronizer.setModelledFeatureSynchronizer(this.featureSynchronizer);

        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultParticipantSynchronizer() {
        // basic cv synchronizers
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = this.featureSynchronizer.getCvAliasSynchronizer();
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = this.featureSynchronizer.getCvAnnotationSynchronizer();
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = this.featureSynchronizer.getCvXrefSynchronizer();

        // generate participant synchronizer and set basic types
        IntActEntitySynchronizer pSynchronizer = new IntActEntitySynchronizer(getEntityManager());
        this.participantSynchronizer = pSynchronizer;
        pSynchronizer.setModelledFeatureSynchronizer(getFeatureSynchronizer());
        pSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        pSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
        pSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
    }

    protected void initialiseDefaultFeatureSynchronizer() {
        // basic cv synchronizers
        AliasDbSynchronizer<CvTermAlias> cvAliasSynchronizer = this.participantSynchronizer.getCvAliasSynchronizer();
        AnnotationDbSynchronizer<CvTermAnnotation> cvAnnotationSynchronizer = this.participantSynchronizer.getCvAnnotationSynchronizer();
        XrefDbSynchronizer<CvTermXref> cvXrefSynchronizer = this.participantSynchronizer.getCvXrefSynchronizer();
        // generate feature synchronizer and set basic types
        IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature> fSynchronizer = new IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature>(getEntityManager(), IntactModelledFeature.class);
        this.featureSynchronizer = fSynchronizer;

        fSynchronizer.setCvAliasSynchronizer(cvAliasSynchronizer);
        fSynchronizer.setCvAnnotationSynchronizer(cvAnnotationSynchronizer);
        fSynchronizer.setCvXrefSynchronizer(cvXrefSynchronizer);
    }

    protected void prepareAllostericEffector(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        switch (object.getAllostericEffector().getEffectorType()){
            case molecule:
                MoleculeEffector moleculeEffector = (MoleculeEffector)object.getAllostericEffector();
                if (!(moleculeEffector instanceof IntactMoleculeEffector)){
                    IntactMoleculeEffector newEffector = new IntactMoleculeEffector(moleculeEffector.getMolecule());
                    moleculeEffector = newEffector;

                    object.setAllostericEffector(newEffector);
                }

                ModelledParticipant participant = moleculeEffector.getMolecule();
                ((IntactMoleculeEffector) moleculeEffector).setMolecule((ModelledParticipant)getParticipantSynchronizer().synchronize(participant, false));
                break;
            case feature_modification:
                FeatureModificationEffector featureEffector = (FeatureModificationEffector)object.getAllostericEffector();
                if (!(featureEffector instanceof IntactFeatureModificationEffector)){
                    IntactFeatureModificationEffector newEffector = new IntactFeatureModificationEffector(featureEffector.getFeatureModification());
                    featureEffector = newEffector;

                    object.setAllostericEffector(newEffector);
                }

                ModelledFeature feature = featureEffector.getFeatureModification();
                ((IntactFeatureModificationEffector) featureEffector).setFeatureModification(getFeatureSynchronizer().synchronize(feature, false));
                break;
            default:
                throw new SynchronizerException("Does not recognize allosteric effector "+object.getAllostericEffector()+", so cannot synchronize the allosteric effector. Was expecting a molecule effector or a feature modification effector.");
        }
    }

    protected void prepareMechanism(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        CvTerm mehcanism = object.getAllostericMechanism();
        if (mehcanism != null){
            object.setAllostericMechanism(getCvSynchronizer().synchronize(mehcanism, true));
        }
    }

    protected void prepareAllosteryType(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        CvTerm type = object.getAllosteryType();
        if (type != null){
            object.setAllosteryType(getCvSynchronizer().synchronize(type, true));
        }
    }

    protected void prepareAllostericMolecule(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        ModelledParticipant participant = object.getAllostericMolecule();
        object.setAllostericMolecule((ModelledParticipant)getParticipantSynchronizer().synchronize(participant, false));

    }

    @Override
    protected IntactAllostery instantiateNewPersistentInstance(Allostery object, Class<? extends IntactAllostery> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactAllostery newAllostery;
        switch (object.getAllostericEffector().getEffectorType()){
            case molecule:
                newAllostery = new IntactAllosteryWithMolecule(object.getOutCome(), object.getAllostericMolecule(), new IntactMoleculeEffector(((MoleculeEffector)object.getAllostericEffector()).getMolecule()));
                break;
            case feature_modification:
                newAllostery = new IntactAllosteryWithFeature(object.getOutCome(), object.getAllostericMolecule(), new IntactFeatureModificationEffector(((FeatureModificationEffector)object.getAllostericEffector()).getFeatureModification()));
            default:
                newAllostery = new IntactAllostery<AllostericEffector>(object.getOutCome(), object.getAllostericMolecule(), object.getAllostericEffector());
                break;
        }
        CooperativeEffectCloner.copyAndOverrideAllosteryProperties(object, newAllostery);
        return newAllostery;
    }
}
