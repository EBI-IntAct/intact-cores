package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of xref for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_experiment_xref" )
public class ExperimentXref extends AbstractIntactXref{

    private Experiment parent;

    public ExperimentXref() {
    }

    public ExperimentXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public ExperimentXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public ExperimentXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public ExperimentXref(CvTerm database, String id) {
        super(database, id);
    }

    @ManyToOne( targetEntity = IntactExperiment.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactExperiment.class)
    public Experiment getParent() {
        return parent;
    }

    public void setParent(Experiment parent) {
        this.parent = parent;
    }
}
