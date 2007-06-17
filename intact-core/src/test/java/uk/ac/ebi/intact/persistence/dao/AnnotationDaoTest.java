/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.fail;
import org.junit.Test;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.Collection;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: CvObjectDaoTest.java 7261 2007-01-09 14:07:34Z CatherineLeroy $
 */
public class AnnotationDaoTest  extends IntactAbstractTestCase {
     private static final Log log = LogFactory.getLog(AnnotationDaoTest.class);

    @Test
    @IntactUnitDataset(dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class)
    public void testSaveOrUpdate () throws IntactTransactionException {
        Institution ebi = IntactContext.getCurrentInstance().getInstitution();
        CvObjectDao<CvTopic> cvTopicDao = getDaoFactory().getCvObjectDao(CvTopic.class);
        CvTopic authorConfidence = cvTopicDao.getByXref(CvTopic.AUTHOR_CONFIDENCE_MI_REF);

        String label = "Sky my husband";

        //CASE ONE, create an annotation with an already existing cv and saveOrUpdate it. Check that the annotation is
        //saved.
        Annotation annotation = new Annotation(ebi,authorConfidence, label);

        AnnotationDao annotationDao =  getDaoFactory().getAnnotationDao();
        annotationDao.saveOrUpdate(annotation);

        IntactContext.getCurrentInstance().getDataContext().flushSession();

        Collection<Annotation> annotations = annotationDao.getByTextLike(label);
        boolean found = false;
        for(Annotation annot : annotations){
            if(label.equals(annot.getAnnotationText())){
                found = true;
            }
        }
        if(found == false){
            fail("Annotation \""+label+"\" was not saved");
        }




        // CASE 2 = Create a cv_topic, persist() it, create an annotation using this cvtopic saveOrUpdate the annotation.
        // Check that the cvTopic is created, check that the annotation is created.
        CvTopic newTopic = new CvTopic(ebi,"Moscow");
        cvTopicDao.persist(newTopic);

        String label2 = "He left without screeming station";

        annotation = new Annotation(ebi,newTopic, label2);
        annotationDao.saveOrUpdate(annotation);

        IntactContext.getCurrentInstance().getDataContext().flushSession();

        CvTopic topics = cvTopicDao.getByShortLabel("Moscow");
        if(topics == null){
            fail("Topic Moscow not created at all ");
        }

        found = false;
        for(Annotation annot : annotationDao.getByTextLike(label2)){
            if("He left without screeming station".equals(annot.getAnnotationText())){
                found = true;
                break;
            }
        }
        if(found == false){
            fail("Annotation '"+label2+"' was not saved");
        }

    }
}
