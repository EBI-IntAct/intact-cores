package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.datasource.InteractionWriterOptions;

/**
 * Class that lists all the options for a IntAct database writer.
 * The options listed in InteractionWriterOptions are also valid for a Intact database Writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/12/13</pre>
 */

public class IntactWriterOptions extends InteractionWriterOptions{

    /**
     * The source location of the spring file which describe IntAct configuration for the IntAct database writer.
     * If provided, it will override any existing spring configuration.
     * If no spring configuration was provided, it will inject spring configuration file classpath*:/META-INF/intact-jami.spring.xml which expects some properties to be set such as :
     * intact.hbm2ddl
     * intact.platform
     * intact.driver
     * intact.db.url
     * intact.db.user
     * intact.db.password
     * intact.user.id
     * intact.acPrefix
     * intact.default.institution
     */
    public static final String SPRING_CONFIG_OPTION = "intact_spring_source_key";

    /**
     * The bean name of the interaction service to use. If not provided, will use the default interactionEvidenceService or complexService
     */
    public static final String INTERACTION_SERVICE_NAME_OPTION = "interaction_service_name_key";
}
