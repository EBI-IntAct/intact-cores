package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Synchronizer for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class CompositeInteractorSynchronizer extends AbstractIntactDbSynchronizer<Interactor, IntactInteractor>
        implements InteractorFetcher<Interactor>, InteractorSynchronizer<Interactor, IntactInteractor>{

    public InteractorCompositeSynchronizer(SynchronizerContext context){
        super(context, IntactInteractor.class);
    }

    public IntactInteractor find(Interactor term) throws FinderException {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return getContext().getProteinSynchronizer().find((Protein) term);
                }
                else if (term instanceof NucleicAcid){
                    return getContext().getNucleicAcidSynchronizer().find((NucleicAcid) term);
                }
                else{
                    return getContext().getPolymerSynchronizer().find((Polymer) term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return getContext().getBioactiveEntitySynchronizer().find((BioactiveEntity) term);
            }
            else if (term instanceof Gene){
                return getContext().getGeneSynchronizer().find((Gene) term);
            }
            else{
                return getContext().getMoleculeSynchronizer().find((Molecule) term);
            }
        }
        else if (term instanceof Complex){
            return getContext().getComplexSynchronizer().find((Complex) term);
        }
        else if (term instanceof InteractorPool){
            return getContext().getInteractorPoolSynchronizer().find((InteractorPool) term);
        }
        else {
            return getContext().getInteractorBaseSynchronizer().find(term);
        }
    }

    public IntactInteractor persist(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    return getContext().getProteinSynchronizer().persist((IntactProtein) term);
                }
                else if (term instanceof IntactNucleicAcid){
                    return getContext().getNucleicAcidSynchronizer().persist((IntactNucleicAcid) term);
                }
                else{
                    return getContext().getPolymerSynchronizer().persist((IntactPolymer) term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                return getContext().getBioactiveEntitySynchronizer().persist((IntactBioactiveEntity) term);
            }
            else if (term instanceof IntactGene){
                return getContext().getGeneSynchronizer().persist((IntactGene) term);
            }
            else{
                return getContext().getMoleculeSynchronizer().persist((IntactMolecule) term);
            }
        }
        else if (term instanceof IntactComplex){
            return getContext().getComplexSynchronizer().persist((IntactComplex) term);
        }
        else if (term instanceof IntactInteractorPool){
            return getContext().getInteractorPoolSynchronizer().persist((IntactInteractorPool) term);
        }
        else {
            return getContext().getInteractorBaseSynchronizer().persist(term);
        }
    }

    @Override
    public IntactInteractor synchronize(Interactor term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
            if (term instanceof IntactMolecule){
                if (term instanceof IntactPolymer){
                    if (term instanceof IntactProtein){
                        return getContext().getProteinSynchronizer().synchronize((IntactProtein) term, persist);
                    }
                    else if (term instanceof IntactNucleicAcid){
                        return getContext().getNucleicAcidSynchronizer().synchronize((IntactNucleicAcid) term, persist);
                    }
                    else{
                        return getContext().getPolymerSynchronizer().synchronize((IntactPolymer) term, persist);
                    }
                }
                else if (term instanceof IntactBioactiveEntity){
                    return getContext().getBioactiveEntitySynchronizer().synchronize((IntactBioactiveEntity) term, persist);
                }
                else if (term instanceof IntactGene){
                    return getContext().getGeneSynchronizer().synchronize((IntactGene) term, persist);
                }
                else{
                    return getContext().getMoleculeSynchronizer().synchronize((IntactMolecule)term, persist);
                }
            }
            else if (term instanceof IntactComplex){
                return getContext().getComplexSynchronizer().synchronize((IntactComplex) term, persist);
            }
            else if (term instanceof IntactInteractorPool){
                return getContext().getInteractorPoolSynchronizer().synchronize((IntactInteractorPool) term, persist);
            }
            else {
                return getContext().getInteractorBaseSynchronizer().synchronize(term, persist);
            }
    }

    public void synchronizeProperties(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    getContext().getProteinSynchronizer().synchronizeProperties((IntactProtein) term);
                }
                else if (term instanceof IntactNucleicAcid){
                    getContext().getNucleicAcidSynchronizer().synchronizeProperties((IntactNucleicAcid) term);
                }
                else{
                    getContext().getPolymerSynchronizer().synchronizeProperties((IntactPolymer) term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                getContext().getBioactiveEntitySynchronizer().synchronizeProperties((IntactBioactiveEntity) term);
            }
            else if (term instanceof IntactGene){
                getContext().getGeneSynchronizer().synchronizeProperties((IntactGene) term);
            }
            else{
                getContext().getMoleculeSynchronizer().synchronizeProperties((IntactMolecule) term);
            }
        }
        else if (term instanceof IntactComplex){
            getContext().getComplexSynchronizer().synchronizeProperties((IntactComplex) term);
        }
        else if (term instanceof IntactInteractorPool){
            getContext().getInteractorPoolSynchronizer().synchronizeProperties((IntactInteractorPool) term);
        }
        else {
            getContext().getInteractorBaseSynchronizer().synchronizeProperties(term);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public Collection<Interactor> fetchByIdentifier(String identifier) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)getContext().getInteractorBaseSynchronizer()).fetchByIdentifier(identifier);
    }

    public Collection<Interactor> fetchByIdentifiers(Collection<String> identifiers) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)getContext().getInteractorTypeSynchronizer()).fetchByIdentifiers(identifiers);
    }

    @Override
    protected Object extractIdentifier(IntactInteractor object) {
        return object.getAc();
    }

    @Override
    protected IntactInteractor instantiateNewPersistentInstance(Interactor object, Class<? extends IntactInteractor> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactInteractor newInteractor = intactClass.getConstructor(String.class).newInstance(object.getShortName());
        InteractorCloner.copyAndOverrideBasicInteractorProperties(object, newInteractor);
        return newInteractor;
    }
}
