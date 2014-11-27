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
package uk.ac.ebi.intact.jami.model.meta;

import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.model.listener.AuditableEventListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Contains metadata about the schema
 *
 */
@Entity
@Table( name = "ia_db_info" )
@EntityListeners(value = {AuditableEventListener.class})
public class DbInfo implements Auditable,Serializable {

    public static final String SCHEMA_VERSION = "schema_version";
    public static final String LAST_PROTEIN_UPDATE = "last_protein_update";
    public static final String LAST_CV_UPDATE_PSIMI = "last_cv_update[PSI-MI]";
    public static final String NAMESPACE_PSIMI = "PSI-MI";

    public static final String LAST_CV_IDENTIFIER = "last_cv_identifier";

    private transient UserContext localContext;

    private String key;

    private String value;

    /**
     * The curator who has last edited the object.
     */
    private String updator;

    /**
     * The curator who has created the edited object
     */
    private String creator;

    /**
     * Creation date of an object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    private Date created;

    /**
     * The last update of the object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    private Date updated;

    public DbInfo() {
    }

    public DbInfo( String key, String value ) {
        this.key = key;
        this.value = value;
    }

    @Id
    @Column( name = "dbi_key", length = 50 )
    @Size(max = 50)
    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    @Column( name = "value", length = 512 )
    @Size(max = 512)
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Column( name = "updated_user", length = 30, nullable = false)
    @NotNull
    public String getUpdator() {
        return updator;
    }

    public void setUpdator( String updator ) {
        this.updator = updator;
    }

    @Override
    @Transient
    /**
     * Property not saved, only for auditing
     */
    public UserContext getLocalUserContext() {
        return this.localContext;
    }

    @Override
    public void setLocalUserContext(UserContext context) {
        this.localContext = context;
    }

    @Column( name = "created_user", length = 30, nullable = false)
    @NotNull
    public String getCreator() {
        return creator;
    }

    public void setCreator( String creator ) {
        this.creator = creator;
    }

    @Column( name = "created_date", nullable = false)
    @NotNull
    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    @Column( name = "updated_date", nullable = false)
    @NotNull
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
