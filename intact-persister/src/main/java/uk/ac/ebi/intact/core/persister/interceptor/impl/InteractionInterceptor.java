package uk.ac.ebi.intact.core.persister.interceptor.impl;

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
    public void onPrePersist(Interaction objToPersist)
    {
        String shortLabel = objToPersist.getShortLabel();
        shortLabel = InteractionUtils.syncShortLabelWithDb(shortLabel);
        objToPersist.setShortLabel(shortLabel);
    }

}