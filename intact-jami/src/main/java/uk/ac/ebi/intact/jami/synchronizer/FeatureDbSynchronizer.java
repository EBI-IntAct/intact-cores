package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.*;

/**
 * Db synchronizers for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface FeatureDbSynchronizer<F extends Feature, I extends AbstractIntactFeature> extends IntactDbSynchronizer<F,I> {

    public AliasDbSynchronizer<FeatureAlias> getAliasSynchronizer();

    public void setAliasSynchronizer(AliasDbSynchronizer<FeatureAlias> aliasSynchronizer);

    public AnnotationDbSynchronizer<FeatureAnnotation> getAnnotationSynchronizer();

    public void setAnnotationSynchronizer(AnnotationDbSynchronizer<FeatureAnnotation> annotationSynchronizer);

    public XrefDbSynchronizer<FeatureXref> getXrefSynchronizer();

    public void setXrefSynchronizer(XrefDbSynchronizer<FeatureXref> xrefSynchronizer);

    public CvTermDbSynchronizer getEffectSynchronizer();

    public void setEffectSynchronizer(CvTermDbSynchronizer effectSynchronizer);

    public CvTermDbSynchronizer getTypeSynchronizer();

    public void setTypeSynchronizer(CvTermDbSynchronizer typeSynchronizer);

    public IntactDbSynchronizer<Range, IntactRange> getRangeSynchronizer();

    public AliasDbSynchronizer<CvTermAlias> getCvAliasSynchronizer();

    public FeatureDbSynchronizer<F, I> setCvAliasSynchronizer(AliasDbSynchronizer<CvTermAlias> aliasSynchronizer);

    public AnnotationDbSynchronizer<CvTermAnnotation> getCvAnnotationSynchronizer();
    public FeatureDbSynchronizer<F, I> setCvAnnotationSynchronizer(AnnotationDbSynchronizer<CvTermAnnotation> annotationSynchronizer);

    public XrefDbSynchronizer<CvTermXref> getCvXrefSynchronizer();

    public FeatureDbSynchronizer<F, I> setCvXrefSynchronizer(XrefDbSynchronizer<CvTermXref> xrefSynchronizer);
}
