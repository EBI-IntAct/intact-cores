package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactCvSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactCvEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.CvUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;
import java.util.Map;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbCvEnricherListener implements IntactCvEnricherListener {
    private Map<CvTerm, CvUpdates> cvUpdates;
    private SynchronizerContext context;
    private IntactCvSynchronizer dbSynchronizer;

    public DbCvEnricherListener(SynchronizerContext context, IntactCvSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null cv synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.cvUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(CvTerm interactor, Annotation annotation) {
        if (this.cvUpdates.containsKey(interactor)){
            this.cvUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            CvUpdates updates = new CvUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.cvUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(CvTerm interactor, Annotation annotation) {
        // nothing to do
    }


    @Override
    public void onEnrichmentComplete(CvTerm object, EnrichmentStatus status, String message) {
        if (cvUpdates.containsKey(object)){
            CvUpdates updates = cvUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getCvXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            context.getCvXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getIdentifiers().contains(obj)){
                            object.getIdentifiers().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getCvAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedDbAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedDbAnnotations(),
                            context.getCvAnnotationSynchronizer());
                    ((IntactCvTerm)object).getDbAnnotations().removeAll(updates.getAddedDbAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!((IntactCvTerm)object).getDbAnnotations().contains(obj)){
                            ((IntactCvTerm)object).getDbAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getCvAliasSynchronizer());
                    object.getSynonyms().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getSynonyms().contains(obj)){
                            object.getSynonyms().add(obj);
                        }
                    }
                }
                if (!updates.getAddedParents().isEmpty()){

                    List<OntologyTerm> synchronizedParents = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedParents(),
                            dbSynchronizer);
                    ((IntactCvTerm)object).getParents().removeAll(updates.getAddedParents());
                    for (OntologyTerm obj : synchronizedParents){
                        if (!((IntactCvTerm) object).getParents().contains(obj)){
                            ((IntactCvTerm) object).getParents().add(obj);
                        }
                    }
                }

                cvUpdates.remove(object);
            } catch (PersisterException e) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e);
            } catch (FinderException e) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e);
            } catch (SynchronizerException e) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(CvTerm object, String message, Exception e) {
        if (cvUpdates.containsKey(object)){
            CvUpdates updates = cvUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getCvXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            context.getCvXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getIdentifiers().contains(obj)){
                            object.getIdentifiers().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getCvAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedDbAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedDbAnnotations(),
                            context.getCvAnnotationSynchronizer());
                    ((IntactCvTerm)object).getDbAnnotations().removeAll(updates.getAddedDbAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!((IntactCvTerm)object).getDbAnnotations().contains(obj)){
                            ((IntactCvTerm)object).getDbAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getCvAliasSynchronizer());
                    object.getSynonyms().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getSynonyms().contains(obj)){
                            object.getSynonyms().add(obj);
                        }
                    }
                }
                if (!updates.getAddedParents().isEmpty()){

                    List<OntologyTerm> synchronizedParents = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedParents(),
                            dbSynchronizer);
                    ((IntactCvTerm)object).getParents().removeAll(updates.getAddedParents());
                    for (OntologyTerm obj : synchronizedParents){
                        if (!((IntactCvTerm) object).getParents().contains(obj)){
                            ((IntactCvTerm) object).getParents().add(obj);
                        }
                    }
                }

                cvUpdates.remove(object);
            } catch (PersisterException e2) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e2);
            } catch (FinderException e2) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e2);
            } catch (SynchronizerException e2) {
                cvUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged cv", e2);
            }
        }
    }

    @Override
    public void onAddedXref(CvTerm interactor, Xref xref) {
        if (this.cvUpdates.containsKey(interactor)){
            this.cvUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            CvUpdates updates = new CvUpdates();
            updates.getAddedXrefs().add(xref);
            this.cvUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(CvTerm interactor, Xref xref) {
        // nothing to do
    }

    public Map<CvTerm, CvUpdates> getCvUpdates() {
        return cvUpdates;
    }

    @Override
    public void onShortNameUpdate(CvTerm t, String s) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeShortLabel((IntactCvTerm)t);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize cv label", e);
        }
    }

    @Override
    public void onFullNameUpdate(CvTerm t, String s) {
        // nothing to do
    }

    @Override
    public void onMIIdentifierUpdate(CvTerm cvTerm, String s) {
        // nothing to do
    }

    @Override
    public void onMODIdentifierUpdate(CvTerm cvTerm, String s) {
        // nothing to do
    }

    @Override
    public void onPARIdentifierUpdate(CvTerm cvTerm, String s) {
        // nothing to do
    }

    @Override
    public void onAddedAlias(CvTerm t, Alias alias) {
        if (this.cvUpdates.containsKey(t)){
            this.cvUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            CvUpdates updates = new CvUpdates();
            updates.getAddedAliases().add(alias);
            this.cvUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedAlias(CvTerm t, Alias alias) {
        // nothing to do
    }

    @Override
    public void onAddedIdentifier(CvTerm t, Xref xref) {
        if (this.cvUpdates.containsKey(t)){
            this.cvUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            CvUpdates updates = new CvUpdates();
            updates.getAddedIdentifiers().add(xref);
            this.cvUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(CvTerm t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactCvSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    @Override
    public void onAddedParent(IntactCvTerm t, OntologyTerm added) {
        if (this.cvUpdates.containsKey(t)){
            this.cvUpdates.get(t).getAddedParents().add(added);
        }
        else{
            CvUpdates updates = new CvUpdates();
            updates.getAddedParents().add(added);
            this.cvUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedParent(IntactCvTerm t, OntologyTerm removed) {
         // nothing to do
    }

    @Override
    public void onUpdatedDefinition(IntactCvTerm cv, String def) {
        if (cv.getDefinition() != null && cv instanceof IntactCvTerm){
            if (this.cvUpdates.containsKey(cv)){
                this.cvUpdates.get(cv).getAddedDbAnnotations().addAll(
                        AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getDbAnnotations(),
                                null, "definition"));
            }
            else{
                CvUpdates updates = new CvUpdates();
                updates.getAddedDbAnnotations().addAll(AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getDbAnnotations(),
                        null, "definition"));
                this.cvUpdates.put(cv, updates);
            }
        }
    }
}
