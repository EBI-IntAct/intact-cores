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

        // a range null is not a valid range for a feature
        if (range == null){
            return true;
        }

        // get the start and end status of the range
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        // If the range is the start status and the begin position (s) are not consistent, or the end status and the end position (s) are not consistent
        // or the start status is not consistent with the end status, the range is not valid
        if (!checkRangePositionsAccordingToRangeType(startStatus, range.getFromIntervalStart(), range.getFromIntervalEnd(), sequence)
                || !checkRangePositionsAccordingToRangeType(endStatus, range.getToIntervalStart(), range.getToIntervalEnd(), sequence)
                || areRangeStatusInconsistent(startStatus, endStatus)){
            return true;
        }
        else{
            // in case of a sequence null, it is not possible to check that the range is overlapping the end of the protein sequence
            if (sequence == null){
                // both start/end status are not null
                if (startStatus != null && endStatus != null){
                    // We can only check if the start interval is not overlapping with the end interval if the status is not C-terminal
                    // because as the sequence is null, the range C-terminal position is 0
                    if (!startStatus.isCTerminal() && !endStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                // only the start status is null, which means that only the start status can be a C-terminal and the position 0
                else if (startStatus != null){
                    // We can only check if the start interval is not overlapping with the end interval if the status is not C-terminal
                    // because as the sequence is null, the range C-terminal position is 0
                    if (!startStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                // only the end status is null, which means that only the end status can be a C-terminal
                else if (endStatus != null){
                    // We can only check if the start interval is not overlapping with the end interval if the status is not C-terminal
                    // because as the sequence is null, the range C-terminal position is 0
                    if (!endStatus.isCTerminal() && areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
                else {
                    // we need to check that the start interval and end interval are not overlapping
                    if (areRangePositionsOverlapping(range)){
                        return true;
                    }
                }
            }
            else {
                 // we need to check that the start interval and end interval are not overlapping
                if (areRangePositionsOverlapping(range)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * A podition is out of bound if inferior or equal to 0 or superior to the sequence length.
     * @param start : the start position of the interval
     * @param end  : the end position of the interval
     * @param sequenceLength : the length of the sequence, 0 if the sequence is null
     * @return true if the start or the end is inferior or equal to 0 and if the start or the end is superior to the sequence length
     */
    public static boolean areRangePositionsOutOfBound(int start, int end, int sequenceLength){

        if (start <= 0 || end <= 0 || start > sequenceLength || end > sequenceLength){
            return true;
        }
        return false;
    }

    /**
     * A range interval is invalid if the start is after the end
     * @param start : the start position of the interval
     * @param end : the end position of the interval
     * @return true if the start is after the end
     */
    public static boolean areRangePositionsInvalid(int start, int end){

        if (start > end){
            return true;
        }
        return false;
    }

    /**
     *
     * @param rangeType : the status of the position
     * @param start : the start of the position
     * @param end : the end of the position (equal to start if the range position is a single position and not an interval)
     * @param sequence : the sequence of the protein
     * @return true if the range positions and the position status are consistent
     */
    public static boolean checkRangePositionsAccordingToRangeType(CvFuzzyType rangeType, int start, int end, String sequence){
        // the sequence length is 0 if the sequence is null
        int sequenceLength = 0;

        if (sequence != null){
            sequenceLength = sequence.length();
        }

        // the position status is null, we can only check that the numerical values of the positions are valid
        if (rangeType == null){
            // start not superior to end
            if (!areRangePositionsInvalid(start, end)){
                // the sequence is null, we can only check if the range positions are positive
                if (sequenceLength == 0 ){
                    if (start > 0 && end > 0){
                        return true;
                    }
                }
                // the sequence is not null, we can check if the range are out of bound
                else {
                    if (!areRangePositionsOutOfBound(start, end, sequenceLength)){
                        return true;
                    }
                }
            }
        }
        // the position status is defined
        else {
            // undetermined position, we expect to have a position equal to 0 for both the start and the end
            if (rangeType.isUndetermined()){
                if (start == 0 && end == 0){
                    return true;
                }
            }
            // n-terminal position : we expect to have a position equal to 1 for both the start and the end
            else if (rangeType.isNTerminal()){
                if (start == 1 && end == 1){
                    return true;
                }
            }
            // c-terminal position : we expect to have a position equal to the sequence length (0 if the sequence is null) for both the start and the end
            else if (rangeType.isCTerminal()){

                if (start == sequenceLength && end == sequenceLength){
                    return true;
                }
            }
            // greater than position : we don't expect an interval for this position so the start should be equal to the end
            else if (rangeType.isGreaterThan()){
                // The sequence is null, all we can expect is at least a start superior to 0.
                if (sequenceLength == 0){
                    if (start == end && start >= 1 ){
                        return true;
                    }
                }
                // The sequence is not null, we expect to have positions superior to 0 and STRICTLY inferior to the sequence length
                else {
                    if (start == end && start < sequenceLength && start >= 1 ){
                        return true;
                    }
                }
            }
            // less than position : we don't expect an interval for this position so the start should be equal to the end
            else if (rangeType.isLessThan()){
                // The sequence is null, all we can expect is at least a start STRICTLY superior to 1.
                if (sequenceLength == 0){
                    if (start == end && start > 1){
                        return true;
                    }
                }
                // The sequence is not null, we expect to have positions STRICTLY superior to 1 and inferior or equal to the sequence length
                else {
                    if (start == end && start > 1 && start <= sequenceLength){
                        return true;
                    }
                }
            }
            // if the range position is certain or ragged-n-terminus, we expect to have the positions superior to 0 and inferior or
            // equal to the sequence length (only possible to check if the sequence is not null)
            // We don't expect any interval for this position so the start should be equal to the end
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
            // the range status is not well known, so we allow the position to be an interval, we just check that the start and end are superior to 0 and inferior to the sequence
            // length (only possible to check if the sequence is not null)
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

    /**
     * Checks if the interval positions are overlapping
     * @param fromStart
     * @param fromEnd
     * @param toStart
     * @param toEnd
     * @return true if the range intervals are overlapping
     */
    public static boolean arePositionsOverlapping(int fromStart, int fromEnd, int toStart, int toEnd){

        if (fromStart > toStart || fromEnd > toStart || fromStart > toEnd || fromEnd > toEnd){
            return true;
        }
        return false;
    }

    /**
     * Checks if the interval positions of the range are overlapping
     * @param range
     * @return true if the range intervals are overlapping
     */
    public static boolean areRangePositionsOverlapping(Range range){
        // get the range status
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        // both the end and the start have a specific status
        if (startStatus != null && endStatus != null){
            // in the specific case where the start is superior to a position and the end is inferior to another position, we need to check that the
            // range is not invalid because 'greater than' and 'less than' are both exclusive
            if (startStatus.isGreaterThan() && endStatus.isLessThan() && range.getToIntervalEnd() - range.getFromIntervalStart() < 2){
                 return true;
            }
            // As the range positions are 0 when the status is undetermined, we can only check if the ranges are not overlapping when both start and end are not undetermined
            else if (!startStatus.isUndetermined() && !endStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        // only the end position has a status
        else if (endStatus != null){
            // in the specific case where the end is inferior to another position, we need to check that the
            // start is not equal to the end because 'less than' is exclusive
            if (endStatus.isLessThan() && range.getFromIntervalStart() == range.getToIntervalEnd()){
                return true;
            }
            // As the range positions are 0 when the status is undetermined, we can only check if the ranges are not overlapping when the end is not undetermined
            else if (!endStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        // only the start position has a status
        else if (startStatus != null){
            // in the specific case where the start is superior to another position, we need to check that the
            // end is not equal to the start because 'greater than' is exclusive
            if (startStatus.isGreaterThan() && range.getFromIntervalStart() == range.getToIntervalEnd()){
                return true;
            }
            // As the range positions are 0 when the status is undetermined, we can only check if the ranges are not overlapping when the start is not undetermined
            else if (!startStatus.isUndetermined()){
                return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
            }
        }
        // both start and end positions don't have any status, all we can do is checking that the start interval is not overlapping with the end interval
        else {
            return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
        }

        return false;
    }

    /**
     *
     * @param startStatus : the status of the start position
     * @param endStatus : the status of the end position
     * @return  true if the range status are inconsistent (n-terminal is the end, c-terminal is the beginning)
     */
    public static boolean areRangeStatusInconsistent(CvFuzzyType startStatus, CvFuzzyType endStatus){

        // both status are not null
        if (startStatus != null && endStatus != null){
            // the start position is C-terminal but the end position is different from C-terminal
            if (startStatus.isCTerminal() && !endStatus.isCTerminal()){
                return true;
            }
            // the end position is N-terminal but the start position is different from N-terminal
            else if (endStatus.isNTerminal() && !startStatus.isNTerminal()){
                return true;
            }
        }
        // only the start position has a status
        else if (startStatus != null){
            // it cannot be C-terminal
            if (startStatus.isCTerminal()){
                return true;
            }
        }
        // only the end position has a status
        else if (endStatus != null){
            // it cannot be N-terminal
            if (endStatus.isNTerminal()){
                return true;
            }
        }
        return false;
    }
}
