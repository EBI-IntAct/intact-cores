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
import uk.ac.ebi.intact.config.CvPrimer;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * Creates the essential set of controlled vocabularies required to run an IntAct database.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EssentialCvPrimer implements CvPrimer {

    private static final Log log = LogFactory.getLog(SmallCvPrimer.class);

    private DaoFactory daoFactory;

    public EssentialCvPrimer(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public void createCVs() {
        //boolean initialAutoflush = getDaoFactory().getDataConfig().isAutoFlush();
        //getDaoFactory().getDataConfig().setAutoFlush(false);

        IntactContext intactContext = IntactContext.getCurrentInstance();

        CvDatabase intact = CvObjectUtils.createCvObject(intactContext.getInstitution(), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        PersisterHelper.saveOrUpdate(intact);

        /*


        // CvXrefQualifier( identity )
        CvXrefQualifier identity = getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);

        CvObjectBuilder cvBuilder = new CvObjectBuilder();

        Institution owner = getDaoFactory().getInstitutionDao().getByAc( IntactContext.getCurrentInstance().getInstitution().getAc() );

        if (identity == null) {
            identity = cvBuilder.createIdentityCvXrefQualifier( owner );
            identity.setFullName("identical object");
            getDaoFactory().getCvObjectDao(CvXrefQualifier.class).persist(identity);
        }

        // CvDatabase( psi-mi )
        CvDatabase psi = getDaoFactory().getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.PSI_MI_MI_REF);

        if (psi == null) {
            psi = cvBuilder.createPsiMiCvDatabase(IntactContext.getCurrentInstance().getInstitution());
            psi.setFullName("psi-mi");
            intactContext.getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).persist(psi);
        }

        intactContext.getDataContext().flushSession();

        // CvDatabase( intact )

        final CvDatabase intactDb = new CvDatabase(IntactContext.getCurrentInstance().getInstitution(),
                                                   CvDatabase.INTACT);
        CvObjectXref intactXref = new CvObjectXref(intactDb.getOwner(), psi, CvDatabase.INTACT_MI_REF, identity);
        intactDb.addXref(intactXref);

        getDaoFactory().getCvObjectDao(CvDatabase.class).persist(intactDb);

        intactContext.getDataContext().flushSession();


        //intactContext.getDataContext().flushSession();
        getDaoFactory().getDataConfig().setAutoFlush(initialAutoflush);
        */
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }
}