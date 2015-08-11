package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2010</pre>
 */

public class FeatureDaoImplTest extends IntactBasicTestCase {

    @Test
    public void addCautionToFeature(){
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        Feature feature = mockBuilder.createFeatureRandom();
        Range range = mockBuilder.createRangeRandom();
        feature.getRanges().clear();
        feature.addRange(range);

        Interaction interaction = mockBuilder.createInteractionRandomBinary();
        Component c = interaction.getComponents().iterator().next();
        c.getBindingDomains().clear();
        c.addBindingDomain(feature);

        IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(feature);

        // get the caution from the DB or create it and persist it
        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDaoFactory();
        CvTopic caution = daoFactory
                .getCvObjectDao(CvTopic.class).getByPsiMiRef(CvTopic.CAUTION_MI_REF);

        if (caution == null) {
            caution = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvTopic.class, CvTopic.CAUTION_MI_REF, CvTopic.CAUTION);
            IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(caution);
        }

        Annotation cautionRange = new Annotation(caution, "test message");
        daoFactory.getAnnotationDao().persist(cautionRange);

        range.getFeature().addAnnotation(cautionRange);
        daoFactory.getFeatureDao().update(range.getFeature());

        Assert.assertEquals(1, feature.getAnnotations().size());
    }
}
