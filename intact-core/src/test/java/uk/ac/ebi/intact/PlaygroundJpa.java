package uk.ac.ebi.intact;

import uk.ac.ebi.intact.context.IntactContext;

import javax.persistence.Query;
import java.util.Arrays;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundJpa {

    public static void main(String[] args) throws Exception {


        IntactContext context = IntactContext.getCurrentInstance();
        //System.out.println(context.getDataContext().getDaoFactory().getInstitutionDao().getAll());
        //System.out.println(context.getDataContext().getDaoFactory().getDbInfoDao().getAll());
 

        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager()
                .createQuery("from Institution i where i.shortLabel in (:shorts)");
        q.setParameter("shorts", Arrays.asList("ebi", "intact", "bla"));

        System.out.println(q.getResultList());
    }
}
