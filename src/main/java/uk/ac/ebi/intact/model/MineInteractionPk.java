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
package uk.ac.ebi.intact.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Primary key for the mineInteraction entity
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Embeddable
public class MineInteractionPk implements Serializable {

    @ManyToOne 
    @JoinColumn( name = "protein1_ac" )
    private InteractorImpl protein1;

    @ManyToOne
    @JoinColumn( name = "protein2_ac" )
    private InteractorImpl protein2;

    @ManyToOne
    @JoinColumn( name = "interaction_ac" )
    private InteractionImpl interaction;

    public MineInteractionPk() {
    }

    public MineInteractionPk( ProteinImpl protein1, ProteinImpl protein2, InteractionImpl interaction ) {
        this.protein1 = protein1;
        this.protein2 = protein2;
        this.interaction = interaction;
    }

    public InteractorImpl getProtein1() {
        return protein1;
    }

    public void setProtein1( InteractorImpl protein1 ) {
        this.protein1 = protein1;
    }

    public InteractorImpl getProtein2() {
        return protein2;
    }

    public void setProtein2( InteractorImpl protein2 ) {
        this.protein2 = protein2;
    }

    public InteractionImpl getInteraction() {
        return interaction;
    }

    public void setInteraction( InteractionImpl interaction ) {
        this.interaction = interaction;
    }


    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        MineInteractionPk that = ( MineInteractionPk ) o;

        if ( protein1 != null ? !protein1.equals( that.protein1 ) : that.protein1 != null ) {
            return false;
        }
        if ( protein2 != null ? !protein2.equals( that.protein2 ) : that.protein2 != null ) {
            return false;
        }
        if ( interaction != null && that.getInteraction() != null ) {
            if ( interaction.getAc() == null && that.getInteraction().getAc() != null ) {
                if ( !interaction.getAc().equals( that.getInteraction().getAc() ) ) {
                    return false;
                }
            }
        }
        if ( interaction != null ? !interaction.equals( that.interaction ) : that.interaction != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( protein1 != null ? protein1.hashCode() : 0 );
        result = 31 * result + ( protein2 != null ? protein2.hashCode() : 0 );
        result = 31 * result + ( interaction != null ? interaction.hashCode() : 0 );
        return result;
    }
}
