/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util;

import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvExperimentalRole;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public class ComponentUtilsTest extends IntactBasicTestCase {


    @Test
    public void isPrey() {

        CvExperimentalRole bait = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole prey = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY );
        CvExperimentalRole neutral = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        Collection<CvExperimentalRole> roles = new ArrayList<CvExperimentalRole>();
        roles.add( bait );
        roles.add( prey );
        roles.add( neutral );

        Assert.assertTrue( ComponentUtils.isPrey( roles ) );


        CvExperimentalRole bait2 = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole neutral2 = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );


        Collection<CvExperimentalRole> roles2 = new ArrayList<CvExperimentalRole>();
        roles2.add( bait2 );
        roles2.add( neutral2 );


        Assert.assertFalse( ComponentUtils.isPrey( roles2 ) );
    }


    @Test
    public void isBait() {

        CvExperimentalRole bait = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole prey = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY );
        CvExperimentalRole neutral = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        Collection<CvExperimentalRole> roles = new ArrayList<CvExperimentalRole>();
        roles.add( bait );
        roles.add( prey );
        roles.add( neutral );

        Assert.assertTrue( ComponentUtils.isBait( roles ) );


        CvExperimentalRole neutral2 = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );
        Collection<CvExperimentalRole> roles2 = new ArrayList<CvExperimentalRole>();
        roles2.add( neutral2 );


        Assert.assertFalse( ComponentUtils.isBait( roles2 ) );
    }


}
