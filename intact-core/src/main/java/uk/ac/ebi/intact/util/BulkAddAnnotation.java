package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.persistence.dao.AnnotationDao;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.util.Collection;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.sql.SQLException;

/**
 * Utility class helping to bulk add Annotations.
 *
 * @author Samuel Kerrien
 * @version 1.0
 * @since specify the maven artifact version
 */
public class BulkAddAnnotation {

    private int updatedCount = 0;
    private int upToDate = 0;

    ///////////////////////
    // Getters

    public int getUpdatedCount() {
        return updatedCount;
    }

    public int getUpToDate() {
        return upToDate;
    }

    public void reset() {
        updatedCount = 0;
        upToDate = 0;
    }

    ////////////////////////
    // Bulk updater

    public void addAnnotation( Class<? extends AnnotatedObject> clazz, String ac, CvTopic topic, String text )
            throws IntactTransactionException {

        if ( clazz == null ) {
            throw new IllegalArgumentException( "You must give a non null class" );
        }

        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null ac" );
        }

        if ( topic == null ) {
            throw new IllegalArgumentException( "You must give a non null topic" );
        }

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        AnnotatedObjectDao dao = daoFactory.getAnnotatedObjectDao( clazz );

        // search the object
        AnnotatedObject ao = ( AnnotatedObject ) dao.getByAc( ac );

        if ( ao != null ) {
            Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();
            Annotation a = new Annotation( owner, topic, text );

            if ( !ao.getAnnotations().contains( a ) ) {
                AnnotationDao adao = daoFactory.getAnnotationDao();
                adao.persist( a );

                ao.addAnnotation( a );
                dao.update( ao );
                System.out.println( "Created annotation [" + a + "] on " + clazz.getSimpleName() + " [AC: " + ac + "]." );

                updatedCount++;
            } else {
                System.out.println( clazz.getSimpleName() + " [AC: " + ac + "] is up-to-date" );

                upToDate++;
            }
        } else {
            System.out.println( "Could not find " + clazz.getSimpleName() + " [AC: " + ac + "] in the database." );
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    //////////////////////
    // D E M O

    public static void main( String[] args ) throws IntactTransactionException, IOException, SQLException {


        Collection<String> acs = new ArrayList<String>();

        // Load a collection of ACs
        BufferedReader in = new BufferedReader( new FileReader( new File( "C:\\interactionACs.txt" ) ) );
        String str;
        System.out.println( "Opening file" );
        while ( ( str = in.readLine() ) != null ) {
            acs.add( str );
        }
        in.close();
        System.out.println( "Read " + acs.size() + " entries." );

        IntactContext.getCurrentInstance().getConfig().setReadOnlyApp( false );


        // Prepare the parameters of the Annotations
        String text = "high";
        
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        System.out.println( "Database: " + daoFactory.getBaseDao().getDbName() );
        System.out.println( "User: " + daoFactory.getBaseDao().getDbUserName() );
        CvTopic topic = daoFactory.getCvObjectDao( CvTopic.class ).getByShortLabel( "author-confidence" );
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // process these ACs
        BulkAddAnnotation bulkUpdater = new BulkAddAnnotation();
        for ( String ac : acs ) {
            bulkUpdater.addAnnotation( InteractionImpl.class, ac, topic, text );
        }

        System.out.println( "Updated interactions:    " + bulkUpdater.getUpdatedCount() );
        System.out.println( "Up-to-date interactions: " + bulkUpdater.getUpToDate() );
    }
}
