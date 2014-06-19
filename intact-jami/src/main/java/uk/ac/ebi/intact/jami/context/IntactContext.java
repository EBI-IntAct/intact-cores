package uk.ac.ebi.intact.jami.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.dao.IntactDao;

/**
 * The {@code IntactContext} class is the general context for the IntAct Core API.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class IntactContext {

    private static final Log log = LogFactory.getLog(IntactContext.class);

    @Autowired
    private IntactConfiguration config;

    @Autowired
    private UserContext userContext;

    @Autowired
    private IntactDao intactDao;

    public IntactContext() {
    }

    public IntactConfiguration getConfig() {
        return config;
    }

    public void setConfig(IntactConfiguration config) {
        this.config = config;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public IntactDao getIntactDao() {
        return intactDao;
    }

    public void setIntactDao(IntactDao intactDao) {
        this.intactDao = intactDao;
    }
}
