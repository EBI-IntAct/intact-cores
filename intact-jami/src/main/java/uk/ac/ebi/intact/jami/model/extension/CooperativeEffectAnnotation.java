package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for cooperative effects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_cooperative_effect_annot" )
public class CooperativeEffectAnnotation extends AbstractIntactAnnotation{

    public CooperativeEffectAnnotation() {
        super();
    }

    public CooperativeEffectAnnotation(CvTerm topic) {
        super(topic);
    }

    public CooperativeEffectAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
