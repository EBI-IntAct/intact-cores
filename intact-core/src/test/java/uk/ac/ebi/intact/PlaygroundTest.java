package uk.ac.ebi.intact;

import org.junit.Test;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.util.CrcCalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundTest {

    @Test
    public void letsPlay() throws Exception {


        //IntactContext context = IntactContext.getCurrentInstance();
        IntactContext.initStandaloneContext(new File(PlaygroundTest.class.getResource("/META-INF/zpro-hibernate.cfg.xml").getFile()));


//        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager()
//                .createQuery("from Institution i where i.shortLabel in (:shorts)");
//        q.setParameter("shorts", Arrays.asList("ebi", "intact", "bla"));
        //System.out.println(q.getResultList());

        //final EntityManager entityManager = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager();
        //Query q = entityManager.createQuery("from InteractionImpl order by shortLabel");

        CrcCalculator calculator = new CrcCalculator();

        Map<String,String> crcs = new HashMap<String,String>();

        int i=35000;

        List<InteractionImpl> interactionsPage;
        int firstResult = i;
        int maxResults = 200;

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        Writer writer = new FileWriter("/scratch/projects/intact-current/core/intact-core/src/test/resources/redundant.txt", true);

        do {
            //q.setFirstResult(firstResult);
            //q.setMaxResults(maxResults);
            dataContext.beginTransaction();
            interactionsPage = dataContext.getDaoFactory()
                    .getInteractionDao().getAll(firstResult, maxResults);

            for (InteractionImpl interaction : interactionsPage) {
                String crc = calculator.crc64(interaction);

                if (crcs.containsKey(crc)) {
                    String redundancy = crcs.get(crc);
                    writer.write("REDUNDANT: " + interaction.getAc() + " (" + interaction.getShortLabel() + ") - " + redundancy + "\n");
                    writer.flush();
                } else {
                    crcs.put(crc, interaction.getAc() + " (" + interaction.getShortLabel() + ")");
                }

                i++;

                if (i % 1000 == 0) {
                    writer.write("Processed: " + i+"\n");
                    writer.flush();
                }
            }
            
            dataContext.commitTransaction();

            firstResult = firstResult + maxResults;

        } while (!interactionsPage.isEmpty());

        writer.close();
    }
}
