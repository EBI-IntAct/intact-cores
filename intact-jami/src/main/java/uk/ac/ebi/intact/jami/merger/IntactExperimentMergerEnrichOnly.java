package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.impl.FullExperimentEnricher;
import psidev.psi.mi.jami.enricher.listener.ExperimentEnricherListener;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;

import java.util.Collection;
import java.util.Iterator;

/**
 * Experiment merger based on the jami experiment enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactExperimentMergerEnrichOnly extends IntactDbMergerEnrichOnly<Experiment,IntactExperiment> implements ExperimentEnricher {

    public IntactExperimentMergerEnrichOnly(){
        super(new FullExperimentEnricher());
    }

    @Override
    protected ExperimentEnricher getBasicEnricher() {
        return (ExperimentEnricher)super.getBasicEnricher();
    }

    public void setOrganismEnricher(OrganismEnricher organismEnricher) {
        getBasicEnricher().setOrganismEnricher(organismEnricher);
    }

    public OrganismEnricher getOrganismEnricher() {
        return getBasicEnricher().getOrganismEnricher();
    }

    public void setCvTermEnricher(CvTermEnricher cvTermEnricher) {
        getBasicEnricher().setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher getCvTermEnricher() {
        return getBasicEnricher().getCvTermEnricher();
    }

    public void setPublicationEnricher(PublicationEnricher publicationEnricher) {
        getBasicEnricher().setPublicationEnricher(publicationEnricher);
    }

    public PublicationEnricher getPublicationEnricher() {
        return getBasicEnricher().getPublicationEnricher();
    }

    public ExperimentEnricherListener getExperimentEnricherListener() {
        return getBasicEnricher().getExperimentEnricherListener();
    }

    public void setExperimentEnricherListener(ExperimentEnricherListener listener) {
        getBasicEnricher().setExperimentEnricherListener(listener);
    }

    @Override
    public IntactExperiment merge(IntactExperiment exp1, IntactExperiment exp2) {
        // reset parent to source parent
        exp2.setPublication(exp1.getPublication());

        // obj2 is mergedExp
        IntactExperiment mergedExp = super.merge(exp1, exp2);

        // merge shortLabel
        if (mergedExp.getShortLabel() == null){
            mergedExp.setShortLabel(exp1.getShortLabel());
        }
        // merge participant identification method
        if (mergedExp.getParticipantIdentificationMethod() == null && exp1.getParticipantIdentificationMethod() != null){
            mergedExp.setParticipantIdentificationMethod(exp1.getParticipantIdentificationMethod());
        }
        //merge interactions
        if (exp1.areInteractionEvidencesInitialized()){
            mergeInteractions(mergedExp, mergedExp.getInteractionEvidences(), exp1.getInteractionEvidences());
        }

        return mergedExp;
    }

    private void mergeInteractions(Experiment exp, Collection<InteractionEvidence> toEnrichInteractions, Collection<InteractionEvidence> sourceInteractions) {

        Iterator<InteractionEvidence> interactionIterator = sourceInteractions.iterator();
        while(interactionIterator.hasNext()){
            InteractionEvidence interaction = interactionIterator.next();
            boolean containsInteraction = false;
            for (InteractionEvidence interaction2 : toEnrichInteractions){
                // identical interaction
                if (interaction == interaction2){
                    containsInteraction = true;
                    break;
                }
            }
            // add missing interaction not in second list
            if (!containsInteraction){
                exp.addInteractionEvidence(interaction);
            }
        }
    }
}

