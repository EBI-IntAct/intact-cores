package uk.ac.ebi.intact.model;

import static org.junit.Assert.*;
import org.junit.*;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * Parameter Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.9.0
 * @version $Id$
 */
public class ParameterTest extends IntactBasicTestCase {

    @Test
    public void setGetBase() throws Exception {
        Parameter p = new InteractionParameter( );
        Assert.assertEquals( Parameter.DEFAULT_BASE, p.getBase() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( Parameter.DEFAULT_BASE, p.getBase() );

        p.setBase( 5 );
        Assert.assertEquals( 5, p.getBase() );
    }

    @Test
    public void setGetExponent() throws Exception {
        Parameter p = new InteractionParameter( );
        Assert.assertEquals( Parameter.DEFAULT_EXPONENT, p.getExponent() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( Parameter.DEFAULT_EXPONENT, p.getExponent() );

        p.setExponent( 2 );
        Assert.assertEquals( 2, p.getExponent() );
    }

    @Test
    public void setGetFactor() throws Exception {
        Parameter p = new InteractionParameter( ); // should not be used this way though ... a factor has to be defined.
        Assert.assertEquals( null, p.getFactor() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( 3D, p.getFactor() );

        p.setFactor( 50D );
        Assert.assertEquals( 50D, p.getFactor() );
    }

    @Test
    public void setGetUncertainty() throws Exception {
        Parameter p = new InteractionParameter( );
        Assert.assertEquals( null, p.getUncertainty() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( null, p.getUncertainty() );

        p.setUncertainty( 0.05D );
        Assert.assertEquals( 0.05D, p.getUncertainty() );
    }

    @Test
    public void setGetCvParameterType() throws Exception {
        Parameter p = new InteractionParameter( );
        Assert.assertEquals( null, p.getCvParameterType() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( type, p.getCvParameterType() );

        try {
            p.setCvParameterType( null );
            fail("You should not have been able to set a null type");
        } catch ( Exception e ) {
            // ok
        }

        CvParameterType type2 = getMockBuilder().createCvObject( CvParameterType.class, "IA:9993", "ka" );
        p.setCvParameterType( type2 );
        Assert.assertEquals( type2, p.getCvParameterType() );
    }

    @Test
    public void setGetCvParameterUnit() throws Exception {
        Parameter p = new InteractionParameter( );
        Assert.assertEquals( null, p.getCvParameterType() );

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );
        p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        Assert.assertEquals( unit, p.getCvParameterUnit() );

        p.setCvParameterUnit( null );
        Assert.assertEquals( null, p.getCvParameterUnit() );

        CvParameterUnit unit2 = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9911", "molar" );
        p.setCvParameterUnit( unit2 );
        Assert.assertEquals( unit2, p.getCvParameterUnit() );
    }

    @Test
    public void toStringTest() throws Exception {

        CvParameterType type = getMockBuilder().createCvObject( CvParameterType.class, "IA:9999", "kd" );
        CvParameterUnit unit = getMockBuilder().createCvObject( CvParameterUnit.class, "IA:9990", "molar/L" );

        Parameter p = new InteractionParameter( getMockBuilder().getInstitution(), type, unit, 3D );
        System.out.println( p );

    }
}