package uk.ac.ebi.intact.jami.dao.impl;


import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ChecksumDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.synchronizer.impl.ChecksumSynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of checksum dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class ChecksumDaoImpl<C extends AbstractIntactChecksum> extends AbstractIntactBaseDao<Checksum, C> implements ChecksumDao<C> {

    public ChecksumDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<C>)AbstractIntactChecksum.class, entityManager, context);
    }

    public ChecksumDaoImpl(Class<C> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

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
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.method as m " +
                    "where m.shortName = :checksumName " +
                    "and c.value = :checksumValue");
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

    @Override
    public void setEntityClass(Class<C> entityClass) {
        super.setEntityClass(entityClass);
        getDbSynchronizer().setIntactClass(entityClass);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ChecksumSynchronizerTemplate<C>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }
}