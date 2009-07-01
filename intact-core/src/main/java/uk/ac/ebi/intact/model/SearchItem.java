/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * For an item in the ia_search table, which is a materialized view
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
@Entity
@Table( name = "ia_search" )
@org.hibernate.annotations.Table( appliesTo = "ia_search",
                                  indexes = {
                                  @Index( name = "i_ia_search", columnNames = {"value", "objclass"} )
                                          }
)
@Deprecated
public class SearchItem implements Serializable {

    private SearchItemPk pk;

    public SearchItem() {
        // nothing
    }

    public SearchItem( String ac, String value, String objClass, String type ) {
        this.pk = new SearchItemPk( ac, value, objClass, type );
    }

    @EmbeddedId
    public SearchItemPk getPk() {
        return pk;
    }

    public void setPk( SearchItemPk pk ) {
        this.pk = pk;
    }

    @Column( insertable = false, updatable = false )
    public String getAc() {
        return pk.getAc();
    }

    public void setAc( String ac ) {
        pk.setAc( ac );
    }

    @Column( insertable = false, updatable = false )
    public String getValue() {
        return pk.getValue();
    }

    public void setValue( String value ) {
        pk.setValue( value );
    }

    @Column( insertable = false, updatable = false )
    public String getObjClass() {
        return pk.getObjClass();
    }

    public void setObjClass( String objClass ) {
        pk.setObjClass( objClass );
    }

    @Column( insertable = false, updatable = false )
    public String getType() {
        return pk.getType();
    }

    public void setType( String type ) {
        pk.setType( type );
    }

    @Override
    public String toString() {
        return pk.toString();
    }

    @Override
    public boolean equals( Object o ) {
        return pk.equals( o );
    }

    @Override
    public int hashCode() {
        return pk.hashCode();
    }
}
