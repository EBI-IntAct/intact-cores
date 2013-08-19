/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.1
 */
public final class ExperimentUtils {

	private static final Log log = LogFactory.getLog(ExperimentUtils.class);

	private static final String SYNCED_LABEL_PATTERN = "(\\w+)-((\\d{4})[a-z]?)-(\\d+)";
	private static final String NOT_SYNCED_LABEL_PATTERN = "(\\w+)-(\\d{4})";

	private ExperimentUtils() {
	}

	/**
	 * Gets the pubmed ID for an Experiment - whitout hitting the database
	 *
	 * @param experiment the experiment to get the pubmed id from
	 * @return the pubmed id
	 */
	public static String getPubmedId(Experiment experiment) {
		String pubmedId = null;

		Publication publication = experiment.getPublication();
		if (publication != null) {
            PublicationXref xref = PublicationUtils.getPubmedPrimaryReferenceXref(publication);
            if (xref != null){
               pubmedId = xref.getPrimaryId();
            }
		}

		if (pubmedId == null) {
			ExperimentXref xref = getPubmedPrimaryReferenceXref(experiment);

			if (xref != null) {
				pubmedId = xref.getPrimaryId();
			}
		}

		return pubmedId;
	}

	/**
	 * Gets the first primary reference of an existing experiment without connecting with the database
	 *
	 * @param experiment the experiment
	 * @return the primary reference xref
	 */
	public static ExperimentXref getPrimaryReferenceXref(Experiment experiment) {
		for (ExperimentXref xref : experiment.getXrefs()) {
			String qualMi = null;

			if (xref.getCvXrefQualifier() != null) {
				qualMi = xref.getCvXrefQualifier().getIdentifier();
			}

			if (CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(qualMi)) {
				return xref;
			}
		}

		return null;
	}

	/**
	 * Gets the first pubmed primary reference of an existing experiment without connecting with the database
	 *
	 * @param experiment the experiment
	 * @return the primary reference xref
	 */
	public static ExperimentXref getPubmedPrimaryReferenceXref(Experiment experiment) {
		for (ExperimentXref xref : experiment.getXrefs()) {
            boolean qualMi = false;
            boolean dbMi = false;

            if (xref.getCvXrefQualifier() != null) {
                if (xref.getCvXrefQualifier().getIdentifier() != null){
                    qualMi = CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(xref.getCvXrefQualifier().getIdentifier());
                }
                else {
                    qualMi = CvXrefQualifier.PRIMARY_REFERENCE.equalsIgnoreCase(xref.getCvXrefQualifier().getShortLabel());
                }
            }

            if (xref.getCvDatabase() != null) {
                if (xref.getCvDatabase().getIdentifier() != null){
                    dbMi = CvDatabase.PUBMED_MI_REF.equals(xref.getCvDatabase().getIdentifier());
                }
                else {
                    dbMi = CvDatabase.PUBMED.equalsIgnoreCase(xref.getCvDatabase().getShortLabel());
                }
            }


            if (qualMi && dbMi) {
                return xref;
            }
		}

		return null;
	}

	/**
	 * Syncs a short label with the database, checking that there are no duplicates and that the correct suffix is added.
	 * <p/>
	 * Concurrency note: just after getting the new short label, it is recommended to persist/update the interaction immediately
	 * in the database - so this method should ONLY be used before saving the interaction to the database. In some
	 * race conditions, two interactions could be created with the same id; currently there is no way to
	 * reserve a short label
	 *
	 * @param shortLabel the short label to sync
	 * @return the synced short label
	 * @since 1.6.2
	 */
	@Deprecated
	public static String syncShortLabelWithDb(String shortLabel) {
		return syncShortLabelWithDb(shortLabel, null);
	}

	/**
	 * Syncs a short label with the database, checking that there are no duplicates and that the correct suffix is added.
	 * <p/>
	 * Concurrency note: just after getting the new short label, it is recommended to persist/update the interaction immediately
	 * in the database - so this method should ONLY be used before saving the interaction to the database. In some
	 * race conditions, two interactions could be created with the same id; currently there is no way to
	 * reserve a short label
	 *
	 * @param shortLabel the short label to sync
	 * @param pubmedId   the pubmed corresponding to the experiment
	 * @return the synced short label
	 * @since 1.7.0
	 */
	public static String syncShortLabelWithDb(String shortLabel, String pubmedId) {
		String syncedLabel = null;

		if (!(matchesMotSyncedLabel(shortLabel) || matchesSyncedLabel(shortLabel))) {
			throw new IllegalArgumentException("Short label with wrong format: " + shortLabel);
		}

		// get only the author-YYYY part (truncate any prefix)
		shortLabel = shortLabel.substring(0, shortLabel.indexOf("-") + 5);

		ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();

		if (pubmedId != null) {
			Collection<Experiment> experiments = experimentDao.getByShortLabelLike(shortLabel + "%");

			//sort the experiments based on shortlabel, otherwise is not giving expected results
			List<Experiment> sortedExperiments = new ArrayList<Experiment>(new HashSet<Experiment>(experiments));

			Collections.sort(sortedExperiments, new Comparator<Experiment>() {
				public int compare(Experiment exp1, Experiment exp2) {
					// We can not compare directly the shortlabels, because the current chunk is
					// compared as character not as an interger and then the order is not right.

					// As we know the query getByShortLabelLike(shortLabel + "%"), we can assume thar the year and the
					// author is the same and only compare the current chunk and the letters
					int result = 0;


					Pattern pattern = Pattern.compile("([a-z]?)-(\\d+)");

					String label1 = exp1.getShortLabel();
					String label2 = exp2.getShortLabel();

					label1 = label1.substring(label1.indexOf("-") + 5,label1.length());
					label2 = label2.substring(label2.indexOf("-") + 5,label2.length());

					Matcher matcher1 = pattern.matcher(label1);
					Matcher matcher2 = pattern.matcher(label2);

					int currentChunk1 = 0;
					int currentChunk2 = 0;

					String letter1="";
					String letter2="";

					if (matcher1.find()) {
						letter1 = matcher1.group(1);

						try {
							currentChunk1 = Integer.parseInt(matcher1.group(2));
						} catch (NumberFormatException e) {
							throw new IntactException("The experiment number is not available");
						}

					}

					if (matcher2.find()) {
						letter2 = matcher2.group(1);

						try {
							currentChunk2 = Integer.parseInt(matcher2.group(2));
						} catch (NumberFormatException e) {
							throw new IntactException("The experiment number is not available");
						}

					}

					result = letter1.compareTo(letter2);
					if(result == 0){
						if (currentChunk1 < currentChunk2) {
							result = -1;
						} else if (currentChunk1 == currentChunk2) {
							result = 0;
						} else {
							result = 1;
						}
					}

					return result;

				}

			});

			ExperimentShortlabelGenerator generator = new ExperimentShortlabelGenerator();

			Pattern pattern = Pattern.compile(SYNCED_LABEL_PATTERN);

			for (Experiment exp : sortedExperiments) {
				final String label = exp.getShortLabel();
				final String expPubId = getPubmedId(exp);

				Matcher matcher = pattern.matcher(label);

				if (matcher.find()) {
					String author = matcher.group(1);
					String strYear = matcher.group(3);
					int year;
					int currentChunk;

					try {
						year = Integer.parseInt(strYear);
						currentChunk = Integer.parseInt(matcher.group(4));
					} catch (NumberFormatException e) {
						throw new IntactException("The year part of the experiment short label is not numberic: " + strYear + " (" + exp.getShortLabel() + ")");
					}

					generator.getSuffix(author, year, currentChunk, expPubId);
				}
			}

			Pattern notSyncedPattern = Pattern.compile(NOT_SYNCED_LABEL_PATTERN);
			Matcher matcher = notSyncedPattern.matcher(shortLabel);
			matcher.find();

			String author = matcher.group(1);
			String strYear = matcher.group(2);

			int year;

			try {
				year = Integer.parseInt(strYear);
			} catch (NumberFormatException e) {
				throw new IntactException("The year part of the experiment short label is not numberic: " + strYear + " (" + shortLabel + ")");
			}

			syncedLabel = shortLabel + generator.getSuffix(author, year, 0, pubmedId);

		} else {

			List<String> expLabels = experimentDao.getShortLabelsLike(shortLabel + "%");

			int maxSuffix = 0;

			for (String expLabel : expLabels) {
				String strSuffix = expLabel.substring(expLabel.lastIndexOf("-") + 1, expLabel.length());

				int suffix = Integer.valueOf(strSuffix);

				maxSuffix = Math.max(maxSuffix, suffix);
			}

			syncedLabel = shortLabel + "-" + (maxSuffix + 1);
		}

		//} else {
		//    throw new IllegalArgumentException("Short label with wrong format: "+shortLabel);
		//}

		return syncedLabel;
	}

	/**
	 * Returns true if the experiment label matches this regex: wwww-dddd-d+
	 *
	 * @param experimentShortLabel the experiment short label to match
	 * @return true if matched
	 * @since 1.6.2
	 */
	public static boolean matchesSyncedLabel(String experimentShortLabel) {
		return experimentShortLabel.matches(SYNCED_LABEL_PATTERN);
	}

	/**
	 * Returns true if the experiment label matches this regex: wwww-dddd
	 *
	 * @param experimentShortLabel the experiment short label to match
	 * @return true if matched
	 * @since 1.6.2
	 */
	public static boolean matchesMotSyncedLabel(String experimentShortLabel) {
		return experimentShortLabel.matches(NOT_SYNCED_LABEL_PATTERN);
	}

	/**
	 * Checks if the given Experiment has an annotation with CvTopic( accepted ).
	 *
	 * @param experiment the experiment to check on
	 * @return true is at least one of such annotation is found.
	 */
	public static boolean isAccepted(Experiment experiment) {

		if (experiment == null) {
			throw new NullPointerException("You must give a non null experiment");
		}

		for (Annotation a : experiment.getAnnotations()) {
			if (a.getCvTopic() != null && CvTopic.ACCEPTED.equals(a.getCvTopic().getShortLabel())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if all given Experiments have an annotation with CvTopic( accepted ).
	 * <p/>
	 * An empty collection is seen as not accepted.
	 *
	 * @param experiments experiments to check on
	 * @return true is at least one of such annotation is found on each experiment.
	 */
	public static boolean areAllAccepted(Collection<Experiment> experiments) {

		if (experiments == null) {
			throw new IllegalArgumentException("You must give a non null experiments");
		}

		if (experiments.isEmpty()) {
			return false;
		}

		boolean allAccepted = true;

		// Check that all of these experiments have been accepted
		for (Experiment experiment : experiments) {

			boolean accepted = isAccepted(experiment);

			if (log.isDebugEnabled()) {
				if (!accepted) {
					log.debug(experiment.getShortLabel() + " was NOT accepted.");
				} else {
					log.debug(experiment.getShortLabel() + " was accepted.");
				}
			}
			allAccepted = accepted && allAccepted;
		}

		if (log.isDebugEnabled()) {
			if (!allAccepted) {
				log.debug("Not all experiment were accepted. abort.");
			}
		}
		return allAccepted;
	}


	/**
	 * Checks if an experiment or the experiments for the same publication are on hold.
	 * If the Experiment object contains a Publication, all the experiments from the publication
	 * are checked. If any of them is on hold, the method will return true.
	 *
	 * @param experiment The experiment to check
	 * @return true if the experiment is on hold, or any of the experiments for the same publication object
	 */
	public static boolean isOnHold(Experiment experiment) {
		if (experiment.getPublication() != null) {
			return isPublicationOnHold(experiment.getPublication());
		}

		for (Annotation annotation : experiment.getAnnotations()) {
			if (annotation.getCvTopic() != null && CvTopic.ON_HOLD.equals(annotation.getCvTopic().getShortLabel())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if any of the experiments for a publication is On Hold
	 *
	 * @param publication The publication to check
	 * @return true if any of the experiments has the cv topic on hold in its annotations
	 */
	public static boolean isPublicationOnHold(Publication publication) {
		for (Experiment experiment : publication.getExperiments()) {
			for (Annotation annotation : experiment.getAnnotations()) {
				if (annotation.getCvTopic() != null && CvTopic.ON_HOLD.equals(annotation.getCvTopic().getShortLabel())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if an experiment contains the annotation with topic "To be reviewed"
	 *
	 * @param experiment The experiment to check
	 * @return true if it contains the annotation
	 */
	public static boolean isToBeReviewed(Experiment experiment) {
		for (Annotation annotation : experiment.getAnnotations()) {
			if (annotation.getCvTopic() != null && CvTopic.TO_BE_REVIEWED.equals(annotation.getCvTopic().getShortLabel())) {
				return true;
			}
		}
		return false;
	}

}
