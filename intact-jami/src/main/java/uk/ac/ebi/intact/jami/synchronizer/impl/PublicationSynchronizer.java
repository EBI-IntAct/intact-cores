package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.PublicationMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.extension.PublicationAnnotation;
import uk.ac.ebi.intact.jami.model.extension.PublicationXref;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactPublicationComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for simple publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class PublicationSynchronizer extends AbstractIntactDbSynchronizer<Publication, IntactPublication>
        implements PublicationFetcher{

    private Map<Publication, IntactPublication> persistedObjects;
    private Map<Publication, IntactPublication> convertedObjects;

    private IntactComparator<Publication> publicationComparator;

    private static final Log log = LogFactory.getLog(PublicationSynchronizer.class);

    public PublicationSynchronizer(SynchronizerContext context) {
        super(context, IntactPublication.class);
        this.publicationComparator = new IntactPublicationComparator();
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Publication, IntactPublication>(this.publicationComparator);
        // to keep track of converted objects
        this.convertedObjects = new IdentityMap();
    }

    public IntactPublication find(Publication publication) throws FinderException {
        try {
            if (publication == null){
                return null;
            }
            else if (this.persistedObjects.containsKey(publication)){
                return this.persistedObjects.get(publication);
            }
            else if (publication.getPubmedId() != null){
                return fetchByIdentifier(publication.getPubmedId(), Xref.PUBMED, false);
            }
            else if (publication.getDoi() != null){
                return fetchByIdentifier(publication.getDoi(), Xref.DOI, false);
            }
            else if (publication.getImexId() != null){
                return fetchByImexId(publication.getImexId());
            }
            else if (!publication.getIdentifiers().isEmpty()){
                boolean foundSeveral = false;
                for (Xref ref : publication.getIdentifiers()){
                    try{
                        IntactPublication fetchedPublication = fetchByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true);
                        if (fetchedPublication != null){
                            return fetchedPublication;
                        }
                    }
                    catch (BridgeFailedException e){
                        foundSeveral = true;
                    }
                }

                if (foundSeveral){
                    throw new FinderException("The publication "+publication.toString() + " has some identifiers that can match several publications in the database and we cannot determine which one is valid.");
                }
                else{
                    return fetchByTitleAndJournal(publication.getTitle(), publication.getJournal(), publication.getPublicationDate(), publication.getAuthors());
                }
            }
            else{
                return fetchByTitleAndJournal(publication.getTitle(), publication.getJournal(), publication.getPublicationDate(), publication.getAuthors());
            }
        } catch (BridgeFailedException e) {
            throw new FinderException("Problem fetching publications from the database", e);
        }
    }

    @Override
    public Collection<IntactPublication> findAll(Publication publication) {
        if (publication == null){
            return Collections.EMPTY_LIST;
        }
        else if (this.persistedObjects.containsKey(publication)){
            return Collections.singleton(this.persistedObjects.get(publication));
        }
        else if (publication.getPubmedId() != null){
            return fetchAllByIdentifier(publication.getPubmedId(), Xref.PUBMED, false);
        }
        else if (publication.getDoi() != null){
            return fetchAllByIdentifier(publication.getDoi(), Xref.DOI, false);
        }
        else if (publication.getImexId() != null){
            return fetchAllByImexId(publication.getImexId());
        }
        else if (!publication.getIdentifiers().isEmpty()){
            Collection<IntactPublication> fetchedPublications = new ArrayList<IntactPublication>();
            for (Xref ref : publication.getIdentifiers()){
                fetchedPublications.addAll(fetchAllByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true));
            }

            return fetchedPublications;
        }
        else{
            return fetchAllByTitleAndJournal(publication.getTitle(), publication.getJournal(), publication.getPublicationDate(), publication.getAuthors());
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(Publication publication) {
        if (publication == null){
            return Collections.EMPTY_LIST;
        }
        else if (this.persistedObjects.containsKey(publication)){
            IntactPublication fetched = this.persistedObjects.get(publication);
            if (fetched.getAc() != null){
                return Collections.singleton(fetched.getAc());
            }
            return Collections.EMPTY_LIST;
        }
        else if (publication.getPubmedId() != null){
            return fetchAllAcsByIdentifier(publication.getPubmedId(), Xref.PUBMED, false);
        }
        else if (publication.getDoi() != null){
            return fetchAllAcsByIdentifier(publication.getDoi(), Xref.DOI, false);
        }
        else if (publication.getImexId() != null){
            return fetchAllAcsByImexId(publication.getImexId());
        }
        else if (!publication.getIdentifiers().isEmpty()){
            Collection<String> fetchedPublications = new ArrayList<String>();
            for (Xref ref : publication.getIdentifiers()){
                fetchedPublications.addAll(fetchAllAcsByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true));
            }

            return fetchedPublications;
        }
        else{
            return fetchAllAcsByTitleAndJournal(publication.getTitle(), publication.getJournal(), publication.getPublicationDate(), publication.getAuthors());
        }
    }

    public void synchronizeProperties(IntactPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactPublication);
        // then check xrefs
        prepareXrefs(intactPublication, true);
        // then check source
        prepareSource(intactPublication, true);
        // then check experiments
        prepareExperiments(intactPublication, true);
        // then prepare users
        prepareStatusAndCurators(intactPublication, true);
        // then check publication lifecycle
        prepareLifeCycleEvents(intactPublication, true);
        // then check full name
        prepareTitle(intactPublication);
        // then check authors
        preparePublicationAuthors(intactPublication);
        // then check annotations
        prepareAnnotations(intactPublication, true);
    }

    public Publication fetchByIdentifier(String identifier, String source) throws BridgeFailedException {
        if(identifier == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        if(source == null)
            throw new IllegalArgumentException("Can not search for a publication without a source (Ex: pubmed or doi).");
        return fetchByIdentifier(identifier, source, true);
    }

    public Collection<Publication> fetchByIdentifiers(Map<String, Collection<String>> identifiers) throws BridgeFailedException {
        if(identifiers == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        Collection<Publication> pubs = new ArrayList<Publication>(identifiers.size());
        for (Map.Entry<String, Collection<String>> entry : identifiers.entrySet()){
            for (String identifier : entry.getValue()){
                Publication pub = fetchByIdentifier(identifier, entry.getKey());
                if (pub != null){
                    pubs.add(pub);
                }
            }
        }
        return pubs;
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
    }

    protected void prepareAndSynchronizeShortLabel(IntactPublication intactPublication) throws SynchronizerException {
        // first initialise shortlabel if not done
        String pubmed = intactPublication.getPubmedId();
        String doi = intactPublication.getDoi();
        if (pubmed != null ){
            intactPublication.setShortLabel(pubmed);
        }
        else if (doi != null){
            intactPublication.setShortLabel(doi);
        }
        else if (!intactPublication.getIdentifiers().isEmpty()){
            Iterator<Xref> idIterator = intactPublication.getIdentifiers().iterator();
            while (idIterator.hasNext()){
                String id = idIterator.next().getId();
                if (intactPublication.getAc() == null || !intactPublication.getAc().equals(id)){
                    intactPublication.setShortLabel(id);
                }
            }
        }
        else {
            // create unassigned pubmed id
            SequenceManager seqManager = ApplicationContextProvider.getBean("jamiSequenceManager", SequenceManager.class);
            if (seqManager == null){
                throw new SynchronizerException("The publication synchronizer needs a sequence manager to automatically generate a unassigned pubmed identifier for backward compatibility. No sequence manager bean " +
                        "was found in the spring context.");
            }
            seqManager.createSequenceIfNotExists(IntactUtils.UNASSIGNED_SEQ, 1);
            String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.UNASSIGNED_SEQ));
            String identifier = "unassigned" + nextIntegerAsString;
            // set identifier
            intactPublication.setShortLabel(identifier);
        }
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactPublication.getShortLabel().length()){
            log.warn("Publication shortLabel too long: "+intactPublication.getShortLabel()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactPublication.setShortLabel(intactPublication.getShortLabel().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    protected IntactPublication fetchByTitleAndJournal(String title, String journal, Date publicationDate, Collection<String> authors) throws BridgeFailedException {
        Query query;
        if(title == null && journal == null && publicationDate == null && authors.isEmpty()) {
           return null;
        }
        else if (authors.isEmpty()){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate":"is null "));
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
        }
        else {
            query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                    "join p.dbAnnotations as a "+
                    "join a.topic as topic "+
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal ":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate ":"is null ") +
                    "and topic.shortLabel = :authors " +
                    "and upper(a.value) = :authorsList");
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
            query.setParameter("authors", Annotation.AUTHOR);
            query.setParameter("authorsList",StringUtils.join(authors, ", ").toUpperCase());
        }

        Collection<IntactPublication> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new BridgeFailedException("The publication title, journal, publication date and authors can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    protected Collection<IntactPublication> fetchAllByTitleAndJournal(String title, String journal, Date publicationDate, Collection<String> authors) {
        Query query;
        if(title == null && journal == null && publicationDate == null && authors.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        else if (authors.isEmpty()){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate":"is null "));
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
        }
        else {
            query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                    "join p.dbAnnotations as a "+
                    "join a.topic as topic "+
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal ":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate ":"is null ") +
                    "and topic.shortLabel = :authors " +
                    "and upper(a.value) = :authorsList");
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
            query.setParameter("authors", Annotation.AUTHOR);
            query.setParameter("authorsList",StringUtils.join(authors, ", ").toUpperCase());
        }

        return query.getResultList();
    }

    protected Collection<String> fetchAllAcsByTitleAndJournal(String title, String journal, Date publicationDate, Collection<String> authors) {
        Query query;
        if(title == null && journal == null && publicationDate == null && authors.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        else if (authors.isEmpty()){
            query = getEntityManager().createQuery("select distinct p.ac from IntactPublication p " +
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate":"is null "));
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
        }
        else {
            query = getEntityManager().createQuery("select distinct p.ac from IntactPublication p " +
                    "join p.dbAnnotations as a "+
                    "join a.topic as topic "+
                    "where "+(title != null ? "upper(p.fullName) = :title " : "p.fullName is null ") +
                    "and "+(journal != null ? "upper(p.journal) = :journal ":"p.journal is null ") +
                    "and p.publicationDate "+(publicationDate != null ? "= :pubDate ":"is null ") +
                    "and topic.shortLabel = :authors " +
                    "and upper(a.value) = :authorsList");
            if (title != null){
                query.setParameter("title", title.toUpperCase().trim());
            }
            if (journal != null){
                query.setParameter("journal", journal.toUpperCase().trim());
            }
            if (publicationDate != null){
                query.setParameter("pubDate", publicationDate);
            }
            query.setParameter("authors", Annotation.AUTHOR);
            query.setParameter("authorsList",StringUtils.join(authors, ", ").toUpperCase());
        }

        return query.getResultList();
    }

    protected IntactPublication fetchByIdentifier(String identifier, String source, boolean checkAc) throws BridgeFailedException {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.ac = :id");
            query.setParameter("id", identifier);
            Collection<IntactPublication> publications = query.getResultList();
            if (publications.size() == 1){
                return publications.iterator().next();
            }
        }

        query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc or q.shortName = :primary) " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY);
        query.setParameter("dbName", source.toLowerCase().trim());
        query.setParameter("dbId", identifier);

        Collection<IntactPublication> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new BridgeFailedException("The publication "+identifier + " can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    protected Collection<IntactPublication> fetchAllByIdentifier(String identifier, String source, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.ac = :id");
            query.setParameter("id", identifier);
            Collection<IntactPublication> publications = query.getResultList();
            if (!publications.isEmpty()){
                return publications;
            }
        }

        query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc or q.shortName = :primary) " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY);
        query.setParameter("dbName", source.toLowerCase().trim());
        query.setParameter("dbId", identifier);

        return query.getResultList();
    }

    protected Collection<String> fetchAllAcsByIdentifier(String identifier, String source, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select distinct p.ac from IntactPublication p " +
                    "where p.ac = :id");
            query.setParameter("id", identifier);
            Collection<String> publications = query.getResultList();
            if (!publications.isEmpty()){
                return publications;
            }
        }

        query = getEntityManager().createQuery("select distinct p.ac from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc or q.shortName = :primary) " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY);
        query.setParameter("dbName", source.toLowerCase().trim());
        query.setParameter("dbId", identifier);

        return query.getResultList();
    }

    protected IntactPublication fetchByImexId(String imex) throws FinderException {

        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where q.shortName = :imexPrimary " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("imexPrimary", Xref.IMEX_PRIMARY.toLowerCase());
        query.setParameter("dbName", Xref.IMEX.toLowerCase());
        query.setParameter("dbId", imex);

        Collection<IntactPublication> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new FinderException("The publication "+imex + " can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    protected Collection<IntactPublication> fetchAllByImexId(String imex){

        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where q.shortName = :imexPrimary " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("imexPrimary", Xref.IMEX_PRIMARY.toLowerCase());
        query.setParameter("dbName", Xref.IMEX.toLowerCase());
        query.setParameter("dbId", imex);

        return query.getResultList();
    }

    protected Collection<String> fetchAllAcsByImexId(String imex){

        Query query = getEntityManager().createQuery("select distinct p.ac from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where q.shortName = :imexPrimary " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("imexPrimary", Xref.IMEX_PRIMARY.toLowerCase());
        query.setParameter("dbName", Xref.IMEX.toLowerCase());
        query.setParameter("dbId", imex);

        return query.getResultList();
    }

    @Override
    protected Object extractIdentifier(IntactPublication object) {
        return object.getAc();
    }

    @Override
    protected IntactPublication instantiateNewPersistentInstance(Publication object, Class<? extends IntactPublication> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactPublication pub = new IntactPublication();
        PublicationCloner.copyAndOverridePublicationPropertiesAndExperiments(object, pub);
        return pub;
    }

    @Override
    protected void storeInCache(Publication originalObject, IntactPublication persistentObject, IntactPublication existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactPublication fetchObjectFromCache(Publication object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(Publication object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(Publication object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Publication object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactPublication fetchMatchingObjectFromIdentityCache(Publication object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactPublication intactPublication) throws SynchronizerException, PersisterException, FinderException {
        // then check xrefs
        prepareXrefs(intactPublication, false);
        // then check source
        prepareSource(intactPublication, false);
        // then check experiments
        prepareExperiments(intactPublication, false);
        // then prepare users
        prepareStatusAndCurators(intactPublication, false);
        // then check publication lifecycle
        prepareLifeCycleEvents(intactPublication, false);
        // then check annotations
        prepareAnnotations(intactPublication, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Publication originalObject, IntactPublication persistableObject) {
         this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(Publication originalObject) {
        return !this.publicationComparator.canCompare(originalObject);
    }

    protected void preparePublicationAuthors(IntactPublication intactPublication) {
        if (intactPublication.areAnnotationsInitialized()){
            if (intactPublication.getAuthors().isEmpty()){
                AnnotationUtils.removeAllAnnotationsWithTopic(intactPublication.getDbAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
            }
            else{
                String authorAnnot = StringUtils.join(intactPublication.getAuthors(), ", ");
                // truncate if necessary
                if (IntactUtils.MAX_DESCRIPTION_LEN < authorAnnot.length()){
                    log.warn("Publication authors too long: "+authorAnnot+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
                    authorAnnot = authorAnnot.substring(0, IntactUtils.MAX_DESCRIPTION_LEN);
                }
                Annotation authorList = AnnotationUtils.collectFirstAnnotationWithTopic(intactPublication.getDbAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
                if (authorList != null){
                    if (!authorAnnot.equalsIgnoreCase(authorList.getValue())){
                        authorList.setValue(authorAnnot);
                    }
                }
                else{
                    intactPublication.getDbAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.AUTHOR, Annotation.AUTHOR_MI), authorAnnot));
                }
            }
        }
    }

    protected void prepareXrefs(IntactPublication intactPublication, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactPublication.getDbXrefs());
            intactPublication.getDbXrefs().clear();
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref pubRef = enableSynchronization ?
                        getContext().getPublicationXrefSynchronizer().synchronize(xref, false) :
                        getContext().getPublicationXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactPublication.getDbXrefs().add(pubRef);
            }
        }
    }

    protected void prepareAnnotations(IntactPublication intactPublication, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactPublication.getDbAnnotations());
            intactPublication.getDbAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation pubAnnotation = enableSynchronization ?
                        getContext().getPublicationAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getPublicationAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactPublication.getDbAnnotations().add(pubAnnotation);
            }
        }
    }

    protected void prepareTitle(IntactPublication intactPublication) {
        // truncate if necessary
        if (intactPublication.getTitle() != null && IntactUtils.MAX_FULL_NAME_LEN < intactPublication.getTitle().length()){
            log.warn("Publication title too long: "+intactPublication.getTitle()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactPublication.setTitle(intactPublication.getTitle().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new PublicationMergerEnrichOnly(this));
    }

    protected void prepareStatusAndCurators(IntactPublication intactPublication, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        // first the status
        CvTerm status = intactPublication.getStatus().toCvTerm();
        intactPublication.setCvStatus(enableSynchronization ?
                getContext().getLifecycleStatusSynchronizer().synchronize(status, true) :
                getContext().getLifecycleStatusSynchronizer().convertToPersistentObject(status));

        // then curator
        User curator = intactPublication.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactPublication.setCurrentOwner(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(curator, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(curator));
        }

        // then reviewer
        User reviewer = intactPublication.getCurrentReviewer();
        if (reviewer != null){
            intactPublication.setCurrentReviewer(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(reviewer, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(reviewer));
        }
    }

    protected void prepareSource(IntactPublication intactPublication, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        Source source = intactPublication.getSource();
        if (source != null){
            intactPublication.setSource(enableSynchronization ?
                    getContext().getSourceSynchronizer().synchronize(source, true) :
                    getContext().getSourceSynchronizer().convertToPersistentObject(source));
        }
    }

    protected void prepareLifeCycleEvents(IntactPublication intactPublication, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactPublication.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactPublication.getLifecycleEvents());
            intactPublication.getLifecycleEvents().clear();
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = enableSynchronization ?
                        getContext().getPublicationLifecycleSynchronizer().synchronize(event, false) :
                        getContext().getPublicationLifecycleSynchronizer().convertToPersistentObject(event);
                // we have a different instance because needed to be synchronized
                intactPublication.getLifecycleEvents().add(evt);
            }
        }
    }

    protected void prepareExperiments(IntactPublication intactPublication, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactPublication.areExperimentsInitialized()){
            List<Experiment> experimentToPersist = new ArrayList<Experiment>(intactPublication.getExperiments());
            Set<Experiment> processedExperiments = new HashSet<Experiment>(intactPublication.getExperiments().size());
            intactPublication.getExperiments().clear();
            for (Experiment experiment : experimentToPersist){
                // do not persist or merge experiments because of cascades
                Experiment pubExperiment = enableSynchronization ?
                        getContext().getExperimentSynchronizer().synchronize(experiment, false) :
                        getContext().getExperimentSynchronizer().convertToPersistentObject(experiment);
                // we have a different instance because needed to be synchronized
                if (processedExperiments.add(pubExperiment)){
                    intactPublication.addExperiment(pubExperiment);
                }
            }
        }
    }

    @Override
    public void deleteRelatedProperties(IntactPublication intactParticipant){
        for (Experiment f : intactParticipant.getExperiments()){
            getContext().getExperimentSynchronizer().delete(f);
        }
        intactParticipant.getExperiments().clear();
    }
}
