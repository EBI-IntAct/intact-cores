package uk.ac.ebi.intact.config.impl.compat;

import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.config.impl.InMemoryDataConfig;
import uk.ac.ebi.intact.context.IntactSession;

import java.net.URL;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InMemoryCompat13DataConfig extends InMemoryDataConfig
{
    public static final String NAME = "uk.ac.ebi.intact.config.IN_MEMORY_COMPAT_13";

    private CommonCompat13 commonCompat;

    public InMemoryCompat13DataConfig(IntactSession session)
    {
        super(session);
        this.commonCompat = new CommonCompat13();
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public SchemaVersion getMinimumRequiredVersion()
    {
        return commonCompat.getMinimumRequiredVersion();
    }

    public List<String> getExcludedEntities()
    {
        return commonCompat.getExcludedEntities();
    }

    public URL getMappings()
    {
        return commonCompat.getMappings();
    }
}