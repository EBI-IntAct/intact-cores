package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.CausalRelationshipDao;
import uk.ac.ebi.intact.jami.dao.PreferenceDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.synchronizer.IntactCausalRelationchipSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactPreferenceSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of preference dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class PreferenceDaoImpl extends AbstractIntactBaseDao<Preference, Preference> implements PreferenceDao {

    public PreferenceDaoImpl() {
        super(Preference.class);
    }

    public PreferenceDaoImpl(EntityManager entityManager) {
        super(Preference.class, entityManager);
    }

    public Collection<Preference> getByKey(String key, int first, int max) {
        Query query = getEntityManager().createQuery("select p from Preference p " +
                "where p.key = :keyValue");
        query.setParameter("keyValue", key);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<Preference> getByUserAc(String userAc) {
        Query query = getEntityManager().createQuery("select p from Preference p " +
                "join p.user as u " +
                "where u.ac = :ac ");
        query.setParameter("ac",userAc);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactPreferenceSynchronizer(getEntityManager()));
    }
}