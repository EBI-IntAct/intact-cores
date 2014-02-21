package uk.ac.ebi.intact.jami.service;

import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service interface for intact basic services
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */

public interface IntactService<I> {

    public List<I> fetchIntactObjects(String query, Map<String, Object> queryParameters, int first, int max);

    public void saveOrUpdate(I object) throws PersisterException, FinderException, SynchronizerException;

    public void saveOrUpdate(Collection<? extends I> objects) throws SynchronizerException, PersisterException, FinderException;

    public void delete(I object) throws PersisterException, FinderException, SynchronizerException;

    public void delete(Collection<? extends I> objects) throws SynchronizerException, PersisterException, FinderException;
}
