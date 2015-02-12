package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.SourceEnricherListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactSourceSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.SourceUpdates;
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

public class DbSourceEnricherListener implements SourceEnricherListener {
    private Map<Source, SourceUpdates> sourceUpdates;
    private SynchronizerContext context;
    private IntactSourceSynchronizer dbSynchronizer;

    public DbSourceEnricherListener(SynchronizerContext context, IntactSourceSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null source synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.sourceUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(CvTerm interactor, Annotation annotation) {
        if (this.sourceUpdates.containsKey(interactor)){
            this.sourceUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            SourceUpdates updates = new SourceUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.sourceUpdates.put((Source)interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(CvTerm interactor, Annotation annotation) {
        // nothing to do
    }


    @Override
    public void onEnrichmentComplete(Source object, EnrichmentStatus status, String message) {
        if (sourceUpdates.containsKey(object)){
            SourceUpdates updates = sourceUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getSourceXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            context.getSourceXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getIdentifiers().contains(obj)){
                            object.getIdentifiers().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getSourceAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getSourceAliasSynchronizer());
                    object.getSynonyms().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getSynonyms().contains(obj)){
                            object.getSynonyms().add(obj);
                        }
                    }
                }
                if (!updates.getAddedPrimaryXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedPrimaryXrefs(),
                            context.getSourceXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedPrimaryXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!((IntactSource)object).getDbXrefs().contains(obj)){
                            ((IntactSource)object).getDbXrefs().add(obj);
                        }
                    }
                }

                sourceUpdates.remove(object);
            } catch (PersisterException e) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e);
            } catch (FinderException e) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e);
            } catch (SynchronizerException e) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(Source object, String message, Exception e) {
        if (sourceUpdates.containsKey(object)){
            SourceUpdates updates = sourceUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getSourceXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            context.getSourceXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getIdentifiers().contains(obj)){
                            object.getIdentifiers().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getSourceAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getSourceAliasSynchronizer());
                    object.getSynonyms().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getSynonyms().contains(obj)){
                            object.getSynonyms().add(obj);
                        }
                    }
                }
                if (!updates.getAddedPrimaryXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedPrimaryXrefs(),
                            context.getSourceXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedPrimaryXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!((IntactSource)object).getDbXrefs().contains(obj)){
                            ((IntactSource)object).getDbXrefs().add(obj);
                        }
                    }
                }
                sourceUpdates.remove(object);
            } catch (PersisterException e2) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e2);
            } catch (FinderException e2) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e2);
            } catch (SynchronizerException e2) {
                sourceUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged source", e2);
            }
        }
    }

    @Override
    public void onAddedXref(CvTerm interactor, Xref xref) {
        if (this.sourceUpdates.containsKey(interactor)){
            this.sourceUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            SourceUpdates updates = new SourceUpdates();
            updates.getAddedXrefs().add(xref);
            this.sourceUpdates.put((Source)interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(CvTerm interactor, Xref xref) {
        // nothing to do
    }

    public Map<Source, SourceUpdates> getSourceUpdates() {
        return sourceUpdates;
    }

    @Override
    public void onShortNameUpdate(CvTerm t, String s) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeShortLabel((IntactSource)t);
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
        if (this.sourceUpdates.containsKey(t)){
            this.sourceUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            SourceUpdates updates = new SourceUpdates();
            updates.getAddedAliases().add(alias);
            this.sourceUpdates.put((Source)t, updates);
        }
    }

    @Override
    public void onRemovedAlias(CvTerm t, Alias alias) {
        // nothing to do
    }

    @Override
    public void onAddedIdentifier(CvTerm t, Xref xref) {
        if (this.sourceUpdates.containsKey(t)){
            this.sourceUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            SourceUpdates updates = new SourceUpdates();
            updates.getAddedIdentifiers().add(xref);
            this.sourceUpdates.put((Source)t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(CvTerm t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactSourceSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    @Override
    public void onUrlUpdate(Source source, String s) {
        // nothing to do
    }

    @Override
    public void onPostalAddressUpdate(Source source, String s) {
        // nothing to do
    }

    @Override
    public void onPublicationUpdate(Source t, Publication publication) {
        if (t.getPublication() != null){
            if (this.sourceUpdates.containsKey(t)){
                this.sourceUpdates.get(t).getAddedPrimaryXrefs().addAll(XrefUtils.collectAllXrefsHavingQualifier(t.getXrefs(), Xref.PRIMARY_MI, Xref.PRIMARY));
            }
            else{
                SourceUpdates updates = new SourceUpdates();
                updates.getAddedPrimaryXrefs().addAll(XrefUtils.collectAllXrefsHavingQualifier(t.getXrefs(), Xref.PRIMARY_MI, Xref.PRIMARY));
                this.sourceUpdates.put(t, updates);
            }
        }
    }
}
