package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvExperimentalPreparation;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.CvIdentification;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Jul-2006</pre>
 */
@Mockable
public interface ComponentDao extends AnnotatedObjectDao<Component> {

    List<Component> getByInteractorAc( String interactorAc );
    List<Component> getByInteractionAc( String interactionAc );
    List<Component> getByExpressedIn(String biosourceAc);
    List<CvExperimentalPreparation> getExperimentalPreparationsForComponentAc( String componentAc);
    List<CvExperimentalRole> getExperimentalRolesForComponentAc( String componentAc);
    List<CvIdentification> getParticipantIdentificationMethodsForComponentAc( String componentAc);
}
