/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister.finder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.core.persister.Finder;
import uk.ac.ebi.intact.core.persister.FinderException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.*;
import uk.ac.ebi.intact.model.util.filter.CvObjectFilterGroup;
import uk.ac.ebi.intact.model.util.filter.XrefCvFilter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * Default implementation of the intact finder.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultFinder implements Finder {

    private IntactConfiguration config;

    public DefaultFinder() {
        config = IntactContext.getCurrentInstance().getConfig();
    }

    public DefaultFinder(IntactContext intactContext) {
        config = intactContext.getConfig();
    }

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog( DefaultFinder.class );

    @Transactional
    public String findAc( AnnotatedObject annotatedObject ) {
        String ac;

        if ( annotatedObject.getAc() != null ) {
            return annotatedObject.getAc();
        }

        try {
            if ( annotatedObject instanceof Institution ) {
                ac = findAcForInstitution( ( Institution ) annotatedObject );
            } else if ( annotatedObject instanceof Publication ) {
                ac = findAcForPublication( ( Publication ) annotatedObject );
            } else if ( annotatedObject instanceof CvObject ) {
                ac = findAcForCvObject( ( CvObject ) annotatedObject );
            } else if ( annotatedObject instanceof Experiment ) {
                ac = findAcForExperiment( ( Experiment ) annotatedObject );
            } else if ( annotatedObject instanceof Interaction ) {
                ac = findAcForInteraction( ( Interaction ) annotatedObject );
            } else if ( annotatedObject instanceof Interactor ) {
                ac = findAcForInteractor( ( InteractorImpl ) annotatedObject );
            } else if ( annotatedObject instanceof BioSource ) {
                ac = findAcForBioSource( ( BioSource ) annotatedObject );
            } else if ( annotatedObject instanceof Component ) {
                ac = findAcForComponent( ( Component ) annotatedObject );
            } else if ( annotatedObject instanceof Feature ) {
                ac = findAcForFeature( ( Feature ) annotatedObject );
            } else {
                throw new IllegalArgumentException( "Cannot find Ac for type: " + annotatedObject.getClass().getName() );
            }
        } catch (Throwable t) {
            throw new FinderException("Unable to find AC for "+annotatedObject.getClass().getSimpleName()+": "+annotatedObject, t);
        }

        return ac;
    }

    @Override
    @Transactional
    public String findAc( Role role ) {
        String ac = null;

        if ( role.getAc() != null ) {
            ac = role.getAc();
        } else {
            Role aRole = getDaoFactory().getRoleDao().getRoleByName( role.getName() );
            if( aRole != null ) {
                ac = aRole.getAc();
            }
        }

        return ac;
    }

    @Override
    public String findAc(User user) {
        String ac = null;

        if (user.getAc() != null) {
            ac = user.getAc();
        } else {
            User aUser = getDaoFactory().getUserDao().getByLogin(user.getLogin());

            if (aUser == null && user.getEmail() != null) {
                aUser = getDaoFactory().getUserDao().getByEmail(user.getEmail());
            }

            if (aUser != null) {
                ac = aUser.getAc();
            }
        }

        return ac;
    }

    /**
     * Finds an institution based on its properties.
     *
     * @param institution the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForInstitution( Institution institution ) {
        String ac = null;

        // try to fetch it first using the xref. If not, use the shortlabel
        Xref institutionXref = XrefUtils.getPsiMiIdentityXref( institution );

        if ( institutionXref != null ) {
            Query query = getEntityManager().createQuery( "select distinct institution.ac from Institution institution " +
                    "left join institution.xrefs as xref " +
                    "where xref.primaryId = :primaryId" );
            query.setParameter( "primaryId", institutionXref.getPrimaryId() );
            ac = getFirstAcForQuery( query, institution );
        }

        if ( ac == null ) {
            Institution fetchedInstitution = getDaoFactory().getInstitutionDao().getByShortLabel( institution.getShortLabel() );

            if ( fetchedInstitution != null ) {
                ac = fetchedInstitution.getAc();
            }
        }

        return ac;
    }

    /**
     * Finds a publication based on its properties.
     *
     * @param publication the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForPublication( Publication publication ) {
        // TODO add primary-reference first, then shortlabel
        Query query = getEntityManager().createQuery( "select pub.ac from Publication pub where pub.shortLabel = :shortLabel" );
        query.setParameter( "shortLabel", publication.getShortLabel() );

        return getFirstAcForQuery( query, publication );
    }

    /**
     * Finds an experiment based on its properties.
     *
     * @param experiment the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForExperiment(Experiment experiment) {
        final ExperimentXref xref = ExperimentUtils.getPrimaryReferenceXref( experiment );
        String pubId = (xref != null ? xref.getPrimaryId() : null );

        if (experiment.getCvIdentification() == null) throw new IllegalArgumentException("Cannot get the AC from an Experiment without Participant identification method: "+experiment.getShortLabel());
        if (experiment.getCvInteraction() == null) throw new IllegalArgumentException("Cannot get the AC from an Experiment without Interaction detection method: "+experiment.getShortLabel());

        Query query;
        if (pubId != null) {

            if( experiment.getBioSource() != null ) {
                query = getEntityManager().createQuery("select exp.ac " +
                        "from Experiment exp " +
                        "     left join exp.publication as pub " +
                        "     join exp.xrefs as xref  " +
                        "where (pub.shortLabel = :pubId or xref.primaryId = :pubId) and " +
                        "      exp.bioSource.taxId = :taxId and " +
                        "      exp.cvIdentification.identifier = :participantDetMethodMi and " +
                        "      exp.cvInteraction.identifier = :interactionDetectionMethodMi");

                query.setParameter("taxId", experiment.getBioSource().getTaxId());

            } else {

                query = getEntityManager().createQuery("select exp.ac " +
                        "from Experiment exp " +
                        "     left join exp.publication as pub " +
                        "     join exp.xrefs as xref  " +
                        "where (pub.shortLabel = :pubId or xref.primaryId = :pubId) and " +
                        "      exp.cvIdentification.identifier = :participantDetMethodMi and " +
                        "      exp.cvInteraction.identifier = :interactionDetectionMethodMi");
            }

            query.setParameter("pubId", pubId);

        } else {
            log.warn("Experiment without publication, getting its AC using the shortLabel: "+experiment.getShortLabel());

            query = getEntityManager().createQuery("select exp.ac " +
                    "from Experiment exp " +
                    "where exp.shortLabel = :shortLabel and" +
                    "      exp.cvIdentification.identifier = :participantDetMethodMi and " +
                    "      exp.cvInteraction.identifier = :interactionDetectionMethodMi");

            query.setParameter("shortLabel", experiment.getShortLabel().toLowerCase());
        }

        query.setParameter("participantDetMethodMi", experiment.getCvIdentification().getIdentifier());
        query.setParameter("interactionDetectionMethodMi", experiment.getCvInteraction().getIdentifier());

        Set<String> experimentAcs = new HashSet<String>(query.getResultList());

        String experimentAc = null;

        BioSource biosource = experiment.getBioSource();
        if (biosource != null){
            CvCellType cellType = biosource.getCvCellType();
            CvTissue cvTissue = biosource.getCvTissue();

            List<String> acs = new ArrayList<String>(experimentAcs);
            for (String candidateExperimentAc : acs) {
                Experiment match = getDaoFactory().getExperimentDao().getByAc(candidateExperimentAc);

                if ( !same( cvTissue, match.getBioSource().getCvTissue() ) ||
                        !same( cellType, match.getBioSource().getCvCellType() ) ) {
                    experimentAcs.remove(candidateExperimentAc);
                }
            }
        }

        if (experimentAcs.size() == 1 && experiment.getAnnotations().isEmpty()) {

            experimentAc = experimentAcs.iterator().next();

            Experiment match = getDaoFactory().getExperimentDao().getByAc(experimentAc);

            if (!match.getAnnotations().isEmpty()){
                experimentAc = null;
            }
        }
        else if (experimentAcs.size() != 0 && experiment.getAnnotations().isEmpty()){
            Set<String> experimentsWithoutAnnotations = new HashSet<String>();

            for (String candidateExperimentAc : experimentAcs) {
                Experiment match = getDaoFactory().getExperimentDao().getByAc(candidateExperimentAc);

                if (match.getAnnotations().isEmpty()){
                    experimentsWithoutAnnotations.add(candidateExperimentAc);
                }
            }

            if (experimentsWithoutAnnotations.size() == 1){
                experimentAc = experimentsWithoutAnnotations.iterator().next();
            }
            else if (experimentsWithoutAnnotations.size() > 1){
                log.warn( "There were " + experimentsWithoutAnnotations.size() +" experiments matching " + experiment.getShortLabel() +
                        "["+ experiment.getAc() +"]: "+ experimentAcs +
                        " However, none of them had no annotations attached to it. Consequently, none was selected." );
            }
        }
        else if ( experimentAcs.size() != 0 && !experiment.getAnnotations().isEmpty()){
            // check the annotations
            Collection<String> expAnnotDescs = convertAnnotToString(experiment.getAnnotations());

            for (String candidateExperimentAc : experimentAcs) {
                Query annotQuery = getEntityManager().createQuery("select concat(annot.cvTopic.shortLabel, annot.annotationText) from Experiment exp " +
                        "left join exp.annotations as annot " +
                        "where exp.ac = :experimentAc");
                annotQuery.setParameter("experimentAc", candidateExperimentAc);
                List<String> annotDescs = annotQuery.getResultList();

                if (CollectionUtils.isEqualCollection(expAnnotDescs, annotDescs)) {
                    experimentAc = candidateExperimentAc;
                    break;
                }
            }

            if( experimentAc == null ) {
                log.warn( "There were " + experimentAcs.size() +" experiments matching " + experiment.getShortLabel() +
                        "["+ experiment.getAc() +"]: "+ experimentAcs +
                        " However, none of them had the same annotation set. Consequently, none was selected." );
            }
        }

        return experimentAc;
    }

    private Collection<String> convertAnnotToString(Collection<Annotation> annot){
        Collection<String> annotations = new ArrayList<String>(annot.size());

        for (Annotation a : annot){
             if (a.getCvTopic() == null){
                 annotations.add(a.getAnnotationText() != null ? a.getAnnotationText() : "");
             }
            else {
                 annotations.add(a.getCvTopic().getIdentifier() + (a.getAnnotationText() != null ? a.getAnnotationText() : ""));
             }
        }

        return annotations;
    }

    /**
     * Finds an interaction based on its properties.
     *
     * @param interaction the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForInteraction( Interaction interaction ) {
        CrcCalculator crcCalculator = new CrcCalculator();
        String interactionCrc = crcCalculator.crc64( interaction );

        Query query = getEntityManager().createQuery("select i.ac from InteractionImpl i where i.crc = :crc");
        query.setParameter("crc", interactionCrc);

        Set<String> acs = new HashSet<String>(query.getResultList());

        if (acs.isEmpty()) {
            return null;
        }

        if (acs.size() > 1) {
            log.error("More than one interaction found using the CRC ("+interactionCrc+"). Returning the first one");
        }

        return acs.iterator().next();
    }

    /**
     * Finds an interactor based on its properties.
     * <p/>
     * <b>Search criteria</b>: uniprot identity, or if not found, the first identity found (that is not INTACT, MINT or DIP) and finally shortlabel.
     *
     * @param interactor the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected <T extends InteractorImpl> String findAcForInteractor( T interactor ) {
        String ac = null;
        Interactor possibleMatch = null;

        // first check if the identities refer to the database itself
        for (InteractorXref idXref : ProteinUtils.getIdentityXrefs(interactor, false)) {
            if (xrefPointsToOwnAc(idXref)) {
                // check if exists in the db
                Query acQuery = getEntityManager().createQuery("select i.ac from " + CgLibUtil.removeCglibEnhanced(interactor.getClass()).getName() + " i " +
                        "where i.ac = :ac ");
                acQuery.setParameter("ac", idXref.getPrimaryId());

                if (!acQuery.getResultList().isEmpty()) {
                    return idXref.getPrimaryId();
                }
            }
        }

        // Strategy to find is a protein is already in the database:
        // 1. same set of parents
        // 2. Same set of identities (uniprotkb, chebi) and no no-uniprot-update annotation
        // 3. Same set of identities (uniprotkb, chebi) and no-uniprot-update annotation and same sequence
        //    note sequence would be checked on if the interactors are polymers.

        CvObjectFilterGroup databaseGroup1 = new CvObjectFilterGroup();
        databaseGroup1.addIncludedIdentifier(CvDatabase.INTACT_MI_REF);

        CvObjectFilterGroup qualifierGroup1 = new CvObjectFilterGroup();
        qualifierGroup1.addIncludedIdentifier(CvXrefQualifier.CHAIN_PARENT_MI_REF);
        qualifierGroup1.addIncludedIdentifier(CvXrefQualifier.ISOFORM_PARENT_MI_REF);

        XrefCvFilter xrefFilter1 = new XrefCvFilter(databaseGroup1, qualifierGroup1);

        List<InteractorXref> parents = AnnotatedObjectUtils.searchXrefs(interactor, xrefFilter1);

        CvObjectFilterGroup databaseGroup = new CvObjectFilterGroup();
        databaseGroup.addIncludedIdentifier(CvDatabase.UNIPROT_MI_REF);
        databaseGroup.addIncludedIdentifier(CvDatabase.CHEBI_MI_REF);
        databaseGroup.addIncludedIdentifier(CvDatabase.ENSEMBL_MI_REF);


        CvObjectFilterGroup qualifierGroup = new CvObjectFilterGroup();
        qualifierGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseGroup, qualifierGroup);

        List<InteractorXref> identities = AnnotatedObjectUtils.searchXrefs(interactor, xrefFilter);

        if (!identities.isEmpty()) {

            final boolean hasNoUniprotUpdate = hasNoUniprotUpdateAnnotation( interactor );

            // get the first xref and retrieve all the interactors with that xref. We will filter later
            Query query = getEntityManager().createQuery("select i from " + CgLibUtil.removeCglibEnhanced(interactor.getClass()).getName() + " i " +
                    "join i.xrefs as xref " +
                    "where xref.primaryId = :primaryId");
            query.setParameter("primaryId", identities.iterator().next().getPrimaryId());

            List<Interactor> interactors = query.getResultList();

            for (Interactor interactorCandidate : interactors) {
                if (AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, interactor, interactorCandidate)) {

                    if( log.isWarnEnabled() ) {
                        if( interactor.getBioSource() != null && interactorCandidate.getBioSource() != null ) {
                            final String t = interactor.getBioSource().getTaxId();
                            final String tc = interactorCandidate.getBioSource().getTaxId();
                            if( t != null && !t.equals(tc) ) {
                                log.warn( "Interactors with the same identity xref(s) but with different BioSource: " +
                                        "["+ interactor.getShortLabel() +" / "+ interactor.getAc() +" / taxid:"+ t +"] and " +
                                        "["+ interactorCandidate.getShortLabel() +" / "+ interactorCandidate.getAc() +" / taxid:"+ tc +"]" );
                            }
                        }
                    }

                    if( hasNoUniprotUpdate ) {
                        if( hasNoUniprotUpdateAnnotation( interactorCandidate )) {
                            // both have Annotation( no-uniprot-update ), check on the sequence
                            if( interactor instanceof Polymer ) {
                                final String sequence = ((Polymer) interactor).getSequence();
                                final String sequenceCandidate = ((Polymer) interactorCandidate).getSequence();
                                if( StringUtils.equals( sequence, sequenceCandidate) ) {
                                    ac = interactorCandidate.getAc();
                                    possibleMatch = interactorCandidate;
                                    break;
                                }
                            }

                        } else {
                            // mismatch, keep trying ...
                        }
                    } else {
                        ac = interactorCandidate.getAc();
                        possibleMatch = interactorCandidate;
                        break;
                    }
                }
            }
        } else {
            log.warn("Interactor without identity xref/s - will try to find the AC using the shortLabel: " + interactor);

            // BUG - if a small molecule have the same Xref as a protein is searched - protein might be returned
            final InteractorDao<T> interactorDao = getDaoFactory().getInteractorDao((Class<T>) interactor.getClass());
            Interactor existingObject = interactorDao.getByShortLabel(interactor.getShortLabel());
            if (existingObject != null) {
                ac = existingObject.getAc();
                possibleMatch = existingObject;
            }
        }

        if (possibleMatch != null && !parents.isEmpty()){

            List<InteractorXref> parentsToCompare = AnnotatedObjectUtils.searchXrefs(possibleMatch, xrefFilter1);

            if (parentsToCompare.size() != parents.size()){
                ac = null;
            }
            else {
                for (InteractorXref refParent : parents){
                    boolean hasFoundParent = false;

                    for (InteractorXref ref : parentsToCompare){
                        if (ref.getPrimaryId().equals(refParent.getPrimaryId())){
                            hasFoundParent = true;
                            break;
                        }
                    }

                    if (!hasFoundParent){
                        ac = null;
                        break;
                    }
                }
            }
        }

        return ac;
    }

    private boolean hasNoUniprotUpdateAnnotation( AnnotatedObject ao ) {

        for ( Annotation annot : ao.getAnnotations() ) {
            if( annot.getCvTopic().getShortLabel().equals( CvTopic.NON_UNIPROT ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param xref the xref to check
     * @return
     */
    private boolean xrefPointsToOwnAc(Xref xref) {
        if (xref.getPrimaryId().startsWith(config.getAcPrefix())) {
            return true;
        } else {
            for (InstitutionXref institutionXref : IntactContext.getCurrentInstance().getInstitution().getXrefs()) {
                if (institutionXref.getPrimaryId().equals(xref.getCvDatabase().getIdentifier())) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Finds a biosource based on its properties.
     *
     * @param bioSource the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForBioSource( BioSource bioSource ) {

        Query query = getEntityManager().createQuery( "select bio.ac, cellType, tissue " +
                "from BioSource bio " +
                "left join bio.cvCellType as cellType " +
                "left join bio.cvTissue as tissue " +
                "where bio.taxId = :taxId" );
        query.setParameter( "taxId", bioSource.getTaxId() );

        final List<Object[]> biosources = query.getResultList();
        for ( Object[] bs : biosources ) {

            String ac = ( String ) bs[0];
            CvCellType cellType = ( CvCellType ) bs[1];
            CvTissue tissue = ( CvTissue ) bs[2];

            if ( same( tissue, bioSource.getCvTissue() ) &&
                    same( cellType, bioSource.getCvCellType() ) ) {
                return ac;
            }
        }

        return null;
    }

    /**
     *
     * @param cv1 : the cv in the database to compare with
     * @param cvToFind : the CV to find in the database
     * @return  true if the cv in the database is matches the CV to find. If the cv to find doesn't have any cross references, we look only
     * if the short label is matching
     */
    private boolean same( CvObject cv1, CvObject cvToFind ) {
        if ( cv1 == null && cvToFind == null ) {
            return true;
        }

        if (cv1 == null || cvToFind == null) {
            return false;
        }

        if (!cv1.getClass().equals(cvToFind.getClass())) {
            return false;
        }

        // if class and identifier is the same, it is the same cv object
        if (cv1.getIdentifier() != null && cvToFind.getIdentifier() != null) {
            return cv1.getIdentifier().equals(cvToFind.getIdentifier());
        }

        // The cv to find doesn't have any cross references, we need to look only the shortLabel of the match in the database
        if (cvToFind.getXrefs().isEmpty()){
            if (cvToFind.getShortLabel() != null){
                return cvToFind.getShortLabel().equals(cv1.getShortLabel());
            }
            return false;
        }

        // if any of the identities is the same, we consider them to be the same
        final Collection<CvObjectXref> xrefs1 = XrefUtils.getIdentityXrefs(cv1);
        final Collection<CvObjectXref> xrefs2 = XrefUtils.getIdentityXrefs(cvToFind);

        Collection<String> ids1 = getIds(xrefs1);
        Collection<String> ids2 = getIds(xrefs2);

        return !(CollectionUtils.intersection(ids1, ids2).isEmpty());
    }

    private Collection<String> getIds(Collection<CvObjectXref> xrefs) {
        List<String> ids = new ArrayList<String>(xrefs.size());

        for (Xref xref : xrefs) {
            ids.add(xref.getPrimaryId());
        }

        return ids;
    }

    /**
     * Finds a component based on its properties.
     *
     * @param component the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForComponent( Component component ) {
        return null;
    }

    /**
     * Finds a feature based on its properties.
     *
     * @param feature the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForFeature( Feature feature ) {
        return null;
    }

    private boolean hasAnIdentityCrossReference(CvObject cvObject){
        if (cvObject.getXrefs() != null){
            if (!cvObject.getXrefs().isEmpty()){
                for (Xref ref : cvObject.getXrefs()){
                    CvXrefQualifier qualifier = ref.getCvXrefQualifier();
                    if (qualifier.getAc() != null){
                        if (qualifier.getAc().equals("MI:0356")){
                            return true;
                        }
                    }
                    else {
                        if (qualifier.getShortLabel().equals("identity") || qualifier.getFullName().equals("identical object")){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isAnIdentityOrSecondaryCrossReference(CvXrefQualifier qualifier){
        if (qualifier == null){
            return false;
        }
        if (qualifier.getIdentifier() != null){
            if (qualifier.getIdentifier().equals(CvXrefQualifier.IDENTITY_MI_REF) || qualifier.getIdentifier().equals(CvXrefQualifier.SECONDARY_AC_MI_REF)){
                return true;
            }
        }
        else {
            if (qualifier.getShortLabel() != null){
                if (qualifier.getShortLabel().equals(CvXrefQualifier.IDENTITY) || qualifier.getShortLabel().equals(CvXrefQualifier.SECONDARY_AC)){
                    return true;
                }
            }
            else if (qualifier.getFullName() != null){
                if (qualifier.getFullName().equals("identical object")){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSearchByLabelEnabled(CvObject cvObject){
        if (cvObject.getIdentifier() != null){
            return false;
        }

        if (hasAnIdentityCrossReference(cvObject)){
            return false;
        }
        return true;
    }

    private String findAcForCvObjectUsingShortLabel( CvObject cvObject, Class cvClass ){
        Query query = getEntityManager().createQuery( "select cv.ac from "+cvClass.getName()+" cv where lower(cv.shortLabel) = lower(:label) ");
        query.setParameter( "label", cvObject.getShortLabel() );

        return getFirstAcForQuery( query, cvObject );
    }

    /**
     * Finds a cvObject based on its properties.
     *
     * @param cvObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForCvObject( CvObject cvObject ) {
        /* Class cvClass = CgLibUtil.removeCglibEnhanced(cvObject.getClass());

       Query query = getEntityManager().createQuery( "select cv.ac from "+cvClass.getName()+" cv where cv.identifier = :id " );
       query.setParameter( "id", cvObject.identifier() );

       String value = getFirstAcForQuery( query, cvObject );

       if ( value == null ) {
           // TODO we should check on CvXrefQualifier(identity)
           query = getEntityManager().createQuery( "select cv.ac from "+cvClass.getName()+" cv where lower(cv.shortLabel) = lower(:label) " );
           query.setParameter( "label", cvObject.getShortLabel() );

           value = getFirstAcForQuery( query, cvObject );
       }

       return value;*/
        Class cvClass = CgLibUtil.removeCglibEnhanced(cvObject.getClass());
        String value = null;

        boolean isSearchByLabelEnabled = isSearchByLabelEnabled(cvObject);

        if (!isSearchByLabelEnabled){
            HashSet<String> identifiersToTest = new HashSet<String>();
            if (cvObject.getIdentifier() != null){
                identifiersToTest.add(cvObject.getIdentifier());
            }

            if (cvObject.getXrefs() != null && !cvObject.getXrefs().isEmpty()){
                Collection<CvObjectXref> ref = cvObject.getXrefs();

                for (CvObjectXref cr : ref){
                    if (cr.getCvXrefQualifier() != null){
                        CvXrefQualifier qualifier = cr.getCvXrefQualifier();
                        if (isAnIdentityOrSecondaryCrossReference(qualifier)){
                            identifiersToTest.add(cr.getPrimaryId());
                        }
                    }
                }
            }

            for (String id : identifiersToTest){
                Query query = getEntityManager().createQuery( "select r.ac from "+ cvClass.getName() +" r left join r.xrefs as xref where r.identifier = :primaryId or (xref.primaryId = :primaryId and (xref.cvXrefQualifier.shortLabel = :identity or xref.cvXrefQualifier.shortLabel = :secondary))" );
                query.setParameter( "primaryId", id );
                query.setParameter( "identity", "identity" );
                query.setParameter( "secondary", CvXrefQualifier.SECONDARY_AC);

                value = getFirstAcForQuery( query, cvObject );
                if (value != null){
                    return value;
                }
            }

            String valueForShortLabel = findAcForCvObjectUsingShortLabel( cvObject, cvClass );

            if (valueForShortLabel != null){
                throw new FinderException(" The CV object " + cvObject.getIdentifier() + ":" + cvObject.getShortLabel() + " has an identifier" +
                        " which can't match any CV object identifiers of type " + cvClass +" but the shortlabel is matching one CVObject in the database : "+
                        valueForShortLabel + ". The CV object can't be duplicated with a different identifier, you should change the identifier.");
            }
        }
        else{
            value = findAcForCvObjectUsingShortLabel( cvObject, cvClass );
        }
        // TODO what happens if we have several matching entries (short label for instance)
        return value;
    }

    @Transactional
    private String getFirstAcForQuery( Query query, AnnotatedObject ao ) {
        List<String> results = query.getResultList();
        String ac = null;

        if ( !results.isEmpty() ) {
            ac = results.get( 0 );
        } else if ( results.size() > 1 ) {
            throw new IllegalStateException( "Found more than one AC (" + results + ") for " + ao.getClass().getSimpleName() + ": " + ao );
        }

        return ac;
    }

    protected EntityManager getEntityManager() {
        EntityManager em = getDaoFactory().getEntityManager();
        //em.setFlushMode(FlushModeType.COMMIT);
        return em;
    }

    protected DaoFactory getDaoFactory() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }
}
