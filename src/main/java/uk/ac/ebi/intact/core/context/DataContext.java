/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;

import java.io.Serializable;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
@Component
public class DataContext implements Serializable {

    private static final Log log = LogFactory.getLog( DataContext.class );

    @Autowired
    private DaoFactory daoFactory;

    @Autowired
    private ApplicationContext applicationContext;

    public DataContext( ) {
    }

//    public void beginTransaction() {
//        beginTransaction(  );
//    }

    @Deprecated
    public TransactionStatus beginTransactionManualFlush() {
        //getDefaultDataConfig().setAutoFlush(true);
        return beginTransaction( TransactionDefinition.PROPAGATION_REQUIRED );
    }

    public TransactionStatus beginTransaction() {
        return beginTransaction( TransactionDefinition.PROPAGATION_REQUIRES_NEW );
    }

    public TransactionStatus beginTransaction( int propagation ) {
        PlatformTransactionManager transactionManager = getTransactionManager();

        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition(propagation);
        
        if (log.isDebugEnabled()) log.debug("Beginning transaction: "+transactionDefinition.getName()+" Propagation="+propagation);

        return transactionManager.getTransaction(transactionDefinition);
    }

    public void commitTransaction( TransactionStatus transactionStatus ) throws IntactTransactionException {
        PlatformTransactionManager transactionManager = getTransactionManager();
        try {
            if (log.isDebugEnabled()) log.debug("Committing transaction");
            transactionManager.commit(transactionStatus);
        } catch (TransactionException e) {
            rollbackTransaction(transactionStatus);
            throw new IntactTransactionException( e );
        }
    }

    public void rollbackTransaction( TransactionStatus transactionStatus ) throws IntactTransactionException {
        PlatformTransactionManager transactionManager = getTransactionManager();

        try {
            if (log.isDebugEnabled()) log.debug("Rolling back transaction");
            transactionManager.rollback(transactionStatus);
        } catch (TransactionException e) {
            throw new IntactTransactionException(e);
        }
    }

    public PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager) applicationContext.getBean("transactionManager");
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Deprecated
    public boolean isReadOnly() {
        return false;
    }

    public void flushSession() {
        getDaoFactory().getEntityManager().flush();
    }


}
