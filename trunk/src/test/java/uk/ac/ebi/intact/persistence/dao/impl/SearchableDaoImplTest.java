/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.DatabaseTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.query.impl.StandardQueryPhraseConverter;

import java.util.List;
import java.util.Map;

/**
 * Test for <code>SearchableDaoImplTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 10/10/2006
 */
public class SearchableDaoImplTest extends DatabaseTestCase {

    public SearchableDaoImplTest( String name ) {
        super( name );
    }

    private SearchableDao dao;
    private String proteinAc1 = null;
    private String proteinAc2 = null;

    private StandardQueryPhraseConverter converter;

    public void setUp() throws Exception {
        super.setUp();
        dao = getDaoFactory().getSearchableDao();
        converter = new StandardQueryPhraseConverter();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        converter = null;
    }

    public void testCountByQuery_ac() throws Exception {
        SearchableQuery query = new SearchableQuery();
        setProteinAcs();
        query.setAc( converter.objectToPhrase( proteinAc1 ) );

        int count = dao.countByQuery( InteractorImpl.class, query );
        assertEquals( 1, count );
    }

    public void testCountByQuery_2ac() throws Exception {
        SearchableQuery query = new SearchableQuery();
        setProteinAcs();
        query.setAc( converter.objectToPhrase( proteinAc1 + ","+ proteinAc2 ) );

        int count = dao.countByQuery( InteractorImpl.class, query );
        assertEquals( 2, count );
    }

    public void testCountByQuery_2ac_self() throws Exception {
        SearchableQuery query = new SearchableQuery();
        setProteinAcs();
        query.setAc( converter.objectToPhrase( proteinAc1 + ","+ proteinAc1 ) );

        int count = dao.countByQuery( InteractorImpl.class, query );
        assertEquals( 1, count );
    }

    public void testCountByQuery_all_standard() throws Exception {
        SearchableQuery query = new SearchableQuery();

        Map<Class<? extends Searchable>, Integer> counts = dao.countByQuery( query );
        assertEquals( 5, counts.entrySet().size() );


        for ( Map.Entry<Class<? extends Searchable>, Integer> entry : counts.entrySet() ) {
            System.out.println( entry.getKey() + ": " + entry.getValue() );

            if ( entry.getKey().equals( NucleicAcidImpl.class ) ) {
                assertTrue( entry.getValue() == 0 );
            } else {
                assertTrue( entry.getValue() > 0 );
            }
        }

    }

    public void testGetByQuery_fullText() throws Exception {
        SearchableQuery query = new SearchableQuery();
        query.setFullText( converter.objectToPhrase( "%phosphate%" ) );

        List<InteractorImpl> results = dao.getByQuery( InteractorImpl.class, query, 0, 50 );

        assertEquals( 18, results.size() );
    }

    public void testGetByQuery_description() throws Exception {
        SearchableQuery query = new SearchableQuery();
        query.setDescription( converter.objectToPhrase( "%\"phosphate synthetase\"%" ) );

        List<Experiment> results = dao.getByQuery( Experiment.class, query, 0, 50 );

        assertEquals( 1, results.size() );
    }

    public void testGetByQuery_annotation() throws Exception {
        SearchableQuery query = new SearchableQuery();
        query.setAnnotationText( converter.objectToPhrase( "%diploid%" ) );

        List<CvObject> results = dao.getByQuery( CvObject.class, query, 0, 50 );
        assertFalse( results.isEmpty() );
    }

    public void testGetByQuery_annotation_and_topic() throws Exception {
        SearchableQuery query = new SearchableQuery();
        query.setCvTopicLabel( converter.objectToPhrase( CvTopic.COMMENT ) );
        query.setAnnotationText( converter.objectToPhrase( "%\"tagged DIP\"%" ) );

        List<? extends Searchable> results = dao.getByQuery( InteractorImpl.class, query, 0, 50 );

        assertEquals( 1, results.size() );
    }

    public void testGetByQuery_experiments_standard_disjunction() throws Exception {
        QueryPhrase search = converter.objectToPhrase( "tho%" );

        SearchableQuery query = new SearchableQuery();
        query.setShortLabel( search );
        query.setDescription( search );
        query.setXref( search );
        query.setAc( search );
        query.setDisjunction( true );

        List<? extends Searchable> results = dao.getByQuery( Experiment.class, query, 0, 50 );
        assertEquals( 1, results.size() );

    }

    public void testGetByQuery_std() throws Exception {
        SearchableQuery query = new SearchableQuery();

        List<? extends Searchable> results = dao.getByQuery( query, 0, 50 );

        assertEquals( 50, results.size() );
    }

    /*
    public void testGetByQuery_cvDatabase_xref() throws Exception
    {
        SearchableQuery query = new SearchableQuery();
        query.setCvDatabaseLabel(converter.objectToPhrase("ipi"));
        query.setXref(converter.objectToPhrase("IPI%"));

        Map results = dao.countByQuery(new Class[] { Experiment.class, ProteinImpl.class}, query);
        assertFalse(results.isEmpty());
    }
    */
    public void testGetByQuery_cvInteraction_withChildren() {
        SearchableQuery query = new SearchableQuery();
        query.setCvInteractionLabel( converter.objectToPhrase( "\"2h fragment pooling\"" ) );
        query.setIncludeCvInteractionChildren( true );

        int count = dao.countByQuery( Experiment.class, query );
        /*
        assertTrue(count > 600);

        List<Experiment> results = dao.getByQuery(Experiment.class, query, 0, 50);

        assertFalse(results.isEmpty());
        assertEquals(50, results.size());
          */
        assertEquals( 0, count );
    }

    public void testGetByQuery_cvInteraction_cvIdentification_withChildren() {
        SearchableQuery query = new SearchableQuery();
        query.setCvInteractionLabel( converter.objectToPhrase( "biophysical" ) );
        query.setIncludeCvInteractionChildren( true );
        query.setCvIdentificationLabel( converter.objectToPhrase( "\"mass spectrometry\"" ) );
        query.setIncludeCvIdentificationChildren( true );

        int count = dao.countByQuery( Experiment.class, query );
        assertTrue( count == 0 );

        List<Experiment> results = dao.getByQuery( Experiment.class, query, 0, 50 );

        assertTrue( results.isEmpty() );
    }

    public void testCountByQuery_interaction_label() throws Exception {
        QueryPhrase search = converter.objectToPhrase( "cara%" );

        SearchableQuery query = new SearchableQuery();
        query.setDisjunction( true );
        query.setShortLabel( search );
        query.setXref( search );

        assertEquals( Integer.valueOf( 1 ), dao.countByQuery( InteractionImpl.class, query ) );

    }

    public void setProteinAcs(){
        InteractorDao interactorDao = getDaoFactory().getInteractorDao();
        Interactor protein = (Interactor) interactorDao.getByShortLabel("cara_ecoli");
        proteinAc1 = protein.getAc();

        protein = (Interactor) interactorDao.getByShortLabel("ubx_drome");
        proteinAc2 = protein.getAc();


    }
}