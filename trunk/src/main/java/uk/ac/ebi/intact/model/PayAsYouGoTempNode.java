/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Pay As You Go Table
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Entity
@Table( name = "ia_payg_temp_node" )
public class PayAsYouGoTempNode implements Serializable {

    @EmbeddedId
    private PayAsYouGoPk pk;

    @Column( updatable = false, insertable = false )
    private String nid;

    @Column( updatable = false, insertable = false )
    private String species;

    public PayAsYouGoTempNode() {
    }


    public PayAsYouGoPk getPk() {
        return pk;
    }

    public void setPk( PayAsYouGoPk pk ) {
        this.pk = pk;
    }

    public String getNid() {
        return nid;
    }

    public void setNid( String nid ) {
        this.nid = nid;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies( String species ) {
        this.species = species;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PayAsYouGoTempNode that = ( PayAsYouGoTempNode ) o;

        if ( nid != null ? !nid.equals( that.nid ) : that.nid != null ) {
            return false;
        }
        if ( pk != null ? !pk.equals( that.pk ) : that.pk != null ) {
            return false;
        }
        if ( species != null ? !species.equals( that.species ) : that.species != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( pk != null ? pk.hashCode() : 0 );
        result = 31 * result + ( nid != null ? nid.hashCode() : 0 );
        result = 31 * result + ( species != null ? species.hashCode() : 0 );
        return result;
    }
}
