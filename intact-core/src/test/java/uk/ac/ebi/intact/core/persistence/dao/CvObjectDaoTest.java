/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.core.persistence.dao;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectDaoTest extends IntactBasicTestCase {

    public void createSomeCvs()  {
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        SmallCvPrimer primer = new SmallCvPrimer( dataContext.getDaoFactory() );

        primer.createCVs();

        // add some nucleic acid CVs
        CvInteractorType nucAcid = new IntactMockBuilder().createCvObject( CvInteractorType.class, CvInteractorType.NUCLEIC_ACID_MI_REF, CvInteractorType.NUCLEIC_ACID );
        CvInteractorType dna = new IntactMockBuilder().createCvObject( CvInteractorType.class, CvInteractorType.DNA_MI_REF, CvInteractorType.DNA );
        CvInteractorType otherNucAcid = new IntactMockBuilder().createCvObject( CvInteractorType.class, "IA:0005", "otherNucAcid" );
        nucAcid.addChild( dna );
        nucAcid.addChild( otherNucAcid );

        getCorePersister().saveOrUpdate( nucAcid, dna, otherNucAcid );
    }

    @Test
    public void testgetByPsiMiRefCollection() {
        createSomeCvs();
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
        createSomeCvs();

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

        assertEquals( 9, cvTopicCount );
        assertEquals( 3, cvXrefQualifierCount );
    }

    @Test
    public void getNucleicAcidMIs() throws Exception {
        createSomeCvs();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final Collection<String> mis = daoFactory.getCvObjectDao().getNucleicAcidMIs();

        System.out.println( mis );
        Assert.assertNotNull( mis );
        Assert.assertEquals( 3, mis.size() );
    }

    @Test
    public void getAll() throws Exception {
        createSomeCvs();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final Collection<CvObject> cvs = daoFactory.getCvObjectDao().getAll();
        Assert.assertTrue( cvs.size() > 0 );
    }

    @Test
    public void getLastCvIdentifierWithPrefix() throws Exception {
        createSomeCvs();
        Assert.assertEquals(634, getDaoFactory().getCvObjectDao().getLastCvIdentifierWithPrefix("MI").intValue());
    }
}
