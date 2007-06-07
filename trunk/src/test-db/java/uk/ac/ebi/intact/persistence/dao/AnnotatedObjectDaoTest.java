/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectDaoTest extends TestCase {

    private static final Log log = LogFactory.getLog(AnnotatedObjectDaoTest.class);

    private DaoFactory daoFactory;

    protected void setUp() throws Exception
    {
        super.setUp();
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        daoFactory = null;
    }

    public void testGetByAnnotationAc()
    {
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationAc("EBI-648094");
        assertEquals(annotatedObjects.size(),37);

        ExperimentDao experimentDao = daoFactory.getExperimentDao();
        annotatedObjects = experimentDao.getByAnnotationAc("EBI-648094");
        assertEquals(annotatedObjects.size(),37);

    }

    public void testGetByAnnotationTopicAndDescription()
    {
        CvObjectDao<CvTopic> cvObjectDao = daoFactory.getCvObjectDao(CvTopic.class);
        CvTopic cvTopic = cvObjectDao.getByAc("EBI-821310");
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationTopicAndDescription(cvTopic, "Mouse cardiac cell library used for yeast two-hybrid screening.");
        assertEquals(annotatedObjects.size(),37);
    }

    public void testGetByShortlabelOrAcLike(){
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByShortlabelOrAcLike("butkevich-2004-%");
        assertEquals(annotatedObjects.size(),4);
    }

    public void testGetByXref(){
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        Experiment experiment = annotatedObjectDao.getByXref("10029528");
        assertNotNull(experiment);
    }

    public void testGetByXrefLike()
    {
        CvXrefQualifier qualifier = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
        CvDatabase pubmed = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.PUBMED_MI_REF);
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByXrefLike(pubmed, qualifier, "10029528" );
        assertEquals(annotatedObjects.size(),1);
    }

    public void testGetAll(){
        // Mis of obsolete cvs, this list might change when updating the cv's, this test might need  to be updated
        // from time to time
        String obsoleteMis[] = {"MI:0196","MI:0215","MI:0600","MI:0273","MI:0265","MI:0264","MI:0266","MI:0262"
                                ,"MI:0268","MI:0274","MI:0267","MI:0270","MI:0271","MI:0219","MI:0275","MI:0205"
                                ,"MI:0269","MI:0261","MI:0060","MI:0025","MI:0061","MI:0260","MI:0258","MI:0050"
                                ,"MI:0109","MI:0062","MI:0075","MI:0059","MI:0418","MI:0079","MI:0022","MI:0259"
                                ,"MI:0023","MI:0021","MI:0587","MI:0493","MI:0494","MI:0650","MI:0443","MI:0653"
                                ,"MI:0309","MI:0491","MI:0654","MI:0490","MI:0492","MI:0651","MI:0652"};

        //Put mis in a collection more easy to handle.
        Collection<String> mis = new ArrayList<String>(obsoleteMis.length);
        for(String mi : obsoleteMis){
            mis.add(mi);
        }

        Collection<CvObject> cvObjects = new ArrayList<CvObject>();
        AnnotatedObjectDao annotatedObjectDao = daoFactory.getAnnotatedObjectDao(CvObject.class);
        // Get cvs, filtered on obsolete annotation, and check that there are no obsolete cvs in the list returned
        cvObjects = annotatedObjectDao.getAll(true,false);
        System.out.println("cvObjects.size() = " + cvObjects.size());
        for(CvObject cvObject : cvObjects){
            System.out.println("cvObject.getShortLabel() = " + cvObject.getShortLabel());
            Xref xref = CvObjectUtils.getPsiMiIdentityXref(cvObject);
            if( xref != null){
                String psiMiId = xref.getPrimaryId();
                if(mis.contains(psiMiId)){
                    fail("CvObject[ " + psiMiId + "," + cvObject.getShortLabel() + "] is obsolete should not be in " +
                            "the list.");
                }
            }
            
        }

    }



}
