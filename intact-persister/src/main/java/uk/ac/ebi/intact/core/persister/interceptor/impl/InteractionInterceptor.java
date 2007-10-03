package uk.ac.ebi.intact.core.persister.interceptor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.persister.interceptor.PrePersistInterceptor;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.InteractionUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionInterceptor implements PrePersistInterceptor<Interaction>
{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InteractionInterceptor.class);

    public void onPrePersist(Interaction objToPersist)
    {
        String shortLabel = objToPersist.getShortLabel();
        String newShortLabel = InteractionUtils.syncShortLabelWithDb(shortLabel);

        if (!shortLabel.equals(newShortLabel)) {
            if (log.isDebugEnabled()) log.debug("Interaction with label '"+shortLabel+"' renamed '"+newShortLabel+"'" );
            objToPersist.setShortLabel(newShortLabel);
        }
    }

}