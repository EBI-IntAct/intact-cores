package uk.ac.ebi.intact.jami.dao.impl;


import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.ConfidenceDao;
import uk.ac.ebi.intact.jami.finder.FinderException;
import uk.ac.ebi.intact.jami.finder.IntactCvTermFinderPersister;
import uk.ac.ebi.intact.jami.finder.IntactDbFinderPersister;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of confidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class ConfidenceDaoImpl<C extends AbstractIntactConfidence> extends AbstractIntactBaseDao<C> implements ConfidenceDao<C> {
    private IntactDbFinderPersister<CvTerm> typeFinder;

    public Collection<C> getByValue(String value) {
        Query query = getEntityManager().createQuery("select c from " + getEntityClass() + " c where c.value = :confValue");
        query.setParameter("confValue",value);
        return query.getResultList();
    }

    public Collection<C> getByType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "where t.shortName = :confName");
            query.setParameter("confName", typeName);
        }
        return query.getResultList();
    }

    public Collection<C> getByTypeAndValue(String typeName, String typeMI, String value) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and c.value = :confValue");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("confValue", value);
        }
        else{
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "where t.shortName = :confName " +
                    "and c.value = :confValue");
            query.setParameter("confName", typeName);
            query.setParameter("confValue", value);
        }
        return query.getResultList();
    }

    public Collection<C> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select c from " + getEntityClass() + " c " +
                "join c.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    public IntactDbFinderPersister<CvTerm> getTypeFinder() {
        if (this.typeFinder == null){
            this.typeFinder = new IntactCvTermFinderPersister(getEntityManager(), IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        }
        return this.typeFinder;
    }

    public void setTypeFinder(IntactDbFinderPersister<CvTerm> typeFinder) {
        this.typeFinder = typeFinder;
    }

    @Override
    public void merge(C objToReplicate) {
        prepareConfidenceTypeAndValue(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(C  objToPersist) {
        prepareConfidenceTypeAndValue(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public C update(C  objToUpdate) {
        prepareConfidenceTypeAndValue(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void prepareConfidenceTypeAndValue(C objToPersist) {
        // prepare type
        CvTerm type = objToPersist.getType();
        IntactDbFinderPersister<CvTerm> typeFinder = getTypeFinder();
        typeFinder.clearCache();
        try {
            CvTerm existingType = typeFinder.find(type);
            if (existingType == null){
                existingType = typeFinder.persist(type);
            }
            objToPersist.setType(existingType);
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the confidence because could not synchronize its confidence type.");
        }
        // check confidence value
        if (objToPersist.getValue() != null && objToPersist.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            objToPersist.setValue(objToPersist.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}