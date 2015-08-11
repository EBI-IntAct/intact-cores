package uk.ac.ebi.intact.model.util;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Interaction;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionUtilsTest extends IntactBasicTestCase {

    @Test
    public void isNegative_yes() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.addAnnotation(getMockBuilder().createAnnotation("because of this and that", null, CvTopic.NEGATIVE));

        Assert.assertTrue(InteractionUtils.isNegative(interaction));
    }

    @Test
    public void isNegative_no() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        Assert.assertFalse(InteractionUtils.isNegative(interaction));
    }

    @Test
    public void isNegative_no_db() throws Exception {
        Interaction negative = getMockBuilder().createInteractionRandomBinary();
        negative.setShortLabel( "neg-neg" );
        negative.addAnnotation(getMockBuilder().createAnnotation("because of this and that", null, CvTopic.NEGATIVE));

        Interaction positive = getMockBuilder().createInteractionRandomBinary();
        positive.setShortLabel( "pos-pos" );

        getCorePersister().saveOrUpdate(negative, positive);

        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        daoFactory.getEntityManager().clear();

        Interaction p = daoFactory.getInteractionDao().getByShortLabel( "pos-pos" );
        Assert.assertFalse(InteractionUtils.isNegative(p));

        Interaction n = daoFactory.getInteractionDao().getByShortLabel( "neg-neg" );
        Assert.assertTrue(InteractionUtils.isNegative(n));
    }
}
