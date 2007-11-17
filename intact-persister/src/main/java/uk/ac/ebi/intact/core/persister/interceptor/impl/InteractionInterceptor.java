package uk.ac.ebi.intact.core.persister.interceptor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.persister.interceptor.PrePersistInterceptor;
import uk.ac.ebi.intact.core.persister.interceptor.PreUpdateInterceptor;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.model.util.IllegalLabelFormatException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionInterceptor implements PrePersistInterceptor<Interaction>, PreUpdateInterceptor<Interaction>
{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InteractionInterceptor.class);

    public void onPrePersist(Interaction objToPersist)
    {
        String shortLabel = objToPersist.getShortLabel();
        String newShortLabel = null;
        try {
            newShortLabel = InteractionUtils.syncShortLabelWithDb(shortLabel);
        } catch (IllegalLabelFormatException e) {
            if (log.isErrorEnabled()) log.error("Interaction with unexpected label, but will be persisted as is: "+objToPersist, e);
            newShortLabel = shortLabel;
        }

        if (!shortLabel.equals(newShortLabel)) {
            if (log.isDebugEnabled()) log.debug("Interaction with label '"+shortLabel+"' renamed '"+newShortLabel+"'" );
            objToPersist.setShortLabel(newShortLabel);
        }
    }

    public void onPreUpdate(Interaction objToPersist) {
        // nothing
    }
}