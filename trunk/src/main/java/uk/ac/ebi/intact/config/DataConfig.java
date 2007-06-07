/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public abstract class DataConfig<T, C> {

    private static final Log log = LogFactory.getLog( DataConfig.class );

    private IntactSession session;

    private boolean autoFlush;

    public DataConfig( IntactSession session ) {
        this.session = session;
    }

    public abstract String getName();

    public abstract void initialize();

    public abstract T getSessionFactory();

    public abstract void closeSessionFactory();

    public abstract C getConfiguration();

    private boolean initialized;

    protected void checkInitialization() {
        if ( !isInitialized() ) {
            initialize();
            this.initialized = true;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized( boolean initialized ) {
        this.initialized = initialized;
    }

    public IntactSession getSession() {
        return session;
    }

    public void setSession( IntactSession session ) {
        this.session = session;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isAutoFlush() {
        return autoFlush;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }
}
