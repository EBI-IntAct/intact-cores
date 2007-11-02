package uk.ac.ebi.intact;

import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.util.CrcCalculator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundJpa {

    public static void main(String[] args) throws Exception {


        //IntactContext context = IntactContext.getCurrentInstance();
        IntactContext.initStandaloneContext(new File(PlaygroundJpa.class.getResource("/META-INF/zpro-hibernate.cfg.xml").getFile()));


//        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager()
//                .createQuery("from Institution i where i.shortLabel in (:shorts)");
//        q.setParameter("shorts", Arrays.asList("ebi", "intact", "bla"));
        //System.out.println(q.getResultList());

        //final EntityManager entityManager = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager();
        //Query q = entityManager.createQuery("from InteractionImpl order by shortLabel");

        CrcCalculator calculator = new CrcCalculator();

        Map<String,String> crcs = new HashMap<String,String>();

        int i=0;

        List<InteractionImpl> interactionsPage;
        int firstResult = 0;
        int maxResults = 200;

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

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
                    System.out.println("REDUNDANT: " + interaction.getAc() + " (" + interaction.getShortLabel() + ") - " + redundancy);
                } else {
                    crcs.put(crc, interaction.getAc() + " (" + interaction.getShortLabel() + ")");
                }

                i++;

                if (i % 1000 == 0) {
                    System.out.println("Processed: " + i);
                }
            }
            
            dataContext.commitTransaction();

            firstResult = firstResult + maxResults;

        } while (!interactionsPage.isEmpty());

    }
}
