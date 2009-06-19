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
import uk.ac.ebi.intact.model.AbstractAuditable;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity(name="ia_imex_export")
@org.hibernate.annotations.Table (appliesTo = "ia_imex_export",
                                  comment = "Represents an IMEx exported interaction.")
public class ImexExport extends AbstractAuditable{

    private Long id;
    private Interaction interaction;
    private String imexId;
    private String publicationId;

    private Date deleted;
    private String deletor;

    public ImexExport() {

    }

    public ImexExport(Interaction interaction) {
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
            throw new IllegalArgumentException("An interaction must have an IMEX ID to be tracked through the ImexExport table: "+interaction);
        }

        if (!interaction.getExperiments().isEmpty()) {
            this.publicationId = interaction.getExperiments().iterator().next().getPublication().getPublicationId();
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

    @OneToOne (targetEntity = InteractionImpl.class)
    @JoinColumn(name = "ac")
    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    @Column(name = "imex_id", unique = true)
    @Length(max = 16)
    @NotNull
    public String getImexId() {
        return imexId;
    }

    public void setImexId(String imexId) {
        this.imexId = imexId;
    }

    @Length(max = 16)
    @NotNull
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

        ImexExport that = (ImexExport) o;

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
        sb.append("ImexExport");
        sb.append("{interaction=").append(interaction);
        sb.append(", imexId='").append(imexId).append('\'');
        sb.append(", publicationId='").append(publicationId).append('\'');
        sb.append(", deleted=").append(deleted);
        sb.append(", deletor='").append(deletor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
