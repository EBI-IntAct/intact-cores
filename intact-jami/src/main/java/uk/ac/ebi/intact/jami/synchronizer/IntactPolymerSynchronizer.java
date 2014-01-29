package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;

/**
 * Synchronizer for IntAct polymers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactPolymerSynchronizer<P extends IntactPolymer> extends IntactInteractorSynchronizer<P>{
    public IntactPolymerSynchronizer(EntityManager entityManager, Class<P> intactClass) {
        super(entityManager, intactClass);
    }

    public IntactPolymerSynchronizer(EntityManager entityManager, Class<P> intactClass, IntactDbSynchronizer<Alias, InteractorAlias> aliasSynchronizer, IntactDbSynchronizer<Annotation, InteractorAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, InteractorXref> xrefSynchronizer, IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<Checksum, InteractorChecksum> checksumSynchronizer) {
        super(entityManager, intactClass, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, organismSynchronizer, typeSynchronizer, checksumSynchronizer);
    }
}
