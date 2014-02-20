package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.AliasDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
import uk.ac.ebi.intact.jami.synchronizer.impl.AliasSynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of alias dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class AliasDaoImpl<A extends AbstractIntactAlias> extends AbstractIntactBaseDao<Alias, A> implements AliasDao<A> {

    public AliasDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<A>)AbstractIntactAlias.class, entityManager, context);
    }

    public AliasDaoImpl(Class<A> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public Collection<A> getByName(String name) {
        Query query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where a.name = :name");
        query.setParameter("name",name);
        return query.getResultList();
    }

    public Collection<A> getByNameLike(String name) {
        Query query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where upper(a.name) like :name");
        query.setParameter("name", "%" + name.toUpperCase() + "%");
        return query.getResultList();
    }

    public Collection<A> getByType(String typeName, String typeMI) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a where a.type is null");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
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
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :typeName");
            query.setParameter("typeName", typeName);
        }
        return query.getResultList();
    }

    public Collection<A> getByTypeAndName(String name, String typeName, String typeMI) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "where a.type is null " +
                    "and a.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and a.name = :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", name);
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :typeName " +
                    "and a.name = :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<A> getByTypeAndNameLike(String name, String typeName, String typeMI) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "where a.type is null " +
                    "and upper(a.name) like :name");
            query.setParameter("name", "%" + name.toUpperCase() + "%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and upper(a.name) like :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", "%" + name.toUpperCase() + "%");
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :typeName " +
                    "and upper(a.name) like :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", "%" + name.toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public Collection<A> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select a from " + getEntityClass() + " a " +
                "join a.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public void setEntityClass(Class<A> entityClass) {
        super.setEntityClass(entityClass);
        getDbSynchronizer().setIntactClass(entityClass);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new AliasSynchronizerTemplate<A>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }
}
