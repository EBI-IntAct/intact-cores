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
}
