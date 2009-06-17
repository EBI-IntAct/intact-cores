/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.svc.impl;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.core.persistence.svc.DbInfoService;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.meta.DbInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Test cases to check DbInfoService methods
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public class DbInfoServiceTest extends IntactBasicTestCase {

    private static final Log log = LogFactory.getLog( DbInfoServiceTest.class );

    @Before
    public void before() throws Exception {
        SchemaUtils.createSchema();
    }


    @Test
    public void saveLastProteinUpdateTest() throws Exception {


        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        Date yesterday = sdf.parse( "2008-06-17" );


        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();

        //Updating first time with previous date

        DbInfoService service = new DbInfoServiceImpl();
        service.saveLastProteinUpdate( yesterday );

        DbInfo dbinfoBefore = daof.getDbInfoDao().get( DbInfo.LAST_PROTEIN_UPDATE );
        if ( log.isDebugEnabled() ) {
            log.debug( "DbInfo Before : " + dbinfoBefore.toString() );
        }
        Assert.assertEquals( sdf.format( yesterday ), dbinfoBefore.getValue() );

        //Updating second time with later date
        Date today = sdf.parse( "2008-06-20" );
        service.saveLastProteinUpdate( today );

        DbInfo dbinfoAfter = daof.getDbInfoDao().get( DbInfo.LAST_PROTEIN_UPDATE );
        if ( log.isDebugEnabled() ) {
            log.debug( "DbInfo After: " + dbinfoAfter.toString() );
        }
        Assert.assertEquals( sdf.format( today ), dbinfoAfter.getValue() );


        Date lastProteinUpdate = service.getLastProteinUpdate();
        String formattedDate = new SimpleDateFormat( "yyyy-MM-dd" ).format( lastProteinUpdate );
        Assert.assertEquals( sdf.format( today ), formattedDate );

    }//end method


    @Test
    public void saveLastCvUpdateTest() throws Exception {


        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        Date yesterday = sdf.parse( "2008-06-17" );


        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();

        //Updating first time with previous date

        DbInfoService service = new DbInfoServiceImpl();
        service.saveLastCvUpdate( yesterday, DbInfo.NAMESPACE_PSIMI );

        DbInfo dbinfoBefore = daof.getDbInfoDao().get( DbInfo.LAST_CV_UPDATE_PSIMI );
        if ( log.isDebugEnabled() ) {
            log.debug( "DbInfo Before updating CV : " + dbinfoBefore.toString() );
        }
        Assert.assertEquals( sdf.format( yesterday ), dbinfoBefore.getValue() );

        //Updating second time with later date
        Date today = sdf.parse( "2008-06-20" );
        service.saveLastCvUpdate( today, DbInfo.NAMESPACE_PSIMI );

        DbInfo dbinfoAfter = daof.getDbInfoDao().get( DbInfo.LAST_CV_UPDATE_PSIMI );
        if ( log.isDebugEnabled() ) {
            log.debug( "DbInfo After updating CV : " + dbinfoAfter.toString() );
        }

        Assert.assertEquals( sdf.format( today ), dbinfoAfter.getValue() );

        Date lastCvUpdate = service.getLastCvUpdate( DbInfo.NAMESPACE_PSIMI );
        String formattedDate = new SimpleDateFormat( "yyyy-MM-dd" ).format( lastCvUpdate );
        Assert.assertEquals( sdf.format( today ), formattedDate );


    }//end method

    
}
