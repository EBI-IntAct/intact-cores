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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.ac.ebi.intact.model.AbstractAuditable;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity (name = "ia_imex_import_pub")
@org.hibernate.annotations.Table (appliesTo = "ia_imex_import_pub",
                                  comment = "Table used to track the IMEx imported publications")
public class ImexImportPublication extends AbstractAuditable {

    private ImexImportPublicationPk pk;

    private String originalFilename;

    private Institution provider;

    private ImexImportPublicationStatus status;

    private String message;

    private DateTime releaseDate;

    /////////////////////////////////
    // Constructors

    public ImexImportPublication() {
    }

    public ImexImportPublication(ImexImport imexImport, String pmid) {
        this.pk = new ImexImportPublicationPk();
        pk.setImexImport(imexImport);
        pk.setPmid(pmid);
    }

    public ImexImportPublication(ImexImport imexImport, String pmid, Institution provider, ImexImportPublicationStatus status) {
        this(imexImport, pmid);
        this.provider = provider;
        this.status = status;
    }

    ////////////////////////////////
    // Getters and Setters
    @Id
    public ImexImportPublicationPk getPk() {
        return pk;
    }

    public void setPk(ImexImportPublicationPk pk) {
        this.pk = pk;
    }

    @Transient
    public ImexImport getImexImport() {
        return pk.getImexImport();
    }

    public void setImexImport(ImexImport imexImport) {
        this.pk.setImexImport(imexImport);
    }

    @Transient
    public String getPmid() {
        return pk.getPmid();
    }

    public void setPmid(String pmid) {
        this.pk.setPmid(pmid);
    }

    @Lob
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

    @Enumerated(EnumType.STRING)
    public ImexImportPublicationStatus getStatus() {
        return status;
    }

    public void setStatus(ImexImportPublicationStatus status) {
        this.status = status;
    }

    @ManyToOne
    @ForeignKey(name="fk_Institution_provider")
    public Institution getProvider() {
        return provider;
    }

    public void setProvider(Institution provider) {
        this.provider = provider;
    }

    @Column(name = "release_date")
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(DateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImexImportPublication that = (ImexImportPublication) o;

        if (pk != null ? !pk.equals(that.pk) : that.pk != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (originalFilename != null ? !originalFilename.equals(that.originalFilename) : that.originalFilename != null)
            return false;
        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (pk != null ? pk.hashCode() : 0);
        result = 31 * result + (originalFilename != null ? originalFilename.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}