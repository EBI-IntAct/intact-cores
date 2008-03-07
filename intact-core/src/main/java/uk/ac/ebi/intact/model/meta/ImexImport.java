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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity (name="ia_imex_import")
@org.hibernate.annotations.Table (appliesTo = "ia_imex_import",
                                  comment = "Represents an IMEx import action, which may contain many publications.")
public class ImexImport extends AbstractAuditable {

    private Long id;

    private String repository;

    private DateTime importDate;

    private int countTotal;

    private int countFailed;

    private int countNotFound;

    private ImexImportActivationType activationType;

    private List<ImexImportPublication> imexImportPublications;

    /////////////////////////////////
    // Constructors

    public ImexImport() {
        this.importDate = new DateTime();
    }

    public ImexImport(String repository, ImexImportActivationType activationType) {
        this();
        this.repository = repository;
        this.activationType = activationType;
    }


    ////////////////////////////////
    // Listeners

    @PrePersist
    @PreUpdate
    public void processStats() {
        for (ImexImportPublication iip : getImexImportPublications()) {

            countTotal++;

            switch (iip.getStatus()) {
                case ERROR:
                    countFailed++;
                    break;
                case NOT_FOUND:
                    countNotFound++;
                    break;
            }
        }
    }


    ////////////////////////////////
    // Getters and Setters

    @Id
    @SequenceGenerator(name="IMEX_SEQ_GENERATOR",sequenceName="imex_sequence", allocationSize=20)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="IMEX_SEQ_GENERATOR")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "import_date")
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(DateTime importDate) {
        this.importDate = importDate;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @OneToMany ( targetEntity = ImexImportPublication.class,cascade = CascadeType.PERSIST )
    @ForeignKey(name = "fk_ImexImport_imexImportPub", inverseName = "fk_ImexImportPub_imexImport")
    public List<ImexImportPublication> getImexImportPublications() {
        if (imexImportPublications == null) {
            imexImportPublications = new ArrayList<ImexImportPublication>();
        }
        return imexImportPublications;
    }

    public void setImexImportPublications(List<ImexImportPublication> imexImportPublications) {
        this.imexImportPublications = imexImportPublications;
    }

    @Column(name = "count_failed")
    public int getCountFailed() {
        return countFailed;
    }

    public void setCountFailed(int countFailed) {
        this.countFailed = countFailed;
    }

    @Column(name = "count_total")
    public int getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(int countTotal) {
        this.countTotal = countTotal;
    }

    @Column(name = "count_not_found")
    public int getCountNotFound() {
        return countNotFound;
    }

    public void setCountNotFound(int countNotFound) {
        this.countNotFound = countNotFound;
    }

    @Enumerated(EnumType.STRING)
    public ImexImportActivationType getActivationType() {
        return activationType;
    }

    public void setActivationType(ImexImportActivationType activationType) {
        this.activationType = activationType;
    }
}