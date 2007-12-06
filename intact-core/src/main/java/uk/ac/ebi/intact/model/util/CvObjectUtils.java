package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.ClassUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Util methods for interactions
 *
 * @author Bruno Aranda
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: InteractionUtils.java 8541 2007-06-07 13:28:13Z baranda $
 * @since 1.7.0
 */
public class CvObjectUtils {

    public static CvObjectXref getPsiMiIdentityXref( CvObject cvObject ) {
        return XrefUtils.getPsiMiIdentityXref( cvObject );
    }

    /**
     * Gets the unique identifier of a CvObject. If it has PSI MI Identifier (miIdentifier) return it,
     * if not, return the 'CvDatabase.intact' identifier; otherwise return the primaryId of the first identity xref found.
     * @param cvObject The object to get the identifier from.
     * @return The identifier. Will be null if no miIdentifier or identity xref is found.
     *
     * @since 1.8.0
     */
    public static String getIdentity( CvObject cvObject ) {
        // try the PSI MI first
        CvObjectXref idXref = XrefUtils.getPsiMiIdentityXref( cvObject );

        // try to get the identity with CvDatabase 'intact'
        if (idXref == null) {
            idXref = XrefUtils.getIdentityXref(cvObject, CvDatabase.INTACT);

            // get the first identity, if any
            if (idXref == null) {
                Collection<CvObjectXref> idXrefs = XrefUtils.getIdentityXrefs(cvObject);
                if (!idXrefs.isEmpty()) {
                    idXref = idXrefs.iterator().next();
                }
            }
        }

        return (idXref != null)? idXref.getPrimaryId() : null;
    }

    // ex1 : cvObject is supposibly the CvDatabase psi-mi, psiMi is CvDatabase.PSI_MI_MI_REF
    // ex2: cvObject is supposibly the CvXrefQualifier identity , psiMi is  CvXrefQualifier.IDENTITY_MI_REF
    public static boolean hasIdentity( CvObject cvObject, String psiMi ) {
        return XrefUtils.hasIdentity( cvObject, psiMi );
    }

    /**
     * Creates a wrapper of experimental role and biological role altogether that have methods to output
     * the meaningful label for both roles
     *
     * @param experimentalRole the experimental role to use
     * @param biologicalRole   the biological role to use
     * @return the object containing both roles
     */
    public static RoleInfo createRoleInfo( CvExperimentalRole experimentalRole, CvBiologicalRole biologicalRole ) {
        return new RoleInfo( biologicalRole, experimentalRole );
    }

    public static <T extends CvObject> T createCvObject( Institution institution, Class<T> cvClass, String miIdentifier, String shortLabel ) {
        T cv = ClassUtils.newInstance( cvClass );
        cv.setOwner( institution );
        cv.setShortLabel( shortLabel );

        if (miIdentifier != null) {
            CvObjectXref idXref = XrefUtils.createIdentityXrefPsiMi( cv, miIdentifier );
            cv.addXref( idXref );
            idXref.prepareParentMi();
        }

        return cv;
    }

    public static boolean isProteinType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.PROTEIN_MI_REF, true );
    }

    public static boolean isNucleicAcidType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.NUCLEIC_ACID_MI_REF, true );
    }

    public static boolean isSmallMoleculeType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.SMALL_MOLECULE_MI_REF, true );
    }

    public static boolean isDnaType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.DNA_MI_REF, true );
    }

    /**
     * Checks if the given term has the given MI identifier. If recursive is true, we also search recursively through its parents.
     *
     * @param type      the type to check on.
     * @param mi        the MI term to look for.
     * @param recursive request recursive search amongst parents.
     * @return true of the term or one of its parents has the given MI identity.
     */
    public static boolean isChildOfType( CvInteractorType type, final String mi, final boolean recursive ) {
        if ( type == null ) {
            throw new IllegalArgumentException( "You must give a non null CvInteractorType" );
        }
        if ( mi == null ) {
            throw new IllegalArgumentException( "You must give a non null parent MI" );
        }

        final Collection<CvObjectXref> identities = XrefUtils.getIdentityXrefs( type );
        for ( CvObjectXref identity : identities ) {
            if ( mi.equals( identity.getPrimaryId() ) ) {
                return true;
            }
        }

        if ( recursive ) {
            final Collection<CvDagObject> parents = type.getParents();
            for ( CvDagObject parent : parents ) {
                if ( isChildOfType( ( CvInteractorType ) parent, mi, recursive ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Collect all children psi-mi identity identifier including the given root term's.
     *
     * @param root         term from which we start traversing children.
     * @param collectedMIs non null collection in which we store collected MIs (if giving a List, you may have
     * duplicated in case some terms have multiple parents).
     */
    public static void getChildrenMIs( CvDagObject root, Collection<String> collectedMIs ) {

        if ( root == null ) {
            throw new IllegalArgumentException( "You must give a non null root term" );
        }

        if ( collectedMIs == null ) {
            throw new IllegalArgumentException( "The given collection must not be null" );
        }

        // 1. Add the current term
        final CvObjectXref xref = XrefUtils.getPsiMiIdentityXref( root );
        if ( xref != null ) {
            collectedMIs.add( xref.getPrimaryId() );
        }

        // 2. Add children recursively
        for ( CvDagObject child : root.getChildren() ) {
            getChildrenMIs( child, collectedMIs );
        }
    }

    /**
     * Collect a non redundant list of all children psi-mi identity identifier including the given root term's.
     *
     * @param root term from which we start traversing children.
     * @return a non null collection of MIs.
     */
    public static Set<String> getChildrenMIs( CvDagObject root ) {

        Set<String> collectedMIs = new HashSet<String>();
        getChildrenMIs( root, collectedMIs );
        return collectedMIs;
    }


    /**
     * This method is an alternative equals to the CvObject.equals method, that basically checks
     * on the MI identifiers and then of the short label if the first check returns false
     * @param cv1 One of the CvObjects
     * @param cv2 The other CvObject
     * @return True if (A) the MI are the same or (B) the short labels are the same in case A has failed
     *
     * @since 1.8.0
     */
    public static boolean areEqual(CvObject cv1, CvObject cv2) {
        if ( cv1 == null || cv2 == null ) {
            return false;
        }
        
        if (cv1.getMiIdentifier() != null && cv2.getMiIdentifier() != null) {
            return cv1.getMiIdentifier().equals(cv2.getMiIdentifier());
        }

        return cv1.getShortLabel().equals(cv2.getShortLabel());
    }
}