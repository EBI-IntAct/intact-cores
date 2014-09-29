package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Implementation of xref for interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_interactor_xref" )
public class InteractionXref extends AbstractIntactXref{

    protected InteractionXref() {
    }

    public InteractionXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public InteractionXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public InteractionXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public InteractionXref(CvTerm database, String id) {
        super(database, id);
    }

    @Column(name = "category", nullable = false, updatable = false)
    @NotNull
    @Deprecated
    /**
     * @deprecated this method only exist for backward compatibility and the fact that all xrefs of interactors and interactions are in the same table.
     * When the tables are separated, this method can be removed
     */
    private String getCategory() {
        return "simple";
    }

    @Deprecated
    /**
     * @deprecated this method only exist for backward compatibility and the fact that all xrefs of interactors and interactions are in the same table.
     * When the tables are separated, this method can be removed
     */
    private void setCategory(String category){
        // nothing to do
    }
}
