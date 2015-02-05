package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.FeatureEvidenceEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.FeatureUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbFeatureEvidenceEnricherListener extends AbstractDbFeatureEnricherListener<FeatureEvidence> implements FeatureEvidenceEnricherListener{

    public DbFeatureEvidenceEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(FeatureEvidence object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        if (getFeatureUpdates().containsKey(object)) {
            FeatureUpdates<FeatureEvidence> updates = getFeatureUpdates().get(object);
            if (!updates.getAddedDetectionMethods().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedDetectionMethods(),
                        getContext().getFeatureDetectionMethodSynchronizer());
                object.getDetectionMethods().removeAll(updates.getAddedDetectionMethods());
                object.getDetectionMethods().addAll(synchronizedMethods);
            }
            if (!updates.getAddedParameters().isEmpty()) {

                List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getFeatureParameterSynchronizer());
                object.getParameters().removeAll(updates.getAddedParameters());
                object.getParameters().addAll(synchronizedParameters);
            }
        }

    }

    @Override
    protected void processOtherUpdates(FeatureEvidence object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        if (getFeatureUpdates().containsKey(object)) {
            FeatureUpdates<FeatureEvidence> updates = getFeatureUpdates().get(object);
            if (!updates.getAddedDetectionMethods().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedDetectionMethods(),
                        getContext().getFeatureDetectionMethodSynchronizer());
                object.getDetectionMethods().removeAll(updates.getAddedDetectionMethods());
                object.getDetectionMethods().addAll(synchronizedMethods);
            }
            if (!updates.getAddedParameters().isEmpty()) {

                List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getFeatureParameterSynchronizer());
                object.getParameters().removeAll(updates.getAddedParameters());
                object.getParameters().addAll(synchronizedParameters);
            }
        }
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return getContext().getFeatureEvidenceXrefSynchronizer();
    }

    @Override
    protected AnnotationSynchronizer getAnnotationSynchronizer() {
        return getContext().getFeatureEvidenceAnnotationSynchronizer();
    }

    @Override
    protected AliasSynchronizer getAliasSynchronizer() {
        return getContext().getFeatureEvidenceAliasSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getRangeSynchronizer() {
        return getContext().getExperimentalRangeSynchronizer();
    }

    @Override
    public void onAddedDetectionMethod(FeatureEvidence t, CvTerm cvTerm) {
        if (getFeatureUpdates().containsKey(t)){
            getFeatureUpdates().get(t).getAddedDetectionMethods().add(cvTerm);
        }
        else{
            FeatureUpdates<FeatureEvidence> updates = new FeatureUpdates<FeatureEvidence>();
            updates.getAddedDetectionMethods().add(cvTerm);
            getFeatureUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedDetectionMethod(FeatureEvidence featureEvidence, CvTerm cvTerm) {
        // nothing to do
    }

    @Override
    public void onAddedParameter(FeatureEvidence t, Parameter parameter) {
        if (getFeatureUpdates().containsKey(t)){
            getFeatureUpdates().get(t).getAddedParameters().add(parameter);
        }
        else{
            FeatureUpdates<FeatureEvidence> updates = new FeatureUpdates<FeatureEvidence>();
            updates.getAddedParameters().add(parameter);
            getFeatureUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedParameter(FeatureEvidence featureEvidence, Parameter parameter) {
        // nothing to do
    }
}
