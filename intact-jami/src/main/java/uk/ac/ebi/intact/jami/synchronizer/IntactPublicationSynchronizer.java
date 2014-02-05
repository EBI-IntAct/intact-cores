package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import psidev.psi.mi.jami.utils.comparator.publication.UnambiguousPublicationComparator;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.merger.IntactCvTermMergerEnrichOnly;
import uk.ac.ebi.intact.jami.merger.IntactPublicationMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class IntactPublicationSynchronizer extends AbstractIntactDbSynchronizer<Publication, IntactPublication> implements PublicationFetcher {

    private Map<Publication, IntactPublication> persistedObjects;

    private IntactDbSynchronizer<Annotation, PublicationAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref, PublicationXref> xrefSynchronizer;
    private IntactDbSynchronizer<Source, IntactSource> sourceSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer;
    private IntactDbSynchronizer<User, User> userSynchronizer;

    private IntactDbSynchronizer<Experiment, IntactExperiment> experimentSynchronizer;
    private IntactDbSynchronizer<LifeCycleEvent, PublicationLifecycleEvent> lifecycleEventSynchronizer;

    private static final Log log = LogFactory.getLog(IntactPublicationSynchronizer.class);

    public IntactPublicationSynchronizer(EntityManager entityManager){
        super(entityManager, IntactPublication.class);
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Publication, IntactPublication>(new UnambiguousPublicationComparator());
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer<PublicationAnnotation>(entityManager, PublicationAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer<PublicationXref>(entityManager, PublicationXref.class);
        this.sourceSynchronizer = new IntactSourceSynchronizer(entityManager);
        this.statusSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PUBLICATION_STATUS_OBJCLASS);

        // TODO experiment synchronizer
        this.lifecycleEventSynchronizer = new IntactLifeCycleSynchronizer<PublicationLifecycleEvent>(entityManager, PublicationLifecycleEvent.class);
        this.userSynchronizer = new IntactUserSynchronizer(entityManager);
    }

    public IntactPublicationSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Experiment, IntactExperiment> expSynchronizer,
                                         IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer, IntactDbSynchronizer<User, User> userSynchronizer,
                                         IntactDbSynchronizer<LifeCycleEvent, PublicationLifecycleEvent> lifecycleEventSynchronizer, IntactDbSynchronizer<Source, IntactSource> sourceSynchronizer,
                                         IntactDbSynchronizer<Annotation, PublicationAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, PublicationXref> xrefSynchronizer){
        super(entityManager, IntactPublication.class);
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Publication, IntactPublication>(new UnambiguousPublicationComparator());
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer<PublicationAnnotation>(entityManager, PublicationAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null ? xrefSynchronizer : new IntactXrefSynchronizer<PublicationXref>(entityManager, PublicationXref.class);
        this.sourceSynchronizer = sourceSynchronizer != null ? sourceSynchronizer : new IntactSourceSynchronizer(entityManager);
        this.statusSynchronizer = statusSynchronizer != null ? statusSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PUBLICATION_STATUS_OBJCLASS);

        // TODO experiment synchronizer
        this.lifecycleEventSynchronizer = lifecycleEventSynchronizer != null ? lifecycleEventSynchronizer : new IntactLifeCycleSynchronizer<PublicationLifecycleEvent>(entityManager, PublicationLifecycleEvent.class);
        this.userSynchronizer = userSynchronizer != null ? userSynchronizer : new IntactUserSynchronizer(entityManager);
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

    public IntactPublication persist(IntactPublication object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactPublication persisted = super.persist(object);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    @Override
    public IntactPublication synchronize(Publication object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactPublication persisted = super.synchronize(object, persist);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    public void synchronizeProperties(IntactPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactPublication);
        // then check full name
        prepareTitle(intactPublication);
        // then check journal
        prepareJournal(intactPublication);
        // then check publicationDate
        preparePublicationDate(intactPublication);
        // then check authors
        preparePublicationAuthors(intactPublication);
        // then check curation depth
        prepareCurationDepth(intactPublication);
        // then check source
        prepareSource(intactPublication);
        // then check annotations
        prepareAnnotations(intactPublication);
        // then check xrefs
        prepareXrefs(intactPublication);
        // then check experiments
        prepareExperiments(intactPublication);
        // then prepare users
        prepareStatusAndCurators(intactPublication);
        // then check publication lifecycle
        prepareLifeCycleEvents(intactPublication);
    }

    private void prepareStatusAndCurators(IntactPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        // first the status
        CvTerm status = intactPublication.getStatus() != null ? intactPublication.getStatus() : IntactUtils.createLifecycleStatus(LifeCycleEvent.NEW_STATUS);
        intactPublication.setStatus(this.statusSynchronizer.synchronize(status, true));

        // then curator
        User curator = intactPublication.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactPublication.setCurrentOwner(this.userSynchronizer.synchronize(curator, false));
        }

        // then reviewer
        User reviewer = intactPublication.getCurrentReviewer();
        if (reviewer != null){
            intactPublication.setCurrentReviewer(this.userSynchronizer.synchronize(reviewer, false));
        }
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
        this.experimentSynchronizer.clearCache();
        this.xrefSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();
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
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "join p.persistentAnnotations as a "+
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

        query = getEntityManager().createQuery("select p from IntactPublication p " +
                "join p.persistentXrefs as x " +
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

    protected IntactPublication fetchByImexId(String imex) throws FinderException {

        Query query = getEntityManager().createQuery("select p from IntactPublication p " +
                "join p.persistentXrefs as x " +
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

    protected void prepareSource(IntactPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        Source source = intactPublication.getSource();
        if (source != null){
            intactPublication.setSource(this.sourceSynchronizer.synchronize(source, true));
        }
    }

    protected void prepareLifeCycleEvents(IntactPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {

        if (intactPublication.areLifecycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactPublication.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = this.lifecycleEventSynchronizer.synchronize(event, false);
                // we have a different instance because needed to be synchronized
                if (evt != event){
                    intactPublication.getLifecycleEvents().add(intactPublication.getLifecycleEvents().indexOf(event), evt);
                    intactPublication.getLifecycleEvents().remove(event);
                }
            }
        }
    }

    protected void prepareExperiments(IntactPublication intactPublication) throws PersisterException, FinderException, SynchronizerException {
        if (intactPublication.areExperimentsInitialized()){
            List<Experiment> experimentToPersist = new ArrayList<Experiment>(intactPublication.getExperiments());
            for (Experiment experiment : experimentToPersist){
                // do not persist or merge experiments because of cascades
                Experiment pubExperiment = this.experimentSynchronizer.synchronize(experiment, false);
                // we have a different instance because needed to be synchronized
                if (pubExperiment != experiment){
                    intactPublication.removeExperiment(experiment);
                    intactPublication.addExperiment(pubExperiment);
                }
            }
        }
    }

    protected void preparePublicationAuthors(IntactPublication intactPublication) {
        if (intactPublication.getAuthors().isEmpty()){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactPublication.getAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
        }
        else{
            String authorAnnot = StringUtils.join(intactPublication.getAuthors(), ", ");
            // truncate if necessary
            if (IntactUtils.MAX_DESCRIPTION_LEN < authorAnnot.length()){
                log.warn("Publication authors too long: "+authorAnnot+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
                authorAnnot = authorAnnot.substring(0, IntactUtils.MAX_DESCRIPTION_LEN);
            }
            Annotation authorList = AnnotationUtils.collectFirstAnnotationWithTopic(intactPublication.getAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
            if (authorList != null){
                if (!authorAnnot.equalsIgnoreCase(authorList.getValue())){
                    authorList.setValue(authorAnnot);
                }
            }
            else{
                intactPublication.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.AUTHOR, Annotation.AUTHOR_MI), authorAnnot));
            }
        }
    }

    protected void prepareJournal(IntactPublication intactPublication) {
        if (intactPublication.getJournal() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactPublication.getAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
        }
        else{
            Annotation journal = AnnotationUtils.collectFirstAnnotationWithTopic(intactPublication.getAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
            if (journal != null){
                if (!intactPublication.getJournal().equalsIgnoreCase(journal.getValue())){
                    journal.setValue(intactPublication.getJournal());
                }
            }
            else{
                intactPublication.getAnnotations().add(new CvTermAnnotation(IntactUtils.createMITopic(Annotation.PUBLICATION_JOURNAL, Annotation.PUBLICATION_JOURNAL_MI), intactPublication.getJournal()));
            }
        }
    }

    protected void prepareCurationDepth(IntactPublication intactPublication) {
        Annotation depth = AnnotationUtils.collectFirstAnnotationWithTopic(intactPublication.getAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
        switch (intactPublication.getCurationDepth()){
            case IMEx:
                if (depth != null){
                    if (!Annotation.IMEX_CURATION.equalsIgnoreCase(depth.getValue())){
                        depth.setValue(Annotation.IMEX_CURATION);
                    }
                }
                else{
                    intactPublication.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.IMEX_CURATION));
                }
                break;
            case MIMIx:
                if (depth != null){
                    if (!Annotation.MIMIX_CURATION.equalsIgnoreCase(depth.getValue())){
                        depth.setValue(Annotation.MIMIX_CURATION);
                    }
                }
                else{
                    intactPublication.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.MIMIX_CURATION));
                }
                break;
            case rapid_curation:
                if (depth != null){
                    if (!Annotation.RAPID_CURATION.equalsIgnoreCase(depth.getValue())){
                        depth.setValue(Annotation.RAPID_CURATION);
                    }
                }
                else{
                    intactPublication.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.RAPID_CURATION));
                }
                break;
            default:
                AnnotationUtils.removeAllAnnotationsWithTopic(intactPublication.getAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
                break;
        }
    }

    protected void prepareXrefs(IntactPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactPublication.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref pubRef = this.xrefSynchronizer.synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (pubRef != xref){
                    intactPublication.getPersistentXrefs().remove(xref);
                    intactPublication.getPersistentXrefs().add(pubRef);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactPublication.getPersistentAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation pubAnnotation = this.annotationSynchronizer.synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (pubAnnotation != annotation){
                    intactPublication.getPersistentAnnotations().remove(annotation);
                    intactPublication.getPersistentAnnotations().add(pubAnnotation);
                }
            }
        }
    }

    protected void preparePublicationDate(IntactPublication intactPublication) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.getPublicationDate() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactPublication.getAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
        }
        else{
            Annotation year = AnnotationUtils.collectFirstAnnotationWithTopic(intactPublication.getAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
            String yearValue = IntactUtils.YEAR_FORMAT.format(intactPublication.getPublicationDate());
            if (year != null){
                if (year.getValue() != null){
                    if (!yearValue.equalsIgnoreCase(year.getValue())){
                        year.setValue(yearValue);
                    }
                }
                else{
                    year.setValue(yearValue);
                }
            }
            else{
                intactPublication.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.PUBLICATION_YEAR, Annotation.PUBLICATION_YEAR_MI), yearValue));
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

    protected void prepareAndSynchronizeShortLabel(IntactPublication intactPublication) throws SynchronizerException {
        // first initialise shortlabel if not done
        String pubmed = intactPublication.getPubmedId();
        String doi = intactPublication.getDoi();
        if (pubmed != null ){
            intactPublication.setShortLabel(pubmed);
        }
        else if (doi != null){
            intactPublication.setDoi(doi);
        }
        else if (!intactPublication.getIdentifiers().isEmpty()){
            intactPublication.setShortLabel(intactPublication.getIdentifiers().iterator().next().getId());
        }
        else {
            // create unassigned pubmed id
            SequenceManager seqManager = ApplicationContextProvider.getBean(SequenceManager.class);
            if (seqManager == null){
                throw new SynchronizerException("The publication synchronizer needs a sequence manager to automatically generate a unassigned pubmed identifier for backward compatibility. No sequence manager bean " +
                        "was found in the spring context.");
            }
            seqManager.createSequenceIfNotExists(IntactUtils.UNASSIGNED_SEQ, 1);
            String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.UNASSIGNED_SEQ));
            String identifier = "unassigned" + nextIntegerAsString;
            // set identifier
            intactPublication.setShortLabel(identifier);
            // add xref
            intactPublication.getIdentifiers().add(new PublicationXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), identifier, IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI)));
        }
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactPublication.getShortLabel().length()){
            log.warn("Publication shortLabel too long: "+intactPublication.getShortLabel()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactPublication.setShortLabel(intactPublication.getShortLabel().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactPublicationMergerEnrichOnly(this));
    }
}
