package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.Stoichiometry;
import psidev.psi.mi.jami.utils.comparator.participant.StoichiometryComparator;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Intact implementation of stoichiometry
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Embeddable
public class IntactStoichiometry implements Stoichiometry{

    private int minValue;
    private int maxValue;

    protected IntactStoichiometry(){

    }

    public IntactStoichiometry(int value){

        this.minValue = value;
        this.maxValue = value;
    }

    public IntactStoichiometry(int minValue, int maxValue){
        if (minValue > maxValue){
            throw new IllegalArgumentException("The minValue " + minValue + " cannot be bigger than the maxValue " + maxValue);
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Column(name = "stoichiometry")
    public int getMinValue() {
        return this.minValue;
    }

    @Column(name = "maxstoichiometry")
    public int getMaxValue() {
        return this.maxValue;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o){
            return true;
        }

        if (!(o instanceof Stoichiometry)){
            return false;
        }

        return StoichiometryComparator.areEquals(this, (Stoichiometry) o);
    }

    @Override
    public int hashCode() {
        return StoichiometryComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return "minValue: " + minValue + ", maxValue: " + maxValue;
    }

    private void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    private void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
}
