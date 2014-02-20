package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntity;

import java.util.Collection;

/**
 * experimental entity dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ExperimentalEntityDao<F extends IntactExperimentalEntity> extends EntityDao<F>{

    public Collection<F> getByExperimentalRole(String typeName, String typeMI, int first, int max);

    public Collection<F> getByExperimentalPreparation(String name, String mi, int first, int max);

    public Collection<F> getByDetectionMethod(String name, String mi, int first, int max);

    public Collection<F> getByExpressedInTaxid(String taxid, int first, int max);

    public Collection<F> getByExpressedInAc(String ac, int first, int max);
}
