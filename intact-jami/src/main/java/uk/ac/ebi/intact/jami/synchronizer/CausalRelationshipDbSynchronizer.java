package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CausalRelationship;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;

/**
 * Db synchornizers for causal relationship
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface CausalRelationshipDbSynchronizer extends IntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> {

    public CvTermDbSynchronizer getTypeSynchronizer();

    public CausalRelationshipDbSynchronizer setTypeSynchronizer(CvTermDbSynchronizer typeSynchronizer);

    public IntActEntitySynchronizer getEntitySynchronizer();

    public CausalRelationshipDbSynchronizer setEntitySynchronizer(IntActEntitySynchronizer participantSynchronizer);

    public AliasDbSynchronizer<CvTermAlias> getCvAliasSynchronizer();

    public CausalRelationshipDbSynchronizer setCvAliasSynchronizer(AliasDbSynchronizer<CvTermAlias> aliasSynchronizer);

    public AnnotationDbSynchronizer<CvTermAnnotation> getCvAnnotationSynchronizer();
    public CausalRelationshipDbSynchronizer setCvAnnotationSynchronizer(AnnotationDbSynchronizer<CvTermAnnotation> annotationSynchronizer);

    public XrefDbSynchronizer<CvTermXref> getCvXrefSynchronizer();
    public CausalRelationshipDbSynchronizer setCvXrefSynchronizer(XrefDbSynchronizer<CvTermXref> xrefSynchronizer);
}
