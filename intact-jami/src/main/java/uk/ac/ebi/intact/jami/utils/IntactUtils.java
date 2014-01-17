package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Utility class for intact classes and properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */

public class IntactUtils {

    public static final int MAX_SHORT_LABEL_LEN = 256;
    public static final int MAX_FULL_NAME_LEN = 1000;
    public static final int MAX_DESCRIPTION_LEN = 4000;
    public static final int MAX_ALIAS_NAME_LEN = 256;
    public static final int MAX_ID_LEN = 256;
    public static final int MAX_DB_RELEASE_LEN = 10;
    /**
     * As the maximum size of database objects is limited, the sequence is represented as
     * an array of strings of maximum length.
     */
    public static final int MAX_SEQ_LENGTH_PER_CHUNK = 1000;

    public final static DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    public static final String CV_LOCAL_SEQ = "cv_local_seq";

    public static final String DATABASE_OBJCLASS="uk.ac.ebi.intact.model.CvDatabase";
    public static final String QUALIFIER_OBJCLASS="uk.ac.ebi.intact.model.CvXrefQualifier";
    public static final String TOPIC_OBJCLASS="uk.ac.ebi.intact.model.CvTopic";
    public static final String ALIAS_TYPE_OBJCLASS="uk.ac.ebi.intact.model.CvAliasType";
    public static final String UNIT_OBJCLASS ="uk.ac.ebi.intact.model.CvUnit";
    public static final String FEATURE_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvFeatureType";
    public static final String EXPERIMENTAL_ROLE_OBJCLASS ="uk.ac.ebi.intact.model.CvExperimentalRole";
    public static final String BIOLOGICAL_ROLE_OBJCLASS ="uk.ac.ebi.intact.model.CvBiologicalRole";
    public static final String INTERACTION_DETECTION_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvInteraction";
    public static final String INTERACTOR_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvInteractorType";

    public static final String RELEASED_STATUS = "released";

    public static IntactCvTerm createMIDatabase(String name, String MI){
        return createIntactMITerm(name, MI, DATABASE_OBJCLASS);
    }

    public static IntactCvTerm createMIQualifier(String name, String MI){
        return createIntactMITerm(name, MI, QUALIFIER_OBJCLASS);
    }

    public static IntactCvTerm createMITopic(String name, String MI){
        return createIntactMITerm(name, MI, TOPIC_OBJCLASS);
    }

    public static IntactCvTerm createMIFeatureType(String name, String MI){
        return createIntactMITerm(name, MI, FEATURE_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createMIBiologicalRole(String name, String MI){
        return createIntactMITerm(name, MI, BIOLOGICAL_ROLE_OBJCLASS);
    }

    public static IntactCvTerm createMIExperimentalRole(String name, String MI){
        return createIntactMITerm(name, MI, EXPERIMENTAL_ROLE_OBJCLASS);
    }

    public static IntactCvTerm createMIInteractionDetectionMethod(String name, String MI){
        return createIntactMITerm(name, MI, INTERACTION_DETECTION_METHOD_OBJCLASS);
    }

    public static IntactCvTerm createMIAliasType(String name, String MI){
        return createIntactMITerm(name, MI, ALIAS_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createIntactMITerm(String name, String MI, String objclass){
        if (MI != null){
            return new IntactCvTerm(name, new CvTermXref(new IntactCvTerm(CvTerm.PSI_MI, null, CvTerm.PSI_MI_MI, DATABASE_OBJCLASS), MI, new IntactCvTerm(Xref.IDENTITY, null, Xref.IDENTITY_MI, QUALIFIER_OBJCLASS)), objclass);
        }
        else {
            return new IntactCvTerm(name, (String)null, (String)null, objclass);
        }
    }
}
