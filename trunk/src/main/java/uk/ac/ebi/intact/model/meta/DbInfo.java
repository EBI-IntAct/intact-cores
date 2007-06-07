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
package uk.ac.ebi.intact.model.meta;

import uk.ac.ebi.intact.model.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Contains metadata about the schema
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Sep-2006</pre>
 */
@Entity
@Table( name = "ia_db_info" )
public class DbInfo implements Auditable {

    public static final String SCHEMA_VERSION = "schema_version";

    @Id
    @Column( name = "dbi_key", length = 20 )
    private String key;

    @Column( name = "value", length = 20 )
    private String value;

    /**
     * The curator who has last edited the object.
     */
    @Column( name = "updated_user", length = 30 )
    private String updator;

    /**
     * The curator who has created the edited object
     */
    @Column( name = "created_user", length = 30 )
    private String creator;

    /**
     * Creation date of an object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    @Column( name = "created_date" )
    private Date created;

    /**
     * The last update of the object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    @Column( name = "updated_date" )
    private Date updated;

    public DbInfo() {
    }

    public DbInfo( String key, String value ) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator( String updator ) {
        this.updator = updator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator( String creator ) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated( Date updated ) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "DbInfo{" +
               "key='" + key + '\'' +
               ", value='" + value + '\'' +
               '}';
    }
}
