package uk.ac.ebi.intact;

import org.h2.tools.Server;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

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
        final Server server = Server.createTcpServer("-tcpPort", "39101", "-baseDir", "target", "-tcpDaemon");
        server.start();

        IntactContext.initContext(new String[] {"classpath:/META-INF/persistent-db-test.xml"});

        IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(new IntactMockBuilder().createPublicationRandom());

        System.out.println(IntactContext.getCurrentInstance().getDaoFactory().getPublicationDao().countAll());

//        IntactContext.getCurrentInstance().getSpringContext().close();
        IntactContext.getCurrentInstance().destroy();

        System.out.println("\n\n\n\n\n"+IntactContext.currentInstanceExists()+"\n\n\n\n");

        IntactContext.initContext(new String[] {"classpath:/META-INF/persistent-db-test.xml"});

        System.out.println(IntactContext.getCurrentInstance().getDaoFactory().getPublicationDao().countAll());

        server.stop();
    }
}
