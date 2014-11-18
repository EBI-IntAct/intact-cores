package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.factory.options.MIDataSourceOptions;

/**
 * Class that lists all the options for a IntAct datasource.
 * The options listed in MIDataSourceOptions are also valid for a Intact datasource
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/12/13</pre>
 */

public class IntactDataSourceOptions extends MIDataSourceOptions{

    /**
     * The source location of the spring file which describe IntAct configuration for the IntAct datasource reader.
     * If provided, it will override any existing spring configuration.
     * If no spring configuration was provided, it will inject spring configuration file classpath*:/META-INF/intact-jami-spring.xml which expects some properties to be set such as :
     * intact.hbm2ddl
     * intact.platform
     * intact.driver
     * intact.db.url
     * intact.db.user
     * intact.db.password
     * intact.user.id
     * intact.acPrefix
     * intact.default.institution
     *
     */
    public static final String SPRING_CONFIG_OPTION = "intact_spring_source_key";

    /**
     * The bean name of the interaction service to use. If not provided, will use the default interactionEvidenceService or complexService
     */
    public static final String INTERACTION_SERVICE_NAME_OPTION = "interaction_service_name_key";

    /**
     * The HQL query to use when querying the interaction/complex services. If not provided, it will query for everything in the interaction/complex service
     */
    public static final String HQL_QUERY_OPTION = "hql_query_key";

    /**
     * The HQL query to use when counting the number of results in the interaction/complex services. If not provided, it will count everything in the interaction/complex service
     */
    public static final String HQL_COUNT_QUERY_OPTION = "hql_count_query_key";

    /**
     * The Map<String, Object> which contains the HQL query parameters to apply when querying the interaction/complex service.
     * It is optional and no query parameters will be used by default if this map is not provided
     */
    public static final String HQL_QUERY_PARAMETERS_OPTION = "hql_query_parameter_key";

    /**
     * A boolean value to know if the properties will be lazy loaded (false) or fully initialised (true)
     */
    public static final String DB_INITIALISE_LAZY = "db_initialise_lazy";
}
