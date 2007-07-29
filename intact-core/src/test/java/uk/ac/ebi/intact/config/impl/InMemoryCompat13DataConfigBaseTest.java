package uk.ac.ebi.intact.config.impl;

import org.hibernate.cfg.Settings;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.config.impl.compat.InMemoryCompat13DataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.meta.DbInfo;

import java.util.Properties;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InMemoryCompat13DataConfigBaseTest extends IntactBasicTestCase
{
    @Before
    public void initCompatibility13Context() {
        Properties props = new Properties();
        props.put(IntactEnvironment.DATA_CONFIG_PARAM_NAME.getFqn(), InMemoryCompat13DataConfig.NAME);

        IntactSession session = new StandaloneSession(props);
        DataConfig dataConfig = new InMemoryCompat13DataConfig(session);

        IntactContext.initContext(dataConfig, session);

        beginTransaction();
    }

    @After
    public void after() {
        commitTransaction();
    }

    @Test
    public void versionPersistence() throws Exception {
        DbInfo dbInfo = getDaoFactory().getDbInfoDao().get(DbInfo.SCHEMA_VERSION);

        Assert.assertNotNull(dbInfo);
        Assert.assertEquals(IntactContext.getCurrentInstance().getConfig()
                .getDefaultDataConfig().getMinimumRequiredVersion(), SchemaVersion.parse(dbInfo.getValue()));
    }

    @Test
    public void componentMapping() throws Exception {
        Settings settings = ((AbstractHibernateDataConfig)getIntactContext().getConfig().getDefaultDataConfig()).getConfiguration().buildSettings();
        DatabaseMetadata databaseMetadata = new DatabaseMetadata(getDaoFactory().connection(), settings.getDialect());
        TableMetadata tableMetadata = databaseMetadata.getTableMetadata("ia_component", null, null, false);

        Assert.assertNotNull(tableMetadata.getColumnMetadata("role_ac"));
        Assert.assertNull(tableMetadata.getColumnMetadata("experimentalrole_ac"));
        Assert.assertNull(tableMetadata.getColumnMetadata("biologicalrole_ac"));
    }


}