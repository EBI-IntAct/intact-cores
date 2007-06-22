package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * Util methods for interactions
 *
 * @version $Id: InteractionUtils.java 8541 2007-06-07 13:28:13Z baranda $
 * @since <pre>14-Aug-2006</pre>
 */
public class CvObjectUtils {

    public static CvObjectXref getPsiMiIdentityXref(CvObject cvObject) {
        if (cvObject == null) {
            throw new NullPointerException("cvObject should not be null");
        }
        Collection<CvObjectXref> cvObjectXrefs = cvObject.getXrefs();
        CvObjectXref cvObjectXref = null;
        for (CvObjectXref xref : cvObjectXrefs) {
            //Check that the cvdatabase of the xref has an psi-mi identity xref equal to CvDatabase.PSI_MI_MI_REF ( i.e:
            //check that the database is Psi-mi)
            if (hasIdentity(xref.getCvDatabase(), CvDatabase.PSI_MI_MI_REF)) {
                //Check that the cvdatabase of the xref has an psi-mi identity xref equal to CvDatabase.IDENTITY_MI_REF ( i.e:
                //check that the xref qualifier is identity)
                if (xref.getCvXrefQualifier() != null && hasIdentity(xref.getCvXrefQualifier(), CvXrefQualifier.IDENTITY_MI_REF)) {
                    //If cvObjectXref is null than affect it's value, if it is not null it means that the cvObject has 2
                    //xref identity to psi-mi which is not allowed, then send an error message.
                    if (cvObjectXref == null) {
                        cvObjectXref = xref;
                    } else {
                        String clazz = cvObject.getClass().getSimpleName();
                        throw new IllegalStateException("More than one psi-mi identity in " + clazz + " :" + cvObject.getAc());
                    }
                }
            }
        }
        return cvObjectXref;
    }

    // ex1 : cvObject is supposibly the CvDatabase psi-mi, psiMi is CvDatabase.PSI_MI_MI_REF
    // ex2: cvObject is supposibly the CvXrefQualifier identity , psiMi is  CvXrefQualifier.IDENTITY_MI_REF
    public static boolean hasIdentity(CvObject cvObject, String psiMi) {
        if (cvObject == null) {
            throw new NullPointerException("cvObject should not be null");
        }
        if (psiMi == null) {
            throw new NullPointerException("psiMi should not be null");
        }
        Collection<CvObjectXref> cvObjectXrefs = cvObject.getXrefs();
        for (CvObjectXref xref : cvObjectXrefs) {
            if (psiMi.equals(xref.getPrimaryId())) {
                if (CvXrefQualifier.IDENTITY_MI_REF.equals(psiMi)) {
                    return true;
                }
                if (xref.getCvXrefQualifier() != null && hasIdentity(xref.getCvXrefQualifier(), CvXrefQualifier.IDENTITY_MI_REF)) {
                    return true;
                }
            }
        }
        return false;
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

}
