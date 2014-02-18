package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Db synchronizers for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface CvTermDbSynchronizer extends IntactDbSynchronizer<CvTerm, IntactCvTerm> {

    public AliasDbSynchronizer<CvTermAlias> getAliasSynchronizer();

    public CvTermDbSynchronizer setAliasSynchronizer(AliasDbSynchronizer<CvTermAlias> aliasSynchronizer);

    public AnnotationDbSynchronizer<CvTermAnnotation> getAnnotationSynchronizer();
    public CvTermDbSynchronizer setAnnotationSynchronizer(AnnotationDbSynchronizer<CvTermAnnotation> annotationSynchronizer);

    public XrefDbSynchronizer<CvTermXref> getXrefSynchronizer();

    public CvTermDbSynchronizer setXrefSynchronizer(XrefDbSynchronizer<CvTermXref> xrefSynchronizer);

    public void clearPersistentCvCacheOnly();
}
