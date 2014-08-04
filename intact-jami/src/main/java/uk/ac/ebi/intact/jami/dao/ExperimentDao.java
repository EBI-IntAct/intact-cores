package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;

import java.util.Collection;

/**
 * Experiment DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface ExperimentDao extends IntactBaseDao<IntactExperiment> {
    public IntactExperiment getByAc(String ac);

    public IntactExperiment getByShortLabel(String label);

    public Collection<IntactExperiment> getByShortLabelLike(String value);

    public Collection<IntactExperiment> getByPubmedId(String value);

    public Collection<IntactExperiment> getByDOI(String value);

    public Collection<IntactExperiment> getByIMEx(String value);

    public Collection<IntactExperiment> getByXref(String primaryId);

    public Collection<IntactExperiment> getByXrefLike(String primaryId);

    public Collection<IntactExperiment> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactExperiment> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactExperiment> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactExperiment> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactExperiment> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactExperiment> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactExperiment> getByVariableParameterDescription(String description);

    public Collection<IntactExperiment> getByPublicationAc(String ac);

    public Collection<Xref> getXrefsForExperiment(String ac);

    public int countInteractionsForExperiment(String ac);

    public int countXrefsForExperiment(String ac);

    public int countAnnotationsForExperiment(String ac);
}
