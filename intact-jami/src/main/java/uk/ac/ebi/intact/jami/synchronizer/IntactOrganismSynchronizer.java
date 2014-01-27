package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.OrganismCloner;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Default finder/synchronizer for organisms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactOrganismSynchronizer implements IntactDbSynchronizer<Organism>{
    private EntityManager entityManager;
    private Map<Organism, Organism> persistedObjects;

    private IntactDbSynchronizer<Alias> aliasSynchronizer;
    private IntactDbSynchronizer<CvTerm> cellTypeSynchronizer;
    private IntactDbSynchronizer<CvTerm> tissueSynchronizer;

    private static final Log log = LogFactory.getLog(IntactCvTermSynchronizer.class);

    public IntactOrganismSynchronizer(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("An Organism synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Organism, Organism>();
        this.aliasSynchronizer = new IntactAliasSynchronizer(this.entityManager, OrganismAlias.class);
        this.cellTypeSynchronizer = new IntactCvTermSynchronizer(this.entityManager, IntactUtils.CELL_TYPE_OBJCLASS);
        this.tissueSynchronizer = new IntactCvTermSynchronizer(this.entityManager, IntactUtils.TISSUE_OBJCLASS);
    }

    public IntactOrganismSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias> aliasSynchronizer,
                                      IntactDbSynchronizer<CvTerm> cellSynchronizer,IntactDbSynchronizer<CvTerm> tissueSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("An Organism synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Organism, Organism>();
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer(this.entityManager, OrganismAlias.class);
        this.cellTypeSynchronizer = cellSynchronizer != null ? cellSynchronizer : new IntactCvTermSynchronizer(this.entityManager, IntactUtils.CELL_TYPE_OBJCLASS);
        this.tissueSynchronizer = tissueSynchronizer != null ? tissueSynchronizer : new IntactCvTermSynchronizer(this.entityManager, IntactUtils.TISSUE_OBJCLASS);    }


    public Organism find(Organism term) throws FinderException {
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        // we have a simple organism. Only check its taxid
        else if (term.getCellType() == null && term.getTissue() == null){
            query = this.entityManager.createQuery("select o from IntactOrganism o " +
                    "where o.cellType is null " +
                    "and o.tissue is null " +
                    "and o.persistentTaxid = :taxid");
            query.setParameter("taxid", Integer.toString(term.getTaxId()));
        }
        // we have a celltype/tissue to find first
        else {
            if (term.getCellType() != null && term.getTissue() != null){
                CvTerm existingCell = this.cellTypeSynchronizer.find(term.getCellType());
                CvTerm existingTissue = this.tissueSynchronizer.find(term.getTissue());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if (existingCell == null || existingTissue == null){
                    return null;
                }
                else {
                    query = this.entityManager.createQuery("select o from IntactOrganism o " +
                            "join o.cellType as cell " +
                            "join o.tissue as t " +
                            "where cell.ac = :cellAc " +
                            "and t.ac = :tissueAc " +
                            "and o.persistentTaxid = :taxid");
                    query.setParameter("cellAc", ((IntactCvTerm)existingCell).getAc());
                    query.setParameter("tissueAc", ((IntactCvTerm)existingTissue).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
            else if (term.getCellType() != null){
                CvTerm existingCell = this.cellTypeSynchronizer.find(term.getCellType());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if (existingCell == null){
                    return null;
                }
                else {
                    query = this.entityManager.createQuery("select o from IntactOrganism o " +
                            "join o.cellType as cell " +
                            "where cell.ac = :cellAc " +
                            "and o.persistentTaxid = :taxid");
                    query.setParameter("cellAc", ((IntactCvTerm)existingCell).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
            else{
                CvTerm existingTissue = this.tissueSynchronizer.find(term.getTissue());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if (existingTissue == null){
                    return null;
                }
                else {
                    query = this.entityManager.createQuery("select o from IntactOrganism o " +
                            "join o.tissue as t " +
                            "where t.ac = :tissueAc " +
                            "and o.persistentTaxid = :taxid");
                    query.setParameter("tissueAc", ((IntactCvTerm)existingTissue).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
        }
        return (Organism) query.getSingleResult();
    }

    public Organism persist(Organism object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        this.persistedObjects.put(object, object);

        IntactOrganism intactOrganism = (IntactOrganism)object;
        // synchronize properties
        synchronizeProperties(intactOrganism);

        // persist the cv
        this.entityManager.persist(intactOrganism);

        return intactOrganism;
    }

    public void synchronizeProperties(Organism object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((IntactOrganism)object);
    }

    public Organism synchronize(Organism organism, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (this.persistedObjects.containsKey(organism)){
            return this.persistedObjects.get(organism);
        }

        if (!(organism instanceof IntactOrganism)){
            IntactOrganism newOrganism = new IntactOrganism(organism.getTaxId());
            OrganismCloner.copyAndOverrideOrganismProperties(organism, newOrganism);

            Organism retrievedOrganism = findOrPersist(newOrganism, persist);
            this.persistedObjects.put(retrievedOrganism, retrievedOrganism);

            return retrievedOrganism;
        }
        else{
            IntactOrganism intactType = (IntactOrganism)organism;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // first synchronize properties before merging
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    Organism newOrganism = this.entityManager.merge(intactType);
                    this.persistedObjects.put(newOrganism, newOrganism);
                    return newOrganism;
                }
                else{
                    this.persistedObjects.put(intactType, intactType);
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // retrieves or persist cv
                Organism newOrganism = findOrPersist(intactType, persist);
                this.persistedObjects.put(newOrganism, newOrganism);
                return newOrganism;
            }
            else{
                // only synchronize properties
                synchronizeProperties(organism);
                this.persistedObjects.put(organism, organism);
                return organism;
            }
        }
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.aliasSynchronizer.clearCache();
        this.cellTypeSynchronizer.clearCache();
        this.tissueSynchronizer.clearCache();
    }

    protected void synchronizeProperties(IntactOrganism intactOrganism) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeCommonName(intactOrganism);
        // then check full name
        prepareFullName(intactOrganism);
        // then check aliases
        prepareAliases(intactOrganism);
        // then check annotations
        prepareCellTypeAndTissue(intactOrganism);
    }

    protected void prepareCellTypeAndTissue(IntactOrganism intactOrganism) throws FinderException, PersisterException, SynchronizerException {
        if (intactOrganism.getCellType() != null){
            intactOrganism.setCellType(this.cellTypeSynchronizer.synchronize(intactOrganism.getCellType(), true, true));
        }
        if (intactOrganism.getTissue() != null){
            intactOrganism.setTissue(this.cellTypeSynchronizer.synchronize(intactOrganism.getTissue(), true, true));
        }
    }

    protected void prepareAliases(IntactOrganism intactOrganism) throws FinderException, PersisterException, SynchronizerException {
        if (intactOrganism.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactOrganism.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias organismAlias = this.aliasSynchronizer.synchronize(alias, false, false);
                // we have a different instance because needed to be synchronized
                if (organismAlias != alias){
                    intactOrganism.getAliases().remove(alias);
                    intactOrganism.getAliases().add(organismAlias);
                }
            }
        }
    }

    protected void prepareFullName(IntactOrganism intactOrganism) {
        // truncate if necessary
        if (intactOrganism.getScientificName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactOrganism.getScientificName().length()){
            log.warn("Organism scientific name too long: "+intactOrganism.getScientificName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactOrganism.setScientificName(intactOrganism.getScientificName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeCommonName(IntactOrganism intactOrganism) {
        // set shortname if not done yet
        if (intactOrganism.getCommonName() == null){
            intactOrganism.setCommonName(intactOrganism.getScientificName() != null ? intactOrganism.getScientificName() : Integer.toString(intactOrganism.getTaxId()));
            if (intactOrganism.getCellType() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getCellType().getShortName());
            }
            else if (intactOrganism.getTissue() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getTissue().getShortName());
            }
            else if (intactOrganism.getTissue() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getCompartment().getShortName());
            }
        }
        // truncate if necessary
        if (intactOrganism.getCommonName() != null && IntactUtils.MAX_SHORT_LABEL_LEN < intactOrganism.getCommonName().length()){
            log.warn("Organism common name too long: "+intactOrganism.getCommonName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactOrganism.setCommonName(intactOrganism.getCommonName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        // check if short name already exist, if yes, synchronize
        Query query = this.entityManager.createQuery("select o from IntactOrganism o " +
                "where o.commonName = :name");
        query.setParameter("name", intactOrganism.getCommonName().trim().toLowerCase());
        List<IntactOrganism> existingOrganisms = query.getResultList();
        if (!existingOrganisms.isEmpty()){
            int max = 1;
            for (IntactOrganism organism : existingOrganisms){
                String name = organism.getCommonName();
                if (name.contains("-")){
                    String strSuffix = name.substring(name .lastIndexOf("-") + 1, name.length());
                    Matcher matcher = IntactUtils.decimalPattern.matcher(strSuffix);

                    if (matcher.matches()){
                        max = Math.max(max, Integer.parseInt(matcher.group()));
                    }
                }
            }
            String maxString = Integer.toString(max);
            // retruncate if necessary
            if (IntactUtils.MAX_SHORT_LABEL_LEN < intactOrganism.getCommonName().length()+maxString.length()+1){
                log.warn("Organism common name too long: "+intactOrganism.getCommonName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
                intactOrganism.setCommonName(intactOrganism.getCommonName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN-(maxString.length()+1))
                        +"-"+maxString);
            }
            else{
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+maxString);
            }
        }
    }

    protected Organism findOrPersist(Organism organism, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        Organism existingInstance = find(organism);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            // synchronize before persisting
            synchronizeProperties(organism);
            if (persist){
                this.entityManager.persist(organism);
            }
            return organism;
        }
    }
}
