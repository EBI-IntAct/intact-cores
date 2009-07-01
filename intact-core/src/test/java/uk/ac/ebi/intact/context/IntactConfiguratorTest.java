package uk.ac.ebi.intact.context;

import static org.junit.Assert.*;
import org.junit.*;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.InMemoryDataConfig;

import java.util.Properties;

/**
 * IntactConfigurator Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.9.1
 * @version $Id$
 */
public class IntactConfiguratorTest {

    @Test
    public void initConfig() throws Exception {

        Assert.assertFalse( IntactContext.currentInstanceExists() );

        Properties properties = new Properties( );
        properties.setProperty( IntactEnvironment.AUTO_UPDATE_EXPERIMENT_SHORTLABEL.getFqn(), Boolean.FALSE.toString() );
        properties.setProperty( IntactEnvironment.AUTO_UPDATE_INTERACTION_SHORTLABEL.getFqn(), Boolean.FALSE.toString() );

        final IntactSession session = new StandaloneSession( properties );
        Assert.assertFalse(IntactConfigurator.isInitialized( session ));

        DataConfig config = new InMemoryDataConfig( session );
        IntactContext.initContext( config, session );

        Assert.assertTrue(IntactConfigurator.isInitialized( session ));

        Assert.assertFalse( IntactContext.getCurrentInstance().getConfig().isAutoUpdateExperimentShortlabel() );
        Assert.assertFalse( IntactContext.getCurrentInstance().getConfig().isAutoUpdateInteractionShortlabel() );

        IntactContext.closeCurrentInstance();
        Assert.assertFalse( IntactContext.currentInstanceExists() );
    }

    @Test
    public void initDefaultConfig() throws Exception {

        Assert.assertFalse( IntactContext.currentInstanceExists() );

        IntactSession session = new StandaloneSession();
        Assert.assertFalse(IntactConfigurator.isInitialized( session ));

        DataConfig config = new InMemoryDataConfig( session );
        IntactContext.initContext( config, session );

        Assert.assertTrue(IntactConfigurator.isInitialized( session ));

        Assert.assertTrue( IntactContext.getCurrentInstance().getConfig().isAutoUpdateExperimentShortlabel() );
        Assert.assertTrue( IntactContext.getCurrentInstance().getConfig().isAutoUpdateInteractionShortlabel() );

        IntactContext.closeCurrentInstance();
        Assert.assertFalse( IntactContext.currentInstanceExists() );
    }
}
