package uk.ac.ebi.intact.core.persister.interceptor.impl;

import uk.ac.ebi.intact.core.persister.interceptor.PrePersistInterceptor;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.util.ExperimentUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentInterceptor implements PrePersistInterceptor<Experiment>
{
    public void onPrePersist(Experiment objToPersist)
    {
        String shortLabel = objToPersist.getShortLabel();
        shortLabel = ExperimentUtils.syncShortLabelWithDb(shortLabel);
        objToPersist.setShortLabel(shortLabel);
    }

}
