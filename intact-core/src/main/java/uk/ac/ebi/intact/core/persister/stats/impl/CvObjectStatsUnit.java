package uk.ac.ebi.intact.core.persister.stats.impl;

import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectStatsUnit extends AnnotatedObjectStatsUnit {

    private String identifier;

    public CvObjectStatsUnit(CvObject cvObject) {
        super(cvObject);

        identifier = CvObjectUtils.getIdentity(cvObject);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return super.toString()+", "+getIdentifier();
    }
}
