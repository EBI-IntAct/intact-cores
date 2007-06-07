package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 15-Feb-2007
 * Time: 11:55:23
 * To change this template use File | Settings | File Templates.
 */
public class CvObjectUtils {

    public static CvObjectXref getPsiMiIdentityXref( CvObject cvObject ) {
        if ( cvObject == null ) {
            throw new NullPointerException( "cvObject should not be null" );
        }
        Collection<CvObjectXref> cvObjectXrefs = cvObject.getXrefs();
        CvObjectXref cvObjectXref = null;
        for ( CvObjectXref xref : cvObjectXrefs ) {
            //Check that the cvdatabase of the xref has an psi-mi identity xref equal to CvDatabase.PSI_MI_MI_REF ( i.e:
            //check that the database is Psi-mi)
            if ( hasIdentity( xref.getCvDatabase(), CvDatabase.PSI_MI_MI_REF ) ) {
                //Check that the cvdatabase of the xref has an psi-mi identity xref equal to CvDatabase.IDENTITY_MI_REF ( i.e:
                //check that the xref qualifier is identity)
                if ( xref.getCvXrefQualifier() != null && hasIdentity( xref.getCvXrefQualifier(), CvXrefQualifier.IDENTITY_MI_REF ) ) {
                    //If cvObjectXref is null than affect it's value, if it is not null it means that the cvObject has 2
                    //xref identity to psi-mi which is not allowed, then send an error message.
                    if ( cvObjectXref == null ) {
                        cvObjectXref = xref;
                    } else {
                        String clazz = cvObject.getClass().getSimpleName();
                        throw new IllegalStateException( "More than one psi-mi identity in " + clazz + " :" + cvObject.getAc() );
                    }
                }
            }
        }
        return cvObjectXref;
    }

    // ex1 : cvObject is supposibly the CvDatabase psi-mi, psiMi is CvDatabase.PSI_MI_MI_REF
    // ex2: cvObject is supposibly the CvXrefQualifier identity , psiMi is  CvXrefQualifier.IDENTITY_MI_REF
    public static boolean hasIdentity( CvObject cvObject, String psiMi ) {
        if ( cvObject == null ) {
            throw new NullPointerException( "cvObject should not be null" );
        }
        if ( psiMi == null ) {
            throw new NullPointerException( "psiMi should not be null" );
        }
        Collection<CvObjectXref> cvObjectXrefs = cvObject.getXrefs();
        for ( CvObjectXref xref : cvObjectXrefs ) {
            if ( psiMi.equals( xref.getPrimaryId() ) ) {
                if ( CvXrefQualifier.IDENTITY_MI_REF.equals( psiMi ) ) {
                    return true;
                }
                if ( xref.getCvXrefQualifier() != null && hasIdentity( xref.getCvXrefQualifier(), CvXrefQualifier.IDENTITY_MI_REF ) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
