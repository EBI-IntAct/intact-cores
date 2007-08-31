package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.ClassUtils;

/**
 * Util methods for interactions
 *
 * @version $Id: InteractionUtils.java 8541 2007-06-07 13:28:13Z baranda $
 * @since <pre>14-Aug-2006</pre>
 */
public class CvObjectUtils {

    public static CvObjectXref getPsiMiIdentityXref(CvObject cvObject) {
        return XrefUtils.getPsiMiIdentityXref(cvObject);    
    }

    // ex1 : cvObject is supposibly the CvDatabase psi-mi, psiMi is CvDatabase.PSI_MI_MI_REF
    // ex2: cvObject is supposibly the CvXrefQualifier identity , psiMi is  CvXrefQualifier.IDENTITY_MI_REF
    public static boolean hasIdentity(CvObject cvObject, String psiMi) {
        return XrefUtils.hasIdentity(cvObject, psiMi);
    }

    /**
     * Creates a wrapper of experimental role and biological role altogether that have methods to output
     * the meaningful label for both roles
     * @param experimentalRole the experimental role to use
     * @param biologicalRole the biological role to use
     * @return the object containing both roles
     */
    public static RoleInfo createRoleInfo(CvExperimentalRole experimentalRole, CvBiologicalRole biologicalRole) {
        return new RoleInfo(biologicalRole, experimentalRole);
    }

    public static <T extends CvObject> T createCvObject(Institution institution, Class<T> cvClass, String primaryId, String shortLabel) {
        T cv = ClassUtils.newInstance(cvClass);
        cv.setOwner(institution);
        cv.setShortLabel(shortLabel);

        CvObjectXref idXref = XrefUtils.createIdentityXrefPsiMi(cv, primaryId);
        cv.addXref(idXref);

        return cv;
    }
}
