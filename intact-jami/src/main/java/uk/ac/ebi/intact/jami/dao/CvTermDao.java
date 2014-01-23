package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

import java.util.Collection;

/**
 * DAO factory for Cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public interface CvTermDao extends IntactBaseDao<IntactCvTerm>{

    public IntactCvTerm getByAc(String ac);

    public IntactCvTerm getByShortName(String value, String objClass);

    public Collection<IntactCvTerm> getByShortName(String value);

    public Collection<IntactCvTerm> getByShortNameLike(String value);

    public Collection<IntactCvTerm> getByXref(String primaryId);

    public Collection<IntactCvTerm> getByXrefLike(String primaryId);

    public Collection<IntactCvTerm> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactCvTerm> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactCvTerm> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactCvTerm> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactCvTerm> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactCvTerm> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactCvTerm> getByAliasName(String name);

    public Collection<IntactCvTerm> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public Collection<IntactCvTerm> getByAliasNameLike(String name);

    public Collection<IntactCvTerm> getByAliasTypeAndNameLike(String typeName, String typeMI, String name);

    public Collection<IntactCvTerm> getByDefinition(String des);

    public Collection<IntactCvTerm> getByDescriptionLike(String des);

    public Collection<IntactCvTerm> getByMIIdentifier(String primaryId);

    public Collection<IntactCvTerm> getByMODIdentifier(String primaryId);

    public Collection<IntactCvTerm> getByPARIdentifier(String primaryId);

    public IntactCvTerm getByMIIdentifier(String primaryId, String objClass);

    public IntactCvTerm getByMODIdentifier(String primaryId, String objClass);

    public IntactCvTerm getByPARIdentifier(String primaryId, String objClass);
}
