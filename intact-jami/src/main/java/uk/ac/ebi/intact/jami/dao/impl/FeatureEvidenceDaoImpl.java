package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.FeatureEvidence;
import uk.ac.ebi.intact.jami.dao.FeatureEvidenceDao;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation for feature evidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class FeatureEvidenceDaoImpl extends FeatureDaoImpl<IntactFeatureEvidence, FeatureEvidence> implements FeatureEvidenceDao{

    public FeatureEvidenceDaoImpl() {
        super(IntactFeatureEvidence.class);
    }

    public FeatureEvidenceDaoImpl(EntityManager entityManager) {
        super(IntactFeatureEvidence.class, entityManager);
    }

    public Collection<IntactFeatureEvidence> getByFeatureDetectionMethod(String methodName, String methodMI) {
        Query query = getEntityManager().createQuery("select f from IntactFeatureEvidence f "  +
                "join f.detectionMethods as d " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }
}
