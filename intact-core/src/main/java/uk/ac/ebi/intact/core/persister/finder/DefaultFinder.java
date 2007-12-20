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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.Finder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    private static final Log log = LogFactory.getLog( DefaultFinder.class );

    public String findAc( AnnotatedObject annotatedObject ) {
        String ac;

        if ( annotatedObject.getAc() != null ) {
            return annotatedObject.getAc();
        }

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
        String pubId = ExperimentUtils.getPubmedId(experiment);

        Query query;

        if (pubId != null) {
            query = getEntityManager().createQuery("select exp.ac from Experiment exp " +
                                                   "left join exp.publication as pub " +
                                                   "join exp.xrefs as xref  where (pub.shortLabel = :pubId or xref.primaryId = :pubId) and " +
                                                   "exp.bioSource.taxId = :taxId and " +
                                                   "exp.cvIdentification.miIdentifier = :participantDetMethodMi and " +
                                                   "exp.cvInteraction.miIdentifier = :interactionTypeMi");
            query.setParameter("pubId", pubId);
            query.setParameter("taxId", experiment.getBioSource().getTaxId());

            if (experiment.getCvIdentification() == null) throw new IllegalArgumentException("Cannot get the AC from an Experiment without CvIdentification: "+experiment.getShortLabel());
            if (experiment.getCvInteraction() == null) throw new IllegalArgumentException("Cannot get the AC from an Experiment without CvInteraction: "+experiment.getShortLabel());

            query.setParameter("participantDetMethodMi", experiment.getCvIdentification().getMiIdentifier());
            query.setParameter("interactionTypeMi", experiment.getCvInteraction().getMiIdentifier());

        } else {
            log.warn("Experiment without publication, getting its AC using the shortLabel: "+experiment.getShortLabel());
            
            query = getEntityManager().createQuery("select exp.ac from Experiment exp where exp.shortLabel = :shortLabel");
            query.setParameter("shortLabel", experiment.getShortLabel());
        }

        return getFirstAcForQuery(query, experiment);
    }

    /**
     * Finds an interaction based on its properties.
     *
     * @param interaction the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForInteraction( Interaction interaction ) {
        // replace all this eventually by just using the CRC

        CrcCalculator crcCalculator = new CrcCalculator();
        String interactionCrc = crcCalculator.crc64( interaction );

        Query query = getEntityManager().createQuery("select i.ac from InteractionImpl i where i.crc = :crc");
        query.setParameter("crc", interactionCrc);

        List<String> acs = query.getResultList();

        if (acs.isEmpty()) {
            return null;
        }

        if (acs.size() > 1) {
            log.error("More than one interaction found using the CRC ("+interactionCrc+"). Returning the first one");
        }

        return acs.get(0);
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

        List<InteractorXref> identities = ProteinUtils.getIdentityXrefs(interactor, true);

        if (!identities.isEmpty()) {

            // get the first xref and retrieve all the interactors with that xref. We will filter later
            Query query = getEntityManager().createQuery("select i from " + CgLibUtil.removeCglibEnhanced(interactor.getClass()).getName() + " i " +
                                                         "join i.xrefs as xref " +
                                                         "where xref.primaryId = :primaryId");
            query.setParameter("primaryId", identities.iterator().next().getPrimaryId());

            List<Interactor> interactors = query.getResultList();

            for (Interactor interactorCandidate : interactors) {
               if (ProteinUtils.containTheSameIdentities(interactor, interactorCandidate)) {
                   ac = interactorCandidate.getAc();
                   break;
               }
            }
        } else {
            log.warn("Interactor without identity xref/s - will try to find the AC using the shortLabel: " + interactor);

            // BUG - if a small molecule have the same Xref as a protein is searched - protein might be returned
            final InteractorDao<T> interactorDao = getDaoFactory().getInteractorDao((Class<T>) interactor.getClass());
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

    private boolean same( CvObject cv1, CvObject cv2 ) {
        if ( cv1 == null && cv2 == null ) {
            return true;
        }

        return CvObjectUtils.areEqual(cv1, cv2);
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

    /**
     * Finds a cvObject based on its properties.
     *
     * @param cvObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    protected String findAcForCvObject( CvObject cvObject ) {
        Class cvClass = CgLibUtil.removeCglibEnhanced(cvObject.getClass());
        
        Query query = getEntityManager().createQuery( "select cv.ac from "+cvClass.getName()+" cv where cv.miIdentifier = :mi " );
        query.setParameter( "mi", cvObject.getMiIdentifier() );

        String value = getFirstAcForQuery( query, cvObject );

        if ( value == null ) {
            // TODO we should check on CvXrefQualifier(identity)
            query = getEntityManager().createQuery( "select cv.ac from "+cvClass.getName()+" cv where lower(cv.shortLabel) = lower(:label) " );
            query.setParameter( "label", cvObject.getShortLabel() );
            
            value = getFirstAcForQuery( query, cvObject );
        }

        return value;
    }

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
