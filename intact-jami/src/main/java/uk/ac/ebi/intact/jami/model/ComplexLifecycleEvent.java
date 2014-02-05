package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Complex lifecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue("complex")
public class ComplexLifecycleEvent extends AbstractLifecycleEvent {

    private Complex complex;

    public ComplexLifecycleEvent() {
    }

    public ComplexLifecycleEvent(CvTerm event, User who, Date when, String note) {
        super(event, who, when, note);
    }

    /**
     * Date is set to current time.
     * @param event
     * @param who
     * @param note
     */
    public ComplexLifecycleEvent(CvTerm event, User who, String note) {
        super(event, who, new Date(), note);
    }

    @ManyToOne( targetEntity = IntactComplex.class, fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "complex_ac", referencedColumnName = "ac")
    @ForeignKey(name="FK_LIFECYCLE_EVENT_COMPLEX")
    @Target(IntactComplex.class)
    public Complex getParent() {
        return complex;
    }

    public void setParent(Complex complex) {
        this.complex = complex;
    }
}
