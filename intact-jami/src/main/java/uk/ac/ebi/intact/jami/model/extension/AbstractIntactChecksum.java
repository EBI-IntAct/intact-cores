package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.checksum.UnambiguousChecksumComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Abstract class for intact checksums
 * Note: this implementation was chosen because checksum do not make sense without their parents and are not shared by different entities
 * It is then better to have several checksum tables, one for each entity rather than one big checksum table and x join tables.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class AbstractIntactChecksum extends AbstractIntactPrimaryObject implements Checksum{

    private CvTerm method;
    private String value;

    protected AbstractIntactChecksum(){
        super();
    }

    public AbstractIntactChecksum(CvTerm method, String value){
        super();
        if (method == null){
            throw new IllegalArgumentException("The method is required and cannot be null");
        }
        this.method = method;
        if (value == null){
            throw new IllegalArgumentException("The checksum value is required and cannot be null");
        }
        this.value = value;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "method_ac", referencedColumnName = "ac")
    @ForeignKey(name = "FK_CHECKSUM$METHOD")
    @Target(IntactCvTerm.class)
    @NotNull
    public CvTerm getMethod() {
        return this.method;
    }

    @Column( name = "value", length = IntactUtils.MAX_DESCRIPTION_LEN, nullable = false)
    @Size( max = IntactUtils.MAX_DESCRIPTION_LEN )
    @NotNull
    public String getValue() {
        return this.value;
    }

    public void setMethod(CvTerm method) {
        if (method == null){
            throw new IllegalArgumentException("The method is required and cannot be null");
        }
        this.method = method;
    }

    public void setValue(String value) {
        if (value == null){
            throw new IllegalArgumentException("The checksum value is required and cannot be null");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof psidev.psi.mi.jami.model.Checksum)){
            return false;
        }

        return UnambiguousChecksumComparator.areEquals(this, (psidev.psi.mi.jami.model.Checksum) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousChecksumComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return method.toString() + ": " + value;
    }
}
