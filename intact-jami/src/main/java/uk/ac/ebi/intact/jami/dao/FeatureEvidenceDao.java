package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;

import java.util.Collection;

/**
 * DAO for feature evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public interface FeatureEvidenceDao extends FeatureDao<IntactFeatureEvidence> {

    public Collection<IntactFeatureEvidence> getByFeatureDetectionMethod(String methodName, String methodMI);

    public int countParametersForFeature(String ac);
    public int countDetectionMethodsForFeature(String ac);
}
