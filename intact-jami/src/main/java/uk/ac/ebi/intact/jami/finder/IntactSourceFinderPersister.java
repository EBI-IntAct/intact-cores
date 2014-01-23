package uk.ac.ebi.intact.jami.finder;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.model.extension.SourceAlias;
import uk.ac.ebi.intact.jami.model.extension.SourceAnnotation;
import uk.ac.ebi.intact.jami.model.extension.SourceXref;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Default finder for sources
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public class IntactSourceFinderPersister implements IntactDbFinderPersister<Source>{
    private EntityManager entityManager;
    private Map<Source, Source> persistedObjects;
    private IntactCvTermFinderPersister cvFinderPersister;

    public IntactSourceFinderPersister(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("A Cv Term finder needs a non null entity manager");
        }
        this.entityManager = entityManager;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<Source, Source>();
        this.cvFinderPersister = new IntactCvTermFinderPersister(this.entityManager);
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

    public Source persist(Source object) throws FinderException{
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        this.persistedObjects.put(object, object);

        IntactSource intactSource = (IntactSource)object;
        prepareSourceProperties(intactSource);

        // persist the source
        this.entityManager.persist(intactSource);

        return intactSource;
    }

    public void prepareSourceProperties(IntactSource intactSource) throws FinderException {
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
        this.cvFinderPersister.clearCache();
    }

    protected void prepareXrefs(IntactSource intactSource) throws FinderException {
        List<Xref> xrefsToPersist = new ArrayList<Xref>(intactSource.getXrefs());
        for (Xref xref : xrefsToPersist){
            SourceXref cvXref;
            // we have an instance of CvTermXref
            if (xref instanceof SourceXref){
                cvXref = (SourceXref) xref;
                if (cvXref.getParent() != null && cvXref.getParent() != intactSource){
                    intactSource.getXrefs().remove(cvXref);
                    cvXref = new SourceXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                    intactSource.getXrefs().add(cvXref);
                }
            }
            // we create a brand new source xref and persist
            else{
                cvXref = new SourceXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                intactSource.getXrefs().remove(xref);
                intactSource.getXrefs().add(cvXref);
            }

            // pre persist database
            cvXref.setDatabase(this.cvFinderPersister.preparePreExistingCv(cvXref.getDatabase()));
            // pre persist qualifier
            if (cvXref.getQualifier() != null){
                cvXref.setQualifier(this.cvFinderPersister.preparePreExistingCv(cvXref.getQualifier()));
            }

            // check secondaryId value
            if (cvXref.getSecondaryId() != null && cvXref.getSecondaryId().length() > IntactUtils.MAX_ID_LEN){
                cvXref.setSecondaryId(cvXref.getSecondaryId().substring(0,IntactUtils.MAX_ID_LEN));
            }

            // check version value
            if (cvXref.getVersion() != null && cvXref.getVersion().length() > IntactUtils.MAX_DB_RELEASE_LEN){
                cvXref.setVersion(cvXref.getVersion().substring(0,IntactUtils.MAX_DB_RELEASE_LEN));
            }
        }
    }

    protected void prepareAnnotations(IntactSource intactSource) throws FinderException {
        List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactSource.getAnnotations());
        for (Annotation annotation : annotationsToPersist){
            SourceAnnotation cvAnnot;
            // we have an instance of CvTermAnnotation
            if (annotation instanceof SourceAnnotation){
                cvAnnot = (SourceAnnotation) annotation;
                if (cvAnnot.getParent() != null && cvAnnot.getParent() != intactSource){
                    intactSource.getAnnotations().remove(cvAnnot);
                    cvAnnot = new SourceAnnotation(annotation.getTopic(), annotation.getValue());
                    intactSource.getAnnotations().add(cvAnnot);
                }
            }
            // we create a brand new source annotation and persist
            else{
                cvAnnot = new SourceAnnotation(annotation.getTopic(), annotation.getValue());
                intactSource.getAnnotations().remove(annotation);
                intactSource.getAnnotations().add(cvAnnot);
            }

            // pre persist annotation topic
            cvAnnot.setTopic(this.cvFinderPersister.preparePreExistingCv(cvAnnot.getTopic()));

            // check annotation value
            if (cvAnnot.getValue() != null && cvAnnot.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
                cvAnnot.setValue(cvAnnot.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
            }
        }
    }

    protected void prepareAliases(IntactSource intactSource) throws FinderException {
        List<Alias> aliasesToPersist = new ArrayList<Alias>(intactSource.getSynonyms());
        for (Alias alias : aliasesToPersist){
            SourceAlias cvAlias;
            // we have an instance of CvTermAlias
            if (alias instanceof SourceAlias){
                cvAlias = (SourceAlias) alias;
                if (cvAlias.getParent() != null && cvAlias.getParent() != intactSource){
                    intactSource.getSynonyms().remove(cvAlias);
                    cvAlias = new SourceAlias(alias.getType(), alias.getName());
                    intactSource.getSynonyms().add(cvAlias);
                }
            }
            // we create a brand new source alias and persist
            else{
                cvAlias = new SourceAlias(alias.getType(), alias.getName());
                intactSource.getSynonyms().remove(alias);
                intactSource.getSynonyms().add(cvAlias);
            }

            // check alias type
            CvTerm aliasType = cvAlias.getType();
            if (aliasType != null){
                // pre persist alias type
                cvAlias.setType(this.cvFinderPersister.preparePreExistingCv(cvAlias.getType()));
            }

            // check alias name
            if (cvAlias.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
                cvAlias.setName(cvAlias.getName().substring(0,IntactUtils.MAX_ALIAS_NAME_LEN));
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
}
