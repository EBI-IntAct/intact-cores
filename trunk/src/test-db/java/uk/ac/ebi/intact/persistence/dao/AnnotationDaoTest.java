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
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: CvObjectDaoTest.java 7261 2007-01-09 14:07:34Z CatherineLeroy $
 */
public class AnnotationDaoTest  extends TestCase {
     private static final Log log = LogFactory.getLog(AnnotationDaoTest.class);

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




    public void testSaveOrUpdate () throws IntactTransactionException {
        Institution ebi = IntactContext.getCurrentInstance().getInstitution();
        CvObjectDao<CvTopic> cvTopicDao = daoFactory.getCvObjectDao(CvTopic.class);
        CvTopic authorConfidence = cvTopicDao.getByXref(CvTopic.AUTHOR_CONFIDENCE_MI_REF);


        //CASE ONE, create an annotation with an already existing cv and saveOrUpdate it. Check that the annotation is
        //saved.
        Annotation annotation = new Annotation(ebi,authorConfidence, "Sky my husband");

        AnnotationDao annotationDao =  daoFactory.getAnnotationDao();
        annotationDao.saveOrUpdate(annotation);

        Collection<Annotation> annotations = annotationDao.getAll();
        boolean found = false;
        for(Annotation annot : annotations){
            if("Sky my husband".equals(annot.getAnnotationText())){
                found = true;
            }
        }
        if(found == false){
            fail("Annotation \"Sky my husband\"was not saved");
        }




        // CASE 2 = Create a cv_topic, persist() it, create an annotation using this cvtopic saveOrUpdate the annotation.
        // Check that the cvTopic is created, check that the annotation is created.
        CvTopic newTopic = new CvTopic(ebi,"Moscow");
        cvTopicDao.persist(newTopic);



        annotation = new Annotation(ebi,newTopic, "He left without screeming station");
        annotationDao.saveOrUpdate(annotation);

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        CvTopic topics = cvTopicDao.getByShortLabel("Moscow");
        if(topics == null){
            fail("Topic Moscow not created at all ");
        }

        found = false;
        for(Annotation annot : annotations){
            if("He left without screeming station".equals(annot.getAnnotationText())){
                found = true;
                break;
            }
        }
        if(found == false){
            fail("Annotation \"He left without screeming station\" was not saved");
        }

    }
}
