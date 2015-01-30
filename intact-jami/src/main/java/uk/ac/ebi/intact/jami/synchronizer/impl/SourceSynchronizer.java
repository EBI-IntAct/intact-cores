package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.SourceFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.SourceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactCvTermComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactSourceComparator;

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
    private Map<Source, IntactSource> convertedObjects;

    private IntactComparator<Source> sourceComparator;

    private static final Log log = LogFactory.getLog(SourceSynchronizer.class);

    public SourceSynchronizer(SynchronizerContext context){
        super(context, IntactSource.class);
        this.sourceComparator = new IntactSourceComparator();
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Source, IntactSource>(this.sourceComparator);
        this.convertedObjects = new IdentityMap();
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

    @Override
    public Collection<IntactSource> findAll(Source term) {
        if (term == null){
            return Collections.EMPTY_LIST;
        }
        else if (this.persistedObjects.containsKey(term)){
            return Collections.singleton(this.persistedObjects.get(term));
        }
        else if (term.getMIIdentifier() != null){
            return fetchAllByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MI, false);
        }
        else if (term.getPARIdentifier() != null){
            return fetchAllByIdentifier(term.getMIIdentifier(), CvTerm.PSI_PAR, false);
        }
        else if (!term.getIdentifiers().isEmpty()){
            Collection<IntactSource> fetchedTerms = new ArrayList<IntactSource>();
            for (Xref ref : term.getIdentifiers()){
                fetchedTerms.addAll(fetchAllByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true));
            }

            return fetchedTerms;
        }
        else{
            return fetchAllByName(term.getShortName(), null);
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(Source term) {
        if (term == null){
            return Collections.EMPTY_LIST;
        }
        else if (this.persistedObjects.containsKey(term)){
            IntactSource fetched = this.persistedObjects.get(term);
            if (fetched.getAc() != null){
               return Collections.singleton(fetched.getAc());
            }
            return Collections.EMPTY_LIST;
        }
        else if (term.getMIIdentifier() != null){
            return fetchAllAcsByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MI, false);
        }
        else if (term.getPARIdentifier() != null){
            return fetchAllAcsByIdentifier(term.getMIIdentifier(), CvTerm.PSI_PAR, false);
        }
        else if (!term.getIdentifiers().isEmpty()){
            Collection<String> fetchedTerms = new ArrayList<String>();
            for (Xref ref : term.getIdentifiers()){
                fetchedTerms.addAll(fetchAllAcsByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true));
            }

            return fetchedTerms;
        }
        else{
            return fetchAllAcsByName(term.getShortName(), null);
        }
    }

    public void synchronizeProperties(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactSource);
        // then check full name
        prepareFullName(intactSource);
        // then check aliases
        prepareAliases(intactSource, true);
        // then check annotations
        prepareAnnotations(intactSource, true);
        // then check xrefs
        prepareXrefs(intactSource, true);
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
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

    public Collection<IntactSource> fetchAllByName(String searchName, String miOntologyName) {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select s from IntactSource s " +
                "where s.shortName = :name");
        query.setParameter("name", searchName.trim().toLowerCase());
        return query.getResultList();
    }

    public Collection<String> fetchAllAcsByName(String searchName, String miOntologyName) {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select distinct s.ac from IntactSource s " +
                "where s.shortName = :name");
        query.setParameter("name", searchName.trim().toLowerCase());
        return query.getResultList();
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

        query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
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

    protected Collection<IntactSource> fetchAllByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select s from IntactSource s " +
                    "where s.ac = :id");
            query.setParameter("id", termIdentifier);
            Collection<IntactSource> cvs = query.getResultList();
            if (!cvs.isEmpty()){
                return cvs;
            }
        }

        query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                "and d.shortName = :psiName " +
                "and x.id = :psiId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psiName", miOntologyName.toLowerCase().trim());
        query.setParameter("psiId", termIdentifier);

        return query.getResultList();
    }

    protected Collection<String> fetchAllAcsByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select distinct s.ac from IntactSource s " +
                    "where s.ac = :id");
            query.setParameter("id", termIdentifier);
            Collection<String> cvs = query.getResultList();
            if (!cvs.isEmpty()){
                return cvs;
            }
        }

        query = getEntityManager().createQuery("select distinct s.ac from IntactSource s " +
                "join s.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                "and d.shortName = :psiName " +
                "and x.id = :psiId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psiName", miOntologyName.toLowerCase().trim());
        query.setParameter("psiId", termIdentifier);

        return query.getResultList();
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

    @Override
    protected void storeInCache(Source originalObject, IntactSource persistentObject, IntactSource existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
            this.convertedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
            this.convertedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactSource fetchObjectFromCache(Source object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(Source object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(Source object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Source object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactSource fetchMatchingObjectFromIdentityCache(Source object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactSource intactSource) throws SynchronizerException, PersisterException, FinderException {
        // then check aliases
        prepareAliases(intactSource, false);
        // then check annotations
        prepareAnnotations(intactSource, false);
        // then check xrefs
        prepareXrefs(intactSource, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Source originalObject, IntactSource persistableObject) {
         this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectPartiallyInitialised(Source originalObject) {
        return !this.sourceComparator.canCompare(originalObject);
    }


    protected void prepareXrefs(IntactSource intactSource, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactSource.getDbXrefs());
            intactSource.getDbXrefs().clear();
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref cvXref = enableSynchronization ?
                        getContext().getSourceXrefSynchronizer().synchronize(xref, false) :
                        getContext().getSourceXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactSource.getDbXrefs().add(cvXref);
            }
        }
    }

    protected void prepareAnnotations(IntactSource intactSource, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactSource.getDbAnnotations());
            intactSource.getDbAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation cvAnnotation = enableSynchronization ?
                        getContext().getSourceAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getSourceAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactSource.getDbAnnotations().add(cvAnnotation);
            }
        }
    }

    protected void prepareAliases(IntactSource intactSource, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areSynonymsInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactSource.getSynonyms());
            intactSource.getSynonyms().clear();
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias cvAlias = enableSynchronization ?
                        getContext().getSourceAliasSynchronizer().synchronize(alias, false) :
                        getContext().getSourceAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                intactSource.getSynonyms().add(cvAlias);
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

        IntactUtils.synchronizeSourceShortName(intactSource, getEntityManager());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new SourceMergerEnrichOnly(this));
    }

    @Override
    protected void synchronizePropertiesAfterCacheMerge(IntactSource existingInstance) throws SynchronizerException, PersisterException, FinderException {
        // then check aliases
        prepareAliases(existingInstance, true);
        // then check annotations
        prepareAnnotations(existingInstance, true);
        // then check xrefs
        prepareXrefs(existingInstance, true);
    }
}
