package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.CurationDepth;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

import java.util.Collection;
import java.util.Date;

/**
 * Publication DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface PublicationDao extends IntactBaseDao<IntactPublication> {
    public IntactPublication getByAc(String ac);

    public IntactPublication getByPubmedId(String value);

    public IntactPublication getByDOI(String value);

    public IntactPublication getByIMEx(String value);

    public Collection<IntactPublication> getByTitle(String value);

    public Collection<IntactPublication> getByTitleLike(String value);

    public Collection<IntactPublication> getByJournal(String value);

    public Collection<IntactPublication> getByJournalLike(String value);

    public Collection<IntactPublication> getByPublicationDate(Date value);

    public Collection<IntactPublication> getByReleasedDate(Date value);

    public Collection<IntactPublication> getByXref(String primaryId);

    public Collection<IntactPublication> getByXrefLike(String primaryId);

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactPublication> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactPublication> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactPublication> getByCurationDepth(CurationDepth depth, int first, int max);

    public Collection<IntactPublication> getByLifecycleEvent(String evtName, int first, int max);

    public Collection<IntactPublication> getByStatus(String statusName, int first, int max);

    public Collection<IntactPublication> getByCurator(String login, int first, int max);

    public Collection<IntactPublication> getByReviewer(String login, int first, int max);

    public Collection<IntactPublication> getBySource(String name, int first, int max);
}
