package uk.ac.ebi.intact.core.persister.interceptor;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface PrePersistInterceptor<T>
{
    void onPrePersist(T objToPersist);
}
