package uk.ac.ebi.intact.core.persister;

import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
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
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setEntityStateCopier( null );
    }    

    @Test
    public void setEntityStateCopier() throws Exception {
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setEntityStateCopier( new DefaultEntityStateCopier() );
    }

    @Test (expected = IllegalArgumentException.class)
    public void setFinder_null() throws Exception {
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setFinder( null );
    }    

    @Test
    public void setFinder() throws Exception {
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setFinder( new DefaultFinder() );
    }

    @Test (expected = IllegalArgumentException.class)
    public void setKeyBuilder_null() throws Exception {
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setKeyBuilder( null );
    }

    @Test
    public void setKeyBuilder() throws Exception {
        CorePersisterImpl persister = new CorePersisterImpl(new IntactContext(), new DefaultFinder());
        persister.setKeyBuilder( new KeyBuilder() );
    }
}