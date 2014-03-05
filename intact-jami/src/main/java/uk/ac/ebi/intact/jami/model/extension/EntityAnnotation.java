package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of annotation for entities/participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_component_annot" )
public class EntityAnnotation extends AbstractIntactAnnotation{

    public EntityAnnotation() {
        super();
    }

    public EntityAnnotation(CvTerm topic) {
        super(topic);
    }

    public EntityAnnotation(CvTerm topic, String value) {
        super(topic, value);
    }
}
