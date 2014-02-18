package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Alias;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;

/**
 * Db synchornizers for aliases
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface AliasDbSynchronizer<A extends AbstractIntactAlias> extends IntactDbSynchronizer<Alias, A> {

    public CvTermDbSynchronizer getTypeSynchronizer();

    public AliasDbSynchronizer<A> setTypeSynchronizer(CvTermDbSynchronizer typeSynchronizer);
}
