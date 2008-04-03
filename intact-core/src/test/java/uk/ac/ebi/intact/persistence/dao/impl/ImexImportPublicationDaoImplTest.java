package uk.ac.ebi.intact.persistence.dao.impl;

import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.meta.ImexImport;
import uk.ac.ebi.intact.model.meta.ImexImportPublication;
import uk.ac.ebi.intact.model.meta.ImexImportPublicationStatus;
import uk.ac.ebi.intact.persistence.dao.ImexImportDao;
import uk.ac.ebi.intact.persistence.dao.ImexImportPublicationDao;

import java.util.List;

/**
 * ImexImportPublicationDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */
public class ImexImportPublicationDaoImplTest extends IntactBasicTestCase {

    private ImexImportDao imexImportDao;
    private ImexImportPublicationDao imexImportPublicationDao;


    @Before
    public void prepareTest() throws Exception {
        this.imexImportDao = getDaoFactory().getImexImportDao();
        this.imexImportPublicationDao = getDaoFactory().getImexImportPublicationDao();
    }

    @After
    public void endTest() throws Exception {
        this.imexImportDao = null;
        this.imexImportPublicationDao = null;
    }

    @Test
    public void getFailed() throws Exception {
        ImexImport imexImport = new ImexImport();
        Institution institution = getDaoFactory().getInstitutionDao().getByXref( CvDatabase.INTACT_MI_REF );
        imexImport.getImexImportPublications().add( new ImexImportPublication( imexImport, "1234567", institution, ImexImportPublicationStatus.OK ) );
        imexImport.getImexImportPublications().add( new ImexImportPublication( imexImport, "7654321", institution, ImexImportPublicationStatus.ERROR ) );

        beginTransaction();
        imexImportDao.persist( imexImport );
        commitTransaction();

        final List<ImexImportPublication> publications = imexImportPublicationDao.getFailed();
        Assert.assertNotNull( publications );
        Assert.assertEquals( 1, publications.size() );
    }

    @Test
    public void getByPmid() throws Exception {
        ImexImport imexImport = new ImexImport();
        Institution institution = getDaoFactory().getInstitutionDao().getByXref( CvDatabase.INTACT_MI_REF );
        imexImport.getImexImportPublications().add( new ImexImportPublication( imexImport, "1234567", institution, ImexImportPublicationStatus.OK ) );
        imexImport.getImexImportPublications().add( new ImexImportPublication( imexImport, "7654321", institution, ImexImportPublicationStatus.ERROR ) );

        beginTransaction();
        imexImportDao.persist( imexImport );
        commitTransaction();

        List<ImexImportPublication> publications;

        publications = imexImportPublicationDao.getByPmid("7654321");
        Assert.assertNotNull( publications );
        Assert.assertEquals( 1, publications.size() );

        publications = imexImportPublicationDao.getByPmid("1234567");
        Assert.assertNotNull( publications );
        Assert.assertEquals( 1, publications.size() );

        publications = imexImportPublicationDao.getByPmid("666");
        Assert.assertNotNull( publications );
        Assert.assertEquals( 0, publications.size() );
    }
}
