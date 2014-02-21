package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.CurationDepth;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

import java.util.Collection;
import java.util.Date;

/**
 * Simple Publication DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface PublicationDao<I extends IntactPublication> extends IntactBaseDao<I> {
    public I getByAc(String ac);

    public I getByPubmedId(String value);

    public I getByDOI(String value);

    public Collection<I> getByTitle(String value);

    public Collection<I> getByTitleLike(String value);

    public Collection<I> getByJournal(String value);

    public Collection<I> getByJournalLike(String value);

    public Collection<I> getByPublicationDate(Date value);

    public Collection<I> getByXref(String primaryId);

    public Collection<I> getByXrefLike(String primaryId);

    public Collection<I> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<I> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<I> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<I> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<I> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<I> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

}
