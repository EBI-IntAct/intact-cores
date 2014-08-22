package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.binary.ModelledBinaryInteraction;
import psidev.psi.mi.jami.datasource.ModelledBinaryInteractionSource;
import psidev.psi.mi.jami.datasource.ModelledInteractionSource;
import psidev.psi.mi.jami.exception.ComplexExpansionException;
import psidev.psi.mi.jami.exception.MIIOException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact source for complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactModelledBinarySource extends IntactModelledBinaryStream implements ModelledBinaryInteractionSource{

    Collection<ModelledBinaryInteraction> loadedInteractions;

    public IntactModelledBinarySource() {
        super(new IntactModelledSource());
    }

    public IntactModelledBinarySource(String springFile, String intactServiceName) {
        super(new IntactModelledSource(springFile, intactServiceName));
    }

    public Collection<ModelledBinaryInteraction> getInteractions() throws MIIOException {
        if (loadedInteractions == null){
            Collection<ModelledBinaryInteraction> interactions = ((ModelledInteractionSource)getDelegate()).getInteractions();
            loadedInteractions = new ArrayList<ModelledBinaryInteraction>(interactions.size());
            for (ModelledBinaryInteraction inter : interactions){
                if (getExpansionMethod().isInteractionExpandable(inter)){
                    try {
                        loadedInteractions.addAll(getExpansionMethod().expand(inter));
                    } catch (ComplexExpansionException e) {
                        throw new MIIOException("Impossible to expand n-ary interaction", e);
                    }
                }
            }
        }

        return this.loadedInteractions;
    }

    public long getNumberOfInteractions() {
        return getInteractions().size();
    }
}
