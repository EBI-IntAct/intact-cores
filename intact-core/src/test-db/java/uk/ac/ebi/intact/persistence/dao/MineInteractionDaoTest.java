/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.MineInteraction;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MineInteractionDaoTest extends TestCase
{
     private MineInteractionDao mineInteractionDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mineInteractionDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getMineInteractionDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        mineInteractionDao = null;
    }

    public void testGet() throws Exception
    {
        MineInteraction mineInt = mineInteractionDao.get("EBI-27228", "EBI-12377");

        if (mineInt != null)
        {
            MineInteraction mineIntSame = mineInteractionDao.get("EBI-12377", "EBI-27228");
            assertEquals(mineInt, mineIntSame);
        }
    }

}
