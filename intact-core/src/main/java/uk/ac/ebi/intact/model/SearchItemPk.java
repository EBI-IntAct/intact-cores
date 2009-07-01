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

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary key for the searchItem entity. With the same fields as SearchItem as all fields are part
 * of the composite pk
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
@Embeddable
@Deprecated
public class SearchItemPk implements Serializable {

    private String ac;
    private String value;
    private String objClass;
    private String type;

    public SearchItemPk() {
        // nothing
    }


    public SearchItemPk( String ac, String value, String objClass, String type ) {
        this.ac = ac;
        this.value = value;
        this.objClass = objClass;
        this.type = type;
    }


    public String getAc() {
        return ac;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getObjClass() {
        return objClass;
    }

    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SearchItemPk{" +
               "ac='" + ac + '\'' +
               ", value='" + value + '\'' +
               ", objClass='" + objClass + '\'' +
               ", type='" + type + '\'' +
               '}';
    }


    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        SearchItemPk that = ( SearchItemPk ) o;

        if ( ac != null ? !ac.equals( that.ac ) : that.ac != null ) {
            return false;
        }
        if ( objClass != null ? !objClass.equals( that.objClass ) : that.objClass != null ) {
            return false;
        }
        if ( type != null ? !type.equals( that.type ) : that.type != null ) {
            return false;
        }
        if ( value != null ? !value.equals( that.value ) : that.value != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( ac != null ? ac.hashCode() : 0 );
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        result = 31 * result + ( objClass != null ? objClass.hashCode() : 0 );
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        return result;
    }
}
