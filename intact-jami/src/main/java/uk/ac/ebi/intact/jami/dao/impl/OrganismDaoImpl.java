package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.OrganismDao;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.impl.OrganismSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of DAO for Organism
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class OrganismDaoImpl extends AbstractIntactBaseDao<Organism, IntactOrganism> implements OrganismDao{

    public OrganismDaoImpl() {
        super(IntactOrganism.class);
    }

    public OrganismDaoImpl(EntityManager entityManager) {
        super(IntactOrganism.class, entityManager);
    }

    public IntactOrganism getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public IntactOrganism getByShortName(String value) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where o.commonName = :name ");
        query.setParameter("name",value);
        List<IntactOrganism> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" organisms matching shortlabel "+value);
        }
    }

    public Collection<IntactOrganism> getByShortNameLike(String value) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where upper(o.commonName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByScientificName(String value) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where o.scientificName = :name");
        query.setParameter("name",value);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByScientificNameLike(String value) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where upper(o.scientificName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByTaxid(int taxid) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where o.persistentTaxid = :taxid");
        query.setParameter("taxid",Integer.toString(taxid));
        return query.getResultList();
    }

    public IntactOrganism getByTaxidOnly(int taxid) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where o.persistentTaxid = :taxid " +
                "and o.cellType is null " +
                "and o.tissue is null");
        query.setParameter("taxid",Integer.toString(taxid));
        List<IntactOrganism> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" organisms matching taxid "+taxid);
        }
    }

    public Collection<IntactOrganism> getByAliasName(String name) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.aliases as s " +
                "where s.name = :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByAliasNameLike(String name) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.aliases as s " +
                "where upper(s.name) = :name");
        query.setParameter("name", "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "where s.type is null " +
                    "and s.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "join s.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and s.name = :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", name);
        }
        else{
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and s.name = :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByAliasTypeAndNameLike(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "where s.type is null " +
                    "and upper(s.name) like :name");
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "join s.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and upper(s.name) like :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else{
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "join o.aliases as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and upper(s.name) like :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByCellTypeAc(String cellAc) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.cellType as c " +
                "where c.ac = :cellAc");
        query.setParameter("cellAc", cellAc);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByTissueAc(String tissueAc) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.tissue as t " +
                "where t.ac = :tissueAc");
        query.setParameter("tissueAc", tissueAc);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByCellTypeName(String cellName) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.cellType as c " +
                "where c.shortName = :cellName");
        query.setParameter("cellName", cellName);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByTissueName(String tissueName) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.tissue as t " +
                "where t.shortName = :tissueName");
        query.setParameter("tissueName", tissueName);
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByCellTypeNameLike(String cellName) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.cellType as c " +
                "where upper(c.shortName) like :cellName");
        query.setParameter("cellName", "%"+cellName.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactOrganism> getByTissueNameLike(String tissueName) {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "join o.tissue as t " +
                "where upper(t.shortName) like :tissueName");
        query.setParameter("tissueName", "%"+tissueName.toUpperCase()+"%");
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new OrganismSynchronizer(getEntityManager()));
    }
}
