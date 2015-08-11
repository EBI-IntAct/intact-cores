package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;

import java.util.Collection;

/**
 * Feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface FeatureDao<F extends AbstractIntactFeature> extends IntactBaseDao<F>{

    public F getByAc(String ac);

    public Collection<F> getByShortName(String value);

    public Collection<F> getByShortNameLike(String value);

    public Collection<F> getByXref(String primaryId);

    public Collection<F> getByXrefLike(String primaryId);

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<F> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<F> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<F> getByAliasName(String name);

    public Collection<F> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public Collection<F> getByAliasNameLike(String name);

    public Collection<F> getByAliasTypeAndNameLike(String typeName, String typeMI, String name);

    public Collection<F> getByFeatureType(String typeName, String typeMI);

    public Collection<F> getByFeatureRole(String effectName, String effectMI);

    public Collection<F> getByInterproIdentifier(String primaryId);

    public Collection<F> getByParticipantAc(String ac);

    public Collection<F> getByIsLinkProperty(boolean isLinked);

    public Collection<F> getByRangeStartStatus(String statusName, String statusMI);

    public Collection<F> getByEndStatus(String statusName, String statusMI);

    public Collection<F> getByStartAndEndStatus(String startName, String startMI, String endName, String endMI);

    public Collection<F> getByResultingSequenceXref(String primaryId);

    public Collection<F> getByResultingSequenceXrefLike(String primaryId);

    public Collection<F> getByResultingSequenceXref(String dbName, String dbMI, String primaryId);

    public Collection<F> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<F> getByResultingSequenceXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<F> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<Xref> getXrefsForFeature(String ac);

    public Collection<Annotation> getAnnotationsForFeature(String ac);

    public Collection<Alias> getAliasesForFeature(String ac);

    public int countAliasesForFeature(String ac);

    public int countXrefsForFeature(String ac);

    public int countAnnotationsForFeature(String ac);

    public int countRangesForFeature(String ac);
}
