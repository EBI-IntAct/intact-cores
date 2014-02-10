package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.ParticipantUtils;
import psidev.psi.mi.jami.utils.comparator.IntegerComparator;
import uk.ac.ebi.intact.jami.model.extension.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static final String INTERACTION_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvInteractionType";
    public static final String PARTICIPANT_DETECTION_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvIdentification";
    public static final String INTERACTOR_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvInteractorType";
    public static final String RANGE_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvFuzzyType";
    public static final String CONFIDENCE_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvConfidenceType";
    public static final String PARAMETER_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvParameterType";
    public static final String CELL_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvCellType";
    public static final String TISSUE_OBJCLASS ="uk.ac.ebi.intact.model.CvTissue";
    public static final String FEATURE_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvFeatureIdentification";
    public static final String PUBLICATION_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvPublicationStatus";
    public static final String LIFECYCLE_EVENT_OBJCLASS ="uk.ac.ebi.intact.model.CvLifecycleEvent";

    public static final String RELEASED_STATUS = "released";

    public static String generateAutomaticInteractionEvidenceShortlabelFor(IntactInteractionEvidence intactInteraction, int maxLength){
        if (intactInteraction.getParticipants().isEmpty()){
            return "unknown";
        }
        String label1=null;
        String label2=null;
        String alternateLabel=null;
        String firstAlphabetical=null;
        String secondAlphabetical=null;

        // collect different names from the collection of participants
        // bait -> alternative bait -> prey -> first alphabetical -> second alphabetical
        for (ParticipantEvidence participant : intactInteraction.getParticipants()){
            // extract participant name (gene name or shortlabel if no gene names)
            Alias geneName = AliasUtils.collectFirstAliasWithType(participant.getInteractor().getAliases(), Alias.GENE_NAME_MI, Alias.GENE_NAME);
            String name = (geneName != null ? geneName.getName() : participant.getInteractor().getShortName()).trim().toLowerCase().replaceAll("-", "_"); 
            // bait should be first label
            if (ParticipantUtils.doesParticipantHaveExperimentalRole(participant, Participant.BAIT_ROLE_MI, Participant.BAIT_ROLE_MI)){
                if (label1 == null){
                    label1 = name;
                }
                else if (name.compareTo(label1) < 0){
                    label1 = name;
                }
            }
            // alternative baits
            else if (ParticipantUtils.isParticipantEvidenceAnAlternativeBaitForSpokeExpansion(participant)){
                if (alternateLabel == null){
                    alternateLabel = name;
                }
                else if (name.compareTo(alternateLabel) < 0){
                    alternateLabel = name;
                }
            }
            // preys should be label2
            else if (ParticipantUtils.doesParticipantHaveExperimentalRole(participant, Participant.PREY_MI, Participant.PREY)){
                if (label2 == null){
                    label2 = name;
                }
                else if (name.compareTo(label2) < 0){
                    label2 = name;
                }
            }
            else if (firstAlphabetical == null){
                 firstAlphabetical = name;
            }
            else if (name.compareTo(firstAlphabetical) < 0){
                firstAlphabetical = name;
            }
            else if (secondAlphabetical == null){
                secondAlphabetical = name;
            }
            else if (name.compareTo(secondAlphabetical) < 0){
                secondAlphabetical = name;
            }
        }

        // set label 1 from existing names if not set : label1 is bait or alternative bait or prey or first alphabetical
        if (label1 == null){
            if (alternateLabel != null){
                label1 = alternateLabel;
            }
            else if (label2 != null){
                label1 = label2;
                // reset label2
                label2 = null;
            }
            else {
                label1 = firstAlphabetical;
            }
        }
        // set label 2 from existing names if not set : label2 is prey or second alphabetical or null if no other names
        if (label2 == null){
            if (!label1.equals(firstAlphabetical) && firstAlphabetical != null){
                label2 = firstAlphabetical;
            }
            else if (secondAlphabetical != null){
                label2 = secondAlphabetical;
            }
        }
        // retruncate if necessary (label1 + label2 + 1 (for the '-' between label1 and label2) > maxSize)
        if (maxLength < label1.length() + (label2 == null ? 0 : 1 + label2.length())){
            int label1Size = label1.length();
            int label2Size = label2 != null ? label2.length() : 0;
            int maxSize1 = Math.min(label1Size, maxLength / 2 - 1);
            int maxSize2 = Math.min(label2Size, maxLength/2 - 1);
            int remainingSize = (maxLength - 1) - (maxSize1 + maxSize2);

            int rest1 = Math.min(remainingSize, Math.max(0, label1Size - (maxSize1+maxSize2)));
            int rest2 = Math.min(Math.max(0, remainingSize-rest1), Math.max(0, label2Size - (maxSize1+maxSize2)));

            return label1.substring(0, maxSize1 + rest1)
                    +(label2Size > 0 ? "-"+label2.substring(0, maxSize2 + rest2) : "");
        }

        return label1 + (label2 != null ? "-"+label2 : "");
    }

    public static String generateAutomaticShortlabelForModelledInteraction(ModelledInteraction intactInteraction, int maxLength){
        if (intactInteraction.getParticipants().isEmpty()){
            String unknownLabel = "unknown";
            // retruncate if necessary
            if (maxLength < unknownLabel.length()){
                return unknownLabel.substring(0, maxLength);
            }
            else{
                return unknownLabel;
            }
        }
        String label1=null;
        String firstAlphabetical=null;

        // collect different names from the collection of participants
        // alternative bait -> first alphabetical
        for (ModelledParticipant participant : intactInteraction.getParticipants()){
            // extract participant name (gene name or shortlabel if no gene names)
            Alias geneName = AliasUtils.collectFirstAliasWithType(participant.getInteractor().getAliases(), Alias.GENE_NAME_MI, Alias.GENE_NAME);
            String name = (geneName != null ? geneName.getName() : participant.getInteractor().getShortName()).trim().toLowerCase().replaceAll("-", "_");
            // alternative baits
            if (ParticipantUtils.isParticipantAnAlternativeBaitForSpokeExpansion(participant)){
                if (label1 == null){
                    label1 = name;
                }
                else if (name.compareTo(label1) < 0){
                    label1 = name;
                }
            }
            else if (firstAlphabetical == null){
                firstAlphabetical = name;
            }
            else if (name.compareTo(firstAlphabetical) < 0){
                firstAlphabetical = name;
            }
        }

        // set label 1 from existing names if not set : label1 is bait or alternative bait or prey or first alphabetical
        if (label1 == null){
            label1 = firstAlphabetical;
        }

        // retruncate if necessary
        if (maxLength < label1.length()){
            return label1.substring(0, maxLength);
        }
        return label1;
    }

    public static String generateAutomaticComplexShortlabelFor(IntactComplex intactInteraction, int maxLength){
        String organismName = null;
        if (intactInteraction.getOrganism() != null){
            organismName=(intactInteraction.getOrganism().getCommonName() != null ? intactInteraction.getOrganism().getCommonName().trim().toLowerCase() : Integer.toString(intactInteraction.getOrganism().getTaxId()));
        }
        if (intactInteraction.getParticipants().isEmpty()){
            String unknownLabel = "unknown"+(organismName != null ? "_"+organismName : "");
            // retruncate if necessary
            if (maxLength < unknownLabel.length()){
                return unknownLabel.substring(0, maxLength);
            }
            else{
                return unknownLabel;
            }
        }
        String label1=null;
        String firstAlphabetical=null;

        // collect different names from the collection of participants
        // alternative bait -> first alphabetical
        for (ModelledParticipant participant : intactInteraction.getParticipants()){
            // extract participant name (gene name or shortlabel if no gene names)
            Alias geneName = AliasUtils.collectFirstAliasWithType(participant.getInteractor().getAliases(), Alias.GENE_NAME_MI, Alias.GENE_NAME);
            String name = (geneName != null ? geneName.getName() : participant.getInteractor().getShortName()).trim().toLowerCase().replaceAll("-", "_");
            // alternative baits
            if (ParticipantUtils.isParticipantAnAlternativeBaitForSpokeExpansion(participant)){
                if (label1 == null){
                    label1 = name;
                }
                else if (name.compareTo(label1) < 0){
                    label1 = name;
                }
            }
            else if (firstAlphabetical == null){
                firstAlphabetical = name;
            }
            else if (name.compareTo(firstAlphabetical) < 0){
                firstAlphabetical = name;
            }
        }

        // set label 1 from existing names if not set : label1 is bait or alternative bait or prey or first alphabetical
        if (label1 == null){
            label1 = firstAlphabetical;
        }

        // retruncate if necessary
        if (maxLength < label1.length()+(organismName != null ? organismName.length()+1 : 0)){
            return label1+(organismName != null ? "_"+organismName : "").substring(0, maxLength);
        }
        return label1+(organismName != null ? "_"+organismName : "");
    }

    public static String generateAutomaticExperimentShortlabelFor(IntactExperiment intactExperiment, int maxLength){
        String label = null;
        String yearString = null;
        String finalLabel = null;

        Publication pub = intactExperiment.getPublication();
        if (pub == null){
            Calendar now = Calendar.getInstance();
            yearString = Integer.toString(now.get(Calendar.YEAR));
            label = "unknown";
            finalLabel = "unknown-"+yearString;
        }
        else if (!pub.getAuthors().isEmpty() && pub.getPublicationDate() != null){
            yearString = IntactUtils.YEAR_FORMAT.format(pub.getPublicationDate());
            label = pub.getAuthors().iterator().next().trim().toLowerCase().replaceAll("-", "_");
            finalLabel = label+"-"+yearString;
        }
        else if (!pub.getAuthors().isEmpty()){
            Calendar now = Calendar.getInstance();
            yearString = Integer.toString(now.get(Calendar.YEAR));
            label = pub.getAuthors().iterator().next().trim().toLowerCase().replaceAll("-", "_");
            finalLabel = label+"-"+yearString;
        }
        else{
            label = "unknown";
            yearString = IntactUtils.YEAR_FORMAT.format(pub.getPublicationDate());
            finalLabel = "unknown-"+yearString;
        }

        // retruncate if necessary
        if (maxLength < label.length()){
            finalLabel = label.substring(0, Math.max(1, maxLength-(yearString.length()+1)))
                    +"-"+yearString;
        }

        return finalLabel;
    }

    public static String synchronizeShortlabel(String currentLabel, Collection<String> exitingLabels, int maxLength, boolean alwaysAppendSuffix){
        String nameInSync = currentLabel;
        String indexAsString = "";
        if (!exitingLabels.isEmpty()){
            IntegerComparator comparator = new IntegerComparator();
            SortedSet<Integer> existingIndexes = new TreeSet<Integer>(comparator);
            for (String exitingLabel : exitingLabels){
                existingIndexes.add(extractLastNumberInShortLabel(exitingLabel));
            }

            int freeIndex = alwaysAppendSuffix ? 1 : 0;
            for (Integer existingLabel : existingIndexes){
                // index already exist, increment free index
                if (freeIndex==existingLabel){
                    freeIndex++;
                }
                // even if the label without any suffix is available, we force to use a suffix here
                else if (existingLabel == 0 && alwaysAppendSuffix){
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

    public static String excludeLastNumberInShortLabel(String currentLabel) {
        if (currentLabel.contains("-")){
            int index = currentLabel .lastIndexOf("-");
            String strSuffix = currentLabel.substring(index + 1, currentLabel.length());
            Matcher matcher = IntactUtils.decimalPattern.matcher(strSuffix);

            if (matcher.matches()){
                return currentLabel.substring(0, index);
            }
        }
        return currentLabel;
    }

    public static IntactCvTerm createLifecycleEvent(String name){
        return new IntactCvTerm(name, (String)null, (String)null, LIFECYCLE_EVENT_OBJCLASS);
    }

    public static IntactCvTerm createLifecycleStatus(String name){
        return new IntactCvTerm(name, (String)null, (String)null, PUBLICATION_STATUS_OBJCLASS);
    }

    public static IntactCvTerm createMIInteractionType(String name, String MI){
        return createIntactMITerm(name, MI, INTERACTION_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createMIParticipantIdentificationMethod(String name, String MI){
        return createIntactMITerm(name, MI, PARTICIPANT_DETECTION_METHOD_OBJCLASS);
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

    public static IntactCvTerm createMODFeatureType(String name, String MOD){
        return createIntactMODTerm(name, MOD, FEATURE_TYPE_OBJCLASS);
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

    public static IntactCvTerm createIntactMODTerm(String name, String MOD, String objclass){
        if (MOD != null){
            return new IntactCvTerm(name, new CvTermXref(new IntactCvTerm(CvTerm.PSI_MOD, null, CvTerm.PSI_MOD_MI, DATABASE_OBJCLASS), MOD, new IntactCvTerm(Xref.IDENTITY, null, Xref.IDENTITY_MI, QUALIFIER_OBJCLASS)), objclass);
        }
        else {
            return new IntactCvTerm(name, (String)null, (String)null, objclass);
        }
    }
}
