package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.full.FullExperimentEnricher;
import psidev.psi.mi.jami.enricher.listener.ExperimentEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactExperimentEnricherListener;

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

public class ExperimentMergerEnrichOnly extends IntactDbMergerEnrichOnly<Experiment,IntactExperiment> implements ExperimentEnricher {

    public ExperimentMergerEnrichOnly(){
        super(new FullExperimentEnricher());
    }

    @Override
    protected ExperimentEnricher getBasicEnricher() {
        return (ExperimentEnricher)super.getBasicEnricher();
    }

    public OrganismEnricher getOrganismEnricher() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public PublicationEnricher getPublicationEnricher() {
        return null;
    }

    public ExperimentEnricherListener getExperimentEnricherListener() {
        return getBasicEnricher().getExperimentEnricherListener();
    }

    @Override
    public void setOrganismEnricher(OrganismEnricher organismEnricher) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> cvEnricher) {

    }

    @Override
    public void setPublicationEnricher(PublicationEnricher publicationEnricher) {

    }

    @Override
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
            if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onShortLabelUpdate(mergedExp, null);
            }
        }
        // merge participant identification method
        if (mergedExp.getParticipantIdentificationMethod() == null && exp1.getParticipantIdentificationMethod() != null){
            mergedExp.setParticipantIdentificationMethod(exp1.getParticipantIdentificationMethod());
            if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onParticipantDetectionMethodUpdate(mergedExp, null);
            }
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
                if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                    ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onAddedInteractionEvidence(exp, interaction);
                }
            }
        }
    }

    @Override
    protected void enrichBasicProperties(Experiment objectToEnrich, Experiment objectSource) throws EnricherException {
        super.enrichBasicProperties(objectToEnrich, objectSource);
        mergeInteractions(objectToEnrich, objectToEnrich.getInteractionEvidences(), objectSource.getInteractionEvidences());
    }
}

