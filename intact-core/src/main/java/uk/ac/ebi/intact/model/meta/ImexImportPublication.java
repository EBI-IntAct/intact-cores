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
@org.hibernate.annotations.Table (appliesTo = "ia_imex_import_publication",
                                  comment = "Table used to track the IMEx imported publications")
@IdClass(ImexImportPublicationPk.class)
public class ImexImportPublication extends AbstractAuditable {

    private ImexImport imexImport;

    private String pmid;

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
        this.imexImport = imexImport;
        this.pmid = pmid;
    }

    public ImexImportPublication(ImexImport imexImport, String pmid, Institution provider, ImexImportPublicationStatus status) {
        this.imexImport = imexImport;
        this.pmid = pmid;
        this.provider = provider;
        this.status = status;
    }

    ////////////////////////////////
    // Getters and Setters
    @Id
    public ImexImport getImexImport() {
        return imexImport;
    }

    public void setImexImport(ImexImport imexImport) {
        this.imexImport = imexImport;
    }

    @Id
    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
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

        if (imexImport != null ? !imexImport.equals(that.imexImport) : that.imexImport != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (originalFilename != null ? !originalFilename.equals(that.originalFilename) : that.originalFilename != null)
            return false;
        if (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null) return false;
        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (imexImport != null ? imexImport.hashCode() : 0);
        result = 31 * result + (originalFilename != null ? originalFilename.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (pmid != null ? pmid.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}