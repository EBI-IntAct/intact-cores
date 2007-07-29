package uk.ac.ebi.intact.config.impl.compat;

import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.model.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CommonCompat13
{
    public SchemaVersion getMinimumRequiredVersion()
    {
        return new SchemaVersion(1,3,0);
    }

    public List<String> getExcludedEntities()
    {
        return Arrays.asList( Component.class.getName() );
    }

    public URL getMappings()
    {
        return CommonCompat13.class.getResource("/META-INF/compat/13/mappings.xml");
    }
}
