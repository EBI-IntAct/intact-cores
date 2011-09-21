package uk.ac.ebi.intact.core.batch.reader;

import org.springframework.batch.item.database.JpaPagingItemReader;

/**
 * This reader can read intact objects and order them by created date
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/09/11</pre>
 */

public class orderedPublicationPagingItemReader extends JpaPagingItemReader {

    private String orderBy;

    public orderedPublicationPagingItemReader() {
        super();
    }

    public void setOrderBy(String order) {
        this.orderBy = order;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String query = "select p from Publication p";

        if (orderBy != null){

            query = query + " order by p."+orderBy;
        }

        setQueryString(query);

        super.afterPropertiesSet();
    }
}
