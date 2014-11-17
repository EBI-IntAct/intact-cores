package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.DbSynchronizerListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class CompositeFeatureSynchronizer implements IntactDbSynchronizer<Feature, AbstractIntactFeature> {

    private SynchronizerContext context;

    public CompositeFeatureSynchronizer(SynchronizerContext context){
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
    }

    public AbstractIntactFeature find(Feature term) throws FinderException {
        if (term instanceof FeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().find((FeatureEvidence)term);
        }
        else if (term instanceof ModelledFeature){
            return this.context.getModelledFeatureSynchronizer().find((ModelledFeature)term);
        }
        else {
            return null;
        }
    }

    @Override
    public Collection<AbstractIntactFeature> findAll(Feature term) {
        if (term instanceof FeatureEvidence){
            return new ArrayList<AbstractIntactFeature>(this.context.getFeatureEvidenceSynchronizer().findAll((FeatureEvidence)term));
        }
        else if (term instanceof ModelledFeature){
            return new ArrayList<AbstractIntactFeature>(this.context.getModelledFeatureSynchronizer().findAll((ModelledFeature) term));
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(Feature term) {
        if (term instanceof FeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().findAllMatchingAcs((FeatureEvidence) term);
        }
        else if (term instanceof ModelledFeature){
            return this.context.getModelledFeatureSynchronizer().findAllMatchingAcs((ModelledFeature) term);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    public AbstractIntactFeature persist(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().persist((IntactFeatureEvidence)term);
        }
        else {
            return this.context.getModelledFeatureSynchronizer().find((IntactModelledFeature)term);
        }
    }

    public AbstractIntactFeature synchronize(Feature term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof FeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().synchronize((FeatureEvidence)term, persist);
        }
        else if (term instanceof ModelledFeature) {
            return this.context.getModelledFeatureSynchronizer().synchronize((ModelledFeature)term, persist);
        }
        else{
            IntactFeatureEvidence featureEvidence = new IntactFeatureEvidence();
            FeatureCloner.copyAndOverrideBasicFeaturesProperties(term, featureEvidence);

            return this.context.getFeatureEvidenceSynchronizer().synchronize(featureEvidence, persist);
        }
    }

    public void synchronizeProperties(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            this.context.getFeatureEvidenceSynchronizer().synchronizeProperties((IntactFeatureEvidence)term);
        }
        else {
            this.context.getModelledFeatureSynchronizer().synchronizeProperties((IntactModelledFeature)term);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public IntactDbMerger<Feature, AbstractIntactFeature> getIntactMerger() {
        return null;
    }

    public void setIntactMerger(IntactDbMerger<Feature, AbstractIntactFeature> intactMerger) {
        throw new UnsupportedOperationException("The feature synchronizer does not support this method as it is a composite synchronizer");
    }

    public Class<? extends AbstractIntactFeature> getIntactClass() {
        return AbstractIntactFeature.class;
    }

    public void setIntactClass(Class<? extends AbstractIntactFeature> intactClass) {
        throw new UnsupportedOperationException("The feature synchronizer does not support this method as it is a composite synchronizer");
    }

    public boolean delete(Feature term) {
        if (term instanceof FeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().delete((FeatureEvidence) term);
        }
        else if (term instanceof ModelledFeature){
            return this.context.getModelledFeatureSynchronizer().delete((ModelledFeature)term);
        }
        else{
            return false;
        }
    }

    @Override
    public AbstractIntactFeature convertToPersistentObject(Feature term) throws SynchronizerException, PersisterException, FinderException {
        if (term instanceof FeatureEvidence){
            return this.context.getFeatureEvidenceSynchronizer().convertToPersistentObject((FeatureEvidence)term);
        }
        else if (term instanceof ModelledFeature) {
            return this.context.getModelledFeatureSynchronizer().convertToPersistentObject((ModelledFeature)term);
        }
        else{
            IntactFeatureEvidence featureEvidence = new IntactFeatureEvidence();
            FeatureCloner.copyAndOverrideBasicFeaturesProperties(term, featureEvidence);

            return this.context.getFeatureEvidenceSynchronizer().convertToPersistentObject(featureEvidence);
        }
    }

    @Override
    public void flush() {
        this.context.getFeatureEvidenceSynchronizer().flush();
        this.context.getModelledFeatureSynchronizer().flush();
    }

    @Override
    public DbSynchronizerListener getListener() {
        return this.context.getSynchronizerListener();
    }

    @Override
    public void setListener(DbSynchronizerListener listener) {
        this.context.getModelledFeatureSynchronizer().setListener(listener);
        this.context.getFeatureEvidenceSynchronizer().setListener(listener);
    }
}
