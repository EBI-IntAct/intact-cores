package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

/**
 * Misc publication utilities.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class PublicationUtils {

    public static boolean isAccepted( Publication publication ) {
        if ( publication == null ) {
            throw new NullPointerException( "You must give a non null publication" );
        }

        for ( Annotation a : publication.getAnnotations() ) {
            if ( a.getCvTopic() != null && CvTopic.ACCEPTED.equals( a.getCvTopic().getShortLabel() ) ) {
                return true;
            }
        }

        for ( Experiment experiment : publication.getExperiments()) {
            if( ExperimentUtils.isAccepted( experiment ) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean isToBeReviewed( Publication publication ) {
        if ( publication == null ) {
            throw new NullPointerException( "You must give a non null publication" );
        }

        for ( Annotation a : publication.getAnnotations() ) {
            if ( a.getCvTopic() != null && CvTopic.TO_BE_REVIEWED.equals( a.getCvTopic().getShortLabel() ) ) {
                return true;
            }
        }

        for ( Experiment experiment : publication.getExperiments()) {
            if( ExperimentUtils.isToBeReviewed( experiment ) ) {
                return true;
            }
        }

        return false;
    }
    
    public static boolean isOnHold( Publication publication ) {

        if ( publication == null ) {
            throw new NullPointerException( "You must give a non null publication" );
        }

        for ( Annotation a : publication.getAnnotations() ) {
            if ( a.getCvTopic() != null && CvTopic.ON_HOLD.equals( a.getCvTopic().getShortLabel() ) ) {
                return true;
            }
        }

        for ( Experiment experiment : publication.getExperiments()) {
            if( ExperimentUtils.isOnHold( experiment ) ) {
                return true;
            }
        }
        
        return false;
    }
}
