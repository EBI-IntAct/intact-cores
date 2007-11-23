package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;

public class CvObjectUtilsTest extends IntactBasicTestCase {

    @Test
    public void isNucleicAcidType() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        //       f    g
        //      / \
        //     d   e
        //    / \/  \
        //   a   b   c

        final CvInteractorType a = mockBuilder.createCvObject( CvInteractorType.class, "MI:0001", "a" );
        final CvInteractorType b = mockBuilder.createCvObject( CvInteractorType.class, "MI:0002", "b" );
        final CvInteractorType c = mockBuilder.createCvObject( CvInteractorType.class, "MI:0003", "c" );
        final CvInteractorType d = mockBuilder.createCvObject( CvInteractorType.class, "MI:0004", "d" );
        final CvInteractorType e = mockBuilder.createCvObject( CvInteractorType.class, "MI:0005", "e" );
        final CvInteractorType f = mockBuilder.createCvObject( CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, "f" );
        final CvInteractorType g = mockBuilder.createCvObject( CvInteractorType.class, "XX:1234", "g" );

        a.addParent( d );
        b.addParent( d );
        b.addParent( e );
        c.addParent( e );

        d.addParent( f );
        e.addParent( f );

        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( a ) );
        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( b ) );
        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( c ) );
        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( d ) );
        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( e ) );
        Assert.assertTrue( CvObjectUtils.isNucleicAcidType( f ) );
        Assert.assertFalse( CvObjectUtils.isNucleicAcidType( g ) );

        Assert.assertFalse( CvObjectUtils.isProteinType( a ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( b ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( c ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( d ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( e ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( f ) );
        Assert.assertFalse( CvObjectUtils.isProteinType( g ) );
    }

    @Test
    public void isChildOfType() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        //       f    g
        //      / \
        //     d   e
        //    / \/  \
        //   a   b   c

        final CvInteractorType a = mockBuilder.createCvObject( CvInteractorType.class, "MI:0001", "a" );
        final CvInteractorType b = mockBuilder.createCvObject( CvInteractorType.class, "MI:0002", "b" );
        final CvInteractorType c = mockBuilder.createCvObject( CvInteractorType.class, "MI:0003", "c" );
        final CvInteractorType d = mockBuilder.createCvObject( CvInteractorType.class, "MI:0004", "d" );
        final CvInteractorType e = mockBuilder.createCvObject( CvInteractorType.class, "MI:0005", "e" );
        final CvInteractorType f = mockBuilder.createCvObject( CvInteractorType.class, "MI:0006", "f" );
        final CvInteractorType g = mockBuilder.createCvObject( CvInteractorType.class, "XX:1234", "g" );

        a.addParent( d );
        b.addParent( d );
        b.addParent( e );
        c.addParent( e );

        d.addParent( f );
        e.addParent( f );

        Assert.assertTrue( CvObjectUtils.isChildOfType( a, "MI:0006", true ) );
        Assert.assertFalse( CvObjectUtils.isChildOfType( a, "MI:0006", false ) );

        Assert.assertFalse( CvObjectUtils.isChildOfType( f, "MI:0001", true ) );
        Assert.assertFalse( CvObjectUtils.isChildOfType( f, "MI:0001", false ) );

        Assert.assertFalse( CvObjectUtils.isChildOfType( a, "MI:0003", true ) );
        Assert.assertFalse( CvObjectUtils.isChildOfType( a, "MI:0003", false ) );
        Assert.assertFalse( CvObjectUtils.isChildOfType( c, "MI:0001", true ) );
        Assert.assertFalse( CvObjectUtils.isChildOfType( c, "MI:0001", false ) );
    }

    @Test
    public void getChildrenMIs() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        //       f    g
        //      / \
        //     d   e
        //    / \/  \
        //   a   b   c

        final CvInteractorType a = mockBuilder.createCvObject( CvInteractorType.class, "MI:0001", "a" );
        final CvInteractorType b = mockBuilder.createCvObject( CvInteractorType.class, "MI:0002", "b" );
        final CvInteractorType c = mockBuilder.createCvObject( CvInteractorType.class, "MI:0003", "c" );
        final CvInteractorType d = mockBuilder.createCvObject( CvInteractorType.class, "MI:0004", "d" );
        final CvInteractorType e = mockBuilder.createCvObject( CvInteractorType.class, "MI:0005", "e" );
        final CvInteractorType f = mockBuilder.createCvObject( CvInteractorType.class, "MI:0006", "f" );
        final CvInteractorType g = mockBuilder.createCvObject( CvInteractorType.class, "XX:1234", "g" );

        a.addParent( d );
        b.addParent( d );
        b.addParent( e );
        c.addParent( e );

        d.addParent( f );
        e.addParent( f );

        Assert.assertEquals( 1, CvObjectUtils.getChildrenMIs( a ).size() );
        Assert.assertEquals( 3, CvObjectUtils.getChildrenMIs( d ).size() );
        Assert.assertEquals( 6, CvObjectUtils.getChildrenMIs( f ).size() ); // b is here only once
        Assert.assertEquals( 1, CvObjectUtils.getChildrenMIs( g ).size() );
    }

    @Test
    public void getChildrenMIs_collection() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        //       f    g
        //      / \
        //     d   e
        //    / \/  \
        //   a   b   c

        final CvInteractorType a = mockBuilder.createCvObject( CvInteractorType.class, "MI:0001", "a" );
        final CvInteractorType b = mockBuilder.createCvObject( CvInteractorType.class, "MI:0002", "b" );
        final CvInteractorType c = mockBuilder.createCvObject( CvInteractorType.class, "MI:0003", "c" );
        final CvInteractorType d = mockBuilder.createCvObject( CvInteractorType.class, "MI:0004", "d" );
        final CvInteractorType e = mockBuilder.createCvObject( CvInteractorType.class, "MI:0005", "e" );
        final CvInteractorType f = mockBuilder.createCvObject( CvInteractorType.class, "MI:0006", "f" );
        final CvInteractorType g = mockBuilder.createCvObject( CvInteractorType.class, "XX:1234", "g" );

        a.addParent( d );
        b.addParent( d );
        b.addParent( e );
        c.addParent( e );

        d.addParent( f );
        e.addParent( f );

        Collection<String> collected = new ArrayList<String>( );
        CvObjectUtils.getChildrenMIs( a, collected );
        Assert.assertEquals( 1, collected.size() );

        collected.clear();
        CvObjectUtils.getChildrenMIs( d, collected );
        Assert.assertEquals( 3, collected.size() );

        collected.clear();
        CvObjectUtils.getChildrenMIs( f, collected );
        Assert.assertEquals( 7, collected.size() ); // b is here twice as we use a List

        collected.clear();
        CvObjectUtils.getChildrenMIs( g, collected );
        Assert.assertEquals( 1, collected.size() );
    }

    @Test
    public void createCvObject() throws Exception {
        CvObject cv = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PSI_MI_MI_REF, CvDatabase.PSI_MI);
        Assert.assertNotNull(cv.getMiIdentifier());
        Assert.assertEquals(1, cv.getXrefs().size());
    }

    @Test
    public void getPsiMiXref() throws Exception {
        CvObject cv = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);
        Assert.assertEquals(CvDatabase.UNIPROT_MI_REF, CvObjectUtils.getPsiMiIdentityXref(cv).getPrimaryId());
    }
}
