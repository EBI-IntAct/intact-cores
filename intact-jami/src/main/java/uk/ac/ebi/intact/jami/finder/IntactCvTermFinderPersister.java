package uk.ac.ebi.intact.jami.finder;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Default finder for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class IntactCvTermFinderPersister implements IntactDbFinderPersister<CvTerm> {

    private EntityManager entityManager;
    private String objClass;

    private Map<CvTerm, CvTerm> persistedObjects;

    public IntactCvTermFinderPersister(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("A Cv Term finder needs a non null entity manager");
        }
        this.entityManager = entityManager;
        this.objClass = null;
        // to keep track of persisted cvs
        this.persistedObjects = new HashMap<CvTerm, CvTerm>();
    }

    public IntactCvTermFinderPersister(EntityManager entityManager, String objClass){
        this(entityManager);
        this.objClass = objClass;
    }

    public CvTerm find(CvTerm term) throws FinderException{
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        else if (term.getMIIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", term.getMIIdentifier());
            if (this.objClass != null){
                query.setParameter("objclass", this.objClass);
            }
        }
        else if (term.getMODIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                    "and d.shortName = :psimod " +
                    "and x.id = :mod"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimod", CvTerm.PSI_MOD);
            query.setParameter("mod", term.getMODIdentifier());
            if (this.objClass != null){
                query.setParameter("objclass", this.objClass);
            }
        }
        else if (term.getPARIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                    "and d.shortName = :psipar " +
                    "and x.id = :par"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psipar", CvTerm.PSI_PAR);
            query.setParameter("par", term.getPARIdentifier());
            if (this.objClass != null){
                query.setParameter("objclass", this.objClass);
            }
        }
        else if (!term.getIdentifiers().isEmpty()){
            boolean foundSeveral = false;
            for (Xref ref : term.getIdentifiers()){
                query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                        "and d.shortName = :db " +
                        "and x.id = :id"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("db", ref.getDatabase().getShortName());
                query.setParameter("id", ref.getId());
                if (this.objClass != null){
                    query.setParameter("objclass", this.objClass);
                }

                Collection<CvTerm> cvs = query.getResultList();
                if (cvs.size() == 1){
                    return cvs.iterator().next();
                }
                else if (cvs.size() > 1){
                    foundSeveral = true;
                }
            }
            if (foundSeveral){
                throw new FinderException("The cv "+term.toString() + " has some identifiers that can match several terms in the database and we cannot determine which one is valid.");
            }
            else{
                query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                        "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
                query.setParameter("name", term.getShortName().trim().toLowerCase());
                if (this.objClass != null){
                    query.setParameter("objclass", this.objClass);
                }
            }
        }
        else{
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            if (this.objClass != null){
                query.setParameter("objclass", this.objClass);
            }
        }
        return (CvTerm) query.getSingleResult();
    }

    public CvTerm persist(CvTerm object) throws FinderException{
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        this.persistedObjects.put(object, object);

        IntactCvTerm intactCv = (IntactCvTerm)object;
        // first set objclass
        initialiseObjClass(intactCv);
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactCv);
        // then check full name
        prepareFullName(intactCv);
        // then check aliases
        prepareAliases(intactCv);
        // then check annotations
        prepareAnnotations(intactCv);
        // then check xrefs
        prepareXrefs(intactCv);
        // persist the cv
        this.entityManager.persist(intactCv);

        return intactCv;
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    protected void prepareXrefs(IntactCvTerm intactCv) throws FinderException {
        List<Xref> xrefsToPersist = new ArrayList<Xref>(intactCv.getXrefs());
        for (Xref xref : xrefsToPersist){
            CvTermXref cvXref;
            // we have an instance of CvTermXref
            if (xref instanceof CvTermXref){
                cvXref = (CvTermXref) xref;
                if (cvXref.getParent() != null && cvXref.getParent() != intactCv){
                    intactCv.getXrefs().remove(cvXref);
                    cvXref = new CvTermXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                    intactCv.getXrefs().add(cvXref);
                }
            }
            // we create a brand new cv xref and persist
            else{
                cvXref = new CvTermXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                intactCv.getXrefs().remove(xref);
                intactCv.getXrefs().add(cvXref);
            }

            // pre persist database
            cvXref.setDatabase(preparePreExistingCv(cvXref.getDatabase()));
            // pre persist qualifier
            if (cvXref.getQualifier() != null){
                cvXref.setQualifier(preparePreExistingCv(cvXref.getQualifier()));
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

    protected void prepareAnnotations(IntactCvTerm intactCv) throws FinderException {
        List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactCv.getAnnotations());
        for (Annotation annotation : annotationsToPersist){
            CvTermAnnotation cvAnnot;
            // we have an instance of CvTermAnnotation
            if (annotation instanceof CvTermAnnotation){
                cvAnnot = (CvTermAnnotation) annotation;
                if (cvAnnot.getParent() != null && cvAnnot.getParent() != intactCv){
                    intactCv.getAnnotations().remove(cvAnnot);
                    cvAnnot = new CvTermAnnotation(annotation.getTopic(), annotation.getValue());
                    intactCv.getAnnotations().add(cvAnnot);
                }
            }
            // we create a brand new cv annotation and persist
            else{
                cvAnnot = new CvTermAnnotation(annotation.getTopic(), annotation.getValue());
                intactCv.getAnnotations().remove(annotation);
                intactCv.getAnnotations().add(cvAnnot);
            }

            // pre persist annotation topic
            cvAnnot.setTopic(preparePreExistingCv(cvAnnot.getTopic()));

            // check annotation value
            if (cvAnnot.getValue() != null && cvAnnot.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
                cvAnnot.setValue(cvAnnot.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
            }
        }
    }

    protected void prepareAliases(IntactCvTerm intactCv) throws FinderException {
        List<Alias> aliasesToPersist = new ArrayList<Alias>(intactCv.getSynonyms());
        for (Alias alias : aliasesToPersist){
            CvTermAlias cvAlias;
            // we have an instance of CvTermAlias
            if (alias instanceof CvTermAlias){
                cvAlias = (CvTermAlias) alias;
                if (cvAlias.getParent() != null && cvAlias.getParent() != intactCv){
                    intactCv.getSynonyms().remove(cvAlias);
                    cvAlias = new CvTermAlias(alias.getType(), alias.getName());
                    intactCv.getSynonyms().add(cvAlias);
                }
            }
            // we create a brand new cv alias and persist
            else{
               cvAlias = new CvTermAlias(alias.getType(), alias.getName());
               intactCv.getSynonyms().remove(alias);
               intactCv.getSynonyms().add(cvAlias);
            }

            // check alias type
            CvTerm aliasType = cvAlias.getType();
            if (aliasType != null){
                // pre persist alias type
                cvAlias.setType(preparePreExistingCv(cvAlias.getType()));
            }

            // check alias name
            if (cvAlias.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
                 cvAlias.setName(cvAlias.getName().substring(0,IntactUtils.MAX_ALIAS_NAME_LEN));
            }
        }
    }

    protected void prepareFullName(IntactCvTerm intactCv) {
        // truncate if necessary
        if (intactCv.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactCv.getFullName().length()){
            intactCv.setFullName(intactCv.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactCvTerm intactCv) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()){
            intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        // check if short name already exist, if yes, synchronize
        Query query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", intactCv.getShortName().trim().toLowerCase());
        if (this.objClass != null){
            query.setParameter("objclass", this.objClass);
        }
        List<IntactCvTerm> existingCvs = query.getResultList();
        if (!existingCvs.isEmpty()){
            int max = 1;
            for (IntactCvTerm cv : existingCvs){
                String name = cv.getShortName();
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
            if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()+maxString.length()+1){
                 intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN-(maxString.length()+1))
                 +"-"+maxString);
            }
            else{
                intactCv.setShortName(intactCv.getShortName()+"-"+maxString);
            }
        }
    }

    protected void initialiseObjClass(IntactCvTerm intactCv) {
        intactCv.setObjClass(this.objClass);
    }

    protected CvTerm findOrPersist(CvTerm cvType) throws FinderException {
        CvTerm existingInstance = find(cvType);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            this.entityManager.persist(cvType);
            return cvType;
        }
    }

    protected CvTerm preparePreExistingCv(CvTerm cv) throws FinderException {
        if (this.persistedObjects.containsKey(cv)){
            return this.persistedObjects.get(cv);
        }

        if (!(cv instanceof IntactCvTerm)){
            CvTerm newTopic = new IntactCvTerm(cv.getShortName());
            CvTermCloner.copyAndOverrideCvTermProperties(cv, newTopic);
            this.persistedObjects.put(newTopic, newTopic);

            return newTopic;
        }
        else{
            IntactCvTerm intactType = (IntactCvTerm)cv;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                CvTerm newTopic = this.entityManager.merge(intactType);
                this.persistedObjects.put(newTopic, newTopic);
                return newTopic;
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                CvTerm newTopic = findOrPersist(intactType);
                this.persistedObjects.put(newTopic, newTopic);
                return newTopic;
            }
            else{
                this.persistedObjects.put(cv, cv);
                return cv;
            }
        }
    }
}
