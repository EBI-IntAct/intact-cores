/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.CvPrimer;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectBuilder;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SmallCvPrimer implements CvPrimer {

    private static final Log log = LogFactory.getLog(SmallCvPrimer.class);

    private CvObjectCache cvCache;
    private DaoFactory daoFactory;

    public SmallCvPrimer(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;

        this.cvCache = new CvObjectCache();
    }

    public void createCVs() {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // Note, these object are being created is they don't exist yet. They are part
        // of psi-mi so they will be updated later.

        // CvXrefQualifier( identity )
        CvXrefQualifier identity = intactContext.getDataContext().getDaoFactory()
                .getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);

        CvObjectBuilder cvBuilder = new CvObjectBuilder();

        if (identity == null) {
            identity = cvBuilder.createIdentityCvXrefQualifier(IntactContext.getCurrentInstance().getInstitution());
            identity.setFullName("identical object");
            intactContext.getDataContext().getDaoFactory().getCvObjectDao(CvXrefQualifier.class).persist(identity);
        }

        // CvDatabase( psi-mi )
        CvDatabase psi = intactContext.getDataContext().getDaoFactory()
                .getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.PSI_MI_MI_REF);

        if (psi == null) {
            psi = cvBuilder.createPsiMiCvDatabase(IntactContext.getCurrentInstance().getInstitution());
            psi.setFullName("psi-mi");
            intactContext.getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).persist(psi);
        }

        intactContext.getDataContext().flushSession();

        // CvDatabase( intact )
        getCvObject(CvDatabase.class, CvDatabase.INTACT, CvDatabase.INTACT_MI_REF);

        // CvDatabase( pubmed )
        getCvObject(CvDatabase.class, CvDatabase.PUBMED, CvDatabase.PUBMED_MI_REF);

        // CvDatabase( go )
        getCvObject(CvDatabase.class, CvDatabase.GO, CvDatabase.GO_MI_REF, "gene ontology definition reference");

        // CvDatabase( so )
        getCvObject(CvDatabase.class, CvDatabase.SO, CvDatabase.SO_MI_REF, "sequence ontology");

        // CvDatabase( resid )
        getCvObject(CvDatabase.class, CvDatabase.RESID, CvDatabase.RESID_MI_REF);

        // CvXrefQualifier( go-definition-ref )
        getCvObject(CvXrefQualifier.class, CvXrefQualifier.GO_DEFINITION_REF, CvXrefQualifier.GO_DEFINITION_REF_MI_REF);

        // CvXrefQualifier( see-also )
        getCvObject(CvXrefQualifier.class, CvXrefQualifier.SEE_ALSO, CvXrefQualifier.SEE_ALSO_MI_REF);

        // CvAliasType( go synonym )
        getCvObject(CvAliasType.class, CvAliasType.GO_SYNONYM, CvAliasType.GO_SYNONYM_MI_REF);

        // CvTopic( comment )
        getCvObject(CvTopic.class, CvTopic.COMMENT, CvTopic.COMMENT_MI_REF);

        // CvTopic( obsolete )
        getCvObject(CvTopic.class, CvTopic.OBSOLETE, CvTopic.OBSOLETE_MI_REF);

        //intactContext.getDataContext().flushSession();
    }

    /**
     * Get the requested CvObject from the Database, create it if it doesn't exist. It is searched by shortlabel. If not
     * found, a CvObject is create using the information given (shortlabel)
     *
     * @param clazz      the CvObject concrete type
     * @param shortlabel shortlabel of the term
     *
     * @return a CvObject persistent in the backend.
     *
     * @throws IntactException          is an error occur while writting on the database.
     * @throws IllegalArgumentException if the class given is not a concrete type of CvObject (eg. CvDatabase)
     */
    public CvObject getCvObject(Class clazz,
                                       String shortlabel
    ) throws IntactException {
        return getCvObject(clazz, shortlabel, null);
    }

    public CvObject getCvObject(Class clazz,
                                       String shortlabel,
                                       String mi
    ) throws IntactException {
        return getCvObject(clazz, shortlabel, mi, null);
    }

    /**
     * Get the requested CvObject from the Database, create it if it doesn't exist. It is first searched by Xref(psi-mi)
     * if an ID is given, then by shortlabel. If not found, a CvObject is create using the information given
     * (shortlabel, then potentially a PSI ID)
     *
     * @param clazz      the CvObject concrete type
     * @param shortlabel shortlabel of the term
     * @param mi         MI id of the term
     *
     * @return a CvObject persistent in the backend.
     *
     * @throws IntactException          is an error occur while writting on the database.
     * @throws IllegalArgumentException if the class given is not a concrete type of CvObject (eg. CvDatabase)
     */
    private CvObject getCvObject(Class clazz,
                                       String shortlabel,
                                       String mi,
                                       String defaultFullName) {

        // Check that the given class is a CvObject or one if its sub-type.
        if (!CvObject.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The given class (" + clazz.getSimpleName() + ") must be a sub type of CvObject");
        }

        // Search by MI

        CvObject cv = null;// = cvCache.get(clazz, mi, shortlabel);

        if (cv != null) {
            return cv;
        }
        
        //CvContext cvContext = IntactContext.getCurrentInstance().getCvContext();
        CvObjectDao dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(clazz);

        // if an MI is available, search using it
        if (mi != null) {
            log.debug("Looking up term by mi: " + mi);
            cv = dao.getByPsiMiRef(mi);
        }

        // if not found by MI, then search by shortlabel
        if (cv == null) {
            // Search by Name
            log.debug("Not found. Now, looking up term by short label: " + shortlabel);
            cv = dao.getByShortLabel(clazz, shortlabel);
        }

        // if still not found, then create it.
        if (cv == null) {

            log.debug("Not found. Then, create it: "+shortlabel+" ("+mi+")");

            // create it
            try {

                // create a new object using refection
                Constructor constructor = clazz.getConstructor(new Class[]{Institution.class, String.class});
                cv = (CvObject) constructor.newInstance(IntactContext.getCurrentInstance().getInstitution(), shortlabel);

                // by default add the shortLabel as fullName
                cv.setFullName(defaultFullName);

                cvCache.put(clazz, mi, shortlabel, cv);
                //IntactContext.getCurrentInstance().getDataContext().flushSession();

                // create MI Xref if necessary
                if (mi != null && mi.startsWith("MI:")) {
                    CvDatabase psi = null;
                    if (mi.equals(CvDatabase.PSI_MI_MI_REF)) {
                        psi = (CvDatabase) cv;
                    } else {
                        psi = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                            .getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.PSI_MI_MI_REF);
                    }

                    CvXrefQualifier identity = null;
                    if (mi.equals(CvXrefQualifier.IDENTITY_MI_REF)) {
                        identity = (CvXrefQualifier) cv;
                    } else {
                        identity = (CvXrefQualifier) getCvObject(CvXrefQualifier.class, CvXrefQualifier.IDENTITY, CvXrefQualifier.IDENTITY_MI_REF, "identical object");
                    }

                    CvObjectXref xref = new CvObjectXref(IntactContext.getCurrentInstance().getInstitution(), psi, mi, null, null, identity);

                    cv.addXref(xref);
                    //getDaoFactory().getXrefDao().persist(xref);
                    log.debug("Added required PSI Xref to " + shortlabel + ": " + mi);
                }

// persist it
                getDaoFactory()
                        .getCvObjectDao(clazz).persist(cv);
                log.debug("Created missing CV Term: " + clazz.getSimpleName() + "( " + cv.getShortLabel() + " - " + cv.getFullName() + " ).");
                

            } catch (Exception e) {
                // that's should not happen, but just in case...
                throw new IntactException("Error while creating " + clazz.getSimpleName() + "(" + shortlabel +
                                          ", " + mi + ").", e);
            }
        }

        return cv;
    }

    private DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private class CvObjectCache {

        private Map<String,CvObject> cvMap;

        public CvObjectCache() {
            cvMap = new HashMap<String,CvObject>();
        }

        public CvObject get(Class cvClass, String label, String mi) {
            return cvMap.get(toKey(cvClass, mi, label));
        }

        public void put (Class cvClass, String mi, String label, CvObject cv) {
            cvMap.put(toKey(cvClass, mi, label), cv);
        }

        private String toKey(Class cvClass, String mi, String label) {
            return cvClass.getSimpleName()+"_"+mi+"_"+label;
        }
    }
    
}