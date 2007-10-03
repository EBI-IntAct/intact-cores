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
@Entity (name="ia_imex_import")
public class ImexImport extends AbstractAuditable {

    private String id;

    private String originalFilename;

    private Institution provider;

    private String pmid;

    private ImexImportStatus status;

    private String message;

    /////////////////////////////////
    // Constructors

    public ImexImport() {
    }

    public ImexImport(Institution provider, String pmid, ImexImportStatus status) {
        this.provider = provider;
        this.pmid = pmid;
        this.status = status;
    }

    ////////////////////////////////
    // Getters and Setters

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="imex_sequence")
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

    @Column(name = "original_filename")
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @Length (max = 20)
    @NotNull
    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    @Enumerated(EnumType.STRING)
    public ImexImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImexImportStatus status) {
        this.status = status;
    }

    @ManyToOne
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

        ImexImport that = (ImexImport) o;

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