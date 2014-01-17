package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Molecule;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.Xref;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * IntAct implementation of molecule
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "molecule" )
public class IntactMolecule extends IntactInteractor implements Molecule{

    protected IntactMolecule() {
        super();
    }

    public IntactMolecule(String name, CvTerm type) {
        super(name, type);
    }

    public IntactMolecule(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactMolecule(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactMolecule(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactMolecule(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactMolecule(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactMolecule(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactMolecule(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactMolecule(String name) {
        super(name);
    }

    public IntactMolecule(String name, String fullName) {
        super(name, fullName);
    }

    public IntactMolecule(String name, Organism organism) {
        super(name, organism);
    }

    public IntactMolecule(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactMolecule(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactMolecule(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactMolecule(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactMolecule(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }
}
