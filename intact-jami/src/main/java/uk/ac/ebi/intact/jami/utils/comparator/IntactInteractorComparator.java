package uk.ac.ebi.intact.jami.utils.comparator;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.utils.comparator.interactor.InteractorComparator;

/**
 * Generic Interactor Comparator.
 *
 * Bioactive entities come first, then proteins, then genes, then nucleic acids, then interactorSet and finally Complexes.
 * If two interactors are from the same Interactor interface, it will use a more specific Comparator :
 * - Uses AbstractBioactiveEntityComparator for comparing BioactiveEntity objects.
 * - Uses AbstractProteinComparator for comparing Protein objects.
 * - Uses AbstractGeneComparator for comparing Gene objects.
 * - Uses AbstractNucleicAcidComparator for comparing NucleicAcids objects.
 * - Uses InteractorPoolComparator for comparing interactor candidates
 * - Uses IntactComplexComparator for comparing complexes
 * - Uses AbstractPolymerComparator for comparing polymers
 * - use AbstractInteractorBaseComparator for comparing basic interactors that are not one of the above.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/13</pre>
 */

public class IntactInteractorComparator extends InteractorComparator {

    /**
     * Creates a new InteractorComparator.
     */
    public IntactInteractorComparator(){
        super(new IntactExactInteractorBaseComparator(),
                new IntactComplexComparator(),
                new IntactPolymerComparator(),
                new IntactBioactiveEntityComparator(),
                new IntactGeneComparator(),
                new IntactNucleicAcidComparator(),
                new IntactProteinComparator());
    }

    public IntactInteractorComparator(IntactInteractorPoolComparator poolComparator){
        super(new IntactExactInteractorBaseComparator(),
                new IntactComplexComparator(),
                new IntactPolymerComparator(),
                new IntactBioactiveEntityComparator(),
                new IntactGeneComparator(),
                new IntactNucleicAcidComparator(),
                new IntactProteinComparator(),
                poolComparator);
    }

    public IntactInteractorComparator(IntactComplexComparator complexComparator){
        super(new IntactExactInteractorBaseComparator(),
                complexComparator != null ? complexComparator : new IntactComplexComparator(),
                new IntactPolymerComparator(),
                new IntactBioactiveEntityComparator(),
                new IntactGeneComparator(),
                new IntactNucleicAcidComparator(),
                new IntactProteinComparator());
    }

    @Override
    public int compare(Interactor interactor1, Interactor interactor2) {
        return super.compare(interactor1, interactor2);
    }
}
