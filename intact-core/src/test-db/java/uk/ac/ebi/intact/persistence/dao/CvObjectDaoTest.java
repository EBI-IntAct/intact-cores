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
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.DatabaseTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectDaoTest  extends DatabaseTestCase {
     private static final Log log = LogFactory.getLog(CvObjectDaoTest.class);


    public CvObjectDaoTest(String name) {
        super(name);
    }

    public void setUp() throws Exception
        {
            super.setUp();
        }

        public void tearDown() throws Exception
        {
            super.tearDown();
        }


    public void testgetByPsiMiRefCollection()
    {
        Collection<String> psiMiRefs = new ArrayList<String>();
        psiMiRefs.add(CvDatabase.BIND_MI_REF);
        psiMiRefs.add(CvDatabase.CABRI_MI_REF);
        psiMiRefs.add(CvDatabase.IMEX_MI_REF);

        CvObjectDao<CvDatabase> cvObjectDao = getDaoFactory().getCvObjectDao(CvDatabase.class);

        List cvObjects = cvObjectDao.getByPsiMiRefCollection(psiMiRefs);
        assertEquals(cvObjects.size(),3);
    }

    public void testGetByObjClass (){
        Class[] classes = {CvTopic.class, CvXrefQualifier.class};
        CvObjectDao<CvObject> cvObjectDao = getDaoFactory().getCvObjectDao(CvObject.class);
        List cvObjects = cvObjectDao.getByObjClass(classes);
        int cvTopicCount = 0;
        int cvXrefQualifierCount = 0;
        for (int i = 0; i < cvObjects.size(); i++) {
            Object o =  cvObjects.get(i);
            if(o instanceof CvTopic){
                cvTopicCount++;
            }else if(o instanceof CvXrefQualifier){
                cvXrefQualifierCount++;
            } else {
                fail("Was excpecting an object of class CvObject or CvXrefQualifier but got a " + o.getClass().getName());
            }
        }

        assertEquals(73,cvTopicCount);
        assertEquals(21,cvXrefQualifierCount);
    }
}
