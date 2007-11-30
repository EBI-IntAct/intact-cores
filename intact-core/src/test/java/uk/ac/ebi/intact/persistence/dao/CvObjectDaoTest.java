/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;
import uk.ac.ebi.intact.config.impl.SmallCvPrimer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectDaoTest extends IntactBasicTestCase {

    @After
    public void end() throws Exception {
        // nothing
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        SmallCvPrimer primer = new SmallCvPrimer(dataContext.getDaoFactory());

        dataContext.beginTransaction();
        primer.createCVs();
        dataContext.commitTransaction();

        // add some nucleic acid CVs
        CvInteractorType nucAcid = new IntactMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, CvInteractorType.NUCLEIC_ACID);
        CvInteractorType dna = new IntactMockBuilder().createCvObject(CvInteractorType.class, CvInteractorType.DNA_MI_REF, CvInteractorType.DNA);
        CvInteractorType otherNucAcid = new IntactMockBuilder().createCvObject(CvInteractorType.class, "IA:xxxx", "otherNucAcid");
        nucAcid.addChild(dna);
        nucAcid.addChild(otherNucAcid);

        PersisterHelper.saveOrUpdate(nucAcid, dna, otherNucAcid);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        IntactContext.closeCurrentInstance();
    }

    @Test
    public void testgetByPsiMiRefCollection() {
        Collection<String> psiMiRefs = new ArrayList<String>();
        psiMiRefs.add( CvDatabase.INTACT_MI_REF );
        psiMiRefs.add( CvDatabase.PUBMED_MI_REF );
        psiMiRefs.add( CvDatabase.GO_MI_REF );

        CvObjectDao<CvDatabase> cvObjectDao = getDaoFactory().getCvObjectDao( CvDatabase.class );

        List cvObjects = cvObjectDao.getByPsiMiRefCollection( psiMiRefs );
        assertEquals( cvObjects.size(), 3 );
    }

    @Test
    public void testGetByObjClass() {
        Class[] classes = {CvTopic.class, CvXrefQualifier.class};
        CvObjectDao<CvObject> cvObjectDao = getDaoFactory().getCvObjectDao( CvObject.class );
        List cvObjects = cvObjectDao.getByObjClass( classes );
        int cvTopicCount = 0;
        int cvXrefQualifierCount = 0;
        for ( int i = 0; i < cvObjects.size(); i++ ) {
            Object o = cvObjects.get( i );
            if ( o instanceof CvTopic ) {
                cvTopicCount++;
            } else if ( o instanceof CvXrefQualifier ) {
                cvXrefQualifierCount++;
            } else {
                fail( "Was excpecting an object of class CvObject or CvXrefQualifier but got a " + o.getClass().getName() );
            }
        }

        assertEquals( 2, cvTopicCount );
        assertEquals( 3, cvXrefQualifierCount );
    }

    @Test
    public void getNucleicAcidMIs() throws Exception {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final Collection<String> mis = daoFactory.getCvObjectDao().getNucleicAcidMIs();

        System.out.println( mis );
        Assert.assertNotNull( mis );
        Assert.assertEquals( 3, mis.size() );
    }
}
