package uk.ac.ebi.intact.core.persister;

import org.junit.*;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;

/**
 * CorePersister Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.8.0
 * @version $Id$
 */
public class CorePersisterTest {

    @Test (expected = IllegalArgumentException.class)
    public void setEntityStateCopier_null() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setEntityStateCopier( null );
    }    

    @Test
    public void setEntityStateCopier() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setEntityStateCopier( new DefaultEntityStateCopier() );
    }

    @Test (expected = IllegalArgumentException.class)
    public void setFinder_null() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setFinder( null );
    }    

    @Test
    public void setFinder() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setFinder( new DefaultFinder() );
    }

    @Test (expected = IllegalArgumentException.class)
    public void setKeyBuilder_null() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setKeyBuilder( null );
    }

    @Test
    public void setKeyBuilder() throws Exception {
        CorePersister persister = new CorePersister();
        persister.setKeyBuilder( new KeyBuilder() );
    }
}