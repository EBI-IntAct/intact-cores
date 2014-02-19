package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.SourceFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.SourceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for sources
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public class SourceSynchronizer extends AbstractIntactDbSynchronizer<Source, IntactSource> implements SourceFetcher {
    private Map<Source, IntactSource> persistedObjects;

    private static final Log log = LogFactory.getLog(SourceSynchronizer.class);

    public SourceSynchronizer(SynchronizerContext context){
        super(context, IntactSource.class);
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Source, IntactSource>();
    }

    public IntactSource find(Source term) throws FinderException {
        try {
            if (term == null){
                return null;
            }
            else if (this.persistedObjects.containsKey(term)){
                return this.persistedObjects.get(term);
            }
            else if (term.getMIIdentifier() != null){
                return fetchByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MI, false);
            }
            else if (term.getPARIdentifier() != null){
                return fetchByIdentifier(term.getMIIdentifier(), CvTerm.PSI_PAR, false);
            }
            else if (!term.getIdentifiers().isEmpty()){
                boolean foundSeveral = false;
                for (Xref ref : term.getIdentifiers()){
                    try{
                        IntactSource fetchedTerm = fetchByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true);
                        if (fetchedTerm != null){
                            return fetchedTerm;
                        }
                    }
                    catch (BridgeFailedException e){
                        foundSeveral = true;
                    }
                }

                if (foundSeveral){
                    throw new FinderException("The source "+term.toString() + " has some identifiers that can match several sources in the database and we cannot determine which one is valid.");
                }
                else{
                    return (IntactSource)fetchByName(term.getShortName(), null);
                }
            }
            else{
                return (IntactSource)fetchByName(term.getShortName(), null);
            }
        } catch (BridgeFailedException e) {
            throw new FinderException("Problem fetching source from the database", e);
        }
    }

    public IntactSource persist(IntactSource object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactSource persisted = super.persist(object);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    @Override
    public IntactSource synchronize(Source object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactSource persisted = super.synchronize(object, persist);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    public void synchronizeProperties(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactSource);
        // then check full name
        prepareFullName(intactSource);
        // then check aliases
        prepareAliases(intactSource);
        // then check annotations
        prepareAnnotations(intactSource);
        // then check xrefs
        prepareXrefs(intactSource);
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    public Source fetchByIdentifier(String termIdentifier, String miOntologyName) throws BridgeFailedException {
        if(termIdentifier == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        if(miOntologyName == null)
            throw new IllegalArgumentException("Can not search for an identifier in an ontology without a value.");
        return fetchByIdentifier(termIdentifier, miOntologyName, true);
    }

    public Source fetchByIdentifier(String termIdentifier, CvTerm ontologyDatabase) throws BridgeFailedException {
        if(termIdentifier == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        if(ontologyDatabase == null)
            throw new IllegalArgumentException("Can not search for an identifier in an ontology without a value.");
        return fetchByIdentifier(termIdentifier, ontologyDatabase.getShortName(), true);
    }

    public Source fetchByName(String searchName, String miOntologyName) throws BridgeFailedException {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select s from IntactSource s " +
                "where s.shortName = :name");
        query.setParameter("name", searchName.trim().toLowerCase());
        Collection<Source> cvs = query.getResultList();
        if (cvs.size() == 1){
            return cvs.iterator().next();
        }
        else if (cvs.size() > 1){
            throw new BridgeFailedException("The source "+searchName + " can match "+cvs.size()+" sources in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public Collection<Source> fetchByName(String searchName) throws BridgeFailedException {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select s from IntactSource s " +
                "where s.shortName like :name");
        query.setParameter("name", "%"+searchName.trim().toLowerCase()+"%");
        return query.getResultList();
    }

    public Collection<Source> fetchByIdentifiers(Collection<String> termIdentifiers, String miOntologyName)
            throws BridgeFailedException {
        if (termIdentifiers == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<Source> results = new ArrayList<Source>(termIdentifiers.size());
        for (String id : termIdentifiers){
            Source element = fetchByIdentifier(id, miOntologyName);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<Source> fetchByIdentifiers(Collection<String> termIdentifiers, CvTerm ontologyDatabase)
            throws BridgeFailedException {
        if (termIdentifiers == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<Source> results = new ArrayList<Source>(termIdentifiers.size());
        for (String id : termIdentifiers){
            Source element = fetchByIdentifier(id, ontologyDatabase);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<Source> fetchByNames(Collection<String> searchNames, String miOntologyName)
            throws BridgeFailedException {
        if (searchNames == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<Source> results = new ArrayList<Source>(searchNames.size());
        for (String id : searchNames){
            Source element = fetchByName(id, miOntologyName);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<Source> fetchByNames(Collection<String> searchNames)
            throws BridgeFailedException {
        if (searchNames == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<Source> results = new ArrayList<Source>(searchNames.size());
        for (String id : searchNames){
            results.addAll(fetchByName(id));

        }
        return results;
    }

    protected IntactSource fetchByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) throws BridgeFailedException {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select s from IntactSource s " +
                    "where s.ac = :id");
            query.setParameter("id", termIdentifier);
            Collection<IntactSource> cvs = query.getResultList();
            if (cvs.size() == 1){
                return cvs.iterator().next();
            }
        }

        query = getEntityManager().createQuery("select s from IntactSource s " +
                "join s.persistentXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                "and d.shortName = :psiName " +
                "and x.id = :psiId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psiName", miOntologyName.toLowerCase().trim());
        query.setParameter("psiId", termIdentifier);

        Collection<IntactSource> cvs = query.getResultList();
        if (cvs.size() == 1){
            return cvs.iterator().next();
        }
        else if (cvs.size() > 1){
            throw new BridgeFailedException("The source "+termIdentifier + " can match "+cvs.size()+" sources in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    @Override
    protected Object extractIdentifier(IntactSource object) {
        return object.getAc();
    }

    @Override
    protected IntactSource instantiateNewPersistentInstance(Source object, Class<? extends IntactSource> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactSource cv = intactClass.getConstructor(String.class).newInstance(object.getShortName());
        CvTermCloner.copyAndOverrideSourceProperties(object, cv);
        return cv;
    }


    protected void prepareXrefs(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactSource.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref cvXref = getContext().getSourceXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (cvXref != xref){
                    intactSource.getPersistentXrefs().remove(xref);
                    intactSource.getPersistentXrefs().add(cvXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactSource.getPersistentAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation cvAnnotation = getContext().getSourceAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (cvAnnotation != annotation){
                    intactSource.getPersistentAnnotations().remove(annotation);
                    intactSource.getPersistentAnnotations().add(cvAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areSynonymsInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactSource.getSynonyms());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias cvAlias = getContext().getSourceAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (cvAlias != alias){
                    intactSource.getSynonyms().remove(alias);
                    intactSource.getSynonyms().add(cvAlias);
                }
            }
        }
    }

    protected void prepareFullName(IntactSource intactSource) {
        // truncate if necessary
        if (intactSource.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactSource.getFullName().length()){
            intactSource.setFullName(intactSource.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactSource intactSource) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactSource.getShortName().length()){
            log.warn("Source shortLabel too long: "+intactSource.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactSource.setShortName(intactSource.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        boolean first = true;
        String name;
        List<String> existingSource;
        do{
            name = intactSource.getShortName().trim().toLowerCase();
            existingSource = Collections.EMPTY_LIST;

            String originalName = first ? name : IntactUtils.excludeLastNumberInShortLabel(name);

            if (first){
                first = false;
            }
            else if (originalName.length() > 1){
                name = originalName.substring(0, originalName.length() - 1);
            }
            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select s.shortName from IntactSource s " +
                    "where s.shortName = :name or s.shortName like :nameWithSuffix"
                    + (intactSource.getAc() != null ? "and s.ac <> :sourceAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            if (intactSource.getAc() != null){
                query.setParameter("sourceAc", intactSource.getAc());
            }
            existingSource = query.getResultList();
            String nameInSync = IntactUtils.synchronizeShortlabel(name, existingSource, IntactUtils.MAX_SHORT_LABEL_LEN, false);
            intactSource.setShortName(nameInSync);
        }
        while(name.length() > 1 && !existingSource.isEmpty());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new SourceMergerEnrichOnly(this));
    }
}
