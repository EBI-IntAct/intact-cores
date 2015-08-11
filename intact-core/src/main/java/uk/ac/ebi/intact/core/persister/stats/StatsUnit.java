package uk.ac.ebi.intact.core.persister.stats;

import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface StatsUnit extends Serializable {
    String getAc();

    String getShortLabel();

    Class<? extends AnnotatedObject> getType();
}
