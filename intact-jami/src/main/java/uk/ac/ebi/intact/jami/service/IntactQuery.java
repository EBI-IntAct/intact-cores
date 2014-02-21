package uk.ac.ebi.intact.jami.service;

import java.util.Map;

/**
 * Intact query which provides the query string and the matching count query string
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */

public class IntactQuery {
    private String query;
    private String countQuery;
    private Map<String, Object> queryParameters;

    public IntactQuery(String query, String countQuery){
        if (query == null){
            throw new IllegalArgumentException("The query cannot be null");
        }
        this.query = query;
        if (countQuery == null){
            throw new IllegalArgumentException("The count query cannot be null");
        }
        this.countQuery = countQuery;
    }

    public String getQuery() {
        return query;
    }

    public String getCountQuery() {
        return countQuery;
    }

    public Map<String, Object> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, Object> queryParameters) {
        this.queryParameters = queryParameters;
    }
}
