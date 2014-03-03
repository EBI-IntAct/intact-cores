package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Alias;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;

/**
 * Interface for alias synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface AliasSynchronizer<T extends AbstractIntactAlias> extends IntactDbSynchronizer<Alias, T>{

    public boolean isAliasTypeSynchronizationEnabled();

    public void setAliasTypeSynchronizationEnabled(boolean enabled);
}
