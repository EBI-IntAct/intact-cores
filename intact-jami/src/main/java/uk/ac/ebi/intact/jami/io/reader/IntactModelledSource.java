package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.datasource.ModelledInteractionSource;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.ModelledInteraction;

import java.util.Collection;

/**
 * Intact source for complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactModelledSource extends IntactModelledStream implements ModelledInteractionSource<ModelledInteraction> {

    public IntactModelledSource() {
        super();
    }

    public IntactModelledSource(String springFile, String intactServiceName) {
        super(springFile, intactServiceName);
    }

    public Collection<ModelledInteraction> getInteractions() throws MIIOException {
        if (!isInitialised()){
            initialiseContext(null);
        }

        if (getQuery() != null){
            return getIntactService().fetchIntactObjects(getQuery(), getQueryParameters(), 0, (int) getIntactService().countAll());
        }
        return getIntactService().fetchIntactObjects(0, (int)getIntactService().countAll());
    }

    public long getNumberOfInteractions() {
        if (!isInitialised()){
            initialiseContext(null);
        } 
        if (getCountQuery() != null){
            return getIntactService().countAll(getCountQuery(), getQueryParameters());
        }
        return getIntactService().countAll();
    }
}
