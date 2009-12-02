/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.meta;

import uk.ac.ebi.intact.model.AbstractAuditable;

import javax.persistence.*;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity(name="ia_imex_exp_release")
@org.hibernate.annotations.Table (appliesTo = "ia_imex_exp_release",
                                  comment = "Represents an IMEx release.")
public class ImexExportRelease extends AbstractAuditable{

    private Long id;
    private int createdCount;
    private int updatedCount;
    private int deletedCount;
    private int createdPubCount;
    private int updatedPubCount;
    private int deletedPubCount;
    private List<ImexExportInteraction> imexExportInteractions;

    public ImexExportRelease() {

    }


    @Id
    @SequenceGenerator(name="IMEX_SEQ_GENERATOR",sequenceName="imex_sequence", allocationSize=20)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="IMEX_SEQ_GENERATOR")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "created_count")
    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    @Column(name = "updated_count")
    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    @Column(name = "deleted_count")
    public int getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(int deletedCount) {
        this.deletedCount = deletedCount;
    }

    @Column(name = "created_pub_count")
    public int getCreatedPubCount() {
        return createdPubCount;
    }

    public void setCreatedPubCount(int createdPubCount) {
        this.createdPubCount = createdPubCount;
    }

    @Column(name = "updated_pub_count")
    public int getUpdatedPubCount() {
        return updatedPubCount;
    }

    public void setUpdatedPubCount(int updatedPubCount) {
        this.updatedPubCount = updatedPubCount;
    }

    @Column(name = "deleted_pub_count")
    public int getDeletedPubCount() {
        return deletedPubCount;
    }

    public void setDeletedPubCount(int deletedPubCount) {
        this.deletedPubCount = deletedPubCount;
    }

    @OneToMany(mappedBy = "imexExportRelease", cascade = {CascadeType.ALL})
    public List<ImexExportInteraction> getImexExportInteractions() {
        return imexExportInteractions;
    }

    public void setImexExportInteractions(List<ImexExportInteraction> imexExportInteractions) {
        this.imexExportInteractions = imexExportInteractions;
    }
}