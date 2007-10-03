package uk.ac.ebi.intact.core.persister.interceptor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.persister.interceptor.PrePersistInterceptor;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.util.ExperimentUtils;
import uk.ac.ebi.intact.model.util.InteractionUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentInterceptor implements PrePersistInterceptor<Experiment>
{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(ExperimentInterceptor.class);

    public void onPrePersist(Experiment objToPersist)
    {
        updateShortLabel(objToPersist);
    }

    protected void updateShortLabel(Experiment experiment) {
        String shortLabel = experiment.getShortLabel();
        String newShortLabel = ExperimentUtils.syncShortLabelWithDb(shortLabel);

        if (!shortLabel.equals(newShortLabel)) {
            if (log.isDebugEnabled()) log.debug("Experiment with label '"+shortLabel+"' renamed '"+newShortLabel+"'" );
            experiment.setShortLabel(newShortLabel);
        }
    }

}
