package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.CvTermMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactCvTermComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class CvTermSynchronizer extends AbstractIntactDbSynchronizer<CvTerm, IntactCvTerm> implements CvTermFetcher<CvTerm> {

    private String objClass;
    private Map<CvTerm, IntactCvTerm> persistedObjects;

    private static final Log log = LogFactory.getLog(CvTermSynchronizer.class);

    public CvTermSynchronizer(SynchronizerContext context){
        super(context, IntactCvTerm.class);
        // all new cv created will be annotation topic by default
        this.objClass = IntactUtils.TOPIC_OBJCLASS;
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<CvTerm, IntactCvTerm>(new IntactCvTermComparator());
    }

    public CvTermSynchronizer(SynchronizerContext context, String objClass){
        this(context);
        //If no objclass provided all new cv created will be annotation topic by default
        this.objClass = objClass != null ? objClass : IntactUtils.TOPIC_OBJCLASS;
    }

    public IntactCvTerm find(CvTerm term) throws FinderException {
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
            else if (term.getMODIdentifier() != null){
                return fetchByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MOD, false);
            }
            else if (term.getPARIdentifier() != null){
                return fetchByIdentifier(term.getMIIdentifier(), CvTerm.PSI_PAR, false);
            }
            else if (!term.getIdentifiers().isEmpty()){
                boolean foundSeveral = false;
                for (Xref ref : term.getIdentifiers()){
                    try{
                        IntactCvTerm fetchedTerm = fetchByIdentifier(ref.getId(), ref.getDatabase().getShortName(), true);
                        if (fetchedTerm != null){
                            return fetchedTerm;
                        }
                    }
                    catch (BridgeFailedException e){
                        foundSeveral = true;
                    }
                }

                if (foundSeveral){
                    throw new FinderException("The cv "+term.toString() + " has some identifiers that can match several terms in the database and we cannot determine which one is valid.");
                }
                else{
                    return (IntactCvTerm)fetchByName(term.getShortName(), null);
                }
            }
            else{
                return (IntactCvTerm)fetchByName(term.getShortName(), null);
            }
        } catch (BridgeFailedException e) {
            throw new FinderException("Problem fetching cv term from the database", e);
        }
    }

    public void synchronizeProperties(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        // first set objclass
        initialiseObjClass(intactCv);
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactCv);
        // then check full name
        prepareFullName(intactCv);
        // then check def
        prepareDefinition(intactCv);
        // then check aliases
        prepareAliases(intactCv);
        // then check annotations
        prepareAnnotations(intactCv);
        // set identifier for backward compatibility
        initialiseIdentifier(intactCv);
        // then check xrefs
        prepareXrefs(intactCv);
        // do synchronize children
        prepareChildren(intactCv);
        // do not synchronize parent
        //prepareParents(intactCv);
    }

    public OntologyTerm fetchByIdentifier(String termIdentifier, String miOntologyName) throws BridgeFailedException {
        if(termIdentifier == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        if(miOntologyName == null)
            throw new IllegalArgumentException("Can not search for an identifier in an ontology without a value.");
        return fetchByIdentifier(termIdentifier, miOntologyName, true);
    }

    public OntologyTerm fetchByIdentifier(String termIdentifier, CvTerm ontologyDatabase) throws BridgeFailedException {
        if(termIdentifier == null)
            throw new IllegalArgumentException("Can not search for an identifier without a value.");
        if(ontologyDatabase == null)
            throw new IllegalArgumentException("Can not search for an identifier in an ontology without a value.");
        return fetchByIdentifier(termIdentifier, ontologyDatabase.getShortName(), true);
    }

    public OntologyTerm fetchByName(String searchName, String miOntologyName) throws BridgeFailedException {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", searchName.trim().toLowerCase());
        if (objClass != null){
            query.setParameter("objclass", objClass);
        }
        Collection<IntactCvTerm> cvs = query.getResultList();
        if (cvs.size() == 1){
            return cvs.iterator().next();
        }
        else if (cvs.size() > 1){
            throw new BridgeFailedException("The cv "+searchName + " can match "+cvs.size()+" terms in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public Collection<CvTerm> fetchByName(String searchName) throws BridgeFailedException {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName like :name" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", "%"+searchName.trim().toLowerCase()+"%");
        if (objClass != null){
            query.setParameter("objclass", objClass);
        }
        return query.getResultList();
    }

    public Collection<CvTerm> fetchByIdentifiers(Collection<String> termIdentifiers, String miOntologyName)
            throws BridgeFailedException {
        if (termIdentifiers == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<CvTerm> results = new ArrayList<CvTerm>(termIdentifiers.size());
        for (String id : termIdentifiers){
            OntologyTerm element = fetchByIdentifier(id, miOntologyName);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<CvTerm> fetchByIdentifiers(Collection<String> termIdentifiers, CvTerm ontologyDatabase)
            throws BridgeFailedException {
        if (termIdentifiers == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<CvTerm> results = new ArrayList<CvTerm>(termIdentifiers.size());
        for (String id : termIdentifiers){
            OntologyTerm element = fetchByIdentifier(id, ontologyDatabase);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<CvTerm> fetchByNames(Collection<String> searchNames, String miOntologyName)
            throws BridgeFailedException {
        if (searchNames == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<CvTerm> results = new ArrayList<CvTerm>(searchNames.size());
        for (String id : searchNames){
            OntologyTerm element = fetchByName(id, miOntologyName);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    public Collection<CvTerm> fetchByNames(Collection<String> searchNames)
            throws BridgeFailedException {
        if (searchNames == null){
            throw new IllegalArgumentException("The term identifiers cannot be null.");
        }

        Collection<CvTerm> results = new ArrayList<CvTerm>(searchNames.size());
        for (String id : searchNames){
            results.addAll(fetchByName(id));

        }
        return results;
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    public String getObjClass() {
        return objClass;
    }

    public void setObjClass(String objClass) {
        this.objClass = objClass != null ? objClass : IntactUtils.TOPIC_OBJCLASS;
    }

    protected IntactCvTerm fetchByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) throws BridgeFailedException {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where cv.ac = :id" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("id", termIdentifier);
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
            Collection<IntactCvTerm> cvs = query.getResultList();
            if (cvs.size() == 1){
                return cvs.iterator().next();
            }
        }

        query = getEntityManager().createQuery("select distinct cv from IntactCvTerm cv " +
                "join cv.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                "and d.shortName = :psiName " +
                "and x.id = :psiId" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psiName", miOntologyName.toLowerCase().trim());
        query.setParameter("psiId", termIdentifier);
        if (objClass != null){
            query.setParameter("objclass", objClass);
        }

        Collection<IntactCvTerm> cvs = query.getResultList();
        if (cvs.size() == 1){
            return cvs.iterator().next();
        }
        else if (cvs.size() > 1){
            throw new BridgeFailedException("The cv "+termIdentifier + " can match "+cvs.size()+" terms in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    @Override
    protected Object extractIdentifier(IntactCvTerm object) {
        return object.getAc();
    }

    @Override
    protected IntactCvTerm instantiateNewPersistentInstance(CvTerm object, Class<? extends IntactCvTerm> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCvTerm cv = intactClass.getConstructor(String.class).newInstance(object.getShortName());
        CvTermCloner.copyAndOverrideCvTermProperties(object, cv);
        return cv;
    }

    @Override
    protected void storeInCache(CvTerm originalObject, IntactCvTerm persistentObject, IntactCvTerm existingInstance) {
        this.persistedObjects.put(originalObject, existingInstance != null ? existingInstance : persistentObject);
    }

    @Override
    protected IntactCvTerm fetchObjectFromCache(CvTerm object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(CvTerm object) {
        return this.persistedObjects.containsKey(object);
    }

    protected void initialiseIdentifier(IntactCvTerm intactCv) throws SynchronizerException {
        // if xrefs have been initialised, some identifiers may have changed
        if (intactCv.areXrefsInitialized()){
            // first look at PSI-MI
            if (intactCv.getMIIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMIIdentifier());
            }
            // then MOD identifier
            else if (intactCv.getMODIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMODIdentifier());
            }
            // then PAR identifier
            else if (intactCv.getPARIdentifier() != null){
                intactCv.setIdentifier(intactCv.getPARIdentifier());
            }
            // then first identifier
            else if (!intactCv.getIdentifiers().isEmpty()){
                intactCv.setIdentifier(intactCv.getIdentifiers().iterator().next().getId());
            }
            else {
                final IntactContext context = ApplicationContextProvider.getBean("intactContext");
                String prefix = "IA";
                Source institution = null;
                if (context != null){
                    prefix = context.getConfig().getLocalCvPrefix();
                    institution = context.getConfig().getDefaultInstitution();
                }
                if (institution != null){
                    SequenceManager seqManager = ApplicationContextProvider.getBean(SequenceManager.class);
                    if (seqManager == null){
                        throw new SynchronizerException("The Cv identifier synchronizer needs a sequence manager to automatically generate a cv identifier for backward compatibility. No sequence manager bean " +
                                "was found in the spring context.");
                    }
                    seqManager.createSequenceIfNotExists(IntactUtils.CV_LOCAL_SEQ, 1);
                    String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.CV_LOCAL_SEQ));
                    String identifier = prefix+":" + StringUtils.leftPad(nextIntegerAsString, 4, "0");
                    // set identifier
                    intactCv.setIdentifier(identifier);
                    // add xref
                    intactCv.getIdentifiers().add(new CvTermXref(IntactUtils.createMIDatabase(institution.getShortName(), institution.getMIIdentifier()), identifier, IntactUtils.createIdentityQualifier()));
                }
            }
        }
    }

    protected void prepareDefinition(IntactCvTerm intactCv) {
        if (intactCv.getDefinition() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactCv.getAnnotations(), null, "definition");
        }
        else{
            // truncate if necessary
            if (IntactUtils.MAX_DESCRIPTION_LEN < intactCv.getDefinition().length()){
                log.warn("Cv term definition too long: "+intactCv.getDefinition()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
                intactCv.setDefinition(intactCv.getDefinition().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
            }
            Annotation def = AnnotationUtils.collectFirstAnnotationWithTopic(intactCv.getAnnotations(), null, "definition");
            if (def != null){
                if (!intactCv.getDefinition().equalsIgnoreCase(def.getValue())){
                    def.setValue(intactCv.getDefinition());
                }
            }
            else{
                intactCv.getAnnotations().add(new CvTermAnnotation(IntactUtils.createMITopic("definition", null), intactCv.getDefinition()));
            }
        }
    }

    protected void prepareChildren(IntactCvTerm intactCv) throws PersisterException, FinderException, SynchronizerException {

        if (intactCv.areChildrenInitialized()){
            List<OntologyTerm> termsToPersist = new ArrayList<OntologyTerm>(intactCv.getChildren());
            for (OntologyTerm term : termsToPersist){
                IntactCvTerm cvChild = synchronize(term, true);
                // we have a different instance because needed to be synchronized
                if (cvChild != term){
                    intactCv.removeChild(term);
                    intactCv.addChild(cvChild);
                }
            }
        }
    }

    /*protected void prepareParents(IntactCvTerm intactCv) throws PersisterException, FinderException, SynchronizerException {

        if (intactCv.areParentsInitialized()){
            List<OntologyTerm> termsToPersist = new ArrayList<OntologyTerm>(intactCv.getParents());
            for (OntologyTerm term : termsToPersist){
                IntactCvTerm cvParent = synchronize(term, true);
                // we have a different instance because needed to be synchronized
                if (cvParent != term){
                    intactCv.removeParent(term);
                    intactCv.addParent(cvParent);
                }
            }
        }
    }*/

    protected void prepareXrefs(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactCv.getDbXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                CvTermXref cvXref = getContext().getCvXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (cvXref != xref){
                    intactCv.getDbXrefs().remove(xref);
                    intactCv.getDbXrefs().add(cvXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactCv.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                CvTermAnnotation cvAnnotation = getContext().getCvAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (cvAnnotation != annotation){
                    intactCv.getAnnotations().remove(annotation);
                    intactCv.getAnnotations().add(cvAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areSynonymsInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactCv.getSynonyms());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                CvTermAlias cvAlias = getContext().getCvAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (cvAlias != alias){
                    intactCv.getSynonyms().remove(alias);
                    intactCv.getSynonyms().add(cvAlias);
                }
            }
        }
    }

    protected void prepareFullName(IntactCvTerm intactCv) {
        // truncate if necessary
        if (intactCv.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactCv.getFullName().length()){
            log.warn("Cv term fullName too long: "+intactCv.getFullName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactCv.setFullName(intactCv.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactCvTerm intactCv) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()){
            log.warn("Cv term shortLabel too long: "+intactCv.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        boolean first = true;
        String name;
        List<String> existingCvs;
        do{
            name = intactCv.getShortName().trim().toLowerCase();
            existingCvs = Collections.EMPTY_LIST;
            String originalName = first ? name : IntactUtils.excludeLastNumberInShortLabel(name);

            if (first){
                first = false;
            }
            else if (originalName.length() > 1){
                name = originalName.substring(0, originalName.length() - 1);
            }
            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select cv.shortName from IntactCvTerm cv " +
                    "where (cv.shortName = :name or cv.shortName like :nameWithSuffix)"
                    + (this.objClass != null ? " and cv.objClass = :objclass " : " ")
                    + (intactCv.getAc() != null ? "and cv.ac <> :cvAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            if (this.objClass != null){
                query.setParameter("objclass", this.objClass);
            }
            if (intactCv.getAc() != null){
                query.setParameter("cvAc", intactCv.getAc());
            }
            existingCvs = query.getResultList();
            String nameInSync = IntactUtils.synchronizeShortlabel(name, existingCvs, IntactUtils.MAX_SHORT_LABEL_LEN, false);
            intactCv.setShortName(nameInSync);
        }
        while(name.length() > 1 && !existingCvs.isEmpty());
    }

    protected void initialiseObjClass(IntactCvTerm intactCv) {
        if (this.objClass != null){
            intactCv.setObjClass(this.objClass);
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new CvTermMergerEnrichOnly(this));
    }

    @Override
    protected void persistObject(IntactCvTerm existingInstance) {
        // first remove all dependencies to other cv terms to avoid cycle dependencies when persisting the objects
        Collection<Alias> cvAliases = new ArrayList<Alias>(existingInstance.getSynonyms());
        existingInstance.getSynonyms().clear();
        Collection<Annotation> cvAnnotations = new ArrayList<Annotation>(existingInstance.getAnnotations());
        existingInstance.getAnnotations().clear();
        Collection<Xref> cvRefs = new ArrayList<Xref>(existingInstance.getDbXrefs());
        existingInstance.getDbXrefs().clear();
        Collection<OntologyTerm> children = new ArrayList<OntologyTerm>(existingInstance.getChildren());
        existingInstance.getChildren().clear();
        Collection<OntologyTerm> parents = new ArrayList<OntologyTerm>(existingInstance.getParents());
        existingInstance.getParents().clear();

        super.persistObject(existingInstance);

        // after persistence, re-attach dependent objects
        existingInstance.getSynonyms().addAll(cvAliases);
        existingInstance.getDbXrefs().addAll(cvRefs);
        existingInstance.getAnnotations().addAll(cvAnnotations);
        existingInstance.getChildren().addAll(children);
        existingInstance.getParents().addAll(parents);
    }
}
