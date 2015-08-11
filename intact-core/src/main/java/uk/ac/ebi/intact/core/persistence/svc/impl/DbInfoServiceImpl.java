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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.svc.DbInfoService;
import uk.ac.ebi.intact.core.persistence.svc.DbInfoServiceException;
import uk.ac.ebi.intact.model.meta.DbInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service class to update the last protein and cv update info in to DBinfo table
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public class DbInfoServiceImpl implements DbInfoService {
    private static final Log log = LogFactory.getLog( DbInfoServiceImpl.class );

    /**
     * Saves the last date the during which protein update was undertaken
     *
     * @param date goes to the value column
     * @throws DbInfoServiceException
     */
    public void saveLastProteinUpdate( Date date ) throws DbInfoServiceException {

        if ( date == null ) {
            throw new NullPointerException( "You must give a non null date" );
        }

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        DaoFactory daof = dataContext.getDaoFactory();
        Date lastUpdated = getLastProteinUpdate();

        if ( lastUpdated == null ) {
            //create and persist
            DbInfo newDbInfo = new DbInfo( DbInfo.LAST_PROTEIN_UPDATE, formatDate( date ) );
            daof.getDbInfoDao().persist( newDbInfo );
        } else {
            //update
            DbInfo dbinfo = daof.getDbInfoDao().get( DbInfo.LAST_PROTEIN_UPDATE );
            if ( log.isDebugEnabled() ) {
                log.debug( "Updating ..." + dbinfo.toString() );
            }

            dbinfo.setValue( formatDate( date ) );


        }//end else


    }//end method

    /**
     * Saves the last date the during which Cv update was undertaken
     *
     * @param date      goes to the value field, usually the date the proteinupdate was undertaken
     * @param namespace combines with the key to form last_cv_update[PSI-MI]
     * @throws DbInfoServiceException
     */
    public void saveLastCvUpdate( Date date, String namespace ) throws DbInfoServiceException {


        Date lastUpdated = null;
        if ( date == null ) {
            throw new NullPointerException( "You must give a non null date" );
        }

        if ( namespace == null ) {
            throw new NullPointerException( "You must give a non null namespace" );
        }


        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();

        if ( namespace.equals( DbInfo.NAMESPACE_PSIMI ) ) {

            lastUpdated = getLastCvUpdate( namespace );
        }

        if ( lastUpdated == null ) {
            /*doesn't exists so create a new one and persist*/
            DbInfo newDbInfo = new DbInfo( DbInfo.LAST_CV_UPDATE_PSIMI, formatDate( date ) );
            daof.getDbInfoDao().persist( newDbInfo );
        } else {
            //already existing so fetch and update
            DbInfo dbinfo = daof.getDbInfoDao().get( DbInfo.LAST_CV_UPDATE_PSIMI );
            if ( log.isDebugEnabled() ) {
                log.debug( "Updating ..." + dbinfo.toString() );
            }

            dbinfo.setValue( formatDate( date ) );


        }//end else
    }

    public Date getLastProteinUpdate() throws DbInfoServiceException {
        Date date;
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();

        DbInfo dbinfo = daof.getDbInfoDao().get( DbInfo.LAST_PROTEIN_UPDATE );
        try {
            if ( dbinfo != null && dbinfo.getValue() != null ) {
                date = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dbinfo.getValue() );
                return date;
            } else {
                return null;
            }

        } catch ( ParseException pe ) {
            throw new DbInfoServiceException( pe );
        }


    }

    public Date getLastCvUpdate( String namespace ) throws DbInfoServiceException {
        Date date;
        DbInfo dbinfo = null;
        if ( namespace == null ) {
            throw new NullPointerException( "You must give a non null namespace" );
        }


        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        DaoFactory daof = dataContext.getDaoFactory();

        if ( namespace.equals( DbInfo.NAMESPACE_PSIMI ) ) {
            dbinfo = daof.getDbInfoDao().get( DbInfo.LAST_CV_UPDATE_PSIMI );
        }
        try {
            if ( dbinfo != null && dbinfo.getValue() != null ) {
                date = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dbinfo.getValue() );
                return date;
            } else {
                return null;
            }

        } catch ( ParseException pe ) {
            throw new DbInfoServiceException( pe );
        }

    }

    /**
     * Method to convert date to String in a given format
     *
     * @param date date to be formatted
     * @return Date as String
     */

    private static String formatDate( Date date ) {
        if ( date == null ) {
            throw new NullPointerException( "You must give a non null date" );
        }

        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        return sdf.format( date );

    }//end method



}
