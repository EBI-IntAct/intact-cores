package uk.ac.ebi.intact.jami.utils;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.ExperimentUtils;
import psidev.psi.mi.jami.utils.ParticipantUtils;
import psidev.psi.mi.jami.utils.comparator.IntegerComparator;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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
    public static final Pattern decimalPattern = Pattern.compile("(\\d+)");

    public static final String CV_LOCAL_SEQ = "cv_local_seq";
    public static final String UNASSIGNED_SEQ = "unassigned_seq";

    public static final String DATABASE_OBJCLASS="uk.ac.ebi.intact.model.CvDatabase";
    public static final String QUALIFIER_OBJCLASS="uk.ac.ebi.intact.model.CvXrefQualifier";
    public static final String TOPIC_OBJCLASS="uk.ac.ebi.intact.model.CvTopic";
    public static final String ALIAS_TYPE_OBJCLASS="uk.ac.ebi.intact.model.CvAliasType";
    public static final String UNIT_OBJCLASS ="uk.ac.ebi.intact.model.CvParameterUnit";
    public static final String FEATURE_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvFeatureType";
    public static final String EXPERIMENTAL_ROLE_OBJCLASS ="uk.ac.ebi.intact.model.CvExperimentalRole";
    public static final String BIOLOGICAL_ROLE_OBJCLASS ="uk.ac.ebi.intact.model.CvBiologicalRole";
    public static final String INTERACTION_DETECTION_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvInteraction";
    public static final String INTERACTION_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvInteractionType";
    public static final String PARTICIPANT_DETECTION_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvIdentification";
    public static final String PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS ="uk.ac.ebi.intact.model.CvExperimentalPreparation";
    public static final String INTERACTOR_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvInteractorType";
    public static final String RANGE_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvFuzzyType";
    public static final String CONFIDENCE_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvConfidenceType";
    public static final String PARAMETER_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvParameterType";
    public static final String CELL_TYPE_OBJCLASS ="uk.ac.ebi.intact.model.CvCellType";
    public static final String TISSUE_OBJCLASS ="uk.ac.ebi.intact.model.CvTissue";
    public static final String FEATURE_METHOD_OBJCLASS ="uk.ac.ebi.intact.model.CvFeatureIdentification";
    public static final String PUBLICATION_STATUS_OBJCLASS ="uk.ac.ebi.intact.model.CvPublicationStatus";
    public static final String LIFECYCLE_EVENT_OBJCLASS ="uk.ac.ebi.intact.model.CvLifecycleEvent";

    public static final Pattern EXPERIMENT_SYNCHRONIZED_LABEL_PATTERN = Pattern.compile("(\\w+)-((\\d{4})[a-z]*)-(\\d+)");
    public static final Pattern EXPERIMENT_LABEL_PATTERN = Pattern.compile("(\\w+)-(\\d{4})");

    public static String generateAutomaticInteractionEvidenceShortlabelFor(InteractionEvidence intactInteraction, int maxLength){
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
                secondAlphabetical = firstAlphabetical;
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

            // replace non ASCII characters
            return label1.substring(0, maxSize1 + rest1).replaceAll("[^\\x20-\\x7e]", "")
                    +(label2Size > 0 ? "-"+label2.substring(0, maxSize2 + rest2).replaceAll("[^\\x20-\\x7e]", "") : "");
        }

        return label1.replaceAll("[^\\x20-\\x7e]", "") + (label2 != null ? "-"+label2.replaceAll("[^\\x20-\\x7e]", "") : "-1");
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

    public static String generateAutomaticComplexShortlabelFor(Complex intactInteraction, int maxLength){
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

    public static void synchronizeExperimentShortLabel(IntactExperiment intactExperiment, EntityManager manager, Set<String> persistedNames){
        if (intactExperiment.getShortLabel() == null){
            return;
        }
        // then synchronize with database
        String name;
        Set<String> existingExperiments=Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();

        do{
            name = intactExperiment.getShortLabel().trim().toLowerCase();
            // we increments experiment label if matches simple automatically generated label
            if (EXPERIMENT_LABEL_PATTERN.matcher(name).matches()){
                name = name+"-1";
            }
            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select e.shortLabel from IntactExperiment e " +
                    "where (e.shortLabel = :name) "
                    + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
            query.setParameter("name", name);
            if (intactExperiment.getAc() != null){
                query.setParameter("expAc", intactExperiment.getAc());
            }
            existingExperiments = new HashSet<String>(query.getResultList());
            // check cached names
            if (persistedNames.contains(name)){
                existingExperiments.add(name);
                cachedLabels.add(name);
            }
            // if the experiment shortlabel is the same as another experiment shortlabel
            if (!existingExperiments.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before
                if (EXPERIMENT_SYNCHRONIZED_LABEL_PATTERN.matcher(name).matches()){
                    name = excludeLastNumberInShortLabel(name);
                }

                // check if short name already exist, if yes, synchronize with existing labels
                query = manager.createQuery("select e.shortLabel from IntactExperiment e " +
                        "where (e.shortLabel = :name or e.shortLabel like :nameWithSuffix) "
                        + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (intactExperiment.getAc() != null){
                    query.setParameter("expAc", intactExperiment.getAc());
                }
                existingExperiments.addAll(query.getResultList());
                // check cached names
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingExperiments.addAll(cachedLabels);

                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingExperiments, IntactUtils.MAX_SHORT_LABEL_LEN, true);
                if (!nameInSync.equals(name)){
                    intactExperiment.setShortLabel(nameInSync);
                }
            }
            else{
                intactExperiment.setShortLabel(name);
            }
        }
        while(!existingExperiments.isEmpty());
    }

    public static void synchronizeExperimentComplexShortLabel(IntactExperiment intactExperiment,
                                                              EntityManager manager,
                                                              Set<String> persistedNames){
        if (intactExperiment.getShortLabel() == null){
            return;
        }
        // then synchronize with database
        String name;
        Set<String> existingExperiments = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{
            name = intactExperiment.getShortLabel().trim().toLowerCase();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select e.shortLabel from IntactExperiment e " +
                    "where (e.shortLabel = :name) "
                    + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
            query.setParameter("name", name);
            if (intactExperiment.getAc() != null){
                query.setParameter("expAc", intactExperiment.getAc());
            }
            existingExperiments = new HashSet<String>(query.getResultList());
            // check cached names
            if (persistedNames.contains(name)){
                existingExperiments.add(name);
                cachedLabels.add(name);
            }
            // if the experiment shortlabel is the same as another experiment shortlabel
            if (!existingExperiments.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before
                if (EXPERIMENT_SYNCHRONIZED_LABEL_PATTERN.matcher(name).matches()
                        || name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                // check if short name already exist, if yes, synchronize with existing label
                query = manager.createQuery("select e.shortLabel from IntactExperiment e " +
                        "where (e.shortLabel = :name or e.shortLabel like :nameWithSuffix) "
                        + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (intactExperiment.getAc() != null){
                    query.setParameter("expAc", intactExperiment.getAc());
                }
                existingExperiments.addAll(query.getResultList());
                // check cached names
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingExperiments.addAll(cachedLabels);
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingExperiments, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactExperiment.setShortLabel(nameInSync);
                }
            }
            else{
                intactExperiment.setShortLabel(name);
            }
        }
        while(!existingExperiments.isEmpty());
    }

    public static void synchronizeInteractionEvidenceShortName(IntactInteractionEvidence intactInteraction, EntityManager manager, Set<String> persistedNames){
        if (intactInteraction.getShortName() == null){
            return;
        }
        String name = null;
        Set<String> existingInteractions = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{
            // then synchronize with database
            name = intactInteraction.getShortName().trim().toLowerCase();
            if (!name.matches(".*-\\d+$")){
                name = name+"-1";
            }

            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select i.shortName from IntactInteractionEvidence i " +
                    "where (i.shortName = :name) "
                    + (intactInteraction.getAc() != null ? "and i.ac <> :interAc" : ""));
            query.setParameter("name", name);
            if (intactInteraction.getAc() != null){
                query.setParameter("interAc", intactInteraction.getAc());
            }
            existingInteractions = new HashSet<String>(query.getResultList());
            // check also with interactors
            Query query2 = manager.createQuery("select i.shortName from IntactInteractor i " +
                    "where (i.shortName = :name) "
                    + (intactInteraction.getAc() != null ? "and i.ac <> :interAc" : ""));
            query2.setParameter("name", name);
            if (intactInteraction.getAc() != null){
                query2.setParameter("interAc", intactInteraction.getAc());
            }
            existingInteractions.addAll(query2.getResultList());
            // check cached names
            if (persistedNames.contains(name)){
                existingInteractions.add(name);
                cachedLabels.add(name);
            }

            if (!existingInteractions.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before (last -)
                if (name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                // check if short name already exist, if yes, synchronize with existing label
                query = manager.createQuery("select i.shortName from IntactInteractionEvidence i " +
                        "where (i.shortName = :name or i.shortName like :nameWithSuffix) "
                        + (intactInteraction.getAc() != null ? "and i.ac <> :interAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (intactInteraction.getAc() != null){
                    query.setParameter("interAc", intactInteraction.getAc());
                }
                existingInteractions.addAll(query.getResultList());
                // check also with interactors
                query2 = manager.createQuery("select i.shortName from IntactInteractor i " +
                        "where (i.shortName = :name or i.shortName like :nameWithSuffix) "
                        + (intactInteraction.getAc() != null ? "and i.ac <> :interAc" : ""));
                query2.setParameter("name", name);
                query2.setParameter("nameWithSuffix", name+"-%");
                if (intactInteraction.getAc() != null){
                    query2.setParameter("interAc", intactInteraction.getAc());
                }
                existingInteractions.addAll(query2.getResultList());
                // check cached names
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingInteractions.addAll(cachedLabels);
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingInteractions, IntactUtils.MAX_SHORT_LABEL_LEN, true);
                if (!nameInSync.equals(name)){
                    intactInteraction.setShortName(nameInSync);
                }
            }
            else{
                intactInteraction.setShortName(name);
            }
        }
        while(!existingInteractions.isEmpty());
    }

    public static void synchronizeInteractorShortName(IntactInteractor intactInteractor, EntityManager manager, Set<String> persistedNames){
        if (intactInteractor.getShortName() == null){
            return;
        }
        String name;
        Set<String> existingInteractors = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{
            name = intactInteractor.getShortName().trim().toLowerCase();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select i.shortName from IntactInteractor i " +
                    "where (i.shortName = :name) "
                    + (intactInteractor.getAc() != null ? "and i.ac <> :interactorAc" : ""));
            query.setParameter("name", name);
            if (intactInteractor.getAc() != null){
                query.setParameter("interactorAc", intactInteractor.getAc());
            }
            existingInteractors = new HashSet<String>(query.getResultList());
            // check cached names
            if (persistedNames.contains(name)){
                existingInteractors.add(name);
                cachedLabels.add(name);
            }

            if (!existingInteractors.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before (last -)
                if (name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                // check if short name already exist, if yes, synchronize with existing label
                query = manager.createQuery("select i.shortName from IntactInteractor i " +
                        "where (i.shortName = :name or i.shortName like :nameWithSuffix) "
                        + (intactInteractor.getAc() != null ? "and i.ac <> :interactorAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (intactInteractor.getAc() != null){
                    query.setParameter("interactorAc", intactInteractor.getAc());
                }
                existingInteractors.addAll(query.getResultList());

                // check cached names
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingInteractors.addAll(cachedLabels);

                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingInteractors, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactInteractor.setShortName(nameInSync);
                }
                else{
                    break;
                }
            }
            else{
                intactInteractor.setShortName(name);
            }
        }
        while(!existingInteractors.isEmpty());
    }

    public static void synchronizeCvTermShortName(IntactCvTerm intactCv,
                                                  EntityManager manager,
                                                  String objClass,
                                                  Set<String> persistedNames){
        if (intactCv.getShortName() == null){
            return;
        }
        String name = null;
        String originalName = null;
        Set<String> existingCvs = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{
            name = intactCv.getShortName().trim().toLowerCase();
            originalName = name;
            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select cv.shortName from IntactCvTerm cv " +
                    "where (cv.shortName = :name)"
                    + (objClass != null ? " and cv.objClass = :objclass " : " ")
                    + (intactCv.getAc() != null ? "and cv.ac <> :cvAc" : ""));
            query.setParameter("name", name);
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
            if (intactCv.getAc() != null){
                query.setParameter("cvAc", intactCv.getAc());
            }
            existingCvs = new HashSet<String>(query.getResultList());

            if (persistedNames.contains(name)){
                existingCvs.add(name);
                cachedLabels.add(name);
            }

            if (!existingCvs.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before (last -)
                if (name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                query = manager.createQuery("select cv.shortName from IntactCvTerm cv " +
                        "where (cv.shortName = :name or cv.shortName like :nameWithSuffix)"
                        + (objClass != null ? " and cv.objClass = :objclass " : " ")
                        + (intactCv.getAc() != null ? "and cv.ac <> :cvAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (objClass != null){
                    query.setParameter("objclass", objClass);
                }
                if (intactCv.getAc() != null){
                    query.setParameter("cvAc", intactCv.getAc());
                }
                existingCvs.addAll(query.getResultList());
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingCvs.addAll(cachedLabels);

                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingCvs, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactCv.setShortName(nameInSync);
                }

                // clear existing sources from original name and synchronized name
                existingCvs.remove(name);
            }
            else{
                intactCv.setShortName(name);
            }

            // clear existing sources from original name and synchronized name
            existingCvs.remove(originalName);
        }
        while(!existingCvs.isEmpty());
    }

    public static void synchronizeSourceShortName(IntactSource intactSource, EntityManager manager,
                                                  Set<String> persistedNames){
        if (intactSource.getShortName() == null){
            return;
        }
        String name = null;
        Set<String> existingSource = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{

            name = intactSource.getShortName().trim();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select s.shortName from IntactSource s " +
                    "where (s.shortName = :name) "
                    + (intactSource.getAc() != null ? "and s.ac <> :sourceAc" : ""));
            query.setParameter("name", name);
            if (intactSource.getAc() != null){
                query.setParameter("sourceAc", intactSource.getAc());
            }
            existingSource = new HashSet<String>(query.getResultList());
            if (persistedNames.contains(name)){
                existingSource.add(name);
                cachedLabels.add(name);
            }
            if (!existingSource.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before (last -)
                if (name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                // check if short name already exist, if yes, synchronize with existing label
                query = manager.createQuery("select s.shortName from IntactSource s " +
                        "where (s.shortName = :name or s.shortName like :nameWithSuffix) "
                        + (intactSource.getAc() != null ? "and s.ac <> :sourceAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                if (intactSource.getAc() != null){
                    query.setParameter("sourceAc", intactSource.getAc());
                }
                existingSource.addAll(query.getResultList());
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingSource.addAll(cachedLabels);
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingSource, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactSource.setShortName(nameInSync);
                }
            }
            else{
                intactSource.setShortName(name);
            }
        }
        while(!existingSource.isEmpty());
    }

    public static void synchronizeOrganismShortName(IntactOrganism intactOrganism, EntityManager manager,
                                                  Set<String> persistedNames){
        if (intactOrganism.getCommonName() == null){
            return;
        }
        String name = null;
        Set<String> existingSource = Collections.EMPTY_SET;
        Set<String> cachedLabels = new HashSet<String>();
        do{

            name = intactOrganism.getCommonName().trim();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = manager.createQuery("select o.commonName from IntactOrganism o " +
                    "where (o.commonName = :name) "
                    + (intactOrganism.getAc() != null ? "and o.ac <> :organismAc" : ""));
            query.setParameter("name", name);
            if (intactOrganism.getAc() != null){
                query.setParameter("organismAc", intactOrganism.getAc());
            }
            existingSource = new HashSet<String>(query.getResultList());
            if (persistedNames.contains(name)){
                existingSource.add(name);
                cachedLabels.add(name);
            }
            if (!existingSource.isEmpty()){
                // we have a synchronized label, so we need first to extract original label before (last -)
                if (name.matches(".*-\\d+$")){
                    name = excludeLastNumberInShortLabel(name);
                }
                // check if short name already exist, if yes, synchronize with existing label
                query = manager.createQuery("select o.commonName from IntactOrganism o " +
                        "where (o.commonName = :name or (o.commonName like :nameWithSuffix and o.commonName not like :nameWithCellTissue) ) "
                        + (intactOrganism.getAc() != null ? "and o.ac <> :organismAc" : ""));
                query.setParameter("name", name);
                query.setParameter("nameWithSuffix", name+"-%");
                query.setParameter("nameWithCellTissue", name+"-%-%");
                if (intactOrganism.getAc() != null){
                    query.setParameter("organismAc", intactOrganism.getAc());
                }
                existingSource.addAll(query.getResultList());
                if (persistedNames.contains(name)){
                    cachedLabels.add(name);
                }
                existingSource.addAll(cachedLabels);
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingSource, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactOrganism.setCommonName(nameInSync);
                }
            }
            else{
                intactOrganism.setCommonName(name);
            }
        }
        while(!existingSource.isEmpty());
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
            // replace all non non-ASCII characters
            label = pub.getAuthors().iterator().next().trim().toLowerCase().replaceAll("-", "_").replaceAll("[^\\x20-\\x7e]", "");
            if (label.contains(" ")){
                label = label.split(" ")[0];
            }
            finalLabel = label+"-"+yearString;
        }
        else if (!pub.getAuthors().isEmpty()){
            Calendar now = Calendar.getInstance();
            yearString = Integer.toString(now.get(Calendar.YEAR));
            label = pub.getAuthors().iterator().next().trim().toLowerCase().replaceAll("-", "_").replaceAll("[^\\x20-\\x7e]", "");
            if (label.contains(" ")){
                label = label.split(" ")[0];
            }
            finalLabel = label+"-"+yearString;
        }
        else if (pub.getPublicationDate() != null){
            label = "unknown";
            yearString = IntactUtils.YEAR_FORMAT.format(pub.getPublicationDate());
            finalLabel = "unknown-"+yearString;
        }
        else{
            label = "unknown";
            yearString = IntactUtils.YEAR_FORMAT.format(new Date());
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
            Iterator<String> iterator = exitingLabels.iterator();
            while (iterator.hasNext()){
                String exitingLabel = iterator.next();
                Integer suffix = extractLastNumberInShortLabel(exitingLabel, currentLabel);
                if (suffix != null){
                    existingIndexes.add(suffix);
                }
                // invalid shortlabel
                else{
                    iterator.remove();
                }
            }

            int freeIndex = alwaysAppendSuffix ? 1 : 0;
            for (Integer existingLabel : existingIndexes){
                // index already exist, increment free index
                if (freeIndex==existingLabel){
                    freeIndex++;
                }
                // even if the label without any suffix is available, we force to use a suffix here
                else if (existingLabel == 0 && alwaysAppendSuffix){
                    // we skip
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

    public static Integer extractLastNumberInShortLabel(String currentLabel, String prefixToIgnore) {
        String fixedLabel = prefixToIgnore != null ? currentLabel.substring(prefixToIgnore.length()) : currentLabel;
        if (fixedLabel.contains("-")){
            String strSuffix = fixedLabel.substring(fixedLabel .lastIndexOf("-") + 1, fixedLabel.length());
            Matcher matcher = IntactUtils.decimalPattern.matcher(strSuffix);

            if (matcher.matches()){
                return Integer.parseInt(matcher.group());
            }
            return null;
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

    public static IntactCvTerm createMIUnit(String name, String MI){
        return createIntactMITerm(name, MI, UNIT_OBJCLASS);
    }

    public static IntactCvTerm createMIExperimentalPreparation(String name, String MI){
        return createIntactMITerm(name, MI, PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS);
    }

    public static IntactCvTerm createLifecycleEvent(String name){
        return new IntactCvTerm(name, (String)null, (String)null, LIFECYCLE_EVENT_OBJCLASS);
    }

    public static IntactCvTerm createCellType(String name){
        return new IntactCvTerm(name, (String)null, (String)null, CELL_TYPE_OBJCLASS);
    }

    public static IntactCvTerm createTissue(String name){
        return new IntactCvTerm(name, (String)null, (String)null, TISSUE_OBJCLASS);
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
            CvTerm psimi = createPsiMiDatabase();
            return new IntactCvTerm(name, new CvTermXref(psimi, MI, createIdentityQualifier(psimi)), objclass);
        }
        else {
            return new IntactCvTerm(name, (String)null, (String)null, objclass);
        }
    }

    public static IntactCvTerm createIntactMODTerm(String name, String MOD, String objclass){
        if (MOD != null){
            return new IntactCvTerm(name, new CvTermXref(new IntactCvTerm(CvTerm.PSI_MOD, null, CvTerm.PSI_MOD_MI, DATABASE_OBJCLASS), MOD, createIdentityQualifier()), objclass);
        }
        else {
            return new IntactCvTerm(name, (String)null, (String)null, objclass);
        }
    }

    public static CvTerm createPsiMiDatabase(){
        CvTerm psiMi = new IntactCvTerm(CvTerm.PSI_MI, null, (String)null, DATABASE_OBJCLASS);
        Xref psiMiXref = new CvTermXref(psiMi, CvTerm.PSI_MI_MI, createIdentityQualifier(psiMi));
        psiMi.getIdentifiers().add(psiMiXref);
        return psiMi;
    }

    public static CvTerm createIdentityQualifier(){
        CvTerm identity = new IntactCvTerm(Xref.IDENTITY, null, (String)null, QUALIFIER_OBJCLASS);
        Xref psiMiXref = new CvTermXref(createPsiMiDatabase(identity), Xref.IDENTITY_MI, identity);
        identity.getIdentifiers().add(psiMiXref);
        return identity;
    }

    public static CvTerm createPsiMiDatabase(CvTerm identity){
        CvTerm psiMi = new IntactCvTerm(CvTerm.PSI_MI, null, (String)null, DATABASE_OBJCLASS);
        Xref psiMiXref = new CvTermXref(psiMi, CvTerm.PSI_MI_MI, identity);
        psiMi.getIdentifiers().add(psiMiXref);
        return psiMi;
    }

    public static CvTerm createIdentityQualifier(CvTerm psiMi){
        CvTerm identity = new IntactCvTerm(Xref.IDENTITY, null, (String)null, QUALIFIER_OBJCLASS);
        Xref psiMiXref = new CvTermXref(psiMi, Xref.IDENTITY_MI, identity);
        identity.getIdentifiers().add(psiMiXref);
        return identity;
    }

    public static void createAndAddDefaultExperimentForComplexes(IntactComplex intactComplex, String pubmed) {
        // create default experiment with publication unassigned for complexes
        IntactExperiment defaultExperiment = new IntactExperiment(new IntactPublication(pubmed));
        // inferred by curator
        defaultExperiment.setInteractionDetectionMethod(IntactUtils.createMIInteractionDetectionMethod(Experiment.INFERRED_BY_CURATOR, Experiment.INFERRED_BY_CURATOR_MI));
        // use host organism of interaction
        defaultExperiment.setHostOrganism(intactComplex.getOrganism());
        // use predetermined participant identification method
        defaultExperiment.setParticipantIdentificationMethod(IntactUtils.createMIParticipantIdentificationMethod(Participant.PREDETERMINED, Participant.PREDETERMINED_MI));
        // then add this complex
        intactComplex.getExperiments().add(defaultExperiment);
    }

    public static CvTerm extractMostCommonParticipantDetectionMethodFrom(Experiment exp){
        if (exp instanceof IntactExperiment){
            IntactExperiment intactExp = (IntactExperiment)exp;
            if (intactExp.getParticipantIdentificationMethod() != null){
                return intactExp.getParticipantIdentificationMethod();
            }
        }
        return ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(exp);

    }

    public static void initialiseCvTerm(IntactCvTerm cv){
        if (!cv.areXrefsInitialized()){
            // initialise xrefs
            Hibernate.initialize(cv.getDbXrefs());
        }
        if (!cv.areAnnotationsInitialized()){
            // initialise annotations
            Hibernate.initialize(cv.getDbAnnotations());
        }
        if (!cv.areSynonymsInitialized()){
            // initialise synonyms
            Hibernate.initialize(cv.getSynonyms());
        }
    }

    public static void initialiseSource(IntactSource cv){
        if (!cv.areXrefsInitialized()){
            // initialise xrefs
            Hibernate.initialize(cv.getDbXrefs());
        }
        if (!cv.areAnnotationsInitialized()){
            // initialise annotations
            Hibernate.initialize(cv.getDbAnnotations());
        }
        if (!cv.areSynonymsInitialized()){
            // initialise synonyms
            Hibernate.initialize(cv.getSynonyms());
        }
    }

    public static void initialiseOntologyTerm(IntactCvTerm cv){
        initialiseCvTerm(cv);
        for (CvTerm child : cv.getChildren()){
            initialiseOntologyTerm((IntactCvTerm) child);
        }
        for (CvTerm parent : cv.getParents()){
            initialiseOntologyTerm((IntactCvTerm)parent);
        }
    }

    public static void initialiseAlias(AbstractIntactAlias alias){
        if (alias.getType() != null){
            // initialise type
            initialiseCvTerm((IntactCvTerm)alias.getType());
        }
    }

    public static void initialiseXref(AbstractIntactXref xref){
        // initialise db
        initialiseCvTerm((IntactCvTerm)xref.getDatabase());
        if (xref.getQualifier() != null){
            // initialise qualifier
            initialiseCvTerm((IntactCvTerm)xref.getQualifier());
        }
        if (xref instanceof ComplexGOXref){
            ComplexGOXref goRef = (ComplexGOXref)xref;
            if (goRef.getEvidenceType() != null){
                // initialise evidence type
                initialiseCvTerm((IntactCvTerm)goRef.getEvidenceType());
            }
        }
    }

    public static void initialiseAnnotation(AbstractIntactAnnotation annotation){
        // initialise topic
        initialiseCvTerm((IntactCvTerm)annotation.getTopic());
    }

    public static void initialiseParameter(AbstractIntactParameter param){
        // initialise type
        initialiseCvTerm((IntactCvTerm)param.getType());
        if (param.getUnit() != null){
            // initialise unit
            initialiseCvTerm((IntactCvTerm)param.getUnit());
        }
    }

    public static void initialiseConfidence(AbstractIntactConfidence conf){
        // initialise type
        initialiseCvTerm((IntactCvTerm)conf.getType());
    }

    public static void initialiseVariableParameterValueSet(IntactVariableParameterValueSet set){
        if (!set.areVariableParameterValuesInitialized()){
            Hibernate.initialize(set);
        }
    }

    public static void initialiseVariableParameter(IntactVariableParameter param){
        if (param.getUnit() != null){
            // initialise unit
            initialiseCvTerm((IntactCvTerm) param.getUnit());
        }
        // initialise values
        if (param.areVariableParameterValuesInitialized()){
            Hibernate.initialize(param.getVariableValues());
        }
    }

    public static void initialiseCausalRelationship(AbstractIntactCausalRelationship rel){
        // initialise relation type
        initialiseCvTerm((IntactCvTerm)rel.getRelationType());
        // initialise target
        if (rel.getTarget() instanceof ParticipantEvidence){
            initialiseParticipantEvidence((IntactParticipantEvidence)rel.getTarget());
        }
        else if (rel.getTarget() instanceof ModelledParticipant){
            initialiseModelledParticipant((IntactModelledParticipant) rel.getTarget());
        }
    }

    public static void initialiseCooperativityEvidence(IntactCooperativityEvidence evidence){
        // initialise publication
        initialisePublication((IntactPublication)evidence.getPublication());
        // initialise evidence
        for (CvTerm cv : evidence.getEvidenceMethods()){
            initialiseCvTerm((IntactCvTerm)cv);
        }
    }

    public static void initialiseCooperativeEffect(AbstractIntactCooperativeEffect cooperativeEffect){
        // initialise outcome
        initialiseCvTerm((IntactCvTerm)cooperativeEffect.getOutCome());
        // initialise response
        if (cooperativeEffect.getResponse() != null){
            initialiseCvTerm((IntactCvTerm)cooperativeEffect.getResponse());
        }
        // initialise annotations
        for (Annotation annot : cooperativeEffect.getAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)annot);
        }
        // initialise cooperativity evidences
        for (CooperativityEvidence ev : cooperativeEffect.getCooperativityEvidences()){
            initialiseCooperativityEvidence((IntactCooperativityEvidence) ev);
        }
        // initialise affected interaction
        for (ModelledInteraction inter : cooperativeEffect.getAffectedInteractions()){
            initialiseComplex((IntactComplex) inter);
        }
    }

    public static void initialiseAllostery(AbstractIntactAllostery allostery){
        initialiseCooperativeEffect(allostery);

        // initialise molecule
        initialiseModelledParticipant((IntactModelledParticipant)allostery.getAllostericMolecule());
        // initialise type
        if (allostery.getAllosteryType() != null){
            initialiseCvTerm((IntactCvTerm)allostery.getAllosteryType());
        }
        // initialise mechanism
        if (allostery.getAllostericMechanism() != null){
            initialiseCvTerm((IntactCvTerm)allostery.getAllostericMechanism());
        }
        // initialise effector
        switch (allostery.getAllostericEffector().getEffectorType()){
            case molecule:
                initialiseModelledParticipant((IntactModelledParticipant) ((IntactMoleculeEffector) allostery.getAllostericEffector()).getMolecule());
                break;
            case feature_modification:
                initialiseModelledFeature((IntactModelledFeature) ((IntactFeatureModificationEffector) allostery.getAllostericEffector()).getFeatureModification());
                break;
        }
    }

    public static void initialiseResultingSequence(AbstractIntactResultingSequence sequence){
        // initialise xrefs
        for (Xref ref : sequence.getXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
    }

    public static void initialisePosition(IntactPosition pos){
        // initialise status
        initialiseCvTerm((IntactCvTerm)pos.getStatus());
    }

    public static void initialiseRange(AbstractIntactRange range){
        // initialise start
        initialisePosition((IntactPosition) range.getStart());
        // initialise end
        initialisePosition((IntactPosition)range.getEnd());
        if (range.getResultingSequence() != null){
            // initialise resulting sequence
            initialiseResultingSequence((AbstractIntactResultingSequence)range.getResultingSequence());
        }
        // initialise target
        if (range.getParticipant() instanceof ParticipantEvidence){
            initialiseParticipantEvidence((IntactParticipantEvidence)range.getParticipant());
        }
        else if (range.getParticipant() instanceof ModelledParticipant){
            initialiseModelledParticipant((IntactModelledParticipant)range.getParticipant());
        }
    }
    public static void initialiseFeature(AbstractIntactFeature feature){
        if (feature.getType() != null){
            // initialise type
            initialiseCvTerm((IntactCvTerm)feature.getType());
        }
        // initialise role
        if (feature.getRole() != null){
            initialiseCvTerm((IntactCvTerm)feature.getRole());
        }
        // initialise xrefs
        for (Object ref : feature.getDbXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : feature.getAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise aliases
        for (Object ref : feature.getAliases()){
            initialiseAlias((AbstractIntactAlias)ref);
        }
        // initialise ranges
        for (Object ref : feature.getRanges()){
            initialiseRange((AbstractIntactRange)ref);
        }
    }

    public static void initialiseModelledFeature(IntactModelledFeature feature){
        initialiseFeature(feature);
        // initialise linked features
        for (ModelledFeature feat : feature.getLinkedFeatures()){
            initialiseModelledFeature((IntactModelledFeature)feat);
        }
    }

    public static void initialiseFeatureEvidence(IntactFeatureEvidence feature){
        initialiseFeature(feature);
        // initialise linked features
        for (FeatureEvidence feat : feature.getLinkedFeatures()){
            initialiseFeatureEvidence((IntactFeatureEvidence) feat);
        }
        // initialise db method
        for (CvTerm cv : feature.getDetectionMethods()){
            initialiseCvTerm((IntactCvTerm)cv);
        }
    }

    public static void initialiseParticipant(AbstractIntactParticipant participant){
        // initialise interactor
        initialiseInteractor((IntactInteractor)participant.getInteractor());
        // initialise bio role
        initialiseCvTerm((IntactCvTerm)participant.getBiologicalRole());

        // initialise xrefs
        for (Object ref : participant.getXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : participant.getAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise aliases
        for (Object ref : participant.getAliases()){
            initialiseAlias((AbstractIntactAlias)ref);
        }
        // initialise causal relationships
        for (Object ref : participant.getCausalRelationships()){
            initialiseCausalRelationship((AbstractIntactCausalRelationship) ref);
        }
    }

    public static void initialiseModelledParticipant(IntactModelledParticipant participant){
        initialiseParticipant(participant);

        // initialise features
        for (ModelledFeature feature : participant.getFeatures()){
            initialiseModelledFeature((IntactModelledFeature)feature);
        }
    }

    public static void initialiseParticipantEvidence(IntactParticipantEvidence participant){
        initialiseParticipant(participant);

        // initialise features
        for (FeatureEvidence feature : participant.getFeatures()){
            initialiseFeatureEvidence((IntactFeatureEvidence) feature);
        }

        // initialise exp role
        initialiseCvTerm((IntactCvTerm)participant.getExperimentalRole());
        // initialise host organism
        if (participant.getExpressedInOrganism() != null){
            initialiseOrganism((IntactOrganism)participant.getExpressedInOrganism());
        }
        // initialise exp preparations
        for (CvTerm cv : participant.getExperimentalPreparations()){
            initialiseCvTerm((IntactCvTerm)cv);
        }
        // initialise identification methods
        for (CvTerm cv : participant.getIdentificationMethods()){
            initialiseCvTerm((IntactCvTerm)cv);
        }
        // initialise confidences
        for (Confidence conf : participant.getConfidences()){
            initialiseConfidence((AbstractIntactConfidence) conf);
        }
        // initialise parameters
        for (Parameter param : participant.getParameters()){
            initialiseParameter((AbstractIntactParameter) param);
        }
    }

    public static void initialisePublication(IntactPublication publication){
        // initialise xrefs
        for (Object ref : publication.getDbXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : publication.getDbAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise source
        if (publication.getSource() != null){
            initialiseSource((IntactSource)publication.getSource());
        }
        // initialise experiment
        for (Experiment exp : publication.getExperiments()){
            initialiseExperiment((IntactExperiment)exp, false);
        }
    }

    public static void initialiseExperiment(IntactExperiment experiment, boolean initPublication){
        if (initPublication && experiment.getPublication() != null){
            // initialise publication
            initialisePublication((IntactPublication)experiment.getPublication());
        }
        // initialise xrefs
        for (Object ref : experiment.getXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : experiment.getAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise detection method
        initialiseCvTerm((IntactCvTerm)experiment.getInteractionDetectionMethod());
        // initialise host organism
        if (experiment.getHostOrganism() != null){
            initialiseOrganism((IntactOrganism)experiment.getHostOrganism());
        }
        // initialise participant method
        if (experiment.getParticipantIdentificationMethod() != null){
            initialiseCvTerm((IntactCvTerm)experiment.getParticipantIdentificationMethod());
        }
        // initialise variable parameters
        for (VariableParameter param : experiment.getVariableParameters()){
            initialiseVariableParameter((IntactVariableParameter)param);
        }
        // initialise interaction evidences
        for (InteractionEvidence ev : experiment.getInteractionEvidences()){
            initialiseInteractionEvidence((IntactInteractionEvidence)ev, false);
        }
    }

    public static void initialiseOrganism(IntactOrganism organism){
        // initialise aliases
        for (Object ref : organism.getAliases()){
            initialiseAlias((AbstractIntactAlias)ref);
        }

        // init cell type
        if (organism.getCellType() != null){
            initialiseCvTerm((IntactCvTerm)organism.getCellType());
        }
        // init tissue
        if (organism.getTissue() != null){
            initialiseCvTerm((IntactCvTerm)organism.getTissue());
        }
    }

    public static void initialiseInteractor(IntactInteractor interactor){
        // initialise interactor type
        initialiseCvTerm((IntactCvTerm)interactor.getInteractorType());

        // initialise xrefs
        for (Object ref : interactor.getDbXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : interactor.getDbAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise aliases
        for (Object ref : interactor.getDbAliases()){
            initialiseAlias((AbstractIntactAlias)ref);
        }
        // initialise organism
        if (interactor.getOrganism() != null){
            initialiseOrganism((IntactOrganism)interactor.getOrganism());
        }

        // special cases
        if (interactor instanceof IntactPolymer){
            IntactPolymer polymer = (IntactPolymer)interactor;
            polymer.getSequence();
        }
        else if (interactor instanceof IntactInteractorPool){
            IntactInteractorPool pool = (IntactInteractorPool)interactor;
            for (Interactor inter : pool){
                initialiseInteractor((IntactInteractor)inter);
            }
        }
        else if (interactor instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)interactor;
            // init source
            if (complex.getSource() != null){
                initialiseSource((IntactSource)complex.getSource());
            }
            // init participants
            for (ModelledParticipant participant : complex.getParticipants()){
                initialiseModelledParticipant((IntactModelledParticipant)participant);
            }
            // init confidences
            for (ModelledConfidence conf : complex.getModelledConfidences()){
                initialiseConfidence((AbstractIntactConfidence)conf);
            }
            // init parameters
            for (ModelledParameter param : complex.getModelledParameters()){
                initialiseParameter((AbstractIntactParameter)param);
            }
            // init interaction type
            if (complex.getInteractionType() != null){
                initialiseCvTerm((IntactCvTerm)complex.getInteractionType());
            }
            // init evidence type
            if (complex.getEvidenceType() != null){
                initialiseCvTerm((IntactCvTerm)complex.getEvidenceType());
            }
        }
    }

    public static void initialiseInteractionEvidence(IntactInteractionEvidence interaction, boolean initExperiment){
        // initialise xrefs
        for (Object ref : interaction.getDbXrefs()){
            initialiseXref((AbstractIntactXref)ref);
        }
        // initialise annotations
        for (Object ref : interaction.getDbAnnotations()){
            initialiseAnnotation((AbstractIntactAnnotation)ref);
        }
        // initialise experiment
        if (initExperiment && interaction.getExperiment() != null){
            initialiseExperiment((IntactExperiment)interaction.getExperiment(), true);
        }
        // init participants
        for (ParticipantEvidence participant : interaction.getParticipants()){
            initialiseParticipantEvidence((IntactParticipantEvidence)participant);
        }
        // init confidences
        for (Confidence conf : interaction.getConfidences()){
            initialiseConfidence((AbstractIntactConfidence)conf);
        }
        // init parameters
        for (Parameter param : interaction.getParameters()){
            initialiseParameter((AbstractIntactParameter)param);
        }
        // init interaction type
        if (interaction.getInteractionType() != null){
            initialiseCvTerm((IntactCvTerm)interaction.getInteractionType());
        }
        // init evidence type
        for (VariableParameterValueSet set : interaction.getVariableParameterValues()){
            initialiseVariableParameterValueSet((IntactVariableParameterValueSet)set);
        }
    }

    public static void initialiseComplex(IntactComplex complex){
        initialiseInteractor(complex);
    }
}
