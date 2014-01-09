package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.NamedExperiment;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of alias for experiments
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_experiment_alias" )
public class ExperimentAlias extends AbstractIntactAlias{

    private NamedExperiment parent;

    protected ExperimentAlias() {
    }

    public ExperimentAlias(CvTerm type, String name) {
        super(type, name);
    }

    public ExperimentAlias(String name) {
        super(name);
    }

    @ManyToOne( targetEntity = IntactExperiment.class )
    @JoinColumn( name = "parent_ac" )
    @Target(IntactExperiment.class)
    public NamedExperiment getParent() {
        return parent;
    }

    public void setParent(NamedExperiment parent) {
        this.parent = parent;
    }
}
