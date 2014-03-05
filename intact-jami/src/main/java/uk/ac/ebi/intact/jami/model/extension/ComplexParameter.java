package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.listener.ComplexParameterListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of parameter for modelled interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@javax.persistence.Entity
@Table( name = "ia_complex_parameter" )
public class ComplexParameter extends AbstractIntactParameter implements ModelledParameter{
    private Collection<Publication> publications;

    private Experiment experiment;

    protected ComplexParameter() {
        super();
    }

    public ComplexParameter(CvTerm type, ParameterValue value) {
        super(type, value);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, CvTerm unit) {
        super(type, value, unit);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty) {
        super(type, value, unit, uncertainty);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty) {
        super(type, value, uncertainty);
    }

    public ComplexParameter(CvTerm type, String value) throws IllegalParameterException {
        super(type, value);
    }

    public ComplexParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        super(type, value, unit);
    }

    @Transient
    public Collection<Publication> getPublications() {
        if (this.publications == null){
            this.publications = new ArrayList<Publication>();
        }
        return this.publications;
    }

    /**
     *
     * @param experiment
     * @deprecated Only kept for backward compatibility with intact core.
     * we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    public void setExperiment( Experiment experiment ) {
        this.experiment = experiment;
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac" )
    @Target(IntactExperiment.class)
    /**
     * @deprecated we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    private Experiment getExperiment() {
        return this.experiment;
    }
}
