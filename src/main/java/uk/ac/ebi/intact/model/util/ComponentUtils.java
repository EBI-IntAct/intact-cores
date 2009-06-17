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
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.CvExperimentalRole;


import java.util.Collection;

/**
 * Utility methods for Components
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public class ComponentUtils {


    /**
     *
     * @param experimentalRoles collection of experimental roles
     * @return  CvExperimentalRole that is prey
     */
    public static CvExperimentalRole getPrey( Collection<CvExperimentalRole> experimentalRoles ) {

        for ( CvExperimentalRole expRole : experimentalRoles ) {
            if ( expRole.getIdentifier().equals( CvExperimentalRole.PREY_PSI_REF ) ) {
                return expRole;
            }
        }

        return null;
    }


    /**
     *
     * @param experimentalRoles   collection of experimental roles
     * @return CvExperimentalRole that is bait
     */
    public static CvExperimentalRole getBait( Collection<CvExperimentalRole> experimentalRoles ) {

        for ( CvExperimentalRole expRole : experimentalRoles ) {
            if ( expRole.getIdentifier().equals( CvExperimentalRole.BAIT_PSI_REF ) ) {
                return expRole;
            }
        }
        return null;
    }


    /**
     *
     * @param experimentalRoles collection of experimental roles
     * @return checks if the given collection of roles contains CvExperimentalRole that is prey
     */
    public static boolean isPrey( Collection<CvExperimentalRole> experimentalRoles ) {

        if ( experimentalRoles == null ) {
            throw new NullPointerException( "You must give a non null experimentalRoles" );
        }

        for ( CvExperimentalRole experimentalRole : experimentalRoles ) {
            if ( experimentalRole.getIdentifier().equals( CvExperimentalRole.PREY_PSI_REF ) ) {
                return true;
            }
        }

        return false;
    }


    /**
     *
     * @param experimentalRoles collection of experimental roles
     * @return checks if the given collection of roles contains CvExperimentalRole that is bait
     */
    public static boolean isBait( Collection<CvExperimentalRole> experimentalRoles ) {
        if ( experimentalRoles == null ) {
            throw new NullPointerException( "You must give a non null experimentalRoles" );
        }

        for ( CvExperimentalRole experimentalRole : experimentalRoles ) {
            if ( experimentalRole.getIdentifier().equals( CvExperimentalRole.BAIT_PSI_REF ) ) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     * @param experimentalRoles collection of experimental roles
     * @return  checks if the given collection of roles contains CvExperimentalRole that is either neutral or unspecified
     */

    public static boolean isNeurtralOrUnspecified( Collection<CvExperimentalRole> experimentalRoles ) {
        if ( experimentalRoles == null ) {
            throw new NullPointerException( "You must give a non null experimentalRoles" );
        }

        for ( CvExperimentalRole experimentalRole : experimentalRoles ) {
            if ( experimentalRole.getIdentifier().equals( CvExperimentalRole.NEUTRAL_PSI_REF ) || experimentalRole.getIdentifier().equals( CvExperimentalRole.UNSPECIFIED_PSI_REF ) ) {
                return true;
            }
        }

        return false;
    }

}//end class
