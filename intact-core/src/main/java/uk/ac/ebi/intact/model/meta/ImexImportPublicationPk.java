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

import org.hibernate.validator.NotNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.io.Serializable;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Embeddable
public class ImexImportPublicationPk implements Serializable {

    private ImexImport imexImport;

    private String pmid;

    public ImexImportPublicationPk() {

    }

    /**
     * While manyToOne relationships are not supported in the EBJ3 spec, hibernate supports it
     * @return
     */
    @ManyToOne
    @JoinColumn(insertable = false, updatable = false)
    public ImexImport getImexImport() {
        return imexImport;
    }

    public void setImexImport(ImexImport imexImport) {
        this.imexImport = imexImport;
    }

    @NotNull
    @Column(length = 50)
    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }
}