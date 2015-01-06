package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.CvTermFetcher;
import psidev.psi.mi.jami.model.*;
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
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactCvTermComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for cv terms
 *
 * - The cv term synchronizer does not follow the same strategy of the other synchronizers : it synchronizes its parents but not its children.
 * This is because we don't want to persist a full ontology, we just want to persist the parent relationships. To persist the children, the synchronizer must be called for each children first.
 * - The cv term synchronizer can truncate the shortlabel if it is too long. It can also append a suffix if the shortlabel already exist in the database
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class CvTermSynchronizer extends AbstractIntactDbSynchronizer<CvTerm, IntactCvTerm> implements CvTermFetcher<CvTerm> {

    private String objClass;
    private Map<CvTerm, IntactCvTerm> persistedObjects;
    private Map<CvTerm, IntactCvTerm> convertedObjects;

    private IntactComparator<CvTerm> cvComparator;

    private static final Log log = LogFactory.getLog(CvTermSynchronizer.class);

    public CvTermSynchronizer(SynchronizerContext context){
        super(context, IntactCvTerm.class);
        // all new cv created will be annotation topic by default
        this.objClass = null;
        cvComparator = new IntactCvTermComparator();
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<CvTerm, IntactCvTerm>(cvComparator);
        this.convertedObjects = new IdentityMap();
    }

    public CvTermSynchronizer(SynchronizerContext context, String objClass){
        this(context);
        //If no objclass provided all new cv created will be annotation topic by default
        this.objClass = null;
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

    @Override
    public Collection<IntactCvTerm> findAll(CvTerm term) {
        if (term == null){
            return Collections.EMPTY_LIST;
        }
        else if (this.persistedObjects.containsKey(term)){
            return Collections.singleton(this.persistedObjects.get(term));
        }
        else if (term.getMIIdentifier() != null){
            return fetchAllByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MI, false);
        }
        else if (term.getMODIdentifier() != null){
            return fetchAllByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MOD, false);
        }
        else if (term.getPARIdentifier() != null){
            return fetchAllByIdentifier(term.getMIIdentifier(), CvTerm.PSI_PAR, false);
        }
        else if (!term.getIdentifiers().isEmpty()){
            Collection<IntactCvTerm> fetchedTerms = new ArrayList<IntactCvTerm>();
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
    public Collection<String> findAllMatchingAcs(CvTerm term) {
        if (term == null){
            return Collections.EMPTY_LIST;
        }

        IntactCvTerm cached = this.persistedObjects.get(term);
        if (cached != null && cached.getAc() != null){
            return Collections.singleton(cached.getAc());
        }
        else if (term.getMIIdentifier() != null){
            return fetchAllAcsByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MI, false);
        }
        else if (term.getMODIdentifier() != null){
            return fetchAllAcsByIdentifier(term.getMIIdentifier(), CvTerm.PSI_MOD, false);
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

    public void synchronizeProperties(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        // first set objclass
        initialiseObjClass(intactCv);
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactCv);
        // then check aliases
        prepareAliases(intactCv, true);
        // then check annotations
        prepareAnnotations(intactCv, true);
        // set identifier for backward compatibility
        initialiseIdentifier(intactCv);
        // then check xrefs
        prepareXrefs(intactCv, true);
        // do synchronize parent but not children
        prepareParents(intactCv, true);
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

    public Collection<IntactCvTerm> fetchAllByName(String searchName, String miOntologyName)  {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", searchName.trim().toLowerCase());
        if (objClass != null){
            query.setParameter("objclass", objClass);
        }
        return query.getResultList();
    }

    public Collection<String> fetchAllAcsByName(String searchName, String miOntologyName)  {
        if(searchName == null)
            throw new IllegalArgumentException("Can not search for a name without a value.");
        Query query = getEntityManager().createQuery("select distinct cv.ac from IntactCvTerm cv " +
                "where cv.shortName = :name" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", searchName.trim().toLowerCase());
        if (objClass != null){
            query.setParameter("objclass", objClass);
        }
        return query.getResultList();
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
        this.convertedObjects.clear();
    }

    public String getObjClass() {
        return objClass;
    }

    public void setObjClass(String objClass) {
        this.objClass = objClass != null ? objClass : IntactUtils.TOPIC_OBJCLASS;
    }

    public void prepareAndSynchronizeShortLabel(IntactCvTerm intactCv) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()){
            log.warn("Cv term shortLabel too long: "+intactCv.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        String name;
        List<String> existingCvs;
        do{
            name = intactCv.getShortName().trim().toLowerCase();
            existingCvs = Collections.EMPTY_LIST;

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
            if (!existingCvs.isEmpty()){
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingCvs, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactCv.setShortName(nameInSync);
                }
                else{
                    break;
                }
            }
            else{
                intactCv.setShortName(name);
            }
        }
        while(!existingCvs.isEmpty());
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

    protected Collection<IntactCvTerm> fetchAllByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where cv.ac = :id" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("id", termIdentifier);
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
            Collection<IntactCvTerm> cvs = query.getResultList();
            if (!cvs.isEmpty()){
                return cvs;
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

        return query.getResultList();
    }

    protected Collection<String> fetchAllAcsByIdentifier(String termIdentifier, String miOntologyName, boolean checkAc) {
        Query query;
        if (checkAc){
            query = getEntityManager().createQuery("select distinct cv.ac from IntactCvTerm cv " +
                    "where cv.ac = :id" + (this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("id", termIdentifier);
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
            Collection<String> cvs = query.getResultList();
            if (!cvs.isEmpty()){
                return cvs;
            }
        }

        query = getEntityManager().createQuery("select distinct cv.ac from IntactCvTerm cv " +
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

        return query.getResultList();
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
        // put the synchronized object in the cache
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

    @Override
    protected boolean containsObjectInstance(CvTerm object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(CvTerm object) {
         this.convertedObjects.remove(object);
    }

    @Override
    protected IntactCvTerm fetchMatchingObjectFromIdentityCache(CvTerm object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactCvTerm intactCv) throws SynchronizerException, PersisterException, FinderException {
        // then check aliases
        prepareAliases(intactCv, false);
        // then check annotations
        prepareAnnotations(intactCv, false);
        // then check xrefs
        prepareXrefs(intactCv, false);
        // do synchronize parent but not children
        prepareParents(intactCv, false);
    }

    @Override
    protected void storeObjectInIdentityCache(CvTerm originalObject, IntactCvTerm persistableObject) {
        this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(CvTerm originalObject) {
        return !this.cvComparator.canCompare(originalObject);
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
                final IntactContext context = ApplicationContextProvider.getBean("intactJamiContext");
                String prefix = "IA";
                Source institution = null;
                if (context != null){
                    prefix = context.getIntactConfiguration().getLocalCvPrefix();
                    institution = context.getIntactConfiguration().getDefaultInstitution();
                }
                if (institution != null){
                    SequenceManager seqManager = ApplicationContextProvider.getBean("jamiSequenceManager", SequenceManager.class);
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

    /*protected void prepareChildren(IntactCvTerm intactCv) throws PersisterException, FinderException, SynchronizerException {

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
    }*/

    protected void prepareParents(IntactCvTerm intactCv, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactCv.areParentsInitialized()){
            List<OntologyTerm> termsToPersist = new ArrayList<OntologyTerm>(intactCv.getParents());
            intactCv.getParents().clear();
            for (OntologyTerm term : termsToPersist){
                IntactCvTerm cvParent = enableSynchronization ?
                        synchronize(term, true) :
                        convertToPersistentObject(term);
                // we have a different instance because needed to be synchronized
                intactCv.addParent(cvParent);
            }
        }
    }

    protected void prepareXrefs(IntactCvTerm intactCv, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactCv.getDbXrefs());
            intactCv.getDbXrefs().clear();
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                CvTermXref cvXref = enableSynchronization ?
                        getContext().getCvXrefSynchronizer().synchronize(xref, false) :
                        getContext().getCvXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactCv.getDbXrefs().add(cvXref);
            }
        }
    }

    protected void prepareAnnotations(IntactCvTerm intactCv, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactCv.getDbAnnotations());
            intactCv.getDbAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                CvTermAnnotation cvAnnotation = enableSynchronization ?
                        getContext().getCvAnnotationSynchronizer().synchronize(annotation, false):
                        getContext().getCvAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactCv.getDbAnnotations().add(cvAnnotation);
            }
        }
    }

    protected void prepareAliases(IntactCvTerm intactCv, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areSynonymsInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactCv.getSynonyms());
            intactCv.getSynonyms().clear();
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                CvTermAlias cvAlias = enableSynchronization ?
                        getContext().getCvAliasSynchronizer().synchronize(alias, false):
                        getContext().getCvAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                intactCv.getSynonyms().add(cvAlias);
            }
        }
    }

    protected void initialiseObjClass(IntactCvTerm intactCv) {
        if (this.objClass != null){
            intactCv.setObjClass(this.objClass);
        }
        // CvTopic objclass by default if no other choices
        else if (intactCv.getObjClass() == null){
            intactCv.setObjClass(IntactUtils.TOPIC_OBJCLASS);
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
        Collection<Annotation> cvAnnotations = new ArrayList<Annotation>(existingInstance.getDbAnnotations());
        existingInstance.getAnnotations().clear();
        Collection<Xref> cvRefs = new ArrayList<Xref>(existingInstance.getDbXrefs());
        existingInstance.getDbXrefs().clear();
        Collection<OntologyTerm> children = new ArrayList<OntologyTerm>(existingInstance.getChildren());
        existingInstance.getChildren().clear();
        Collection<OntologyTerm> parents = new ArrayList<OntologyTerm>(existingInstance.getParents());
        existingInstance.getParents().clear();

        super.persistObject(existingInstance);

        // after persistence, re-attach dependent objects to avoid internal loops when cvs are called by each other
        existingInstance.getSynonyms().addAll(cvAliases);
        existingInstance.getDbXrefs().addAll(cvRefs);
        existingInstance.getDbAnnotations().addAll(cvAnnotations);
        existingInstance.getChildren().addAll(children);
        existingInstance.getParents().addAll(parents);
    }

    @Override
    protected void deleteRelatedProperties(IntactCvTerm intactObject){
        for (OntologyTerm parent : intactObject.getParents()){
            parent.getChildren().remove(intactObject);
            parent.getChildren().addAll(intactObject.getChildren());
        }
        intactObject.getParents().clear();
        for (OntologyTerm children : intactObject.getChildren()){
            children.getParents().remove(intactObject);
        }
        intactObject.getChildren().clear();
    }
}
