package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.meta.Application;

/**
 * Application DAO.
 *
 */
public interface ApplicationDao extends IntactBaseDao<Application> {

    public Application getByAc(String ac);

    public Application getByKey(String key);
}
