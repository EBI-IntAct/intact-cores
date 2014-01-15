package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Implementation of annotation for experiments
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_experiment_annot" )
public class ExperimentAnnotation extends AbstractIntactAnnotation{

    private Experiment parent;

    public ExperimentAnnotation() {
        super();
    }

    public ExperimentAnnotation(CvTerm topic) {
        super(topic);
    }

    public ExperimentAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }

    @ManyToOne( targetEntity = IntactExperiment.class )
    @JoinColumn( name = "parent_ac", referencedColumnName = "ac" )
    @Target(IntactExperiment.class)
    public Experiment getParent() {
        return parent;
    }

    public void setParent(Experiment parent) {
        this.parent = parent;
    }
}
