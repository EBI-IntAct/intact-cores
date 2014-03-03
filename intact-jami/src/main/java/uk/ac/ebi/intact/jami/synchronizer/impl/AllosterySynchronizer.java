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

public class AllosterySynchronizer extends CooperativeEffectSynchronizerTemplate<Allostery, IntactAllostery> {

    private static final Log log = LogFactory.getLog(AllosterySynchronizer.class);

    public AllosterySynchronizer(SynchronizerContext context) {
        super(context, IntactAllostery.class);
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
                ((IntactMoleculeEffector) moleculeEffector).setMolecule((ModelledParticipant)getContext().getEntitySynchronizer().synchronize(participant, false));
                break;
            case feature_modification:
                FeatureModificationEffector featureEffector = (FeatureModificationEffector)object.getAllostericEffector();
                if (!(featureEffector instanceof IntactFeatureModificationEffector)){
                    IntactFeatureModificationEffector newEffector = new IntactFeatureModificationEffector(featureEffector.getFeatureModification());
                    featureEffector = newEffector;

                    object.setAllostericEffector(newEffector);
                }

                ModelledFeature feature = featureEffector.getFeatureModification();
                ((IntactFeatureModificationEffector) featureEffector).setFeatureModification(getContext().getModelledFeatureSynchronizer().synchronize(feature, false));
                break;
            default:
                throw new SynchronizerException("Does not recognize allosteric effector "+object.getAllostericEffector()+", so cannot synchronize the allosteric effector. Was expecting a molecule effector or a feature modification effector.");
        }
    }

    protected void prepareMechanism(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        CvTerm mehcanism = object.getAllostericMechanism();
        if (mehcanism != null){
            object.setAllostericMechanism(getContext().getTopicSynchronizer().synchronize(mehcanism, true));
        }
    }

    protected void prepareAllosteryType(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        CvTerm type = object.getAllosteryType();
        if (type != null){
            object.setAllosteryType(getContext().getTopicSynchronizer().synchronize(type, true));
        }
    }

    protected void prepareAllostericMolecule(IntactAllostery object) throws PersisterException, FinderException, SynchronizerException {
        ModelledParticipant participant = object.getAllostericMolecule();
        object.setAllostericMolecule((ModelledParticipant)getContext().getEntitySynchronizer().synchronize(participant, false));

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

    @Override
    protected void storeInCache(Allostery originalObject, IntactAllostery persistentObject, IntactAllostery existingInstance) {
        // nothing to do
    }
}
