package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Range;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains utility methods to check on features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2010</pre>
 */

public class FeatureUtils {

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein features containing bad ranges (overlapping or out of bound)
     */
    public static Set<Feature> getFeaturesWithBadRanges(Protein protein){

        Set<Feature> badFeatures = getFeaturesWithOutOfBoundRanges(protein);
        badFeatures.addAll(getFeaturesWithOverlappingRanges(protein));

        return badFeatures;
    }

    /**
     *
     * @param protein  : the protein to check
     * @return a set of the protein features containing overlapping ranges.
     */
    public static Set<Feature> getFeaturesWithOverlappingRanges(Protein protein){

        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isOverlappingRange(range)){
                        badFeatures.add(feature);
                        break;
                    }
                }
            }
        }

        return badFeatures;
    }

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein features containing out of bound ranges
     */
    public static Set<Feature> getFeaturesWithOutOfBoundRanges(Protein protein){
        String sequence = protein.getSequence();

        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){

                    if (!isRangeWithinSequence(range, sequence)){
                        badFeatures.add(feature);
                        break;
                    }
                }
            }
        }

        return badFeatures;
    }

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein feature ranges which are overlapping or out of bound
     */
    public static Set<Range> getBadRanges(Protein protein){

        Set<Range> badRanges = getOutOfBoundRanges(protein);
        badRanges.addAll(getOverlappingRanges(protein));

        return badRanges;
    }

    /**
     *
     * @param protein  : the protein to check
     * @return a set of the protein feature ranges containing overlapping ranges.
     */
    public static Set<Range> getOverlappingRanges(Protein protein){

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isOverlappingRange(range)){
                        badRanges.add(range);
                        break;
                    }
                }
            }
        }

        return badRanges;
    }

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein feature out of bound ranges
     */
    public static Set<Range> getOutOfBoundRanges(Protein protein){
        String sequence = protein.getSequence();

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){

                    if (!isRangeWithinSequence(range, sequence)){
                        badRanges.add(range);
                    }
                }
            }
        }
        return badRanges;
    }

    /**
     *
     * @param range
     * @return true if the range is overlapping (start > end)
     */
    public static boolean isOverlappingRange(Range range){

        if (range == null){
            return false;
        }

        if (range.getFromIntervalStart() > range.getFromIntervalEnd() || range.getToIntervalStart() > range.getToIntervalEnd() || range.getFromIntervalStart() > range.getToIntervalStart() || range.getFromIntervalEnd() > range.getToIntervalEnd()){
            return true;
        }

        return false;
    }

    /**
     *
     * @param range : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence
     */
    public static boolean isRangeWithinSequence(Range range, String sequence){

        if (sequence == null){
            return true;
        }
        else if (range == null){
            return false;
        }

        int sequenceLength = sequence.length();
        if (range.getFromIntervalEnd() > sequenceLength || range.getToIntervalEnd() > sequenceLength || range.getFromIntervalStart() > sequenceLength || range.getToIntervalStart() > sequenceLength){
            return false;
        }
        else if (range.getFromIntervalEnd() < 0 || range.getToIntervalEnd() < 0 || range.getFromIntervalStart() < 0 || range.getToIntervalStart() < 0){
            return false;
        }

        return true;
    }

    /**
     *
     * @param range
     * @return true if the Cvfuzzy types are coherent with each other : a N, C terminal and undetermined start fuzzy type should always
     * be associated with the same end fuzzy type.
     */
    public static boolean areFrom_ToCvFuzzyTypeConsistent(Range range){

        if (range.getToCvFuzzyType() != null && range.getFromCvFuzzyType() != null){
            if ((range.getToCvFuzzyType().isCTerminal() || range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined()) && !range.getToCvFuzzyType().equals(range.getFromCvFuzzyType())){
                return false;
            }
            else if ((range.getFromCvFuzzyType().isCTerminal() || range.getFromCvFuzzyType().isNTerminal() || range.getFromCvFuzzyType().isUndetermined()) && !range.getToCvFuzzyType().equals(range.getFromCvFuzzyType())){
                return false;
            }
        }
        else if (range.getToCvFuzzyType() != null && range.getFromCvFuzzyType() == null){
            if (range.getToCvFuzzyType().isCTerminal() || range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined()){
                return false;
            }
        }
        else if (range.getFromCvFuzzyType() != null && range.getToCvFuzzyType() == null){
            if (range.getFromCvFuzzyType().isCTerminal() || range.getFromCvFuzzyType().isNTerminal() || range.getFromCvFuzzyType().isUndetermined()){
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param range : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static boolean isABadRange(Range range, String sequence){

        if (sequence == null){
            return false;
        }
        else if (range == null){
            return true;
        }

        boolean isRangeWithinSequence = isRangeWithinSequence(range, sequence);

        if (!isOverlappingRange(range)){
            if (areFrom_ToCvFuzzyTypeConsistent(range)){
                if (range.getToCvFuzzyType() != null) {
                    if (range.getToCvFuzzyType().isCTerminal() || range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined()){
                        if (range.getToIntervalStart() != 0 || range.getToIntervalEnd() != 0){
                            return true;
                        }
                    }
                    else if (range.getToCvFuzzyType().isCertain() || range.getToCvFuzzyType().isRange() || range.getToCvFuzzyType().isLessThan() || range.getToCvFuzzyType().isGreaterThan()){
                        if (range.getToIntervalStart() == 0 || range.getToIntervalEnd() == 0 || !isRangeWithinSequence){
                            return true;
                        }
                    }
                }
                if (range.getFromCvFuzzyType() != null) {
                    if (range.getFromCvFuzzyType().isCTerminal() || range.getFromCvFuzzyType().isNTerminal() || range.getFromCvFuzzyType().isUndetermined()){
                        if (range.getFromIntervalStart() != 0 || range.getFromIntervalEnd() != 0){
                            return true;
                        }
                    }
                    else if (range.getFromCvFuzzyType().isCertain() || range.getFromCvFuzzyType().isRange() || range.getFromCvFuzzyType().isLessThan() || range.getFromCvFuzzyType().isGreaterThan()){
                        if (range.getFromIntervalStart() == 0 || range.getFromIntervalEnd() == 0 || !isRangeWithinSequence){
                            return true;
                        }
                    }
                }

                if ((range.getFromCvFuzzyType() == null || range.getToCvFuzzyType() == null) && !isRangeWithinSequence){
                    return true;
                }
            }
        }
        else {
            return true;
        }

        return false;
    }
}
