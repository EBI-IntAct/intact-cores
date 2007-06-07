package uk.ac.ebi.intact.modelt;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 21-Nov-2006
 * Time: 15:49:40
 * To change this template use File | Settings | File Templates.
 */
public class InteractionTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testClone() throws Exception {
        // Getting this interaction to clone
        InteractionDao interactionDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao();
        InteractionImpl orginal = interactionDao.getByShortLabel( "cara-7" );

        // Getting the experiment to attach to the cloned interaction
        ExperimentDao expDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
        Experiment exp = expDao.getByShortLabel( "thoden-1999-1" );

        // Getting a new CvInteractionType for the cloned Interaction so that it's not the same than the original one.
        CvObjectDao<CvInteractionType> cvObjectDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( CvInteractionType.class );
        CvInteractionType cvInteractionType = cvObjectDao.getByShortLabel( "myristoylation" );
        if ( orginal != null ) {
            // Clone the object
            InteractionImpl copy = ( InteractionImpl ) orginal.clone();
            // Add the experiment
            copy.addExperiment( exp );
            // Change the cvInteractionType
            copy.setCvInteractionType( cvInteractionType );

            interactionDao.persist( copy );

            // FROM HERE I'M TESTING THAT THE CLONE AND THE ORIGINAL ARE "EQUAL"

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();

            interactionDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao();
            String copyShortlabel = copy.getShortLabel();
            copy = interactionDao.getByShortLabel( copyShortlabel );

            assertEquals( copy.getComponents().size(), orginal.getComponents().size() );
            assertEquals( copy.getAnnotations().size(), orginal.getAnnotations().size() );
            assertEquals( copy.getXrefs().size(), orginal.getXrefs().size() );

            int originalFeatureCount = 0;
            int originalRangeCount = 0;
            int originalAnnot = 0;
            int originalXref = 0;
            Collection<String> interactorShorlabels = new ArrayList();
            for ( Component component : orginal.getComponents() ) {
                originalFeatureCount += component.getBindingDomains().size();
                originalAnnot += component.getAnnotations().size();
                originalXref += component.getXrefs().size();
                interactorShorlabels.add( component.getInteractor().getShortLabel() );
                for ( Feature feature : component.getBindingDomains() ) {
                    originalRangeCount += feature.getRanges().size();
                    originalAnnot += component.getAnnotations().size();
                    originalXref += component.getXrefs().size();
                }
            }

            int copyFeatureCount = 0;
            int copyRangeCount = 0;
            int copyAnnot = 0;
            int copyXref = 0;
            for ( Component component : orginal.getComponents() ) {
                copyFeatureCount += component.getBindingDomains().size();
                copyAnnot += component.getAnnotations().size();
                copyXref += component.getXrefs().size();
                for ( Feature feature : component.getBindingDomains() ) {
                    copyRangeCount += feature.getRanges().size();
                    copyAnnot += component.getAnnotations().size();
                    copyXref += component.getXrefs().size();
                }
                assertTrue( interactorShorlabels.contains( component.getInteractor().getShortLabel() ) );
            }
            assertEquals( originalFeatureCount, copyFeatureCount );
            assertEquals( originalRangeCount, copyRangeCount );
            assertEquals( originalAnnot, copyAnnot );
            assertEquals( originalXref, copyXref );

            interactionDao.delete(copy);
        }
    }
}
