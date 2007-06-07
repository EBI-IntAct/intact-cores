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
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary key for the pay as you go entity.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Embeddable
public class PayAsYouGoCurrentEdgePk implements Serializable {

    @Column( length = 20 )
    private String nidA;

    @Column( length = 20 )
    private String nidB;

    @Column( length = 20 )
    private String species;

    public PayAsYouGoCurrentEdgePk() {
    }

    public PayAsYouGoCurrentEdgePk( String nidA, String nidB, String species ) {
        this.nidA = nidA;
        this.nidB = nidB;
        this.species = species;
    }


    public String getNidA() {
        return nidA;
    }

    public void setNidA( String nidA ) {
        this.nidA = nidA;
    }

    public String getNidB() {
        return nidB;
    }

    public void setNidB( String nidB ) {
        this.nidB = nidB;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies( String species ) {
        this.species = species;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PayAsYouGoCurrentEdgePk that = ( PayAsYouGoCurrentEdgePk ) o;

        if ( nidA != null ? !nidA.equals( that.nidA ) : that.nidA != null ) {
            return false;
        }
        if ( nidB != null ? !nidB.equals( that.nidB ) : that.nidB != null ) {
            return false;
        }
        if ( species != null ? !species.equals( that.species ) : that.species != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( nidA != null ? nidA.hashCode() : 0 );
        result = 31 * result + ( nidB != null ? nidB.hashCode() : 0 );
        result = 31 * result + ( species != null ? species.hashCode() : 0 );
        return result;
    }
}
