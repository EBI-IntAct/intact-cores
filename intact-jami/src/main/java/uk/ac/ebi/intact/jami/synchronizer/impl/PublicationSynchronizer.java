package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import psidev.psi.mi.jami.utils.comparator.publication.UnambiguousPublicationComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.PublicationMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.extension.PublicationAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

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

public class PublicationSynchronizer<I extends IntactPublication> extends AbstractIntactDbSynchronizer<Publication, I>
        implements PublicationFetcher{

    private Map<Publication, I> persistedObjects;

    private static final Log log = LogFactory.getLog(PublicationSynchronizer.class);

    public PublicationSynchronizer(SynchronizerContext context, Class<I> intactClass) {
        super(context, intactClass);
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Publication, I>(new UnambiguousPublicationComparator());
    }

    public PublicationSynchronizer(SynchronizerContext context){
        super(context, (Class<I>)IntactPublication.class);
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Publication, I>(new UnambiguousPublicationComparator());
    }

    public I find(Publication publication) throws FinderException {
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
                        I fetchedPublication = fetchByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true);
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

    public I persist(I object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I persisted = super.persist(object);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    @Override
    public I synchronize(Publication object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I persisted = super.synchronize(object, persist);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    public void synchronizeProperties(I intactPublication) throws FinderException, PersisterException, SynchronizerException {
        // then check full name
        prepareTitle(intactPublication);
        // then check journal
        prepareJournal(intactPublication);
        // then check publicationDate
        preparePublicationDate(intactPublication);
        // then check authors
        preparePublicationAuthors(intactPublication);
        // then check annotations
        prepareAnnotations(intactPublication);
        // then check xrefs
        prepareXrefs(intactPublication);
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
    }

    protected I fetchByTitleAndJournal(String title, String journal, Date publicationDate, Collection<String> authors) throws BridgeFailedException {
        Query query;
        if(title == null && journal == null && publicationDate == null && authors.isEmpty()) {
           return null;
        }
        else if (authors.isEmpty()){
            query = getEntityManager().createQuery("select p from "+getIntactClass()+" p " +
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
            query = getEntityManager().createQuery("select p from "+getIntactClass()+" p " +
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

        Collection<I> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new BridgeFailedException("The publication title, journal, publication date and authors can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    protected I fetchByIdentifier(String identifier, String source, boolean checkAc) throws BridgeFailedException {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select p from "+getIntactClass()+" p " +
                    "where p.ac = :id");
            query.setParameter("id", identifier);
            Collection<I> publications = query.getResultList();
            if (publications.size() == 1){
                return publications.iterator().next();
            }
        }

        query = getEntityManager().createQuery("select p from "+getIntactClass()+" p " +
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

        Collection<I> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new BridgeFailedException("The publication "+identifier + " can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    protected I fetchByImexId(String imex) throws FinderException {

        Query query = getEntityManager().createQuery("select p from "+getIntactClass()+" p " +
                "join p.persistentXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where q.shortName = :imexPrimary " +
                "and d.shortName = :dbName " +
                "and x.id = :dbId");
        query.setParameter("imexPrimary", Xref.IMEX_PRIMARY.toLowerCase());
        query.setParameter("dbName", Xref.IMEX.toLowerCase());
        query.setParameter("dbId", imex);

        Collection<I> publications = query.getResultList();
        if (publications.size() == 1){
            return publications.iterator().next();
        }
        else if (publications.size() > 1){
            throw new FinderException("The publication "+imex + " can match "+publications.size()+" publications in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected I instantiateNewPersistentInstance(Publication object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I pub = intactClass.newInstance();
        PublicationCloner.copyAndOverridePublicationProperties(object, pub);
        return pub;
    }

    protected void preparePublicationAuthors(I intactPublication) {
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

    protected void prepareJournal(I intactPublication) {
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

    protected void prepareXrefs(I intactPublication) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactPublication.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref pubRef = getContext().getPublicationXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (pubRef != xref){
                    intactPublication.getPersistentXrefs().remove(xref);
                    intactPublication.getPersistentXrefs().add(pubRef);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactPublication) throws FinderException, PersisterException, SynchronizerException {
        if (intactPublication.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactPublication.getPersistentAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation pubAnnotation = getContext().getPublicationAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (pubAnnotation != annotation){
                    intactPublication.getPersistentAnnotations().remove(annotation);
                    intactPublication.getPersistentAnnotations().add(pubAnnotation);
                }
            }
        }
    }

    protected void preparePublicationDate(I intactPublication) throws FinderException, PersisterException, SynchronizerException {
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

    protected void prepareTitle(I intactPublication) {
        // truncate if necessary
        if (intactPublication.getTitle() != null && IntactUtils.MAX_FULL_NAME_LEN < intactPublication.getTitle().length()){
            log.warn("Publication title too long: "+intactPublication.getTitle()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactPublication.setTitle(intactPublication.getTitle().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger((IntactDbMerger<Publication,I>) new PublicationMergerEnrichOnly(this));
    }
}
