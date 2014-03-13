package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for modelled entities/participants (used in complexes)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_annotation" )
public class ModelledParticipantAnnotation extends AbstractIntactAnnotation{

    public ModelledParticipantAnnotation() {
        super();
    }

    public ModelledParticipantAnnotation(CvTerm topic) {
        super(topic);
    }

    public ModelledParticipantAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
