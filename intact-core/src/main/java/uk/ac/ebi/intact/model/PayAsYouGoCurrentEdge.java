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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Entity
@Table( name = "ia_payg_current_edge" )
public class PayAsYouGoCurrentEdge implements Serializable {

    @EmbeddedId
    private PayAsYouGoCurrentEdgePk pk;

    private int seen;

    private int conf;


    public PayAsYouGoCurrentEdge() {
    }


    public PayAsYouGoCurrentEdgePk getPk() {
        return pk;
    }

    public void setPk( PayAsYouGoCurrentEdgePk pk ) {
        this.pk = pk;
    }


    public int getSeen() {
        return seen;
    }

    public void setSeen( int seen ) {
        this.seen = seen;
    }

    public int getConf() {
        return conf;
    }

    public void setConf( int conf ) {
        this.conf = conf;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PayAsYouGoCurrentEdge that = ( PayAsYouGoCurrentEdge ) o;

        if ( conf != that.conf ) {
            return false;
        }
        if ( seen != that.seen ) {
            return false;
        }
        if ( pk != null ? !pk.equals( that.pk ) : that.pk != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( pk != null ? pk.hashCode() : 0 );
        result = 31 * result + seen;
        result = 31 * result + conf;
        return result;
    }
}
