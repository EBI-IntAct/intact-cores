package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * This class contains utility methods to check on features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2010</pre>
 */

public final class FeatureUtils {

    public static String RANGE_SEPARATOR = "-";
    public static String RANGE_UNDETERMINED = "?";
    public static String RANGE_INTERVAL = "..";
    public static String RANGE_GREATER = ">";
    public static String RANGE_LESS = "<";
    public static String RANGE_N_TERM = "n";
    public static String RANGE_C_TERM = "c";

    public static Map<String, CvFuzzyType> rangeStatusMap;

    static {
        rangeStatusMap = new HashMap<String, CvFuzzyType>();
        CvFuzzyType n_terminal_region = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.N_TERMINAL_REGION_MI_REF, CvFuzzyType.N_TERMINAL_REGION);
        CvFuzzyType c_terminal_region = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.C_TERMINAL_REGION_MI_REF, CvFuzzyType.C_TERMINAL_REGION);
        //CvFuzzyType certain = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.CERTAIN_MI_REF, CvFuzzyType.CERTAIN);
        CvFuzzyType undetermined = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.UNDETERMINED_MI_REF, CvFuzzyType.UNDETERMINED);
        CvFuzzyType greater_than = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.GREATER_THAN_MI_REF, CvFuzzyType.GREATER_THAN);
        CvFuzzyType less_than = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.LESS_THAN_MI_REF, CvFuzzyType.LESS_THAN);
        CvFuzzyType range = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, CvFuzzyType.RANGE);

        rangeStatusMap.put(RANGE_N_TERM, n_terminal_region);
        rangeStatusMap.put(RANGE_C_TERM, c_terminal_region);
        rangeStatusMap.put(RANGE_INTERVAL, range);
        rangeStatusMap.put(RANGE_UNDETERMINED, undetermined);
        rangeStatusMap.put(RANGE_GREATER, greater_than);
        rangeStatusMap.put(RANGE_LESS, less_than);
    }

    private FeatureUtils() {
    }

    /**
     * @param rangeAsString : the string containing the range
     * @return the range instance matching the range described with the String. The feature sequence will be null.
     *         An IllegalRangeException can be thrown if the range is invalid and doesn't fit the protein sequence
     */
    public static Range createRangeFromString(String rangeAsString) {
        return createRangeFromString(rangeAsString, null, false);
    }

    /**
     * @param rangeAsString   : the string containing the range
     * @param proteinSequence : the sequence of the protein. can be null
     * @return the range instance matching the range described with the String. The feature sequence will be null.
     *         An IllegalRangeException can be thrown if the range is invalid and doesn't fit the protein sequence
     */
    public static Range createRangeFromString(String rangeAsString, String proteinSequence) {
        return createRangeFromString(rangeAsString, proteinSequence, false);
    }

    /**
     * @param rangeAsString      the string containing the range
     * @param proteinSequence    the sequence of the protein. can be null
     * @param enforceConsistency throw an exception if the range is invalid when submitting a protein sequence
     * @return the range instance matching the range described with the String. The feature sequence will be null.
     *         An IllegalRangeException can be thrown if the range is invalid and doesn't fit the protein sequence
     */
    public static Range createRangeFromString(String rangeAsString, String proteinSequence, boolean enforceConsistency) {
        if (rangeAsString == null) {
            throw new IllegalArgumentException("The range cannot be null.");
        }

        if (rangeAsString.contains(RANGE_SEPARATOR)) {
            String[] positions = rangeAsString.split(RANGE_SEPARATOR);

            if (positions.length == 2) {
                CvFuzzyType fromStatus = createCvFuzzyType(positions[0]);
                CvFuzzyType toStatus = createCvFuzzyType(positions[1]);

                Integer[] intervalStart = convertPosition(positions[0], fromStatus);
                Integer[] intervalEnd = convertPosition(positions[1], toStatus);

                int fromStart = intervalStart[0];
                int fromEnd = intervalStart[1];
                int toStart = intervalEnd[0];
                int toEnd = intervalEnd[1];

                final Range range = new Range(fromStatus, fromStart, fromEnd, toStatus, toStart, toEnd, null);
                range.prepareSequence(proteinSequence, enforceConsistency);

                return range;
            } else {
                throw new IllegalArgumentException("The range " + rangeAsString + " is not valid. The format should be 'start-end' or 'start1..start2-end1..end2'");
            }
        } else {
            throw new IllegalArgumentException("The range " + rangeAsString + " is not valid. The format should be 'start-end' or 'start1..start2-end1..end2'");
        }
    }

    /**
     * @param range : the range to convert
     * @return the range as a String
     *         If the range is invalid, will return fromIntervalStart-toIntervalEnd
     */
    public static String convertRangeIntoString(Range range) {
        if (range == null) {
            throw new IllegalArgumentException("The range cannot be null.");
        }

        if (isABadRange(range, null)) {
            return range.getFromIntervalStart() + RANGE_SEPARATOR + range.getToIntervalEnd();
            //throw new IllegalRangeException(getBadRangeInfo(range, null));
        }

        String startPosition = positionToString(range.getFromCvFuzzyType(), range.getFromIntervalStart(), range.getFromIntervalEnd());
        String endPosition = positionToString(range.getToCvFuzzyType(), range.getToIntervalStart(), range.getToIntervalEnd());

        return startPosition + RANGE_SEPARATOR + endPosition;
    }

    private static String positionToString(CvFuzzyType status, int start, int end) {
        String position;

        if (status.isUndetermined()) {
            position = RANGE_UNDETERMINED;
        } else if (status.isCTerminalRegion()) {
            position = RANGE_C_TERM;
        } else if (status.isNTerminalRegion()) {
            position = RANGE_N_TERM;
        } else if (status.isGreaterThan()) {
            position = RANGE_GREATER + start;
        } else if (status.isLessThan()) {
            position = RANGE_LESS + start;
        } else if (status.isRange()) {
            position = start + RANGE_INTERVAL + end;
        } else {
            position = Integer.toString(start);
        }

        return position;
    }

    private static Integer[] convertPosition(String position, CvFuzzyType status) {
        if (position == null) {
            throw new IllegalArgumentException("The range position cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("The range status cannot be null.");
        }

        if (status.isNTerminalRegion() || status.isCTerminalRegion() || status.isUndetermined()) {
            return new Integer[]{0, 0};
        } else if (status.isGreaterThan()) {
            String exactPosition = position.replace(RANGE_GREATER, "");
            int p = Integer.parseInt(exactPosition);

            return new Integer[]{p, p};
        } else if (status.isLessThan()) {
            String exactPosition = position.replace(RANGE_LESS, "");
            int p = Integer.parseInt(exactPosition);

            return new Integer[]{p, p};
        } else if (status.isRange()) {
            if (position.contains(RANGE_INTERVAL)) {
                String[] interval = position.split("\\.\\.");

                if (interval.length == 2) {
                    int p1 = Integer.parseInt(interval[0]);
                    int p2 = Integer.parseInt(interval[1]);

                    return new Integer[]{p1, p2};
                } else {
                    throw new IllegalArgumentException("The range position " + position + " is not valid. The status is 'range' and the format should be 'pos1..pos2'");
                }
            } else {
                throw new IllegalArgumentException("The range position " + position + " is not valid. The status is 'range' and the format should be 'pos1..pos2'");
            }
        } else {
            int p = Integer.parseInt(position);

            return new Integer[]{p, p};
        }
    }

    private static CvFuzzyType createCvFuzzyType(String position) {

        if (position == null) {
            throw new IllegalArgumentException("The range position cannot be null.");
        }

        for (String key : rangeStatusMap.keySet()) {
            if (position.contains(key)) {
                return rangeStatusMap.get(key);
            }
        }

        return CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvFuzzyType.class, CvFuzzyType.CERTAIN_MI_REF, CvFuzzyType.CERTAIN);
    }

    /**
     * @param protein : the protein to check
     * @return a set of the protein features containing bad ranges (overlapping or out of bound)
     */
    public static Set<Feature> getFeaturesWithBadRanges(Protein protein) {
        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components) {
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features) {
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges) {
                    if (isABadRange(range, protein.getSequence())) {
                        badFeatures.add(feature);
                        break;
                    }
                }
            }
        }

        return badFeatures;
    }

    /**
     * @param protein : the protein to check
     * @return a set of the protein feature ranges which are overlapping or out of bound
     */
    public static Set<Range> getBadRanges(Protein protein) {

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components) {
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features) {
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges) {
                    if (isABadRange(range, protein.getSequence())) {
                        badRanges.add(range);
                    }
                }
            }
        }

        return badRanges;
    }

    /**
     * @param range    : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static boolean isABadRange(Range range, String sequence) {
        return (getBadRangeInfo(range, sequence) != null);
    }

    public static boolean isABadRange(String range, String sequence) {

        try{
            Range r = createRangeFromString(range, sequence, false);

            return (getBadRangeInfo(r, sequence) != null);
        }
        catch (IllegalArgumentException e){
            return true;
        }
    }

    /**
     * @param rangeAsString : the range to check
     * @param sequence      : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static String getBadRangeInfo(String rangeAsString, String sequence) {
        return getBadRangeInfo(createRangeFromString(rangeAsString, sequence, false), sequence);
    }

    /**
     * @param range    : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static String getBadRangeInfo(Range range, String sequence) {

        // a range null is not a valid range for a feature
        if (range == null) {
            return "Range is null";
        }

        // get the start and end status of the range
        final CvFuzzyType startStatus = range.getFromCvFuzzyType();
        final CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null) {
            return "The start status of the range is null and it is mandatory for PSI-MI.";
        }
        if (endStatus == null) {
            return "The end status of the range is null and it is mandatory for PSI-MI.";
        }

        // If the range is the start status and the begin position (s) are not consistent, or the end status and the end position (s) are not consistent
        // or the start status is not consistent with the end status, the range is not valid
        final int fromIntervalStart = range.getFromIntervalStart();
        final int fromIntervalEnd = range.getFromIntervalEnd();
        final int toIntervalStart = range.getToIntervalStart();
        final int toIntervalEnd = range.getToIntervalEnd();

        String areRangePositionsAccordingToTypeOkStart = getRangePositionsAccordingToRangeTypeErrorMessage(startStatus, fromIntervalStart, fromIntervalEnd, sequence);

        String areRangePositionsAccordingToTypeOkEnd = getRangePositionsAccordingToRangeTypeErrorMessage(endStatus, toIntervalStart, toIntervalEnd, sequence);

        if (areRangePositionsAccordingToTypeOkStart != null) {
            return areRangePositionsAccordingToTypeOkStart;
        }
        if (areRangePositionsAccordingToTypeOkEnd != null) {
            return areRangePositionsAccordingToTypeOkEnd;
        }
        if (areRangeStatusInconsistent(startStatus, endStatus)) {
            return "Start status " + startStatus.getShortLabel() + " and end status " + endStatus.getShortLabel() + " are inconsistent";
        }

        // if the range has not a position undetermined, C terminal region or N-terminal region, we check if the range positions are not overlapping
        if (!(startStatus.isCTerminalRegion() || startStatus.isUndetermined() || startStatus.isNTerminalRegion()) && !(endStatus.isCTerminalRegion() || endStatus.isUndetermined() || endStatus.isNTerminalRegion()) && areRangePositionsOverlapping(range)) {
            return "The range positions overlap : " + startStatus.getShortLabel() + ":" + fromIntervalStart + "-" + fromIntervalEnd + "," + endStatus.getShortLabel() + ":" + toIntervalStart + "-" + toIntervalEnd;
        }

        return null;
    }

    /**
     * A position is out of bound if inferior or equal to 0 or superior to the sequence length.
     *
     * @param start          : the start position of the interval
     * @param end            : the end position of the interval
     * @param sequenceLength : the length of the sequence, 0 if the sequence is null
     * @return true if the start or the end is inferior or equal to 0 and if the start or the end is superior to the sequence length
     */
    public static boolean areRangePositionsOutOfBounds(int start, int end, int sequenceLength) {
        return start <= 0 || end <= 0 || start > sequenceLength || end > sequenceLength;
    }

    /**
     * A range interval is invalid if the start is after the end
     *
     * @param start : the start position of the interval
     * @param end   : the end position of the interval
     * @return true if the start is after the end
     */
    public static boolean areRangePositionsInvalid(int start, int end) {

        if (start > end) {
            return true;
        }
        return false;
    }

    /**
     * @param rangeType : the status of the position
     * @param start     : the start of the position
     * @param end       : the end of the position (equal to start if the range position is a single position and not an interval)
     * @param sequence  : the sequence of the protein
     * @return true if the range positions and the position status are consistent
     */
    public static boolean areRangePositionsAccordingToRangeTypeOk(CvFuzzyType rangeType, int start, int end, String sequence) {
        return (getRangePositionsAccordingToRangeTypeErrorMessage(rangeType, start, end, sequence) == null);
    }

    /**
     * @param rangeType : the status of the position
     * @param start     : the start of the position
     * @param end       : the end of the position (equal to start if the range position is a single position and not an interval)
     * @param sequence  : the sequence of the protein
     * @return message with the error. Null otherwise
     */
    public static String getRangePositionsAccordingToRangeTypeErrorMessage(CvFuzzyType rangeType, int start, int end, String sequence) {

        if (rangeType == null) {
            throw new IllegalArgumentException("It is not possible to check if the range status is compliant with the range positions because it is null and mandatory.");
        }

        // the sequence length is 0 if the sequence is null
        int sequenceLength = 0;

        if (sequence != null) {
            sequenceLength = sequence.length();
        }

        // the position status is defined
        // undetermined position, we expect to have a position equal to 0 for both the start and the end
        if (rangeType.isUndetermined() || rangeType.isCTerminalRegion() || rangeType.isNTerminalRegion()) {
            if (start != 0 || end != 0) {
                return "Undetermined positions (undetermined, N-terminal region, C-terminal region) must always be 0. Actual positions : " + start + "-" + end;
            }
        }
        // n-terminal position : we expect to have a position equal to 1 for both the start and the end
        else if (rangeType.isNTerminal()) {
            if (start != 1 || end != 1) {
                return "N-terminal positions must always be 1. Actual positions : " + start + "-" + end;
            }
        }
        // c-terminal position : we expect to have a position equal to the sequence length (0 if the sequence is null) for both the start and the end
        else if (rangeType.isCTerminal()) {
            if (sequenceLength == 0 && (start < 0 || end < 0 || start != end)) {
                return "C-terminal positions must always be superior to 0. Actual positions : " + start + "-" + end;
            } else if ((start != sequenceLength || end != sequenceLength) && sequenceLength > 0) {
                return "C-terminal positions must always be equal to the length of the protein sequence. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
            }
        }
        // greater than position : we don't expect an interval for this position so the start should be equal to the end
        else if (rangeType.isGreaterThan()) {
            if (start != end) {
                return "Greater than positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }

            // The sequence is null, all we can expect is at least a start superior to 0.
            if (sequenceLength == 0) {
                if (start <= 0) {
                    return "Greater than positions must always be strictly superior to 0. Actual positions : " + start + "-" + end;
                }
            }
            // The sequence is not null, we expect to have positions superior to 0 and STRICTLY inferior to the sequence length
            else {
                if (start >= sequenceLength || start <= 0) {
                    return "Greater than positions must always be strictly superior to 0 and strictly inferior to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // less than position : we don't expect an interval for this position so the start should be equal to the end
        else if (rangeType.isLessThan()) {
            if (start != end) {
                return "Less than positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }
            // The sequence is null, all we can expect is at least a start STRICTLY superior to 1.
            if (sequenceLength == 0) {
                if (start <= 1) {
                    return "Less than positions must always be strictly superior to 1. Actual positions : " + start + "-" + end;
                }
            }
            // The sequence is not null, we expect to have positions STRICTLY superior to 1 and inferior or equal to the sequence length
            else {
                if (start <= 1 || start > sequenceLength) {
                    return "Less than positions must always be strictly superior to 1 and inferior or equal to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // if the range position is certain or ragged-n-terminus, we expect to have the positions superior to 0 and inferior or
        // equal to the sequence length (only possible to check if the sequence is not null)
        // We don't expect any interval for this position so the start should be equal to the end
        else if (rangeType.isCertain() || rangeType.isRaggedNTerminus()) {
            if (start != end) {
                return "Certain and ragged-n-terminus positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }

            if (sequenceLength == 0) {
                if (start <= 0) {
                    return "Certain and ragged-n-terminus positions must always be strictly superior to 0. Actual positions : " + start + "-" + end;
                }
            } else {
                if (areRangePositionsOutOfBounds(start, end, sequenceLength)) {
                    return "Certain and ragged-n-terminus positions must always be strictly superior to 0 and inferior or equal to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // the range status is not well known, so we allow the position to be an interval, we just check that the start and end are superior to 0 and inferior to the sequence
        // length (only possible to check if the sequence is not null)
        else {
            if (sequenceLength == 0) {
                if (areRangePositionsInvalid(start, end) || start <= 0 || end <= 0) {
                    return rangeType.getShortLabel() + " positions must always be strictly superior to 0 and the end must be superior or equal to the start. Actual positions : " + start + "-" + end;
                }
            } else {
                if (areRangePositionsInvalid(start, end) || start <= 0 || end <= 0) {
                    return rangeType.getShortLabel() + " positions must always have an end superior or equal to the start. Actual positions : " + start + "-" + end;
                } else if (areRangePositionsOutOfBounds(start, end, sequenceLength)) {
                    return rangeType.getShortLabel() + " positions must always be strictly superior to 0 and inferior or equal to the sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the interval positions are overlapping
     *
     * @param fromStart
     * @param fromEnd
     * @param toStart
     * @param toEnd
     * @return true if the range intervals are overlapping
     */
    public static boolean arePositionsOverlapping(int fromStart, int fromEnd, int toStart, int toEnd) {

        if (fromStart > toStart || fromEnd > toStart || fromStart > toEnd || fromEnd > toEnd) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the interval positions of the range are overlapping
     *
     * @param range
     * @return true if the range intervals are overlapping
     */
    public static boolean areRangePositionsOverlapping(Range range) {
        // get the range status
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        // both the end and the start have a specific status
        // in the specific case where the start is superior to a position and the end is inferior to another position, we need to check that the
        // range is not invalid because 'greater than' and 'less than' are both exclusive
        if (startStatus.isGreaterThan() && endStatus.isLessThan() && range.getToIntervalEnd() - range.getFromIntervalStart() < 2) {
            return true;
        }
        // we have a greater than start position and the end position is equal to the start position
        else if (startStatus.isGreaterThan() && !endStatus.isGreaterThan() && range.getFromIntervalStart() == range.getToIntervalStart()) {
            return true;
        }
        // we have a less than end position and the start position is equal to the start position
        else if (!startStatus.isLessThan() && endStatus.isLessThan() && range.getFromIntervalEnd() == range.getToIntervalEnd()) {
            return true;
        }
        // As the range positions are 0 when the status is undetermined, we can only check if the ranges are not overlapping when both start and end are not undetermined
        else if (!(startStatus.isUndetermined() || startStatus.isNTerminalRegion() || startStatus.isCTerminalRegion()) && !(endStatus.isUndetermined() || endStatus.isCTerminalRegion() || endStatus.isNTerminalRegion())) {
            return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
        }

        return false;
    }

    /**
     * @param startStatus : the status of the start position
     * @param endStatus   : the status of the end position
     * @return true if the range status are inconsistent (n-terminal is the end, c-terminal is the beginning)
     */
    public static boolean areRangeStatusInconsistent(CvFuzzyType startStatus, CvFuzzyType endStatus) {

        if (startStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        // both status are not null
        // the start position is C-terminal but the end position is different from C-terminal
        if (startStatus.isCTerminal() && !endStatus.isCTerminal()) {
            return true;
        }
        // the end position is N-terminal but the start position is different from N-terminal
        else if (endStatus.isNTerminal() && !startStatus.isNTerminal()) {
            return true;
        }
        // the end status is C terminal region, the start status can only be C-terminal region or C-terminal
        else if (startStatus.isCTerminalRegion() && !(endStatus.isCTerminal() || endStatus.isCTerminalRegion())) {
            return true;
        }
        // the start status is N terminal region, the end status can only be N-terminal region or N-terminal        
        else if (endStatus.isNTerminal() && !(startStatus.isNTerminal() || startStatus.isNTerminalRegion())) {
            return true;
        }

        return false;
    }

    /**
     * If the range status is 'undetermined', the positions will be set to 0. If the range status is 'n-terminal', the positions will be 1
     * and finally if the range status is 'c-terminal' and the sequence is not null, the positions will be the sequence length
     *
     * @param range           : the range
     * @param proteinSequence : the sequence of the protein
     */
    public static void correctRangePositionsAccordingToType(Range range, String proteinSequence) {
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null) {
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        if (startStatus.isUndetermined() || startStatus.isCTerminalRegion() || startStatus.isNTerminalRegion()) {
            range.setFromIntervalStart(0);
            range.setFromIntervalEnd(0);
        } else if (startStatus.isNTerminal()) {
            range.setFromIntervalStart(1);
            range.setFromIntervalEnd(1);
        } else if (startStatus.isCTerminal()) {
            if (proteinSequence != null) {
                range.setFromIntervalStart(proteinSequence.length());
                range.setFromIntervalEnd(proteinSequence.length());
            } else {
                range.setFromIntervalStart(0);
                range.setFromIntervalEnd(0);
            }
        }

        if (endStatus.isUndetermined() || endStatus.isCTerminalRegion() || endStatus.isNTerminalRegion()) {
            range.setToIntervalStart(0);
            range.setToIntervalEnd(0);
        } else if (endStatus.isNTerminal()) {
            range.setToIntervalStart(1);
            range.setToIntervalEnd(1);
        } else if (endStatus.isCTerminal()) {
            if (proteinSequence != null) {
                range.setToIntervalStart(proteinSequence.length());
                range.setToIntervalEnd(proteinSequence.length());
            } else {
                range.setToIntervalStart(0);
                range.setToIntervalEnd(0);
            }
        }
    }
}
