/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.meta;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import uk.ac.ebi.intact.model.AbstractAuditable;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity (name="ia_imex_object")
public class ImexObject extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "original_filename")
    private String originalFilename;

    @ManyToOne
    private Institution provider;

    @Length (max = 20)
    @NotNull
    private String pmid;

    @Enumerated(EnumType.STRING)
    private ImexObjectStatus status;

    private String message;

    /////////////////////////////////
    // Constructors

    public ImexObject() {
    }

    public ImexObject(Institution provider, String pmid, ImexObjectStatus status) {
        this.provider = provider;
        this.pmid = pmid;
        this.status = status;
    }

    ////////////////////////////////
    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public ImexObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ImexObjectStatus status) {
        this.status = status;
    }

    public Institution getProvider() {
        return provider;
    }

    public void setProvider(Institution provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImexObject that = (ImexObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (pmid != null ? pmid.hashCode() : 0);
        return result;
    }
}