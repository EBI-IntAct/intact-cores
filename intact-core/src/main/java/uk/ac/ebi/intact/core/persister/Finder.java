package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.*;

/**
 * Implementing objects allow to find AC of given object based on their properties.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public interface Finder {

    /**
     * Finds an annotatedObject based on its properties.
     *
     * @param annotatedObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( AnnotatedObject annotatedObject );

    /**
     * Finds an institution based on its properties.
     *
     * @param institution the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Institution institution );

    /**
     * Finds a publication based on its properties.
     *
     * @param publication the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Publication publication );

    /**
     * Finds an experiment based on its properties.
     *
     * @param experiment the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Experiment experiment );

    /**
     * Finds an interaction based on its properties.
     *
     * @param interaction the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Interaction interaction );

    /**
     * Finds an interactor based on its properties.
     *
     * @param interactor the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Interactor interactor );

    /**
     * Finds a component based on its properties.
     *
     * @param component the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Component component );

    /**
     * Finds a feature based on its properties.
     *
     * @param feature the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Feature feature );

    /**
     * Finds a range based on its properties.
     *
     * @param range the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( Range range );

    /**
     * Finds a cvObject based on its properties.
     *
     * @param cvObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( CvObject cvObject );
}
