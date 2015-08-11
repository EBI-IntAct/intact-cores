package uk.ac.ebi.intact.jami.utils.comparator;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.utils.comparator.experiment.VariableParameterComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Comparator for IntAct variable parameters
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactVariableParameterComparator extends VariableParameterComparator implements IntactComparator<VariableParameter>{

    public IntactVariableParameterComparator() {
        super(new IntactCvTermComparator());
    }

    @Override
    public IntactCvTermComparator getCvTermComparator() {
        return (IntactCvTermComparator) super.getCvTermComparator();
    }

    @Override
    public boolean canCompare(VariableParameter param) {
        // check unit
        if (param.getUnit() != null && param.getUnit() instanceof IntactCvTerm){
            if (!((IntactCvTerm)param.getUnit()).areXrefsInitialized()){
                return false;
            }
        }
        // check variable parameter values
        if (!Hibernate.isInitialized(param.getVariableValues())){
            return false;
        }

        // then check
        return true;
    }
}
