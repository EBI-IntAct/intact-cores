package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * AnnotatedObjectUtils Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.5
 */
public class AnnotatedObjectUtilsTest {

    private IntactMockBuilder getMockBuilder() {
        return new IntactMockBuilder( new Institution( "testInstitution" ) );
    }

    @Test
    public void findANnotationsByCvTopic() throws Exception {

        final CvInteraction interactionType = getMockBuilder().createCvObject( CvInteraction.class, "MI:xxxx", "bla" );

        final CvTopic usedInClass = getMockBuilder().createCvObject( CvTopic.class, "MI:0001", "used-in-class" );
        final CvTopic obsolete = getMockBuilder().createCvObject( CvTopic.class, "MI:0002", "obsolete" );
        final CvTopic comment = getMockBuilder().createCvObject( CvTopic.class, "MI:0003", "comment" );
        final CvTopic remark = getMockBuilder().createCvObject( CvTopic.class, "MI:0004", "internal-remark" );

        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", usedInClass ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", comment ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", comment ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", obsolete ) );

        Collection<Annotation> annotations = null;

        annotations = AnnotatedObjectUtils.findAnnotationsByCvTopic( interactionType,
                                                                     Arrays.asList( obsolete,
                                                                                    usedInClass,
                                                                                    remark ) );
        Assert.assertNotNull( annotations );
        Assert.assertEquals( 2, annotations.size() );

        annotations = AnnotatedObjectUtils.findAnnotationsByCvTopic( interactionType, new ArrayList<CvTopic>() );
        Assert.assertNotNull( annotations );
        Assert.assertEquals( 0, annotations.size() );

        try {
            AnnotatedObjectUtils.findAnnotationsByCvTopic( null, new ArrayList<CvTopic>() );
            Assert.fail( "AnnotatedObjectUtils.findANnotationsByCvTopic() should not allow null annotatedObject" );
        } catch ( Exception e ) {
            // ok
        }
    }
}
