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
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueResultException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of the intact finder.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultFinder implements Finder {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(DefaultFinder.class);

    public String findAc( AnnotatedObject annotatedObject ) {
        String ac;

        if (annotatedObject.getAc() != null) {
            return annotatedObject.getAc();
        }

        if (annotatedObject instanceof Institution) {
            ac = findAcForInstitution((Institution) annotatedObject);
        } else if (annotatedObject instanceof Publication) {
            ac = findAcForPublication((Publication) annotatedObject);
        } else if (annotatedObject instanceof CvObject) {
            ac = findAcForCvObject((CvObject) annotatedObject);
        } else if (annotatedObject instanceof Experiment) {
            ac = findAcForExperiment((Experiment) annotatedObject);
        } else if (annotatedObject instanceof Interaction) {
            ac = findAcForInteraction((Interaction) annotatedObject);
        } else if (annotatedObject instanceof Interactor) {
            ac = findAcForInteractor((Interactor) annotatedObject);
        } else if (annotatedObject instanceof BioSource) {
            ac = findAcForBioSource((BioSource) annotatedObject);
        } else if (annotatedObject instanceof Component) {
            ac = findAcForComponent((Component) annotatedObject);
        } else if (annotatedObject instanceof Feature) {
            ac = findAcForFeature((Feature) annotatedObject);
        } else {
            throw new IllegalArgumentException("Cannot find Ac for type: " + annotatedObject.getClass().getName());
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
        Xref institutionXref = XrefUtils.getPsiMiIdentityXref(institution);

        if (institutionXref != null) {
            Query query = getEntityManager().createQuery("select distinct institution.ac from Institution institution " +
                                                         "left join institution.xrefs as xref " +
                                                         "where xref.primaryId = :primaryId");
            query.setParameter("primaryId", institutionXref.getPrimaryId());
            ac = getFirstAcForQuery(query, institution);
        }

        if (ac == null) {
            Institution fetchedInstitution = getDaoFactory().getInstitutionDao().getByShortLabel(institution.getShortLabel());

            if (fetchedInstitution != null) {
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
    protected String findAcForPublication( Publication publication ){
        Query query = getEntityManager().createQuery("select pub.ac from Publication pub where pub.shortLabel = :shortLabel");
        query.setParameter("shortLabel", publication.getShortLabel());

        return getFirstAcForQuery(query, publication);
    }

    /**
     * Finds an experiment based on its properties.
     *
     * @param experiment the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForExperiment( Experiment experiment ){
        Query query = getEntityManager().createQuery("select exp.ac from Experiment exp where exp.shortLabel = :shortLabel");
        query.setParameter("shortLabel", experiment.getShortLabel());

        return getFirstAcForQuery(query, experiment);
    }

    /**
     * Finds an interaction based on its properties.
     *
     * @param interaction the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForInteraction(Interaction interaction) {
        // replace all this eventually by just using the CRC
        
        InteractionDao interactionDao = getDaoFactory().getInteractionDao();

        CrcCalculator crcCalculator = new CrcCalculator();

        CrcCalculator ignoreExperimentCrcCalc = new CrcCalculator() {

            @Override
            protected UniquenessStringBuilder createUniquenessString(Experiment experiment) {
                return new UniquenessStringBuilder();
            }
        };

        // Get the interactors where exactly the same interactors are involved
        List<String> interactorPrimaryIDs = InteractionUtils.getInteractorPrimaryIDs(interaction);
        List<Interaction> interactionsWithSameInteractors =
                interactionDao.getByInteractorsPrimaryId(true, interactorPrimaryIDs.toArray(new String[interactorPrimaryIDs.size()]));

        for (Interaction interactionWithSameInteractor : interactionsWithSameInteractors) {
            String interactionCrc = ignoreExperimentCrcCalc.crc64(interaction);
            String interactionWithSameInteractorCrc = crcCalculator.crc64(interactionWithSameInteractor);

            if (interactionCrc.equals(interactionWithSameInteractorCrc)) {
                return interactionWithSameInteractor.getAc();
            }

        }

        return null;
    }

    /**
     * Finds an interactor based on its properties.
     *
     * @param interactor the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForInteractor( Interactor interactor ){
        String ac = null;

        InteractorDao<InteractorImpl> interactorDao = getDaoFactory().getInteractorDao();

        // Try to fetch first the object using the uniprot ID
        Collection<InteractorXref> identityXrefs = new ArrayList<InteractorXref>();

        InteractorXref uniprotXref = XrefUtils.getIdentityXref(interactor, CvDatabase.UNIPROT_MI_REF);

        if (uniprotXref != null) {
            identityXrefs.add(uniprotXref);
        } else {
            // X. try to fetch first the object using the primary ID
            identityXrefs = XrefUtils.getIdentityXrefs(interactor);

            // remove Xrefs to intact, mint or dip
            for (Iterator<InteractorXref> interactorXrefIterator = identityXrefs.iterator(); interactorXrefIterator.hasNext();)
            {
                InteractorXref interactorXref = interactorXrefIterator.next();

                final String databaseMi = CvObjectUtils.getPsiMiIdentityXref(interactorXref.getCvDatabase()).getPrimaryId();
                if (CvDatabase.INTACT_MI_REF.equals(databaseMi) ||
                        CvDatabase.MINT_MI_REF.equals(databaseMi) ||
                        CvDatabase.DIP_MI_REF.equals(databaseMi)) {
                    interactorXrefIterator.remove();
                }
            }

            if (identityXrefs.size() > 1) {
                throw new UndefinedCaseException("Interactor with more than one non-uniprot xref: "+identityXrefs);
            }
        }

        if (identityXrefs.size() == 1) {
            final String primaryId = identityXrefs.iterator().next().getPrimaryId();
            Interactor existingObject = null;
            try {
                existingObject = interactorDao.getByXref(primaryId);
            } catch (NonUniqueResultException e) {
                throw new FinderException("Query for '"+primaryId+"' returned more than one xref: "+interactorDao.getByXrefLike(primaryId));
            }

            if (existingObject != null) {
                if (log.isDebugEnabled()) log.debug("Fetched existing object from the database: "+primaryId);
                ac = existingObject.getAc();
            }
        }

        if (ac == null) {
            Interactor existingObject = interactorDao.getByShortLabel(interactor.getShortLabel());
            if (existingObject != null) {
                ac = existingObject.getAc();
            }
        }
        
        return ac;
    }

    /**
     * Finds a biosource based on its properties.
     *
     * @param bioSource the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForBioSource( BioSource bioSource ){
        Query query = getEntityManager().createQuery("select bio.ac from BioSource bio where bio.taxId = :taxId");
        query.setParameter("taxId", bioSource.getTaxId());

        return getFirstAcForQuery(query, bioSource);
    }

    /**
     * Finds a component based on its properties.
     *
     * @param component the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForComponent( Component component ){
        return null;
    }

    /**
     * Finds a feature based on its properties.
     *
     * @param feature the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForFeature( Feature feature ){
        return null;
    }

      /**
     * Finds a cvObject based on its properties.
     *
     * @param cvObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForCvObject(CvObject cvObject) {
          Query query = getEntityManager().createQuery("select cv.ac from CvObject cv where cv.miIdentifier = :mi " +
                                                       "and cv.objClass = :objclass");
          query.setParameter("mi", cvObject.getMiIdentifier());
          query.setParameter("objclass", cvObject.getObjClass());

          return getFirstAcForQuery(query, cvObject);
      }

    private String getFirstAcForQuery(Query query, AnnotatedObject ao) {
        List<String> results = query.getResultList();
        String ac = null;

        if (!results.isEmpty()) {
            ac = results.get(0);
        } else if (results.size() > 1) {
            throw new IllegalStateException("Found more than one AC (" + results + ") for " + ao.getClass().getSimpleName() + ": " + ao);
        }

        return ac;
    }

    private EntityManager getEntityManager() {
        return getDaoFactory().getEntityManager();
    }

    private DaoFactory getDaoFactory() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }
}
