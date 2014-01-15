package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.ParameterValue;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.model.listener.CvDefinitionListener;
import uk.ac.ebi.intact.jami.model.listener.CvIdentifierListener;
import uk.ac.ebi.intact.jami.model.listener.ParticipantParameterListener;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Intact implementation of parameter for a participant
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@Table( name = "ia_component_parameter" )
@EntityListeners(value = {ParticipantParameterListener.class})
public class ParticipantEvidenceParameter extends AbstractIntactParameter{

    private ParticipantEvidence participant;
    private Experiment experiment;

    protected ParticipantEvidenceParameter() {
        super();
    }

    public ParticipantEvidenceParameter(CvTerm type, ParameterValue value) {
        super(type, value);
    }

    public ParticipantEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit) {
        super(type, value, unit);
    }

    public ParticipantEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty) {
        super(type, value, unit, uncertainty);
    }

    public ParticipantEvidenceParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty) {
        super(type, value, uncertainty);
    }

    public ParticipantEvidenceParameter(CvTerm type, String value) throws IllegalParameterException {
        super(type, value);
    }

    public ParticipantEvidenceParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        super(type, value, unit);
    }

    @ManyToOne( targetEntity = IntactParticipantEvidence.class )
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    public ParticipantEvidence getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantEvidence participant) {
        this.participant = participant;
    }

    /**
     *
     * @param experiment
     * @deprecated Only kept for backward compatibility with intact core.
     * we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    public void setExperiment( Experiment experiment ) {
        this.experiment = experiment;
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac" )
    @Target(IntactExperiment.class)
    /**
     * @deprecated we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    protected Experiment getExperiment() {
        return this.experiment;
    }

}
