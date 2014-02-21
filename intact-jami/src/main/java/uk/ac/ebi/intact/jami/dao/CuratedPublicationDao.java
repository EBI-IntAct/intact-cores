package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.CurationDepth;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;

import java.util.Collection;

/**
 * Curated Publication DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface CuratedPublicationDao extends PublicationDao<IntactCuratedPublication> {

    public IntactCuratedPublication getByIMEx(String value);

    public Collection<IntactCuratedPublication> getByCurationDepth(CurationDepth depth, int first, int max);

    public Collection<IntactCuratedPublication> getByLifecycleEvent(String evtName, int first, int max);

    public Collection<IntactCuratedPublication> getByStatus(String statusName, int first, int max);

    public Collection<IntactCuratedPublication> getByCurator(String login, int first, int max);

    public Collection<IntactCuratedPublication> getByReviewer(String login, int first, int max);

    public Collection<IntactCuratedPublication> getBySource(String name, int first, int max);
}
