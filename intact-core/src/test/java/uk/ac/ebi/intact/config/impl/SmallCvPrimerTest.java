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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.CvXrefQualifier;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SmallCvPrimerTest extends IntactBasicTestCase {

    @Before
    public void prepare() throws Exception {
        new IntactUnit().createSchema();
    }

    @Test
    public void createCvs_default() throws Exception {
        SmallCvPrimer cvPrimer = new SmallCvPrimer(getDaoFactory());

        beginTransaction();
        cvPrimer.createCVs();

        Assert.assertEquals(12, getDaoFactory().getCvObjectDao().countAll());
        commitTransaction();
    }

    /**
     * @see <a href="http://www.ebi.ac.uk/interpro/internal-tools/jira-intact/browse/IAC-95">IAC-95</a>
     */
    @Test
    public void createCvs_iac95() throws Exception {
        SmallCvPrimer cvPrimer = new SmallCvPrimer(getDaoFactory());

        beginTransaction();
        cvPrimer.createCVs();
        commitTransaction();

        beginTransaction();
        CvXrefQualifier goDefinitonRef = getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.GO_DEFINITION_REF_MI_REF);
        Assert.assertNotNull(goDefinitonRef);
        Assert.assertEquals(goDefinitonRef.getShortLabel(), CvXrefQualifier.GO_DEFINITION_REF);
        commitTransaction();
    }
}