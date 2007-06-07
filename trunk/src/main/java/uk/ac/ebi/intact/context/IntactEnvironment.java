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
package uk.ac.ebi.intact.context;

/**
 * Environment properties names
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Sep-2006</pre>
 */
public enum IntactEnvironment {

    /**
     * A comma-separated list with the name of the classes that extends 'uk.ac.ebi.intact.config.DataConfig',
     * used to register data entities
     */
    DATA_CONFIG_PARAM_NAME( "uk.ac.ebi.intact.DATA_CONFIG" ),

    /**
     * The label of the institution, used to retrieve the Institution object
     * if it already exists, or to create a new institution
     */
    INSTITUTION_LABEL( "uk.ac.ebi.intact.INSTITUTION_LABEL" ),

    /**
     * The full name of the institution
     */
    INSTITUTION_FULL_NAME( "uk.ac.ebi.intact.INSTITUTION_FULL_NAME" ),

    /**
     * The postal address of the institution
     */
    INSTITUTION_POSTAL_ADDRESS( "uk.ac.ebi.intact.INSTITUTION_POSTAL_ADDRESS" ),

    /**
     * The URL (web page) of the institution
     */
    INSTITUTION_URL( "uk.ac.ebi.intact.INSTITUTION_URL" ),

    /**
     * The Accession Number Prefix for the objects created
     * by the application and stored in the database. Default: ebi
     */
    AC_PREFIX_PARAM_NAME( "uk.ac.ebi.intact.AC_PREFIX" ),

    /**
     * Preload the most common CvObjects on application start, so it is faster to retrieve them later. Default: false
     */
    PRELOAD_COMMON_CVS_PARAM_NAME( "uk.ac.ebi.intact.PRELOAD_COMMON_CVOBJECTS" ),

    /**
     * If read-only, an application cannot persist data in the database. Default: true
     */
    READ_ONLY_APP( "uk.ac.ebi.intact.READ_ONLY_APP" ),

    /**
     * If true, the IA_SEARCH table is maintained by hibernate in a syncronized way. When an
     * AnnotatedObject is modified, the SearchItems are modified accordingly. Default: false
     */
    SYNCHRONIZED_SEARCH_ITEMS( "uk.ac.ebi.intact.SYNCHRONIZED_SEARCH_ITEMS" ),

    /**
     * If true, don't check the schema version agains the intact-core version. Default: false
     */
    FORCE_NO_SCHEMA_VERSION_CHECK( "uk.ac.ebi.intact.FORCE_NO_SCHEMA_VERSION_CHECK" ),

    /**
     * If true, do open a transaction automatically when DataContext.getDaoFactory() is called. Default: false
     */
    AUTO_BEGIN_TRANSACTION( "uk.ac.ebi.intact.AUTO_BEGIN_TRANSACTION" ),

    /**
     * If true, debug mode such as memorizing who begin a transaction, ... Should not be true in production environment.
     * Default is false.
     */
    DEBUG_MODE( "uk.ac.ebi.intact.DEBUG_MODE" );


    private String fqn;

    private IntactEnvironment( String fqn ) {
        this.fqn = fqn;
    }

    public String getFqn() {
        return fqn;
    }

    @Override
    public String toString() {
        return getFqn();
    }
}
