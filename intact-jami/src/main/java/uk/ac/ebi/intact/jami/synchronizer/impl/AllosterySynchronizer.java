package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for basic allostery effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class AllosterySynchronizer extends CooperativeEffectSynchronizerTemplate<Allostery, AbstractIntactAllostery> {

    private static final Log log = LogFactory.getLog(AllosterySynchronizer.class);

    public AllosterySynchronizer(SynchronizerContext context) {
        super(context, AbstractIntactAllostery.class);
    }

    @Override
    public void synchronizeProperties(AbstractIntactAllostery object) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(object);

        // prepare mechanism
        prepareMechanism(object, true);
        // prepare allostery type
        prepareAllosteryType(object, true);
        // prepare allosteric effector
        prepareAllostericEffector(object, true);
        // prepare allostericMolecule
        prepareAllostericMolecule(object, true);

    }

    @Override
    public void convertPersistableProperties(AbstractIntactAllostery object) throws FinderException, PersisterException, SynchronizerException {
        super.convertPersistableProperties(object);

        // prepare mechanism
        prepareMechanism(object, false);
        // prepare allostery type
        prepareAllosteryType(object, false);
        // prepare allosteric effector
        prepareAllostericEffector(object, false);
        // prepare allostericMolecule
        prepareAllostericMolecule(object, false);

    }

    protected void prepareAllostericEffector(AbstractIntactAllostery object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        switch (object.getAllostericEffector().getEffectorType()){
            case molecule:
                MoleculeEffector moleculeEffector = (MoleculeEffector)object.getAllostericEffector();
                if (!(moleculeEffector instanceof IntactMoleculeEffector)){
                    IntactMoleculeEffector newEffector = new IntactMoleculeEffector(moleculeEffector.getMolecule());
                    moleculeEffector = newEffector;

                    object.setAllostericEffector(newEffector);
                }

                ModelledEntity participant = moleculeEffector.getMolecule();
                if (participant instanceof ModelledParticipant){
                    ((IntactMoleculeEffector) moleculeEffector).setMolecule(enableSynchronization ?
                            getContext().getModelledParticipantSynchronizer().synchronize((ModelledParticipant)participant, false) :
                            getContext().getModelledParticipantSynchronizer().convertToPersistentObject((ModelledParticipant)participant));
                }
                // TODO: what to do with participant set and candidates?
                else{
                   throw new UnsupportedOperationException("The existing allostery synchronizer does not take into account entities that are not participants");
                }
                break;
            case feature_modification:
                FeatureModificationEffector featureEffector = (FeatureModificationEffector)object.getAllostericEffector();
                if (!(featureEffector instanceof IntactFeatureModificationEffector)){
                    IntactFeatureModificationEffector newEffector = new IntactFeatureModificationEffector(featureEffector.getFeatureModification());
                    featureEffector = newEffector;

                    object.setAllostericEffector(newEffector);
                }

                ModelledFeature feature = featureEffector.getFeatureModification();
                ((IntactFeatureModificationEffector) featureEffector).setFeatureModification(enableSynchronization ?
                        getContext().getModelledFeatureSynchronizer().synchronize(feature, false) :
                        getContext().getModelledFeatureSynchronizer().convertToPersistentObject(feature));
                break;
            default:
                throw new SynchronizerException("Does not recognize allosteric effector "+object.getAllostericEffector()+", so cannot synchronize the allosteric effector. Was expecting a molecule effector or a feature modification effector.");
        }
    }

    protected void prepareMechanism(AbstractIntactAllostery object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        CvTerm mechanism = object.getAllostericMechanism();
        if (mechanism != null){
            object.setAllostericMechanism(enableSynchronization ?
                    getContext().getTopicSynchronizer().synchronize(mechanism, true) :
                    getContext().getTopicSynchronizer().convertToPersistentObject(mechanism));
        }
    }

    protected void prepareAllosteryType(AbstractIntactAllostery object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        CvTerm type = object.getAllosteryType();
        if (type != null){
            object.setAllosteryType(enableSynchronization ?
                    getContext().getTopicSynchronizer().synchronize(type, true) :
                    getContext().getTopicSynchronizer().convertToPersistentObject(type));
        }
    }

    protected void prepareAllostericMolecule(AbstractIntactAllostery object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        ModelledEntity participant = object.getAllostericMolecule();
        if (participant instanceof ModelledParticipant){
            object.setAllostericMolecule(enableSynchronization ?
                    getContext().getModelledParticipantSynchronizer().synchronize((ModelledParticipant)participant, false) :
                    getContext().getModelledParticipantSynchronizer().convertToPersistentObject((ModelledParticipant)participant));
        }
        // TODO: what to do with participant set and candidates?
        else{
            throw new UnsupportedOperationException("The existing allostery synchronizer does not take into account entities that are not participants");
        }
    }

    @Override
    protected AbstractIntactAllostery instantiateNewPersistentInstance(Allostery object, Class<? extends AbstractIntactAllostery> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        AbstractIntactAllostery newAllostery;
        switch (object.getAllostericEffector().getEffectorType()){
            case molecule:
                newAllostery = new IntactAllosteryWithMolecule(object.getOutCome(), object.getAllostericMolecule(),
                        new IntactMoleculeEffector(((MoleculeEffector)object.getAllostericEffector()).getMolecule()));
                break;
            case feature_modification:
                newAllostery = new IntactAllosteryWithFeature(object.getOutCome(), object.getAllostericMolecule(),
                        new IntactFeatureModificationEffector(((FeatureModificationEffector)object.getAllostericEffector()).
                                getFeatureModification()));
                break;
            default:
                throw new UnsupportedOperationException("Cannot instantiate a new instance of persistent allostery as it does not recognize the effector type "+object.getAllostericEffector().getEffectorType());
        }
        CooperativeEffectCloner.copyAndOverrideAllosteryProperties(object, newAllostery);
        return newAllostery;
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(AbstractIntactAllostery objectInCache, AbstractIntactAllostery originalObject) throws FinderException, PersisterException, SynchronizerException {
        // ntohing to do
    }

    @Override
    protected void storeInCache(Allostery originalObject, AbstractIntactAllostery persistentObject, AbstractIntactAllostery existingInstance) {
        // nothing to do
    }

    @Override
    protected AbstractIntactAllostery fetchObjectFromCache(Allostery object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(Allostery object) {
        return false;
    }
}
