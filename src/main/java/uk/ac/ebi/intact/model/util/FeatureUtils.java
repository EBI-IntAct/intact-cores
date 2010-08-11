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
                    if (range.getFromIntervalStart() > range.getFromIntervalEnd() || range.getToIntervalStart() > range.getToIntervalEnd() || range.getFromIntervalStart() > range.getToIntervalStart() || range.getFromIntervalEnd() > range.getToIntervalEnd()){
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
                    boolean isRangeWithinSequence = isRangeWithinSequence(range, sequence);

                    if (range.getToCvFuzzyType() != null) {
                        if (range.getToCvFuzzyType().isCTerminal() || range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined()){
                            if (range.getToIntervalStart() != 0 || range.getToIntervalEnd() != 0){
                                badFeatures.add(feature);
                                break;
                            }
                        }
                        else if (range.getToCvFuzzyType().isCertain() || range.getToCvFuzzyType().isRange() || range.getToCvFuzzyType().isLessThan() || range.getToCvFuzzyType().isGreaterThan()){
                            if (range.getToIntervalStart() == 0 || range.getToIntervalEnd() == 0 || !isRangeWithinSequence){
                                badFeatures.add(feature);
                                break;
                            }
                        }
                    }
                    if (range.getFromCvFuzzyType() != null) {
                        if (range.getFromCvFuzzyType().isCTerminal() || range.getFromCvFuzzyType().isNTerminal() || range.getFromCvFuzzyType().isUndetermined()){
                            if (range.getFromIntervalStart() != 0 || range.getFromIntervalEnd() != 0){
                                badFeatures.add(feature);
                                break;
                            }
                        }
                        else if (range.getFromCvFuzzyType().isCertain() || range.getFromCvFuzzyType().isRange() || range.getFromCvFuzzyType().isLessThan() || range.getToCvFuzzyType().isGreaterThan()){
                            if (range.getFromIntervalStart() == 0 || range.getFromIntervalEnd() == 0 || !isRangeWithinSequence){
                                badFeatures.add(feature);
                                break;
                            }
                        }
                    }

                    if ((range.getFromCvFuzzyType() == null || range.getToCvFuzzyType() == null) && !isRangeWithinSequence){
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
                    if (range.getFromIntervalStart() > range.getFromIntervalEnd() || range.getToIntervalStart() > range.getToIntervalEnd() || range.getFromIntervalStart() > range.getToIntervalStart() || range.getFromIntervalEnd() > range.getToIntervalEnd()){
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
        int sequenceLength = 0;
        String sequence = protein.getSequence();
        if (sequence != null){
            sequenceLength = sequence.length();
        }

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    boolean isRangeWithinSequence = isRangeWithinSequence(range, sequence);

                    if (range.getToCvFuzzyType() != null) {
                        if (range.getToCvFuzzyType().isCTerminal() || range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined()){
                            if (range.getToIntervalStart() != 0 || range.getToIntervalEnd() != 0){
                                badRanges.add(range);
                                break;
                            }
                        }
                        else if (range.getToCvFuzzyType().isCertain() || range.getToCvFuzzyType().isRange() || range.getToCvFuzzyType().isLessThan() || range.getToCvFuzzyType().isGreaterThan()){
                            if (range.getToIntervalStart() == 0 || range.getToIntervalEnd() == 0 || !isRangeWithinSequence){
                                badRanges.add(range);
                                break;
                            }
                        }
                    }
                    if (range.getFromCvFuzzyType() != null) {
                        if (range.getFromCvFuzzyType().isCTerminal() || range.getFromCvFuzzyType().isNTerminal() || range.getFromCvFuzzyType().isUndetermined()){
                            if (range.getFromIntervalStart() != 0 || range.getFromIntervalEnd() != 0){
                                badRanges.add(range);
                                break;
                            }
                        }
                        else if (range.getFromCvFuzzyType().isCertain() || range.getFromCvFuzzyType().isRange() || range.getFromCvFuzzyType().isLessThan() || range.getToCvFuzzyType().isGreaterThan()){
                            if (range.getFromIntervalStart() == 0 || range.getFromIntervalEnd() == 0 || !isRangeWithinSequence){
                                badRanges.add(range);
                                break;
                            }
                        }
                    }

                    if ((range.getFromCvFuzzyType() == null || range.getToCvFuzzyType() == null) && !isRangeWithinSequence){
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
}
