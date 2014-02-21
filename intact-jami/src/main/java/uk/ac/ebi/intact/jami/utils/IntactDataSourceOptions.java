package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.datasource.MIDataSourceOptions;

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
     * The source location of the spring file which describe IntAct configuration
     */
    public static final String SPRING_CONFIG_OPTION = "intact_spring_source_key";

}
