package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.ClassUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Util methods for CvObjects, which do not use the database.
 *
 * @author Bruno Aranda
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: InteractionUtils.java 8541 2007-06-07 13:28:13Z baranda $
 * @since 1.7.0
 */
public class CvObjectUtils {

    /**
     * @deprecated To get the PSI-MI identifier, just invoke CvObject.getMiIdentifier()
     */
    @Deprecated
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
        if (cvObject.getIdentifier() != null) {
            return cvObject.getIdentifier();
        }

       // try to get the identity with CvDatabase 'intact'
       CvObjectXref idXref = XrefUtils.getIdentityXref(cvObject, CvDatabase.INTACT);

        // get the first identity, if any
        if (idXref == null) {
            Collection<CvObjectXref> idXrefs = XrefUtils.getIdentityXrefs(cvObject);
            if (!idXrefs.isEmpty()) {
                idXref = idXrefs.iterator().next();
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
        cv.setIdentifier( miIdentifier );

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

    public static boolean isRnaType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.RNA_MI_REF, true );
    }

    public static boolean isInteractionType( CvInteractorType type ) {
        return isChildOfType( type, CvInteractorType.INTERACTION_MI_REF, false );
    }

    /**
     * Checks if the given term has the given MI identifier. If recursive is true, we also search recursively through its parents.
     *
     * @param cvDagObject      the cvDagObject to check on.
     * @param mi        the MI term to look for.
     * @param recursive request recursive search amongst parents.
     * @return true of the term or one of its parents has the given MI identity.
     */
    public static boolean isChildOfType( CvDagObject cvDagObject, final String mi, final boolean recursive ) {
        if ( cvDagObject == null ) {
            throw new IllegalArgumentException( "You must give a non null CvDagObject" );
        }
        if ( mi == null ) {
            throw new IllegalArgumentException( "You must give a non null parent MI" );
        }

        final Collection<CvObjectXref> identities = XrefUtils.getIdentityXrefs( cvDagObject );
        for ( CvObjectXref identity : identities ) {
            if ( mi.equals( identity.getPrimaryId() ) ) {
                return true;
            }
        }

        if ( recursive ) {
            final Collection<CvDagObject> parents = cvDagObject.getParents();
            for ( CvDagObject parent : parents ) {
                if ( isChildOfType( parent, mi, recursive ) ) {
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
        return areEqual(cv1, cv2, false);
    }

    /**
     * This method is an alternative equals to the CvObject.equals method, that basically checks
     * on the MI identifiers and then of the short label if the first check returns false
     * @param cv1 One of the CvObjects
     * @param cv2 The other CvObject
     * @param includeCollectionsCheck if true, check if the annotations/xrefs/aliases are the same
     * @return True if (A) the MI are the same or (B) the short labels are the same in case A has failed
     *
     * @since 1.9.0
     */
    public static boolean areEqual(CvObject cv1, CvObject cv2, boolean includeCollectionsCheck) {
        if ( cv1 == null || cv2 == null ) {
            return false;
        }

        if (includeCollectionsCheck && AnnotatedObjectUtils.isNewOrManaged(cv1) && AnnotatedObjectUtils.isNewOrManaged(cv2)) {
            return AnnotatedObjectUtils.containSameCollections(cv1, cv2);
        }

        if (cv1.getIdentifier() != null && cv2.getIdentifier() != null) {
            return cv1.getIdentifier().equals(cv2.getIdentifier());
        }

        if (cv1.getShortLabel() == null || cv2.getShortLabel() == null) {
            return false;
        }

        return cv1.getShortLabel().equals(cv2.getShortLabel());
    }
}