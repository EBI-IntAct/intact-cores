package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;

import java.util.Collection;

/**
 * Interactor DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface InteractorDao<I extends IntactInteractor> extends IntactBaseDao<I> {
    public I getByAc(String ac);

    public I getByShortName(String value);

    public Collection<I> getByShortNameLike(String value);

    public Collection<I> getByXref(String primaryId);

    public Collection<I> getByXrefLike(String primaryId);

    public Collection<I> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<I> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<I> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<I> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<I> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<I> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<I> getByAliasName(String name);

    public Collection<I> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public Collection<I> getByAliasNameLike(String name);

    public Collection<I> getByAliasTypeAndNameLike(String typeName, String typeMI, String name);

    public Collection<I> getByInteractorType(String typeName, String typeMI);

    public Collection<I> getByTaxId(int taxid);

    public Collection<Xref> getXrefsForInteractor(String ac);

    public Collection<Annotation> getAnnotationsForInteractor(String ac);

    public Collection<Alias> getAliasesForInteractor(String ac);

}
