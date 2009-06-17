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

import javax.persistence.*;
import java.io.Serializable;

/**
 * Interactions for MiNe
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Entity
@Table( name = "ia_interactions" )
public class MineInteraction implements Serializable {

    @EmbeddedId
    private MineInteractionPk pk;

    @Column( name = "protein1_ac", updatable = false, insertable = false )
    private String protein1Ac;

    @Column( name = "protein2_ac", updatable = false, insertable = false )
    private String protein2Ac;

    @Column( name = "interaction_ac", updatable = false, insertable = false )
    private String interactionAc;

    @Column( length = 30 )
    private String shortLabel1;

    @Column( length = 30 )
    private String shortLabel2;

    @Column( length = 30 )
    private String taxid;

    private double weight;

    private int graphId;

    @ManyToOne
    @JoinColumn( name = "experiment_ac" )
    private Experiment experiment;

    @ManyToOne
    @JoinColumn( name = "detectmethod_ac" )
    private CvInteraction detectionMethod;

    @Column( name = "pubmed_id" )
    private String pubmedId;


    public MineInteraction() {
    }


    public MineInteraction( ProteinImpl protein1, ProteinImpl protein2, InteractionImpl interaction ) {
        this.pk = new MineInteractionPk( protein1, protein2, interaction );
        this.shortLabel1 = protein1.getShortLabel();
        this.shortLabel2 = protein2.getShortLabel();
    }

    public MineInteractionPk getPk() {
        return pk;
    }

    public void setPk( MineInteractionPk pk ) {
        this.pk = pk;
    }

    public String getShortLabel1() {
        return shortLabel1;
    }

    public void setShortLabel1( String shortLabel1 ) {
        this.shortLabel1 = shortLabel1;
    }

    public String getShortLabel2() {
        return shortLabel2;
    }

    public void setShortLabel2( String shortLabel2 ) {
        this.shortLabel2 = shortLabel2;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid( String taxid ) {
        this.taxid = taxid;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight( double weight ) {
        this.weight = weight;
    }

    public int getGraphId() {
        return graphId;
    }

    public void setGraphId( int graphId ) {
        this.graphId = graphId;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment( Experiment experiment ) {
        this.experiment = experiment;
    }

    public CvInteraction getDetectionMethod() {
        return detectionMethod;
    }

    public void setDetectionMethod( CvInteraction detectionMethod ) {
        this.detectionMethod = detectionMethod;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId( String pubmedId ) {
        this.pubmedId = pubmedId;
    }

    public String getProtein1Ac() {
        return protein1Ac;
    }

    public void setProtein1Ac( String protein1Ac ) {
        this.protein1Ac = protein1Ac;
    }

    public String getProtein2Ac() {
        return protein2Ac;
    }

    public void setProtein2Ac( String protein2Ac ) {
        this.protein2Ac = protein2Ac;
    }

    public String getInteractionAc() {
        return interactionAc;
    }

    public void setInteractionAc( String interactionAc ) {
        this.interactionAc = interactionAc;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        MineInteraction that = ( MineInteraction ) o;

        if ( pk != null ? !pk.equals( that.pk ) : that.pk != null ) {
            return false;
        }
        if ( graphId != that.graphId ) {
            return false;
        }
        if ( Double.compare( that.weight, weight ) != 0 ) {
            return false;
        }
        if ( detectionMethod != null ? !detectionMethod.equals( that.detectionMethod ) : that.detectionMethod != null ) {
            return false;
        }
        if ( experiment != null ? !experiment.equals( that.experiment ) : that.experiment != null ) {
            return false;
        }

        if ( pubmedId != null ? !pubmedId.equals( that.pubmedId ) : that.pubmedId != null ) {
            return false;
        }
        if ( shortLabel1 != null ? !shortLabel1.equals( that.shortLabel1 ) : that.shortLabel1 != null ) {
            return false;
        }
        if ( shortLabel2 != null ? !shortLabel2.equals( that.shortLabel2 ) : that.shortLabel2 != null ) {
            return false;
        }
        if ( taxid != null ? !taxid.equals( that.taxid ) : that.taxid != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ( pk != null ? pk.hashCode() : 0 );
        result = 31 * result + ( shortLabel1 != null ? shortLabel1.hashCode() : 0 );
        result = 31 * result + ( shortLabel2 != null ? shortLabel2.hashCode() : 0 );
        result = 31 * result + ( taxid != null ? taxid.hashCode() : 0 );
        temp = weight != +0.0d ? Double.doubleToLongBits( weight ) : 0L;
        result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
        result = 31 * result + graphId;
        result = 31 * result + ( experiment != null ? experiment.hashCode() : 0 );
        result = 31 * result + ( detectionMethod != null ? detectionMethod.hashCode() : 0 );
        result = 31 * result + ( pubmedId != null ? pubmedId.hashCode() : 0 );
        return result;
    }
}
