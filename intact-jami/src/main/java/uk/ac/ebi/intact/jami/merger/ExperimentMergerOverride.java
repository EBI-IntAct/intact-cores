package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.full.FullExperimentUpdater;
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

public class ExperimentMergerOverride extends IntactDbMergerOverride<Experiment,IntactExperiment> implements ExperimentEnricher {

    public ExperimentMergerOverride(){
        super(new FullExperimentUpdater());
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
    protected void mergeOtherProperties(IntactExperiment exp1, IntactExperiment exp2) {
        super.mergeOtherProperties(exp1, exp2);
        // reset parent to source parent
        exp2.setPublication(exp1.getPublication());

        // obj2 is mergedExp
        IntactExperiment mergedExp = exp2;

        // merge shortLabel
        if ((mergedExp.getShortLabel() != null && !mergedExp.getShortLabel().equals(exp1.getShortLabel()))
                || mergedExp.getShortLabel() == null){
            String oldLabel = mergedExp.getShortLabel();
            mergedExp.setShortLabel(exp1.getShortLabel());
            if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onShortLabelUpdate(mergedExp, oldLabel);
            }
        }
        // merge participant identification method
        if (mergedExp.getParticipantIdentificationMethod() != exp1.getParticipantIdentificationMethod()){
            CvTerm oldMethod = mergedExp.getParticipantIdentificationMethod();
            mergedExp.setParticipantIdentificationMethod(exp1.getParticipantIdentificationMethod());
            if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onParticipantDetectionMethodUpdate(mergedExp, oldMethod);
            }
        }
        //merge interactions
        if (exp1.areInteractionEvidencesInitialized()){
            mergeInteractions(mergedExp, mergedExp.getInteractionEvidences(), exp1.getInteractionEvidences());
        }
    }

    private void mergeInteractions(Experiment exp, Collection<InteractionEvidence> toEnrichInteractions, Collection<InteractionEvidence> sourceInteractions) {

        Iterator<InteractionEvidence> interactionIterator  = toEnrichInteractions.iterator();
        while(interactionIterator.hasNext()){
            InteractionEvidence interaction = interactionIterator.next();
            boolean containsInteraction = false;
            for (InteractionEvidence interaction2 : sourceInteractions){
                if (interaction == interaction2){
                    containsInteraction = true;
                    break;
                }
            }
            // remove interaction not in second list
            if (!containsInteraction){
                interactionIterator.remove();
                if (getBasicEnricher().getExperimentEnricherListener() instanceof IntactExperimentEnricherListener){
                    ((IntactExperimentEnricherListener)getBasicEnricher().getExperimentEnricherListener()).onRemovedInteractionEvidence(exp, interaction);
                }
            }
        }
        interactionIterator = sourceInteractions.iterator();
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

