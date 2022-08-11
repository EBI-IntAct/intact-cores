/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persistence.dao;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSourceAlias;
import uk.ac.ebi.intact.model.BioSourceXref;
import uk.ac.ebi.intact.model.ExperimentAlias;
import uk.ac.ebi.intact.model.ExperimentXref;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Rollback
public class DaoFactoryTest extends IntactBasicTestCase {

    @Test
    public void daoFactory_correctInstantiation() throws Exception {
        DaoFactory daoFactory1 = (DaoFactory) getSpringContext().getBean("daoFactory");
        DaoFactory daoFactory2 = (DaoFactory) getSpringContext().getBean("daoFactory");

        Assert.assertSame(daoFactory1, daoFactory2);
    }
    
    @Test
    public void xrefDao_correctInstantiation() throws Exception {
        XrefDao xrefDao1 = (XrefDao) getSpringContext().getBean("xrefDaoImpl");
        XrefDao xrefDao2 = (XrefDao) getSpringContext().getBean("xrefDaoImpl");
        
        Assert.assertNotSame(xrefDao1, xrefDao2);
    }

    @Test
    public void xrefDaoFromDaoFactory_correctInstantiation() throws Exception {
        DaoFactory daoFactory = (DaoFactory) getSpringContext().getBean("daoFactory");

        XrefDao xrefDao1 = daoFactory.getXrefDao(ExperimentXref.class);
        XrefDao xrefDao2 = daoFactory.getXrefDao(BioSourceXref.class);
        
        Assert.assertNotSame(xrefDao1, xrefDao2);
    }

    @Test
    public void aliasDaoFromDaoFactory_correctInstantiation() throws Exception {
        DaoFactory daoFactory = (DaoFactory) getSpringContext().getBean("daoFactory");

        AliasDao aliasDao1 = daoFactory.getAliasDao(ExperimentAlias.class);
        AliasDao aliasDao2 = daoFactory.getAliasDao(BioSourceAlias.class);
        
        Assert.assertNotSame(aliasDao1, aliasDao2);
    }

}
