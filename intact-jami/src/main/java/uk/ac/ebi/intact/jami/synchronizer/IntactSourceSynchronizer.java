package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactCvTermComparator;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Default synchronizer for sources
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public class IntactSourceSynchronizer implements IntactDbSynchronizer<Source> {
    private EntityManager entityManager;
    private Map<Source, Source> persistedObjects;

    private IntactDbSynchronizer<Alias> aliasSynchronizer;
    private IntactDbSynchronizer<Annotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref> xrefSynchronizer;

    public IntactSourceSynchronizer(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("A Source synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Source, Source>();
        this.aliasSynchronizer = new IntactAliasSynchronizer(this.entityManager, SourceAlias.class);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer(this.entityManager, SourceAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer(this.entityManager, SourceXref.class);
    }

    public IntactSourceSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias> aliasSynchronizer,
                                    IntactDbSynchronizer<Annotation> annotationSynchronizer, IntactDbSynchronizer<Xref> xrefSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("A Source synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Source, Source>();
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer(this.entityManager, SourceAlias.class);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer(this.entityManager, SourceAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null ? xrefSynchronizer : new IntactXrefSynchronizer(this.entityManager, SourceXref.class);
    }

    public Source find(Source term) throws FinderException{
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        else if (term.getMIIdentifier() != null){
            query = this.entityManager.createQuery("select s from IntactSource s " +
                    "join s.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", term.getMIIdentifier());
        }
        else if (term.getPARIdentifier() != null){
            query = this.entityManager.createQuery("select s from IntactSource s " +
                    "join s.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                    "and d.shortName = :psipar " +
                    "and x.id = :par");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psipar", CvTerm.PSI_PAR);
            query.setParameter("par", term.getPARIdentifier());
        }
        else if (!term.getIdentifiers().isEmpty()){
            boolean foundSeveral = false;
            for (Xref ref : term.getIdentifiers()){
                query = this.entityManager.createQuery("select s from IntactSource s " +
                        "join s.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                        "and d.shortName = :db " +
                        "and x.id = :id");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("db", ref.getDatabase().getShortName());
                query.setParameter("id", ref.getId());

                Collection<Source> cvs = query.getResultList();
                if (cvs.size() == 1){
                    return cvs.iterator().next();
                }
                else if (cvs.size() > 1){
                    foundSeveral = true;
                }
            }
            if (foundSeveral){
                throw new FinderException("The source "+term.toString() + " has some identifiers that can match several institutions in the database and we cannot determine which one is valid.");
            }
            else{
                query = this.entityManager.createQuery("select s from IntactSource s " +
                        "where s.shortName = :name");
                query.setParameter("name", term.getShortName().trim().toLowerCase());
            }
        }
        else{
            query = this.entityManager.createQuery("select s from IntactSource s " +
                    "where s.shortName = :name");
            query.setParameter("name", term.getShortName().trim().toLowerCase());
        }
        return (Source) query.getSingleResult();
    }

    public Source persist(Source object) throws FinderException, PersisterException, SynchronizerException{
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        this.persistedObjects.put(object, object);

        IntactSource intactSource = (IntactSource)object;
        // synchronize properties
        synchronizeProperties(intactSource);

        // persist the source
        this.entityManager.persist(intactSource);

        return intactSource;
    }

    public void synchronizeProperties(Source object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((IntactSource)object);
    }

    public Source synchronize(Source object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        if (!(object instanceof IntactSource)){
            IntactSource newSource = new IntactSource(object.getShortName());
            CvTermCloner.copyAndOverrideCvTermProperties(object, newSource);

            Source retrievedSource = findOrPersist(newSource, persist);
            this.persistedObjects.put(retrievedSource, retrievedSource);
            return retrievedSource;
        }
        else{
            IntactSource intactType = (IntactSource)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // first synchronize properties before merging
                synchronizeProperties(intactType);
                // then merge
                if (merge){
                    IntactSource newSource = this.entityManager.merge(intactType);
                    this.persistedObjects.put(newSource, newSource);
                    return newSource;
                }
                else{
                    this.persistedObjects.put(intactType, intactType);
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                Source newTopic = findOrPersist(intactType, persist);
                this.persistedObjects.put(newTopic, newTopic);
                return newTopic;
            }
            else{
                // only synchronize properties
                synchronizeProperties(intactType);
                this.persistedObjects.put(intactType, intactType);
                return object;
            }
        }
    }

    protected void synchronizeProperties(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
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
        this.aliasSynchronizer.clearCache();
        this.xrefSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();
    }

    protected void prepareXrefs(IntactSource intactSource) throws FinderException, PersisterException, SynchronizerException {
        if (intactSource.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactSource.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref cvXref = this.xrefSynchronizer.synchronize(xref, false, false);
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
                Annotation cvAnnotation = this.annotationSynchronizer.synchronize(annotation, false, false);
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
                Alias cvAlias = this.aliasSynchronizer.synchronize(alias, false, false);
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
            intactSource.setShortName(intactSource.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        // check if short name already exist, if yes, synchronize
        Query query = this.entityManager.createQuery("select s from IntactSource s " +
                "where s.shortName = :name");
        query.setParameter("name", intactSource.getShortName().trim().toLowerCase());
        List<IntactSource> existingSources = query.getResultList();
        if (!existingSources.isEmpty()){
            int max = 1;
            for (IntactSource source : existingSources){
                String name = source.getShortName();
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
            if (IntactUtils.MAX_SHORT_LABEL_LEN < intactSource.getShortName().length()+maxString.length()+1){
                intactSource.setShortName(intactSource.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN-(maxString.length()+1))
                        +"-"+maxString);
            }
            else{
                intactSource.setShortName(intactSource.getShortName()+"-"+maxString);
            }
        }
    }

    protected Source findOrPersist(Source cvType, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        Source existingInstance = find(cvType);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            // synchronize before persisting
            synchronizeProperties(cvType);
            if (persist){
                this.entityManager.persist(cvType);
            }
            return cvType;
        }
    }
}
