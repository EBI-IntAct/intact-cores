/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/


package uk.ac.ebi.intact.model;


/**
 * Basic constants file.
 */

public final class Constants {

    private Constants() {
    }

    /**
     * The session scope attribute under which the User object
     * for the currently logged in user is stored.
     */
    public static final String USER_KEY = "user";

    /**
     * Used in various action classes to define where to forward
     * to on different conditions.  See the struts-config.xml file
     * to see where the page that is using this forwards to.
     */
    public static final String FORWARD_SUCCESS = "success";

    /**
     * Used in various action classes to define where to forward
     * to on different conditions.  See the struts-config.xml file
     * to see where the page that is using this forwards to.
     */
    public static final String FORWARD_FAILURE = "failure";

    /**
     * Used in various action classes to define where to forward
     * to on different conditions.  See the struts-config.xml file
     * to see where the page that is using this forwards to.
     */
    public static final String FORWARD_DETAIL = "showDetail";


    /**
     * The name of an attribute set in the session.
     */
    public static final String ATTRIBUTE_SEARCH = "search";

    /**
     * The name of an attribute set in the session, holding more detailed search info.
     */
    public static final String SEARCH_DETAIL = "detail";

    /**
     * The name to identify that a search is required for detailed objects (ie not
     * from a search JSP).
     */
    public static final String FIND_MORE_DETAILS = "detailedSearch";

    /**
     * The name of an attribute set in the session.
     */
    public static final String ATTRIBUTE_OBJECTTYPE = "objectType";


    /**
     * The name of an attribute set in the session.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The name of a parameter from a form submission.
     */
    public static final String PARAMETER_SEARCH_AC = "searchAc";

    /**
     * The name of a parameter from a form submission.
     */
    public static final String PARAMETER_SEARCH_NAME = "searchName";

    /**
     * The parameter name used to identify a value submitted for a search.
     */
    public static final String SEARCH_VALUE = "searchValue";

    /**
     * session parameter used to identify the search criterion (ac or name)
     */
    public static final String SEARCH_PARAM = "searchParam";

    /**
     * Name of the database engine being used
     */
    public static final String JDO = "jdo";

    /**
     * holds the object type to class mapping within the servlet context
     */
    public static final String TYPE_MAP = "types";


    /**
     * a specific data store column  - better in a config file!!
     */
    public static final String SEARCH_BY_AC = "ac";

    /**
     * a specific data store column - better in a config file!!
     */
    public static final String SEARCH_BY_NAME = "name";

    /**
     * used as a key to identify a DB filename (for Castor)
     * the value is defined in the web.xml file
     */
    public static final String DB_FILE_KEY = "dbfile";

    /**
     * used as a key to identify a mapping filename (for Castor)
     * the value is defined in the web.xml file
     */
    public static final String MAPPING_FILE_KEY = "mappingfile";

    /**
     * used as a key to identify a DB name - value defined in web.xml file
     */
    public static final String DB_NAME_KEY = "dbname";

    /**
     * used as a key to identify a datasource class - its value
     * is deifned in the web.xml file as a servlet context parameter
     */
    public static final String DATASOURCE = "datasource";

    /**
     * used as a key to identify an intact object types property file -
     * its value is defined in the web.xml file as a servlet context parameter
     */
    public static final String INTACT_TYPES_FILE = "intacttypesfile";

    /**
     * used as a key to identify a page to display when no matches are found
     * from a search
     */
    public static final String FORWARD_NO_MATCHES = "noMatch";

    /**
     * used as a servlet context parameter name to refer to reflection data
     * for the intact classes
     */
    public static final String CLASS_INFO = "classInfo";

    /**
     * used as a servlet context parameter name to refer to the path of
     * a log file
     */
    public static final String INTACT_LOG_FILE = "logfile";

    /**
     * used as a servlet session parameter name to refer to an intact helper instance
     */
    public static final String INTACT_HELPER = "intacthelper";

    /**
     * Expansion:
     * If an Interaction has more than two interactors, it has to be defined how pairwise interactions
     * are generated from the complex data. The following constants define the possible expansion modes.
     */

    /**
     * Expand a complex to all pairwise interactions.
     */
    public static final int EXPANSION_ALL = 0;

    /**
     * Expand a complex into all bait-prey pairs. If no bait is defined in the complex,
     * the FIRST interactor is considered to be the bait. A warning should be given.
     */
    public static final int EXPANSION_BAITPREY = 1;


}
