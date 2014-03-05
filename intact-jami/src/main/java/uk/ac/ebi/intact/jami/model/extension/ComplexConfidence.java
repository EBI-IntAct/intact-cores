package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledConfidence;
import psidev.psi.mi.jami.model.Publication;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of complex confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_complex_confidence")
public class ComplexConfidence extends AbstractIntactConfidence implements ModelledConfidence{

    private Collection<Publication> publications;

    public ComplexConfidence() {
    }

    public ComplexConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @Transient
    public Collection<Publication> getPublications() {
        if (this.publications == null){
            this.publications = new ArrayList<Publication>();
        }
        return this.publications;
    }
}
