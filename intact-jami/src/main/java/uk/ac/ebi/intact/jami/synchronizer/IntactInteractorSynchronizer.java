package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Synchronizer for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactInteractorSynchronizer extends AbstractIntactDbSynchronizer<Interactor, IntactInteractor> implements InteractorFetcher<Interactor>{

    private IntactDbSynchronizer<Polymer, IntactPolymer> polymerSynchronizer;
    private IntactDbSynchronizer<Protein, IntactProtein> proteinSynchronizer;
    private IntactDbSynchronizer<NucleicAcid, IntactNucleicAcid> nucleicAcidSynchronizer;
    private IntactDbSynchronizer<Interactor, IntactInteractor> interactorBaseSynchronizer;
    private IntactDbSynchronizer<Molecule, IntactMolecule> moleculeSynchronizer;
    private IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> bioactiveEntitySynchronizer;
    private IntactDbSynchronizer<Gene, IntactGene> geneSynchronizer;
    private IntactDbSynchronizer<InteractorPool, IntactInteractorPool> interactorPoolSynchronizer;
    private IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer;

    public IntactInteractorSynchronizer(EntityManager entityManager){
        super(entityManager, IntactInteractor.class);
        this.polymerSynchronizer = new IntactPolymerSynchronizer<Polymer, IntactPolymer>(entityManager, IntactPolymer.class);
        this.proteinSynchronizer = new IntactPolymerSynchronizer<Protein, IntactProtein>(entityManager, IntactProtein.class);
        this.nucleicAcidSynchronizer = new IntactPolymerSynchronizer<NucleicAcid, IntactNucleicAcid>(entityManager, IntactNucleicAcid.class);
        this.interactorBaseSynchronizer = new IntactInteractorBaseSynchronizer<Interactor, IntactInteractor>(entityManager, IntactInteractor.class);
        this.moleculeSynchronizer = new IntactInteractorBaseSynchronizer<Molecule, IntactMolecule>(entityManager, IntactMolecule.class);
        this.bioactiveEntitySynchronizer = new IntactBioactiveEntitySynchronizer(entityManager);
        this.geneSynchronizer = new IntactInteractorBaseSynchronizer<Gene, IntactGene>(entityManager, IntactGene.class);
        this.interactorPoolSynchronizer = new IntactInteractorPoolSynchronizer(entityManager);
        this.complexSynchronizer = new IntactInteractorBaseSynchronizer<Complex, IntactComplex>(entityManager, IntactComplex.class);

    }

    public IntactInteractorSynchronizer(EntityManager entityManager,IntactDbSynchronizer<Protein, IntactProtein> proteinSynchronizer,
                                        IntactDbSynchronizer<NucleicAcid, IntactNucleicAcid> nucleicAcidSynchronizer, IntactDbSynchronizer<Polymer, IntactPolymer> polymerSynchronizer,
                                        IntactDbSynchronizer<Molecule, IntactMolecule> moleculeBaseSynchronizer, IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> bioactiveEntitySynchronizer,
                                        IntactDbSynchronizer<Gene, IntactGene> geneSynchronizer, IntactDbSynchronizer<InteractorPool, IntactInteractorPool> interactorPoolSynchronizer,
                                        IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer, IntactDbSynchronizer<Interactor, IntactInteractor> interactorBaseSynchronizer){
        super(entityManager, IntactInteractor.class);
        this.polymerSynchronizer = polymerSynchronizer != null ? polymerSynchronizer : new IntactPolymerSynchronizer<Polymer, IntactPolymer>(entityManager, IntactPolymer.class);
        this.proteinSynchronizer = proteinSynchronizer != null ? proteinSynchronizer : new IntactPolymerSynchronizer<Protein, IntactProtein>(entityManager, IntactProtein.class);
        this.nucleicAcidSynchronizer = nucleicAcidSynchronizer != null ? nucleicAcidSynchronizer : new IntactPolymerSynchronizer<NucleicAcid, IntactNucleicAcid>(entityManager, IntactNucleicAcid.class);
        this.interactorBaseSynchronizer = interactorBaseSynchronizer != null ? interactorBaseSynchronizer : new IntactInteractorBaseSynchronizer<Interactor, IntactInteractor>(entityManager, IntactInteractor.class);
        this.moleculeSynchronizer = moleculeBaseSynchronizer != null ? moleculeBaseSynchronizer : new IntactInteractorBaseSynchronizer<Molecule, IntactMolecule>(entityManager, IntactMolecule.class);
        this.bioactiveEntitySynchronizer = bioactiveEntitySynchronizer != null ? bioactiveEntitySynchronizer : new IntactBioactiveEntitySynchronizer(entityManager);
        this.geneSynchronizer = geneSynchronizer != null ? geneSynchronizer : new IntactInteractorBaseSynchronizer<Gene, IntactGene>(entityManager, IntactGene.class);
        this.interactorPoolSynchronizer = interactorPoolSynchronizer != null ? interactorPoolSynchronizer : new IntactInteractorPoolSynchronizer(entityManager);
        this.complexSynchronizer = complexSynchronizer != null ? complexSynchronizer : new IntactInteractorBaseSynchronizer<Complex, IntactComplex>(entityManager, IntactComplex.class);
    }

    public IntactInteractor find(Interactor term) throws FinderException{
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return this.proteinSynchronizer.find((Protein)term);
                }
                else if (term instanceof NucleicAcid){
                    return this.nucleicAcidSynchronizer.find((NucleicAcid)term);
                }
                else{
                    return this.polymerSynchronizer.find((Polymer)term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return this.bioactiveEntitySynchronizer.find((BioactiveEntity)term);
            }
            else if (term instanceof Gene){
                return this.geneSynchronizer.find((Gene)term);
            }
            else{
                return this.moleculeSynchronizer.find((Molecule)term);
            }
        }
        else if (term instanceof Complex){
            return this.complexSynchronizer.find((Complex)term);
        }
        else if (term instanceof InteractorPool){
            return this.interactorPoolSynchronizer.find((InteractorPool)term);
        }
        else {
            return this.interactorBaseSynchronizer.find(term);
        }
    }

    public IntactInteractor persist(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException{
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    return this.proteinSynchronizer.persist((IntactProtein)term);
                }
                else if (term instanceof IntactNucleicAcid){
                    return this.nucleicAcidSynchronizer.persist((IntactNucleicAcid)term);
                }
                else{
                    return this.polymerSynchronizer.persist((IntactPolymer)term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                return this.bioactiveEntitySynchronizer.persist((IntactBioactiveEntity)term);
            }
            else if (term instanceof IntactGene){
                return this.geneSynchronizer.persist((IntactGene)term);
            }
            else{
                return this.moleculeSynchronizer.persist((IntactMolecule)term);
            }
        }
        else if (term instanceof IntactComplex){
            return this.complexSynchronizer.persist((IntactComplex)term);
        }
        else if (term instanceof IntactInteractorPool){
            return this.interactorPoolSynchronizer.persist((IntactInteractorPool)term);
        }
        else {
            return this.interactorBaseSynchronizer.persist(term);
        }
    }

    public void synchronizeProperties(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    this.proteinSynchronizer.synchronizeProperties((IntactProtein)term);
                }
                else if (term instanceof IntactNucleicAcid){
                    this.nucleicAcidSynchronizer.synchronizeProperties((IntactNucleicAcid)term);
                }
                else{
                    this.polymerSynchronizer.synchronizeProperties((IntactPolymer)term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                this.bioactiveEntitySynchronizer.synchronizeProperties((IntactBioactiveEntity)term);
            }
            else if (term instanceof IntactGene){
                this.geneSynchronizer.synchronizeProperties((IntactGene)term);
            }
            else{
                this.moleculeSynchronizer.synchronizeProperties((IntactMolecule)term);
            }
        }
        else if (term instanceof IntactComplex){
            this.complexSynchronizer.synchronizeProperties((IntactComplex)term);
        }
        else if (term instanceof IntactInteractorPool){
            this.interactorPoolSynchronizer.synchronizeProperties((IntactInteractorPool)term);
        }
        else {
            this.interactorBaseSynchronizer.synchronizeProperties(term);
        }
    }

    public void clearCache() {
        this.proteinSynchronizer.clearCache();
        this.nucleicAcidSynchronizer.clearCache();
        this.moleculeSynchronizer.clearCache();
        this.polymerSynchronizer.clearCache();
        this.bioactiveEntitySynchronizer.clearCache();
        this.geneSynchronizer.clearCache();
        this.complexSynchronizer.clearCache();
        this.interactorPoolSynchronizer.clearCache();
    }

    public Collection<Interactor> fetchByIdentifier(String identifier) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.interactorBaseSynchronizer).fetchByIdentifier(identifier);
    }

    public Collection<Interactor> fetchByIdentifiers(Collection<String> identifiers) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.interactorBaseSynchronizer).fetchByIdentifiers(identifiers);
    }

    public IntactDbSynchronizer<Polymer, IntactPolymer> getPolymerSynchronizer() {
        return polymerSynchronizer;
    }

    public IntactDbSynchronizer<Protein, IntactProtein> getProteinSynchronizer() {
        return proteinSynchronizer;
    }

    public IntactDbSynchronizer<NucleicAcid, IntactNucleicAcid> getNucleicAcidSynchronizer() {
        return nucleicAcidSynchronizer;
    }

    public IntactDbSynchronizer<Interactor, IntactInteractor> getInteractorBaseSynchronizer() {
        return interactorBaseSynchronizer;
    }

    public IntactDbSynchronizer<Molecule, IntactMolecule> getMoleculeSynchronizer() {
        return moleculeSynchronizer;
    }

    public IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> getBioactiveEntitySynchronizer() {
        return bioactiveEntitySynchronizer;
    }

    public IntactDbSynchronizer<Gene, IntactGene> getGeneSynchronizer() {
        return geneSynchronizer;
    }

    public IntactDbSynchronizer<InteractorPool, IntactInteractorPool> getInteractorPoolSynchronizer() {
        return interactorPoolSynchronizer;
    }

    public IntactDbSynchronizer<Complex, IntactComplex> getComplexSynchronizer() {
        return complexSynchronizer;
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
