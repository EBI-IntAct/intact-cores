package uk.ac.ebi.intact.jami.dao.impl;


import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.ChecksumDao;
import uk.ac.ebi.intact.jami.finder.FinderException;
import uk.ac.ebi.intact.jami.finder.IntactCvTermFinderPersister;
import uk.ac.ebi.intact.jami.finder.IntactDbFinderPersister;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of checksum dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class ChecksumDaoImpl<C extends AbstractIntactChecksum> extends AbstractIntactBaseDao<C> implements ChecksumDao<C> {
    private IntactDbFinderPersister<CvTerm> methodFinder;

    public Collection<C> getByValue(String value) {
        Query query = getEntityManager().createQuery("select c from " + getEntityClass() + " c where c.value = :checksumValue");
        query.setParameter("checksumValue",value);
        return query.getResultList();
    }

    public Collection<C> getByMethod(String methodName, String methodMI) {
        Query query;
        if (methodMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.method as m " +
                    "join m.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", methodMI);
        }
        else{
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.method as m " +
                    "where m.shortName = :methodName");
            query.setParameter("methodName", methodName);
        }
        return query.getResultList();
    }

    public Collection<C> getByMethodAndValue(String methodName, String methodMI, String value) {
        Query query;
        if (methodMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.method as m " +
                    "join m.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and c.value = :checksumValue");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", methodMI);
            query.setParameter("checksumValue", value);
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :checksumName " +
                    "and a.value = :checksumValue");
            query.setParameter("checksumName", methodName);
            query.setParameter("checksumValue", value);
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

    public IntactDbFinderPersister<CvTerm> getMethodFinder() {
        if (this.methodFinder == null){
            this.methodFinder = new IntactCvTermFinderPersister(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);
        }
        return this.methodFinder;
    }

    public void setMethodFinder(IntactDbFinderPersister<CvTerm> methodFinder) {
        this.methodFinder = methodFinder;
    }

    @Override
    public void merge(C objToReplicate) {
        prepareChecksumMethodAndValue(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(C  objToPersist) {
        prepareChecksumMethodAndValue(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public C update(C  objToUpdate) {
        prepareChecksumMethodAndValue(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void prepareChecksumMethodAndValue(C objToPersist) {
        // prepare method
        CvTerm method = objToPersist.getMethod();
        IntactDbFinderPersister<CvTerm> typeFinder = getMethodFinder();
        typeFinder.clearCache();
        try {
            CvTerm existingType = typeFinder.find(method);
            if (existingType == null){
                existingType = typeFinder.persist(method);
            }
            objToPersist.setMethod(existingType);
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the checksum because could not synchronize its method.");
        }
        // check checksum value
        if (objToPersist.getValue() != null && objToPersist.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            objToPersist.setValue(objToPersist.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}