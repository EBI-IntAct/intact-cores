package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.List;

/**
 * AnnotatedObjectDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class AnnotatedObjectDaoImplTest extends IntactBasicTestCase {

    @Test
    public void testGetByXref_primaryId() throws Exception {
        final Protein protein = getMockBuilder().createProtein( "P12345", "test" );
        Assert.assertEquals( 1, protein.getXrefs().size() );
        Xref x = protein.getXrefs().iterator().next();
        protein.addXref( new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()) );

        final Protein protein2 = getMockBuilder().createProtein( "Q99999", "test2" );

        getCorePersister().saveOrUpdate( protein, protein2 );

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size() );

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike( "P12345" );
        Assert.assertEquals(1, list.size() );
        Assert.assertEquals( "test", list.iterator().next().getShortLabel() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( "P12121" ).size() );
    }

    @Test
    public void testGetByXref_primaryId_database() throws Exception {
        final Protein protein = getMockBuilder().createProtein( "P12345", "test" );
        Assert.assertEquals( 1, protein.getXrefs().size() );
        Xref x = protein.getXrefs().iterator().next();
        CvDatabase uniprot = x.getCvDatabase();
        protein.addXref( new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()) );

        final Protein protein2 = getMockBuilder().createProtein( "Q99999", "test2" );

        getCorePersister().saveOrUpdate( protein, protein2 );

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size() );

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike( uniprot, "P12345" );
        Assert.assertEquals(1, list.size() );
        Assert.assertEquals( "test", list.iterator().next().getShortLabel() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( uniprot, "P12121" ).size() );
        CvDatabase intact = getMockBuilder().createCvObject( CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT );
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( intact, "P12345" ).size() );

    }

    @Test
    public void testGetByXref_primaryId_database_qualifier() throws Exception {
        final Protein protein = getMockBuilder().createProtein( "P12345", "test" );
        Assert.assertEquals( 1, protein.getXrefs().size() );
        Xref x = protein.getXrefs().iterator().next();
        CvDatabase uniprot = x.getCvDatabase();
        CvXrefQualifier identity = x.getCvXrefQualifier();
        protein.addXref( new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()) );

        final Protein protein2 = getMockBuilder().createProtein( "Q99999", "test2" );

        getCorePersister().saveOrUpdate( protein, protein2, identity, uniprot );
        getCorePersister().commit();

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size() );

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike( uniprot, identity, "P12345" );
        Assert.assertEquals(1, list.size() );
        Assert.assertEquals( "test", list.iterator().next().getShortLabel() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( uniprot, identity, "P12121" ).size() );
        CvDatabase intact = getMockBuilder().createCvObject( CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT );
        CvXrefQualifier secondary = getMockBuilder().createCvObject( CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC );
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( uniprot, secondary, "P12345" ).size() );
        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( intact, identity, "P12345" ).size() );
    }

    @Test
    public void testGetByXref_primaryId_database_qualifier_mi_only() throws Exception {
        final Protein protein = getMockBuilder().createProtein( "P12345", "test" );
        Assert.assertEquals( 1, protein.getXrefs().size() );
        Xref x = protein.getXrefs().iterator().next();
        protein.addXref( new InteractorXref(x.getOwner(), x.getCvDatabase(), x.getPrimaryId(), x.getCvXrefQualifier()) );

        final Protein protein2 = getMockBuilder().createProtein( "Q99999", "test2" );

        getCorePersister().saveOrUpdate( protein, protein2 );
        getCorePersister().commit();

        final List<ProteinImpl> all = getDaoFactory().getProteinDao().getAll();
        Assert.assertEquals(2, all.size() );

        final List<ProteinImpl> list = getDaoFactory().getProteinDao().getByXrefLike( CvDatabase.UNIPROT_MI_REF,
                                                                                      CvXrefQualifier.IDENTITY_MI_REF,
                                                                                      "P12345" );
        Assert.assertEquals(1, list.size() );
        Assert.assertEquals( "test", list.iterator().next().getShortLabel() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( CvDatabase.UNIPROT_MI_REF,
                                                                              CvXrefQualifier.IDENTITY_MI_REF,
                                                                              "P12121" ).size() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( CvDatabase.UNIPROT_MI_REF,
                                                                              CvXrefQualifier.SECONDARY_AC_MI_REF,
                                                                              "P12345" ).size() );

        Assert.assertEquals(0, getDaoFactory().getProteinDao().getByXrefLike( CvDatabase.INTACT_MI_REF,
                                                                              CvXrefQualifier.IDENTITY_MI_REF,
                                                                              "P12345" ).size() );
    }



}
