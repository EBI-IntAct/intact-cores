package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ExperimentalEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of participant confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_component_confidence")
public class ExperimentalEntityConfidence extends AbstractIntactConfidence{

    private ExperimentalEntity participant;

    public ExperimentalEntityConfidence() {
    }

    public ExperimentalEntityConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = AbstractIntactExperimentalEntity.class )
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(AbstractIntactExperimentalEntity.class)
    public ExperimentalEntity getParticipant() {
        return participant;
    }

    public void setParticipant(ExperimentalEntity participant) {
        this.participant = participant;
    }
}
