package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.InteractorPoolEnricher;
import psidev.psi.mi.jami.enricher.impl.CompositeInteractorEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullInteractorPoolEnricher;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.InteractorPool;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.synchronizer.impl.InteractorPoolSynchronizer;

import java.util.Comparator;

/**
 * Interactor pool merger based on the jami interactor pool enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class InteractorPoolMergerEnrichOnly extends InteractorBaseMergerEnrichOnly<InteractorPool, IntactInteractorPool>
implements InteractorPoolEnricher{

    public InteractorPoolMergerEnrichOnly(InteractorPoolSynchronizer intactSynchronizer){
        super(new FullInteractorPoolEnricher(intactSynchronizer));
    }

    @Override
    protected InteractorPoolEnricher getBasicEnricher() {
        return (InteractorPoolEnricher)super.getBasicEnricher();
    }

    public CompositeInteractorEnricher getInteractorEnricher() {
        return null;
    }

    public Comparator<Interactor> getInteractorComparator() {
        return getBasicEnricher().getInteractorComparator();
    }

    @Override
    public void setInteractorEnricher(CompositeInteractorEnricher enricher) {

    }

    @Override
    public void setInteractorComparator(Comparator<Interactor> comparator) {

    }
}

