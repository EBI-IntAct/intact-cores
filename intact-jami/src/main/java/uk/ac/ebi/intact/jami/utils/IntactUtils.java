package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.comparator.IntegerComparator;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for intact classes and properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */

public class IntactUtils {

    public static final int MAX_SHORT_LABEL_LEN = 255;
    public static final int MAX_FULL_NAME_LEN = 1000;
    public static final int MAX_DESCRIPTION_LEN = 4000;
    public static final int MAX_ALIAS_NAME_LEN = 256;
    public static final int MAX_SECONDARY_ID_LEN = 256;
    public static final int MAX_ID_LEN = 50;
    public static final int MAX_DB_RELEASE_LEN = 10;
    /**
     * As the maximum size of database objects is limited, the sequence is represented as
     * an array of strings of maximum length.
     */
    public static final int MAX_SEQ_LENGTH_PER_CHUNK = 1000;

    public final static DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    public static final Pattern decimalPattern = Pattern.compile("\\d");

    public static final String CV_LOCAL_SEQ = "cv_local_seq";
    public static final String UNASSIGNED_SEQ = "unassigned_seq";

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
    public static final String RANGE_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvFuzzyType";
    public static final String CONFIDENCE_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvConfidenceType";
    public static final String PARAMETER_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvParameterType";
    public static final String CELL_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvCellType";
    public static final String TISSUE_OBJCLASS ="uk.ac.ebi.intact.model.CvTissue";
    public static final String FEATURE_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvFeatureType";
    public static final String PUBLICATION_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvPublicationStatus";
    public static final String LIFECYCLE_EVENT_OBJCLASS ="uk.ac.ebi.intact.model.CvLifecycleEvent";

    public static final String RELEASED_STATUS = "released";

    public static String synchronizeShortlabel(String currentLabel, Collection<String> exitingLabels, int maxLength){
        String nameInSync = currentLabel;
        String indexAsString = "";
        if (!exitingLabels.isEmpty()){
            IntegerComparator comparator = new IntegerComparator();
            SortedSet<Integer> existingIndexes = new TreeSet<Integer>(comparator);
            for (String exitingLabel : exitingLabels){
                existingIndexes.add(extractLastNumberInShortLabel(exitingLabel));
            }

            int freeIndex = 0;
            for (Integer existingLabel : existingIndexes){
                // index already exist, increment free index
                if (freeIndex==existingLabel){
                    freeIndex++;
                }
                // index does not exist, break the loop
                else{
                    break;
                }
            }
            indexAsString = freeIndex > 0 ? "-"+freeIndex : "";
            nameInSync = currentLabel+indexAsString;
        }
        // retruncate if necessary
        if (maxLength < nameInSync.length()){
            nameInSync = nameInSync.substring(0, Math.max(1, maxLength-(indexAsString.length())))
                    +indexAsString;
        }

        return nameInSync;
    }

    public static int extractLastNumberInShortLabel(String currentLabel) {
        if (currentLabel.contains("-")){
            String strSuffix = currentLabel.substring(currentLabel .lastIndexOf("-") + 1, currentLabel.length());
            Matcher matcher = IntactUtils.decimalPattern.matcher(strSuffix);

            if (matcher.matches()){
                return Integer.parseInt(matcher.group());
            }
        }
        return 0;
    }

    public static IntactCvTerm createLifecycleEvent(String name){
        return new IntactCvTerm(name, (String)null, (String)null, LIFECYCLE_EVENT_OBJCLASS);
    }

    public static IntactCvTerm createLifecycleStatus(String name){
        return new IntactCvTerm(name, (String)null, (String)null, PUBLICATION_STATUS_OBJCLASS);
    }

    public static IntactCvTerm createMIInteractorType(String name, String MI){
        return createIntactMITerm(name, MI, INTERACTOR_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createMIFeatureDetectionMethod(String name, String MI){
        return createIntactMITerm(name, MI, FEATURE_METHOD_OBJCLASS);
    }

    public static IntactCvTerm createMIParameterType(String name, String MI){
        return createIntactMITerm(name, MI, PARAMETER_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createMIConfidenceType(String name, String MI){
        return createIntactMITerm(name, MI, CONFIDENCE_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createMIRangeStatus(String name, String MI){
        return createIntactMITerm(name, MI, RANGE_STATUS_OBJCLASS);
    }

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
