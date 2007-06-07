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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class PolymerDaoTest extends TestCase
{

    private static final Log log = LogFactory.getLog(PolymerDaoTest.class);

    private PolymerDao polymerDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        polymerDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getPolymerDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        polymerDao = null;
    }

    public void testGetSequenceByPolymerAc() throws Exception
    {
        String seq = polymerDao.getSequenceByPolymerAc("EBI-12345");

        String originalSeq = "MTIDDSNRLLMDVDQFDFLDDGTAQLSNNKTDEEEQLYKRDPVSGAILVPMTVNDQPIEKNGDKMPLKFKLGPLSYQNMAFITAKDKYKLYPVRIPRLDTSKEFSAYVSGLFEIYRDLGDDRVFNVPTIGVVNSNFAKEHNATVNLAMEAILNELEVFIGRVKDQDGRVNRFYELEESLTVLNCLRTMYFILDGQDVEENRSEFIESLLNWINRSDGEPDEEYIEQVFSVKDSTAGKKVFETQYFWKLLNQLVLRGLLSQAIGCIERSDLLPYLSDTCAVSFDAVSDSIELLKQYPKDSSSTFREWKNLVLKLSQAFGSSATDISGELRDYIEDFLLVIGGNQRKILQYSRTWYESFCGFLLYYIPSLELSAEYLQMSLEANVVDITNDWEQPCVDIISGKIHSILPVMESLDSCTAAFTAMICEAKGLIENIFEGEKNSDDYSNEDNEMLEDLFSYRNGMASYMLNSFAFELCSLGDKELWPVAIGLIALSATGTRSAKKMVIAELLPHYPFVTNDDIEWMLSICVEWRLPEIAKEIYTTLGNQMLSAHNIIESIANFSRAGKYELVKSYSWLLFEASCMEGQKLDDPVLNAIVSKNSPAEDDVIIPQDILDCVVTNSMRQTLAPYAVLSQFYELRDREDWGQALRLLLLLIEFPYLPKHYLVLLVAKFLYPIFLLDDKKLMDEDSVATVIEVIETKWDDADEKSSNLYETIIEADKSLPSSMATLLKNLRKKLNFKLCQAFM";

        assertEquals(seq, originalSeq);
    }

}
