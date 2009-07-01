/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvDagObject;

/**
 * util class to insert the CvDagObject classes into the database
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class CvDagObjectTool {

    private CvDagObjectTool() {
        // so this util class is never instantiated
    }

    /**
     * this main method expects one argument, which is the name of the CvDagObject class which should be
     * inserted into the database.
     *
     * @param args
     */
    public static void main( String[] args ) {
        Class targetClass = null;

        // create a connection to the database
        try {

            CvDagObjectUtils dagUtil = new CvDagObjectUtils();
            String usage = "Usage CvDagObjectTools <aCvDagObjectClassName>";
            // first check the number of arguments
            if ( args.length != 1 ) {
                System.out.println( "Invalid numbers of argument!\n" + usage );
                System.exit( 1 );
            }

            try {
                targetClass = Class.forName( args[0] );
            }
            catch ( ClassNotFoundException e ) {
                System.out.println( "Class " + args[0] + " not found.\n" + usage );
                System.exit( 1 );
            }
            // check if the class inherits from CvDagObject
            if ( !CvDagObject.class.isAssignableFrom( targetClass ) ) {
                throw new IntactException( "invalid class!" );
            }

            // insert the whole DAG format of that class into the database in a tree format
            dagUtil.insertCVs( targetClass );

        } catch ( IntactException e ) {
            e.printStackTrace();
        }
    }

}
