package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.InteractorEnricher;
import psidev.psi.mi.jami.enricher.InteractorPoolEnricher;
import psidev.psi.mi.jami.enricher.impl.FullInteractorPoolEnricher;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.InteractorPool;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorPoolSynchronizer;

import java.util.Comparator;

/**
 * Interactor pool merger based on the jami interactor pool enricher.
 * It will override properties in database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactInteractorPoolMergerOverride extends IntactInteractorBaseMergerOverride<InteractorPool, IntactInteractorPool>
implements InteractorPoolEnricher{

    public IntactInteractorPoolMergerOverride(IntactInteractorPoolSynchronizer intactSynchronizer){
        super(new FullInteractorPoolEnricher(intactSynchronizer));
    }

    @Override
    protected InteractorPoolEnricher getBasicEnricher() {
        return (InteractorPoolEnricher)super.getBasicEnricher();
    }

    public void setInteractorEnricher(InteractorEnricher<Interactor> interactorEnricher) {
        getBasicEnricher().setInteractorEnricher(interactorEnricher);
    }

    public InteractorEnricher<Interactor> getInteractorEnricher() {
        return getBasicEnricher().getInteractorEnricher();
    }

    public void setInteractorComparator(Comparator<Interactor> interactorComparator) {
        getBasicEnricher().setInteractorComparator(interactorComparator);
    }

    public Comparator<Interactor> getInteractorComparator() {
        return getBasicEnricher().getInteractorComparator();
    }
}

