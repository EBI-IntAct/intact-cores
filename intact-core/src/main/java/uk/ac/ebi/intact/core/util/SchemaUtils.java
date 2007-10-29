/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.Properties;

/**
 * IntAct schema utils, that contains methods to create/drop the database schema, create DDLs...
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SchemaUtils {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(SchemaUtils.class);

    private SchemaUtils(){}

    /**
     * Generates the DDL schema
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDL(String dialect) {
        Ejb3Configuration ejb3Cfg = (Ejb3Configuration) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getConfiguration();
        Configuration cfg = ejb3Cfg.getHibernateConfiguration();

        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        return cfg.generateSchemaCreationScript(Dialect.getDialect(props));
    }

    /**
     * Generates the DDL schema for Oracle 9i
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForOracle() {
        return generateCreateSchemaDDL(Oracle9Dialect.class.getName());
    }

    /**
     * Generates the DDL schema for PostgreSQL
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForPostgreSQL() {
        return generateCreateSchemaDDL(PostgreSQLDialect.class.getName());
    }

    /**
     * Generates the DDL schema
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDL(String dialect) {
        Configuration cfg = ((AbstractHibernateDataConfig) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig()).getConfiguration();

        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        return cfg.generateDropSchemaScript(Dialect.getDialect(props));
    }

    /**
     * Generates the DDL schema for Oracle
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDLForOracle() {
        return generateDropSchemaDDL(Oracle9Dialect.class.getName());
    }

    /**
     * Generates the DDL schema for PostgreSQL
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDLForPostgreSQL() {
        return generateDropSchemaDDL(PostgreSQLDialect.class.getName());
    }

    /**
     * Creates a schema and initialize the database
     */
    public static void createSchema() throws IntactTransactionException {
        createSchema(true);
    }

    /**
     * Creates a schema
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void createSchema(boolean initializeDatabase) throws IntactTransactionException {
        if (log.isDebugEnabled()) log.debug("Creating schema");

        SchemaExport se = newSchemaExport();
        se.create(false, true);

        if (initializeDatabase) {
            if (log.isDebugEnabled()) log.debug("Initializing database");
            IntactConfigurator.initializeDatabase(IntactContext.getCurrentInstance());
        } 
    }

    protected static SchemaExport newSchemaExport() {
        Configuration config = (Configuration) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getConfiguration();
        
        SchemaExport se =  new SchemaExport(config);
        return se;
    }

    /**
     * Drops the current schema, emptying the database
     */
    public static void dropSchema() throws IntactTransactionException {
        if (log.isDebugEnabled()) log.debug("Droping schema");

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        if (dataContext.isTransactionActive()) {
            throw new IllegalStateException("To drop the schema, the transaction must NOT be active");
        }

        dataContext.beginTransaction();

        SchemaExport se = newSchemaExport();
        se.drop(false, true);

        dataContext.commitTransaction();
    }

    /**
     * Drops and creates the schema, initializing intact. Beware that it commits transactions
     */
    public static void resetSchema() throws IntactTransactionException {
        resetSchema(true);
    }

    /**
     * Drops and creates the schema. Beware that it commits transactions
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void resetSchema(boolean initializeDatabase) throws IntactTransactionException {
        if (log.isDebugEnabled()) log.debug("Resetting schema");

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        if (dataContext.isTransactionActive()) {
            throw new IllegalStateException("To reset the schema, the transaction must NOT be active: "+dataContext.getDaoFactory().getCurrentTransaction());
        }

        dropSchema();
        createSchema(initializeDatabase);
    }
}