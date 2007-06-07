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

import org.hibernate.annotations.Type;

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
@Table( name = "ia_payg" )
public class PayAsYouGo implements Serializable {

    @EmbeddedId
    private PayAsYouGoPk pk;

    @Column( updatable = false, insertable = false )
    private String nid;

    @Column( updatable = false, insertable = false )
    private String species;

    private int bait;

    private int prey;

    private int inDegree;

    private float outDegree;

    private int eseen;

    private int econf;

    @Column( name = "really_used_as_bait" )
    @Type( type = "yes_no" )
    private boolean reallyUsedAsBait;


    public PayAsYouGo() {
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

    public int getBait() {
        return bait;
    }

    public void setBait( int bait ) {
        this.bait = bait;
    }

    public int getPrey() {
        return prey;
    }

    public void setPrey( int prey ) {
        this.prey = prey;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree( int inDegree ) {
        this.inDegree = inDegree;
    }

    public float getOutDegree() {
        return outDegree;
    }

    public void setOutDegree( float outDegree ) {
        this.outDegree = outDegree;
    }

    public int getEseen() {
        return eseen;
    }

    public void setEseen( int eseen ) {
        this.eseen = eseen;
    }

    public int getEconf() {
        return econf;
    }

    public void setEconf( int econf ) {
        this.econf = econf;
    }

    public boolean isReallyUsedAsBait() {
        return reallyUsedAsBait;
    }

    public void setReallyUsedAsBait( boolean reallyUsedAsBait ) {
        this.reallyUsedAsBait = reallyUsedAsBait;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PayAsYouGo that = ( PayAsYouGo ) o;

        if ( bait != that.bait ) {
            return false;
        }
        if ( econf != that.econf ) {
            return false;
        }
        if ( eseen != that.eseen ) {
            return false;
        }
        if ( inDegree != that.inDegree ) {
            return false;
        }
        if ( Float.compare( that.outDegree, outDegree ) != 0 ) {
            return false;
        }
        if ( prey != that.prey ) {
            return false;
        }
        if ( reallyUsedAsBait != that.reallyUsedAsBait ) {
            return false;
        }
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

    public int hashCode() {
        int result;
        result = ( pk != null ? pk.hashCode() : 0 );
        result = 31 * result + ( nid != null ? nid.hashCode() : 0 );
        result = 31 * result + ( species != null ? species.hashCode() : 0 );
        result = 31 * result + bait;
        result = 31 * result + prey;
        result = 31 * result + inDegree;
        result = 31 * result + outDegree != +0.0f ? Float.floatToIntBits( outDegree ) : 0;
        result = 31 * result + eseen;
        result = 31 * result + econf;
        result = 31 * result + ( reallyUsedAsBait ? 1 : 0 );
        return result;
    }
}
