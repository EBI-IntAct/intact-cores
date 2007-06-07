/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.persistence.dao.query;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;

import java.util.List;

/**
 * Test for <code>SearchableQueryTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 10/13/2006
 */
public class SearchableQueryTest extends TestCase {

    public SearchableQueryTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseSearchableQuery_Default() throws Exception {
        String str = "{ac='EBI-12345';shortLabel='ab%,hola';includeCvIdentificationChildren='true'}";

        SearchableQuery sq = null;

        try {
            sq = SearchableQuery.parseSearchableQuery( str );
        }
        catch ( Throwable t ) {
            t.printStackTrace();
        }
        finally {
            if ( sq == null ) fail( "Could parse SearchableQuery from String: " + str );
        }

        assertEquals( "EBI-12345", sq.getAc().getTerms().iterator().next().getValue() );
        assertEquals( "ab", ( ( List<QueryTerm> ) sq.getShortLabel().getTerms() ).get( 0 ).getValue() );
        assertEquals( "hola", ( ( List<QueryTerm> ) sq.getShortLabel().getTerms() ).get( 1 ).getValue() );
        assertTrue( sq.isIncludeCvIdentificationChildren() );
    }

    public void testParseSearchableQuery() throws Exception {
        String str = "{ac='EBI-12345';shortLabel='ab%,hola';includeCvIdentificationChildren='true'}";

        SearchableQuery sq = null;

        try {
            sq = SearchableQuery.parseSearchableQuery( str );
        }
        catch ( Throwable t ) {
            t.printStackTrace();
        }
        finally {
            if ( sq == null ) fail( "Could parse SearchableQuery from String: " + str );
        }

        assertEquals( "EBI-12345", sq.getAc().getTerms().iterator().next().getValue() );
        assertEquals( "ab", ( ( List<QueryTerm> ) sq.getShortLabel().getTerms() ).get( 0 ).getValue() );
        assertEquals( "hola", ( ( List<QueryTerm> ) sq.getShortLabel().getTerms() ).get( 1 ).getValue() );
        assertTrue( sq.isIncludeCvIdentificationChildren() );
    }

    public static Test suite() {
        return new TestSuite( SearchableQueryTest.class );
    }
}
