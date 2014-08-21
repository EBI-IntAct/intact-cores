package uk.ac.ebi.intact.jami.interceptor;

import uk.ac.ebi.intact.jami.dao.IntactDao;

import java.util.concurrent.Executor;

/**
 * Interface for classes that will react to a spring transaction commit
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/08/14</pre>
 */

public interface AfterCommitExecutor extends Executor {

    public void registerDaoForSynchronization(IntactDao intactDao);

    public void afterCommit();

    public void afterCompletion(int status);
}
