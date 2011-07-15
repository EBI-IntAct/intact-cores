package uk.ac.ebi.intact.core.persistence.dao.meta;

import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;
import uk.ac.ebi.intact.model.user.Preference;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.5.0
 */
public interface ApplicationPropertyDao extends IntactObjectDao<ApplicationProperty> {

    List<ApplicationProperty> getByApplicationAc(String ac);
}
