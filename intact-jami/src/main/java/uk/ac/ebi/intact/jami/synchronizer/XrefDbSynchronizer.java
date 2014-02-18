package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;

/**
 * Db synchornizers for xrefs
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface XrefDbSynchronizer<X extends AbstractIntactXref> extends IntactDbSynchronizer<Xref, X> {

    public CvTermDbSynchronizer getDbSynchronizer();

    public XrefDbSynchronizer<X> setDbSynchronizer(CvTermDbSynchronizer dbSynchronizer);

    public CvTermDbSynchronizer getQualifierSynchronizer();

    public XrefDbSynchronizer<X> setQualifierSynchronizer(CvTermDbSynchronizer qualifierSynchronizer);
}
