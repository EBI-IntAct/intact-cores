package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

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
        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isABadRange(range, protein.getSequence())){
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

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isABadRange(range, protein.getSequence())){
                        badRanges.add(range);
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
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static boolean isABadRange(Range range, String sequence){

        if (range == null){
            return true;
        }
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (!checkRangePositionsAccordingToRangeType(startStatus, range.getFromIntervalStart(), range.getFromIntervalEnd(), sequence)
                || !checkRangePositionsAccordingToRangeType(endStatus, range.getToIntervalStart(), range.getToIntervalEnd(), sequence)
                || areRangeStatusInconsistent(startStatus, endStatus)){
            return true;
        }
        else{
            if (sequence == null){
                if (startStatus != null && endStatus != null){
                    if (!startStatus.isCTerminal() && !endStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                else if (startStatus != null){
                    if (!startStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                else if (endStatus != null){
                    if (!endStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                else {
                    if (areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
            }
            else {
                if (areRangePositionsOverlapping(range)){
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean areRangePositionsOutOfBound(int start, int end, int sequenceLength){

        if (start <= 0 || end <= 0 || start > sequenceLength || end > sequenceLength){
            return true;
        }
        return false;
    }

    public static boolean areRangePositionsInvalid(int start, int end){

        if (start > end){
            return true;
        }
        return false;
    }

    public static boolean checkRangePositionsAccordingToRangeType(CvFuzzyType rangeType, int start, int end, String sequence){
        int sequenceLength = 0;

        if (sequence != null){
            sequenceLength = sequence.length();
        }

        if (rangeType == null){
            if (!areRangePositionsInvalid(start, end)){
                if (sequenceLength == 0 ){
                    if (start > 0 && end > 0){
                        return true;
                    }
                }
                else {
                    if (!areRangePositionsOutOfBound(start, end, sequenceLength)){
                        return true;
                    }
                }
            }
        }
        else {
            if (rangeType.isUndetermined()){
                if (start == 0 && end == 0){
                    return true;
                }
            }
            else if (rangeType.isNTerminal()){
                if (start == 1 && end == 1){
                    return true;
                }
            }
            else if (rangeType.isCTerminal()){

                if (start == sequenceLength && end == sequenceLength){
                    return true;
                }
            }
            else if (rangeType.isGreaterThan()){
                if (sequenceLength == 0){
                    if (start == end && start >= 1 ){
                        return true;
                    }
                }
                else {
                    if (start == end && start < sequenceLength && start >= 1 ){
                        return true;
                    }
                }
            }
            else if (rangeType.isLessThan()){
                if (sequenceLength == 0){
                    if (start == end && start > 1){
                        return true;
                    }
                }
                else {
                    if (start == end && start > 1 && start <= sequenceLength){
                        return true;
                    }
                }
            }
            else if (rangeType.isCertain() || rangeType.isRaggedNTerminus()){
                if (sequenceLength == 0){
                    if (start == end && start > 0){
                        return true;
                    }
                }
                else {
                    if (start == end && !areRangePositionsOutOfBound(start, end, sequenceLength)){
                        return true;
                    }
                }
            }
            else {
                if (sequenceLength == 0){
                    if (!areRangePositionsInvalid(start, end) && start > 0 && end > 0){
                        return true;
                    }
                }
                else {
                    if (!areRangePositionsInvalid(start, end) && areRangePositionsOutOfBound(start, end, sequenceLength)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean arePositionsOverlapping(int fromStart, int fromEnd, int toStart, int toEnd){

        if (fromStart > toStart || fromEnd > toStart || fromStart > toEnd || fromEnd > toEnd){
            return true;
        }
        return false;
    }

    public static boolean areRangePositionsOverlapping(Range range){
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus != null && endStatus != null){
            if (startStatus.isGreaterThan() && endStatus.isLessThan() && range.getToIntervalEnd() - range.getFromIntervalStart() < 2){
                 return true;
            }
            else if (!startStatus.isUndetermined() && !endStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        else if (endStatus != null){
            if (endStatus.isLessThan() && range.getFromIntervalStart() == range.getToIntervalEnd()){
                return true;
            }
            else if (!endStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        else if (startStatus != null){
            if (startStatus.isGreaterThan() && range.getFromIntervalStart() == range.getToIntervalEnd()){
                return true;
            }
            else if (!startStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        else {
            return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
        }

        return false;
    }

    public static boolean areRangeStatusInconsistent(CvFuzzyType startStatus, CvFuzzyType endStatus){

        if (startStatus != null && endStatus != null){
            if (startStatus.isCTerminal() && !endStatus.isCTerminal()){
                return true;
            }
            else if (endStatus.isNTerminal() && !startStatus.isNTerminal()){
                return true;
            }
        }
        else if (startStatus != null){
            if (startStatus.isCTerminal()){
                return true;
            }
        }
        else if (endStatus != null){
            if (endStatus.isNTerminal()){
                return true;
            }
        }
        return false;
    }
}
