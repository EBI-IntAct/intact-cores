/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.dao.query;

import junit.framework.TestCase;
import uk.ac.ebi.intact.persistence.dao.query.impl.StandardQueryPhraseConverter;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class StandardQueryPhraseConverterTest extends TestCase {

    private StandardQueryPhraseConverter converter;

    protected void setUp() throws Exception {
        super.setUp();
        converter = new StandardQueryPhraseConverter();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        converter = null;
    }

    public void testObjectToPhrase_wildcard_percent() throws Exception {
        String value = "lsm%";
        QueryPhrase phrase = converter.objectToPhrase( value );

        assertEquals( 1, phrase.getTerms().size() );

        QueryTerm term = phrase.getTerms().iterator().next();

        assertEquals( 1, term.getModifiers().length );
        assertEquals( "lsm", term.getValue() );
    }

    public void testObjectToPhrase_wildcard_asterisk() throws Exception {
        String value = "lsm*";
        QueryPhrase phrase = converter.objectToPhrase( value );

        assertEquals( 1, phrase.getTerms().size() );

        QueryTerm term = phrase.getTerms().iterator().next();

        assertEquals( 1, term.getModifiers().length );
        assertEquals( "lsm", term.getValue() );
    }

    public void testPhraseToObject_wild_percent() throws Exception {
        String prevValue = "lsm%";
        QueryPhrase phrase = converter.objectToPhrase( prevValue );

        String value = converter.phraseToObject( phrase );
        assertEquals( prevValue, value );
    }

    public void testPhraseToObject_wild_asterisk() throws Exception {
        String prevValue = "*lsm";
        QueryPhrase phrase = converter.objectToPhrase( prevValue );

        String value = converter.phraseToObject( phrase );
        assertEquals( "%lsm", value );
    }

    public void testPhraseToObject_complex_phrases() throws Exception {
        String prevValue = "\"Hello there\" -lsm";
        QueryPhrase phrase = converter.objectToPhrase( prevValue );

        String value = converter.phraseToObject( phrase );
        assertEquals( "\"Hello there\",-lsm", value );
    }
}
