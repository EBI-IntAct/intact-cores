package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Intact implementation of parameter for modelled interactions
 *
 * NOTE: the experiment property is deprecated and only kept for backward compatibility with intact-core.
 * It should never be used in any application as it will be deleted as soon as intact-core is removed and
 * the database is updated
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction_parameter")
public class ComplexParameter extends AbstractIntactParameter implements ModelledParameter{
    private Publication publication;

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
    public Publication getPublication() {
        return this.publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    /**
     *
     * @param experiment
     * @deprecated Only kept for backward compatibility with intact core.
     * we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    public void setDbExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac" )
    @Target(IntactExperiment.class)
    /**
     * @deprecated we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    private Experiment getDbExperiment() {
        return this.experiment;
    }
}
