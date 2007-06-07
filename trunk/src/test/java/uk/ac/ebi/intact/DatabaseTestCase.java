/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

/**
 * Test for <code>SearchableDaoImplTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: SearchableDaoImplTest.java 7541 2007-02-19 10:40:51Z skerrien $
 * @since 10/10/2006
 */
public abstract class DatabaseTestCase extends TestCase {

    public DatabaseTestCase( String name ) {
        super( name );
    }

    private DaoFactory daoFactory;

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    public void setUp() throws Exception {
        super.setUp();

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        if ( daoFactory.isTransactionActive() ) {

            if ( IntactContext.getCurrentInstance().getConfig().isDebugMode() ) {
                IntactTransaction transaction = daoFactory.getCurrentTransaction();
                StackTraceElement[] stackTraceElements = transaction.getStackTrace();

                StringBuilder sb = new StringBuilder( 512 );
                sb.append( "There is still a transaction active. See StackTrace below:" ).append( NEW_LINE );
                if ( stackTraceElements != null ) {
                    for ( int i = 0; i < stackTraceElements.length; i++ ) {
                        StackTraceElement ste = stackTraceElements[i];
                        sb.append( ste ).append( NEW_LINE );
                    }
                } else {
                    sb.append( "No stack trace is available, despite DEBUG_MODE being true." );
                }

                fail( sb.toString() );

            } else {

                StringBuffer sb = new StringBuffer( 128 );
                sb.append( "There is still a transaction active. Please add the following in your resources/intact.properties:" );
                sb.append( "uk.ac.ebi.intact.DEBUG_MODE=true" );
                fail( sb.toString() );
            }
        }

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    public void tearDown() throws Exception {
        super.tearDown();

        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        daoFactory = null;
    }

    protected DaoFactory getDaoFactory() {
        return daoFactory;
    }
}