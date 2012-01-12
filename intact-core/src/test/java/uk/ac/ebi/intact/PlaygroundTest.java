package uk.ac.ebi.intact;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.hibernate.dialect.H2Dialect;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.config.property.StringPropertyConverter;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Protein;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundTest {

    @Test
    @Ignore
    public void printH2Schema() throws Exception {
        IntactContext.initContext(new String[] {"classpath:/META-INF/persistent-db-test.xml"});

        System.out.println(IntactContext.getCurrentInstance().getDaoFactory().getPublicationDao().countAll());

        IntactContext.getCurrentInstance().destroy();

        System.out.println("\n\n\n\n\n"+IntactContext.currentInstanceExists()+"\n\n\n\n");

        IntactContext.initContext(new String[] {"classpath:/META-INF/persistent-db-test.xml"});

        System.out.println(IntactContext.getCurrentInstance().getDaoFactory().getPublicationDao().countAll());
    }
}
