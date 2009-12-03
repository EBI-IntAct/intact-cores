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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@org.hibernate.annotations.Table (appliesTo = "ia_imex_exp_interaction",
                                  comment = "Represents an IMEx exported interaction.")
@Table( name = "ia_imex_exp_interaction",
        uniqueConstraints = {@UniqueConstraint(columnNames={"imex_exp_release_id", "imex_id"})})
public class ImexExportInteraction extends AbstractAuditable{

    private Long id;
    private ImexExportRelease imexExportRelease;
    private Interaction interaction;
    private String interactionAc;
    private Experiment experiment;
    private String experimentAc;
    private String imexId;
    private Publication publication;
    private String publicationId;

    private Date deleted;
    private String deletor;

    public ImexExportInteraction() {

    }

    public ImexExportInteraction(Interaction interaction) {
        this.interaction = interaction;
        populateInteractionFields(interaction);
    }

    @PrePersist
    public void onPrePersist() {
        populateInteractionFields(interaction);
    }

    @PreUpdate
    public void onPreUpdate() {
        populateInteractionFields(interaction);
    }

    private void populateInteractionFields(Interaction interaction) {
        this.imexId = InteractionUtils.getImexIdentifier(interaction);

        if (imexId == null) {
            throw new IllegalArgumentException("An interaction must have an IMEX ID to be tracked through the ImexExportInteraction table: "+interaction);
        }

        if (!interaction.getExperiments().isEmpty()) {
            this.experiment = interaction.getExperiments().iterator().next();
            this.experimentAc = experiment.getAc();

            this.publication = experiment.getPublication();
            this.publicationId = publication.getShortLabel();
        }
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

    @ManyToOne
    @JoinColumn(name = "imex_exp_release_id")
    public ImexExportRelease getImexExportRelease() {
        return imexExportRelease;
    }

    public void setImexExportRelease(ImexExportRelease imexExportRelease) {
        this.imexExportRelease = imexExportRelease;
    }

    @ManyToOne (targetEntity = InteractionImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "interaction_ac", nullable = true, insertable = false, updatable = false)
    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;

        if (interaction != null) {
            this.interactionAc = interaction.getAc();
        }
    }

    @Column(name = "interaction_ac")
    public String interactionAc() {
        return interactionAc;
    }

    public void setInteractionAc(String interactionAc) {
        this.interactionAc = interactionAc;
    }

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_ac", nullable = true, insertable = false, updatable = false)
    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;

        if (experiment != null) {
            this.experimentAc = experiment.getAc();
        }
    }

    @Column(name = "experiment_ac")
    public String getExperimentAc() {
        return experimentAc;
    }

    public void setExperimentAc(String experimentAc) {
        this.experimentAc = experimentAc;
    }

    @Column(name = "imex_id")
    @Length(max = 16)
    @NotNull
    public String getImexId() {
        return imexId;
    }

    public void setImexId(String imexId) {
        this.imexId = imexId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_ac")
    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    @Temporal( value = TemporalType.TIMESTAMP )
    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Column( name = "deleted_user", length = 30)
    public String getDeletor() {
        return deletor;
    }

    public void setDeletor(String deletor) {
        this.deletor = deletor;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImexExportInteraction that = (ImexExportInteraction) o;

        if (!imexId.equals(that.imexId)) return false;
        if (!interaction.equals(that.interaction)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = interaction.hashCode();
        result = 31 * result + imexId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ImexExportInteraction{");
        if (interaction != null) {
            sb.append("interaction=").append(interaction.getAc());
        }
        sb.append(", imexId='").append(imexId).append('\'');
        sb.append(", publication'").append(publication).append('\'');
        sb.append(", imexExportRelease'").append(imexExportRelease).append('\'');
        sb.append(", deleted=").append(deleted);
        sb.append(", deletor='").append(deletor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
