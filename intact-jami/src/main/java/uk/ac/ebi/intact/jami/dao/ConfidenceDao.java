package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;

import java.util.Collection;

/**
 * Confidence DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ConfidenceDao<C extends AbstractIntactConfidence> extends IntactBaseDao<C>{
    public Collection<C> getByValue(String value);

    public Collection<C> getByType(String typeName, String typeMI);

    public Collection<C> getByTypeAndValue(String typeName, String typeMI, String value);

    public Collection<C> getByParentAc(String ac);
}
