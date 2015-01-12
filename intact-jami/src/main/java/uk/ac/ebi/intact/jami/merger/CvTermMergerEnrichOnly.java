package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullCvTermEnricher;
import psidev.psi.mi.jami.enricher.listener.CvTermEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.OntologyTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.impl.CvTermSynchronizer;

import java.util.Collection;
import java.util.Iterator;

/**
 * Cv term merger based on the jami cv term enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class CvTermMergerEnrichOnly extends IntactDbMergerEnrichOnly<CvTerm, IntactCvTerm> implements CvTermEnricher<CvTerm>{

    public CvTermMergerEnrichOnly(CvTermSynchronizer intactSynchronizer){
        super(IntactCvTerm.class, new FullCvTermEnricher<CvTerm>(intactSynchronizer));
    }

    @Override
    protected CvTermEnricher getBasicEnricher() {
        return (CvTermEnricher) super.getBasicEnricher();
    }

    public CvTermFetcher<CvTerm> getCvTermFetcher() {
        return getBasicEnricher().getCvTermFetcher();
    }

    public CvTermEnricherListener<CvTerm> getCvTermEnricherListener() {
        return null;
    }

    @Override
    public void setCvTermEnricherListener(CvTermEnricherListener<CvTerm> listener) {

    }

    @Override
    public IntactCvTerm merge(IntactCvTerm obj1, IntactCvTerm obj2) {
        // obj2 is mergedCv
        IntactCvTerm mergedCv = super.merge(obj1, obj2);

        //merge parents
        if (obj1.areParentsInitialized()){
            mergeParents(mergedCv, mergedCv.getParents(), obj1.getParents());
        }
        // merge definition
        if (mergedCv.getDefinition() == null && obj1.getDefinition() != null){
            mergedCv.setDefinition(obj1.getDefinition());
        }
        return mergedCv;
    }

    private void mergeParents(IntactCvTerm cvToEnrich, Collection<OntologyTerm> toEnrichParents, Collection<OntologyTerm> sourceParents) {

        Iterator<OntologyTerm> ontologyIterator = sourceParents.iterator();
        while(ontologyIterator.hasNext()){
            OntologyTerm parent = ontologyIterator.next();
            boolean containsParent = false;
            for (OntologyTerm parent2 : toEnrichParents){
                // identical terms
                if (parent == parent2){
                    containsParent = true;
                    break;
                }
            }
            // add missing parent not in second list
            if (!containsParent){
                cvToEnrich.addParent(parent);
            }
        }
    }
}
