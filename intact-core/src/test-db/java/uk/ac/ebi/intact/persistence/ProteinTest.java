/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jul-2006</pre>
 */
public class ProteinTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ProteinTest.class);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testUnexpectedUpdateOnXref()
    {
        Xref xref = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao(InteractorXref.class).getByAc("EBI-595609");
        assertNotNull(xref);
    }

    public void testInteractionsForProtein()
    {
        Protein protein = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao()
                .getByAc("EBI-100018");

        Collection<Component> components = protein.getActiveInstances();
        List<Interaction> interactions = new ArrayList<Interaction>( components.size() );

        for (Component component : components)
        {
            Interaction interaction = component.getInteraction();

            if (!interactions.contains(interaction))
            {
                interactions.add(interaction);
            }
        }

        assertEquals(20, interactions.size());
    }

    public void testIsAssignableFrom(){
        assert(Protein.class.isAssignableFrom(ProteinImpl.class));
    }

}
