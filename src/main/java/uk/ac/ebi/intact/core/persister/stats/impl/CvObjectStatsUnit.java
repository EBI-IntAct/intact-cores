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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CvObjectStatsUnit that = (CvObjectStatsUnit) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }
}
