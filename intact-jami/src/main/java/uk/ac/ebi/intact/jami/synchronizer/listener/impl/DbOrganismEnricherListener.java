package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.OrganismEnricherListener;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactOrganismSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.OrganismUpdates;
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

public class DbOrganismEnricherListener implements OrganismEnricherListener {
    private Map<Organism, OrganismUpdates> organismUpdates;
    private SynchronizerContext context;
    private IntactOrganismSynchronizer dbSynchronizer;

    public DbOrganismEnricherListener(SynchronizerContext context, IntactOrganismSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null organism synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.organismUpdates = new IdentityMap();
    }


    @Override
    public void onEnrichmentComplete(Organism object, EnrichmentStatus status, String message) {
        if (organismUpdates.containsKey(object)){
            OrganismUpdates updates = organismUpdates.get(object);
            try {
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getOrganismAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }

                organismUpdates.remove(object);
            } catch (PersisterException e) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e);
            } catch (FinderException e) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e);
            } catch (SynchronizerException e) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(Organism object, String message, Exception e) {
        if (organismUpdates.containsKey(object)){
            OrganismUpdates updates = organismUpdates.get(object);
            try {
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getOrganismAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }

                organismUpdates.remove(object);
            } catch (PersisterException e2) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e2);
            } catch (FinderException e2) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e2);
            } catch (SynchronizerException e2) {
                organismUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged organism", e2);
            }
        }
    }

    public Map<Organism, OrganismUpdates> getOrganismUpdates() {
        return organismUpdates;
    }

    @Override
    public void onCommonNameUpdate(Organism t, String s) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeCommonName((IntactOrganism) t);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize organism label", e);
        }
    }

    @Override
    public void onScientificNameUpdate(Organism organism, String s) {
        // nothing to do
    }

    @Override
    public void onTaxidUpdate(Organism organism, String s) {
        // nothing to do
    }

    @Override
    public void onCellTypeUpdate(Organism t, CvTerm cvTerm) {
        try {
            if (t.getCellType() != null){
                t.setCellType(
                        context.getCellTypeSynchronizer().synchronize(t.getCellType(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize cell type", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize cell type", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize cell type", e);
        }
    }

    @Override
    public void onTissueUpdate(Organism t, CvTerm cvTerm) {
        try {
            if (t.getTissue() != null){
                t.setTissue(
                        context.getCellTypeSynchronizer().synchronize(t.getTissue(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize tissue", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize tissue", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize tissue", e);
        }
    }

    @Override
    public void onCompartmentUpdate(Organism organism, CvTerm cvTerm) {
         // nothing to do
    }

    @Override
    public void onAddedAlias(Organism t, Alias alias) {
        if (this.organismUpdates.containsKey(t)){
            this.organismUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            OrganismUpdates updates = new OrganismUpdates();
            updates.getAddedAliases().add(alias);
            this.organismUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedAlias(Organism t, Alias alias) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactOrganismSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

}
