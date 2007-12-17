/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;

import javax.persistence.EntityManagerFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public abstract class DataConfig<T, C> {

    private static final Log log = LogFactory.getLog( DataConfig.class );

    // required version is 1.8.0 (build version should always be 0, as a change in the build
    // version should not break compatibility)
    protected static final Integer DEFAULT_REQUIRED_VERSION_MAJOR = 1;
    protected static final Integer DEFAULT_REQUIRED_VERSION_MINOR = 8;
    protected static final Integer DEFAULT_REQUIERD_VERSION_BUILD = 0;

    private IntactSession session;

    private boolean autoFlush = true;

    public DataConfig( IntactSession session ) {
        this.session = session;
    }

    public abstract String getName();

    public abstract void initialize();

    @Deprecated
    public abstract T getSessionFactory();

    public abstract EntityManagerFactory getEntityManagerFactory();

    @Deprecated
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

    @Deprecated
    public abstract void flushSession();

    public abstract boolean isConfigurable();

    public abstract SchemaVersion getMinimumRequiredVersion();
}
