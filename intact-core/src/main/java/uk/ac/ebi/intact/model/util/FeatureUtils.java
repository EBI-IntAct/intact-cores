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
        int sequenceLength = 0;
        String sequence = protein.getSequence();
        if (sequence != null){
            sequenceLength = sequence.length();
        }

        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (range.getToCvFuzzyType() != null &&
                            (range.getToCvFuzzyType().isCTerminal() ||
                                    range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined())) {
                        continue;
                    }
                    else if (range.getFromIntervalStart() < 0 || range.getFromIntervalEnd() < 0 || range.getToIntervalStart() < 0 || range.getToIntervalEnd() < 0){
                        badFeatures.add(feature);
                        break;
                    }
                    else if (sequenceLength > 0){
                        if (range.getFromIntervalStart() > sequenceLength || range.getFromIntervalEnd() > sequenceLength || range.getToIntervalStart() > sequenceLength || range.getToIntervalEnd() > sequenceLength){
                            badFeatures.add(feature);
                            break;
                        }
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
                    if (range.getToCvFuzzyType() != null &&
                            (range.getToCvFuzzyType().isCTerminal() ||
                                    range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined())) {
                        continue;
                    }
                    else if (range.getFromIntervalStart() < 0 || range.getFromIntervalEnd() < 0 || range.getToIntervalStart() < 0 || range.getToIntervalEnd() < 0){
                        badRanges.add(range);
                        break;
                    }
                    else if (sequenceLength > 0){
                        if (range.getFromIntervalStart() > sequenceLength || range.getFromIntervalEnd() > sequenceLength || range.getToIntervalStart() > sequenceLength || range.getToIntervalEnd() > sequenceLength){
                            badRanges.add(range);
                            break;
                        }
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
            return false;
        }
        else if (range == null){
            return false;
        }

        int sequenceLength = sequence.length();
        if (range.getToCvFuzzyType() != null &&
                            (range.getToCvFuzzyType().isCTerminal() ||
                                    range.getToCvFuzzyType().isNTerminal() || range.getToCvFuzzyType().isUndetermined())) {
             return true;
        }
        else if (range.getFromIntervalEnd() > sequenceLength || range.getToIntervalEnd() > sequenceLength || range.getFromIntervalStart() > sequenceLength || range.getToIntervalStart() > sequenceLength){
            return false;
        }
        else if (range.getFromIntervalEnd() < 0 || range.getToIntervalEnd() < 0 || range.getFromIntervalStart() < 0 || range.getToIntervalStart() < 0){
            return false;
        }
        
        return true;
    }
}
