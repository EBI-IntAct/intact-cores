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
    }

    public IntactInteractor find(Interactor term) throws FinderException{
        if (term instanceof Molecule){
            if (term instanceof Polymer){
                if (term instanceof Protein){
                    return getProteinSynchronizer().find((Protein)term);
                }
                else if (term instanceof NucleicAcid){
                    return getNucleicAcidSynchronizer().find((NucleicAcid)term);
                }
                else{
                    return getPolymerSynchronizer().find((Polymer)term);
                }
            }
            else if (term instanceof BioactiveEntity){
                return getBioactiveEntitySynchronizer().find((BioactiveEntity)term);
            }
            else if (term instanceof Gene){
                return getGeneSynchronizer().find((Gene)term);
            }
            else{
                return getMoleculeSynchronizer().find((Molecule)term);
            }
        }
        else if (term instanceof Complex){
            return getComplexSynchronizer().find((Complex)term);
        }
        else if (term instanceof InteractorPool){
            return getInteractorPoolSynchronizer().find((InteractorPool)term);
        }
        else {
            return getInteractorBaseSynchronizer().find(term);
        }
    }

    public IntactInteractor persist(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException{
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    return getProteinSynchronizer().persist((IntactProtein)term);
                }
                else if (term instanceof IntactNucleicAcid){
                    return getNucleicAcidSynchronizer().persist((IntactNucleicAcid)term);
                }
                else{
                    return getPolymerSynchronizer().persist((IntactPolymer)term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                return getBioactiveEntitySynchronizer().persist((IntactBioactiveEntity)term);
            }
            else if (term instanceof IntactGene){
                return getGeneSynchronizer().persist((IntactGene)term);
            }
            else{
                return getMoleculeSynchronizer().persist((IntactMolecule)term);
            }
        }
        else if (term instanceof IntactComplex){
            return getComplexSynchronizer().persist((IntactComplex)term);
        }
        else if (term instanceof IntactInteractorPool){
            return getInteractorPoolSynchronizer().persist((IntactInteractorPool)term);
        }
        else {
            return getInteractorBaseSynchronizer().persist(term);
        }
    }

    @Override
    public IntactInteractor synchronize(Interactor term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
            if (term instanceof IntactMolecule){
                if (term instanceof IntactPolymer){
                    if (term instanceof IntactProtein){
                        return getProteinSynchronizer().synchronize((IntactProtein)term, persist);
                    }
                    else if (term instanceof IntactNucleicAcid){
                        return getNucleicAcidSynchronizer().synchronize((IntactNucleicAcid)term, persist);
                    }
                    else{
                        return getPolymerSynchronizer().synchronize((IntactPolymer)term, persist);
                    }
                }
                else if (term instanceof IntactBioactiveEntity){
                    return getBioactiveEntitySynchronizer().synchronize((IntactBioactiveEntity)term, persist);
                }
                else if (term instanceof IntactGene){
                    return getGeneSynchronizer().synchronize((IntactGene)term, persist);
                }
                else{
                    return getMoleculeSynchronizer().synchronize((IntactMolecule)term, persist);
                }
            }
            else if (term instanceof IntactComplex){
                return getComplexSynchronizer().synchronize((IntactComplex)term, persist);
            }
            else if (term instanceof IntactInteractorPool){
                return getInteractorPoolSynchronizer().synchronize((IntactInteractorPool)term, persist);
            }
            else {
                return getInteractorBaseSynchronizer().synchronize(term, persist);
            }
    }

    public void synchronizeProperties(IntactInteractor term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactMolecule){
            if (term instanceof IntactPolymer){
                if (term instanceof IntactProtein){
                    getProteinSynchronizer().synchronizeProperties((IntactProtein)term);
                }
                else if (term instanceof IntactNucleicAcid){
                    getNucleicAcidSynchronizer().synchronizeProperties((IntactNucleicAcid)term);
                }
                else{
                    getPolymerSynchronizer().synchronizeProperties((IntactPolymer)term);
                }
            }
            else if (term instanceof IntactBioactiveEntity){
                getBioactiveEntitySynchronizer().synchronizeProperties((IntactBioactiveEntity)term);
            }
            else if (term instanceof IntactGene){
                getGeneSynchronizer().synchronizeProperties((IntactGene)term);
            }
            else{
                getMoleculeSynchronizer().synchronizeProperties((IntactMolecule)term);
            }
        }
        else if (term instanceof IntactComplex){
            getComplexSynchronizer().synchronizeProperties((IntactComplex)term);
        }
        else if (term instanceof IntactInteractorPool){
            getInteractorPoolSynchronizer().synchronizeProperties((IntactInteractorPool)term);
        }
        else {
            getInteractorBaseSynchronizer().synchronizeProperties(term);
        }
    }

    public void clearCache() {
        getProteinSynchronizer().clearCache();
        getNucleicAcidSynchronizer().clearCache();
        getMoleculeSynchronizer().clearCache();
        getPolymerSynchronizer().clearCache();
        getBioactiveEntitySynchronizer().clearCache();
        getGeneSynchronizer().clearCache();
        getComplexSynchronizer().clearCache();
        getInteractorPoolSynchronizer().clearCache();
    }

    public Collection<Interactor> fetchByIdentifier(String identifier) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.interactorBaseSynchronizer).fetchByIdentifier(identifier);
    }

    public Collection<Interactor> fetchByIdentifiers(Collection<String> identifiers) throws BridgeFailedException {
        return ((InteractorFetcher<Interactor>)this.interactorBaseSynchronizer).fetchByIdentifiers(identifiers);
    }

    public IntactDbSynchronizer<Polymer, IntactPolymer> getPolymerSynchronizer() {
        if (this.polymerSynchronizer == null){
            this.polymerSynchronizer = new IntactPolymerSynchronizer<Polymer, IntactPolymer>(getEntityManager(), IntactPolymer.class);
        }
        return polymerSynchronizer;
    }

    public IntactDbSynchronizer<Protein, IntactProtein> getProteinSynchronizer() {
        if (this.proteinSynchronizer == null){
            this.proteinSynchronizer = new IntactPolymerSynchronizer<Protein, IntactProtein>(getEntityManager(), IntactProtein.class);
        }
        return proteinSynchronizer;
    }

    public IntactDbSynchronizer<NucleicAcid, IntactNucleicAcid> getNucleicAcidSynchronizer() {
        if (this.nucleicAcidSynchronizer == null){
            this.nucleicAcidSynchronizer = new IntactPolymerSynchronizer<NucleicAcid, IntactNucleicAcid>(getEntityManager(), IntactNucleicAcid.class);
        }
        return nucleicAcidSynchronizer;
    }

    public IntactDbSynchronizer<Interactor, IntactInteractor> getInteractorBaseSynchronizer() {
        if (this.interactorBaseSynchronizer == null){
            this.interactorBaseSynchronizer = new IntactInteractorBaseSynchronizer<Interactor, IntactInteractor>(getEntityManager(), IntactInteractor.class);
        }
        return interactorBaseSynchronizer;
    }

    public IntactDbSynchronizer<Molecule, IntactMolecule> getMoleculeSynchronizer() {
        if (this.moleculeSynchronizer == null){
            this.moleculeSynchronizer = new IntactInteractorBaseSynchronizer<Molecule, IntactMolecule>(getEntityManager(), IntactMolecule.class);
        }
        return moleculeSynchronizer;
    }

    public IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> getBioactiveEntitySynchronizer() {
        if (this.bioactiveEntitySynchronizer == null){
            this.bioactiveEntitySynchronizer = new IntactBioactiveEntitySynchronizer(getEntityManager());
        }
        return bioactiveEntitySynchronizer;
    }

    public IntactDbSynchronizer<Gene, IntactGene> getGeneSynchronizer() {
        if (this.geneSynchronizer == null){
            this.geneSynchronizer = new IntactInteractorBaseSynchronizer<Gene, IntactGene>(getEntityManager(), IntactGene.class);
        }
        return geneSynchronizer;
    }

    public IntactDbSynchronizer<InteractorPool, IntactInteractorPool> getInteractorPoolSynchronizer() {
        if (this.interactorPoolSynchronizer == null){
            this.interactorPoolSynchronizer = new IntactInteractorPoolSynchronizer(getEntityManager());
            ((IntactInteractorPoolSynchronizer)this.interactorPoolSynchronizer).setInteractorSynchronizer(this);
        }
        return interactorPoolSynchronizer;
    }

    public IntactDbSynchronizer<Complex, IntactComplex> getComplexSynchronizer() {
        if (this.complexSynchronizer == null){
            this.complexSynchronizer = new IntactComplexSynchronizer(getEntityManager());
        }
        return complexSynchronizer;
    }

    public void setPolymerSynchronizer(IntactDbSynchronizer<Polymer, IntactPolymer> polymerSynchronizer) {
        this.polymerSynchronizer = polymerSynchronizer;
    }

    public void setProteinSynchronizer(IntactDbSynchronizer<Protein, IntactProtein> proteinSynchronizer) {
        this.proteinSynchronizer = proteinSynchronizer;
    }

    public void setNucleicAcidSynchronizer(IntactDbSynchronizer<NucleicAcid, IntactNucleicAcid> nucleicAcidSynchronizer) {
        this.nucleicAcidSynchronizer = nucleicAcidSynchronizer;
    }

    public void setMoleculeSynchronizer(IntactDbSynchronizer<Molecule, IntactMolecule> moleculeSynchronizer) {
        this.moleculeSynchronizer = moleculeSynchronizer;
    }

    public void setInteractorBaseSynchronizer(IntactDbSynchronizer<Interactor, IntactInteractor> interactorBaseSynchronizer) {
        this.interactorBaseSynchronizer = interactorBaseSynchronizer;
    }

    public void setBioactiveEntitySynchronizer(IntactDbSynchronizer<BioactiveEntity, IntactBioactiveEntity> bioactiveEntitySynchronizer) {
        this.bioactiveEntitySynchronizer = bioactiveEntitySynchronizer;
    }

    public void setGeneSynchronizer(IntactDbSynchronizer<Gene, IntactGene> geneSynchronizer) {
        this.geneSynchronizer = geneSynchronizer;
    }

    public void setInteractorPoolSynchronizer(IntactDbSynchronizer<InteractorPool, IntactInteractorPool> interactorPoolSynchronizer) {
        this.interactorPoolSynchronizer = interactorPoolSynchronizer;
    }

    public void setComplexSynchronizer(IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer) {
        this.complexSynchronizer = complexSynchronizer;
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
