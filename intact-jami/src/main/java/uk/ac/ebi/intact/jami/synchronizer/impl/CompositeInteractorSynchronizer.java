package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.InteractorSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Synchronizer for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class CompositeInteractorSynchronizer implements InteractorFetcher<Interactor>,
        InteractorSynchronizer<Interactor, IntactInteractor>{

    private SynchronizerContext context;

    public CompositeInteractorSynchronizer(SynchronizerContext context){
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
    }

    public IntactInteractor find(Interactor term) throws FinderException {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return this.context.getProteinSynchronizer().find((Protein) term);
                }
                else if (term instanceof NucleicAcid){
                    return this.context.getNucleicAcidSynchronizer().find((NucleicAcid) term);
                }
                else{
                    return this.context.getPolymerSynchronizer().find((Polymer) term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return this.context.getBioactiveEntitySynchronizer().find((BioactiveEntity) term);
            }
            else if (term instanceof Gene){
                return this.context.getGeneSynchronizer().find((Gene) term);
            }
            else{
                return this.context.getMoleculeSynchronizer().find((Molecule) term);
            }
        }
        else if (term instanceof Complex){
            return this.context.getComplexSynchronizer().find((Complex) term);
        }
        else if (term instanceof InteractorPool){
            return this.context.getInteractorPoolSynchronizer().find((InteractorPool) term);
        }
        else {
            return this.context.getInteractorBaseSynchronizer().find(term);
        }
    }

    @Override
    public Collection<IntactInteractor> findAll(Interactor term) {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return new ArrayList<IntactInteractor>(this.context.getProteinSynchronizer().findAll((Protein) term));
                }
                else if (term instanceof NucleicAcid){
                    return new ArrayList<IntactInteractor>(this.context.getNucleicAcidSynchronizer().findAll((NucleicAcid) term));
                }
                else{
                    return new ArrayList<IntactInteractor>(this.context.getPolymerSynchronizer().findAll((Polymer) term));
                }
            }
            else if (term instanceof BioactiveEntity){
                return new ArrayList<IntactInteractor>(this.context.getBioactiveEntitySynchronizer().findAll((BioactiveEntity) term));
            }
            else if (term instanceof Gene){
                return new ArrayList<IntactInteractor>(this.context.getGeneSynchronizer().findAll((Gene) term));
            }
            else{
                return new ArrayList<IntactInteractor>(this.context.getMoleculeSynchronizer().findAll((Molecule) term));
            }
        }
        else if (term instanceof Complex){
            return new ArrayList<IntactInteractor>(this.context.getComplexSynchronizer().findAll((Complex) term));
        }
        else if (term instanceof InteractorPool){
            return new ArrayList<IntactInteractor>(this.context.getInteractorPoolSynchronizer().findAll((InteractorPool) term));
        }
        else {
            return this.context.getInteractorBaseSynchronizer().findAll(term);
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(Interactor term) {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return this.context.getProteinSynchronizer().findAllMatchingAcs((Protein) term);
                }
                else if (term instanceof NucleicAcid){
                    return this.context.getNucleicAcidSynchronizer().findAllMatchingAcs((NucleicAcid) term);
                }
                else{
                    return this.context.getPolymerSynchronizer().findAllMatchingAcs((Polymer) term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return this.context.getBioactiveEntitySynchronizer().findAllMatchingAcs((BioactiveEntity) term);
            }
            else if (term instanceof Gene){
                return this.context.getGeneSynchronizer().findAllMatchingAcs((Gene) term);
            }
            else{
                return this.context.getMoleculeSynchronizer().findAllMatchingAcs((Molecule) term);
            }
        }
        else if (term instanceof Complex){
            return this.context.getComplexSynchronizer().findAllMatchingAcs((Complex) term);
        }
        else if (term instanceof InteractorPool){
            return this.context.getInteractorPoolSynchronizer().findAllMatchingAcs((InteractorPool) term);
        }
        else {
            return this.context.getInteractorBaseSynchronizer().findAllMatchingAcs(term);
        }
    }

    public IntactInteractor persist(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    return this.context.getProteinSynchronizer().persist((IntactProtein) term);
                }
                else if (term instanceof IntactNucleicAcid){
                    return this.context.getNucleicAcidSynchronizer().persist((IntactNucleicAcid) term);
                }
                else{
                    return this.context.getPolymerSynchronizer().persist((IntactPolymer) term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                return this.context.getBioactiveEntitySynchronizer().persist((IntactBioactiveEntity) term);
            }
            else if (term instanceof IntactGene){
                return this.context.getGeneSynchronizer().persist((IntactGene) term);
            }
            else{
                return this.context.getMoleculeSynchronizer().persist((IntactMolecule) term);
            }
        }
        else if (term instanceof IntactComplex){
            return this.context.getComplexSynchronizer().persist((IntactComplex) term);
        }
        else if (term instanceof IntactInteractorPool){
            return this.context.getInteractorPoolSynchronizer().persist((IntactInteractorPool) term);
        }
        else {
            return this.context.getInteractorBaseSynchronizer().persist(term);
        }
    }

    public IntactInteractor synchronize(Interactor term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
            if (term instanceof Molecule){
                if (term instanceof Polymer){
                    if (term instanceof Protein){
                        return this.context.getProteinSynchronizer().synchronize((Protein) term, persist);
                    }
                    else if (term instanceof NucleicAcid){
                        return this.context.getNucleicAcidSynchronizer().synchronize((NucleicAcid) term, persist);
                    }
                    else{
                        return this.context.getPolymerSynchronizer().synchronize((Polymer) term, persist);
                    }
                }
                else if (term instanceof BioactiveEntity){
                    return this.context.getBioactiveEntitySynchronizer().synchronize((BioactiveEntity) term, persist);
                }
                else if (term instanceof Gene){
                    return this.context.getGeneSynchronizer().synchronize((Gene) term, persist);
                }
                else{
                    return this.context.getMoleculeSynchronizer().synchronize((Molecule)term, persist);
                }
            }
            else if (term instanceof Complex){
                return this.context.getComplexSynchronizer().synchronize((Complex) term, persist);
            }
            else if (term instanceof InteractorPool){
                return this.context.getInteractorPoolSynchronizer().synchronize((InteractorPool) term, persist);
            }
            else {
                return this.context.getInteractorBaseSynchronizer().synchronize(term, persist);
            }
    }

    public void synchronizeProperties(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    this.context.getProteinSynchronizer().synchronizeProperties((IntactProtein) term);
                }
                else if (term instanceof IntactNucleicAcid){
                    this.context.getNucleicAcidSynchronizer().synchronizeProperties((IntactNucleicAcid) term);
                }
                else{
                    this.context.getPolymerSynchronizer().synchronizeProperties((IntactPolymer) term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                this.context.getBioactiveEntitySynchronizer().synchronizeProperties((IntactBioactiveEntity) term);
            }
            else if (term instanceof IntactGene){
                this.context.getGeneSynchronizer().synchronizeProperties((IntactGene) term);
            }
            else{
                this.context.getMoleculeSynchronizer().synchronizeProperties((IntactMolecule) term);
            }
        }
        else if (term instanceof IntactComplex){
            this.context.getComplexSynchronizer().synchronizeProperties((IntactComplex) term);
        }
        else if (term instanceof IntactInteractorPool){
            this.context.getInteractorPoolSynchronizer().synchronizeProperties((IntactInteractorPool) term);
        }
        else {
            this.context.getInteractorBaseSynchronizer().synchronizeProperties(term);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public IntactDbMerger<Interactor, IntactInteractor> getIntactMerger() {
        return null;
    }

    public void setIntactMerger(IntactDbMerger<Interactor, IntactInteractor> intactMerger) {
        throw new UnsupportedOperationException("The interactor synchronizer does not support this method as it is a composite synchronizer");
    }

    public Class<? extends IntactInteractor> getIntactClass() {
        return IntactInteractor.class;
    }

    public void setIntactClass(Class<? extends IntactInteractor> intactClass) {
        throw new UnsupportedOperationException("The interactor synchronizer does not support this method as it is a composite synchronizer");
    }

    public boolean delete(Interactor term) {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return this.context.getProteinSynchronizer().delete((Protein) term);
                }
                else if (term instanceof NucleicAcid){
                    return this.context.getNucleicAcidSynchronizer().delete((NucleicAcid) term);
                }
                else{
                    return this.context.getPolymerSynchronizer().delete((Polymer) term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return this.context.getBioactiveEntitySynchronizer().delete((BioactiveEntity) term);
            }
            else if (term instanceof Gene){
                return this.context.getGeneSynchronizer().delete((Gene) term);
            }
            else{
                return this.context.getMoleculeSynchronizer().delete((Molecule) term);
            }
        }
        else if (term instanceof Complex){
            return this.context.getComplexSynchronizer().delete((Complex) term);
        }
        else if (term instanceof InteractorPool){
            return this.context.getInteractorPoolSynchronizer().delete((InteractorPool) term);
        }
        else {
            return this.context.getInteractorBaseSynchronizer().delete(term);
        }
    }

    @Override
    public IntactInteractor convertToPersistentObject(Interactor term) throws SynchronizerException, PersisterException, FinderException {
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return this.context.getProteinSynchronizer().convertToPersistentObject((Protein) term);
                }
                else if (term instanceof NucleicAcid){
                    return this.context.getNucleicAcidSynchronizer().convertToPersistentObject((NucleicAcid) term);
                }
                else{
                    return this.context.getPolymerSynchronizer().convertToPersistentObject((Polymer) term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return this.context.getBioactiveEntitySynchronizer().convertToPersistentObject((BioactiveEntity) term);
            }
            else if (term instanceof Gene){
                return this.context.getGeneSynchronizer().convertToPersistentObject((Gene) term);
            }
            else{
                return this.context.getMoleculeSynchronizer().convertToPersistentObject((Molecule)term);
            }
        }
        else if (term instanceof Complex){
            return this.context.getComplexSynchronizer().convertToPersistentObject((Complex) term);
        }
        else if (term instanceof InteractorPool){
            return this.context.getInteractorPoolSynchronizer().convertToPersistentObject((InteractorPool) term);
        }
        else {
            return this.context.getInteractorBaseSynchronizer().convertToPersistentObject(term);
        }
    }

    public Collection<Interactor> fetchByIdentifier(String identifier) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.context.getInteractorBaseSynchronizer()).fetchByIdentifier(identifier);
    }

    public Collection<Interactor> fetchByIdentifiers(Collection<String> identifiers) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.context.getInteractorTypeSynchronizer()).fetchByIdentifiers(identifiers);
    }

    @Override
    public void flush() {
        this.context.getProteinSynchronizer().flush();
        this.context.getNucleicAcidSynchronizer().flush();
        this.context.getPolymerSynchronizer().flush();
        this.context.getBioactiveEntitySynchronizer().flush();
        this.context.getGeneSynchronizer().flush();
        this.context.getMoleculeSynchronizer().flush();
        this.context.getComplexSynchronizer().flush();
        this.context.getInteractorPoolSynchronizer().flush();
        this.context.getInteractorBaseSynchronizer().flush();
    }
}
