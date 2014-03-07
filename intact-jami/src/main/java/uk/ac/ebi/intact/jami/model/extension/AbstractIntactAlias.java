package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.alias.UnambiguousAliasComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Abstract IntAct implementation for alias
 *
 * Note: this implementation was chosen because aliases do not make sense without their parents and are not shared by different entities
 * It is then better to have several alias tables, one for each entity rather than one big alias table and x join tables.
 * In addition to that, it is backward compatible with previous intact-core data model
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/12/13</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class AbstractIntactAlias extends AbstractIntactPrimaryObject implements Alias{

    private CvTerm type;
    private String name;

    protected AbstractIntactAlias() {
        super();
    }

    public AbstractIntactAlias(CvTerm type, String name) {
        this(name);
        this.type = type;
    }

    public AbstractIntactAlias(String name) {
        if (name == null){
            throw new IllegalArgumentException("The alias name is required and cannot be null");
        }
        this.name = name;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "aliastype_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getType() {
        return this.type;
    }

    public void setType(CvTerm type) {
        this.type = type;
    }

    @Column( length = IntactUtils.MAX_ALIAS_NAME_LEN, nullable = false)
    @NotNull
    @Size(max = IntactUtils.MAX_ALIAS_NAME_LEN)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null){
            throw new IllegalArgumentException("The alias name is required and cannot be null");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof Alias)){
            return false;
        }

        return UnambiguousAliasComparator.areEquals(this, (Alias) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousAliasComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return name + (type != null ? "("+type.toString()+")" : "");
    }
}
