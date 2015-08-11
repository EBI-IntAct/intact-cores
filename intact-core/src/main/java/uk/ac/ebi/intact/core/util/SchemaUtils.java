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
import org.hibernate.dialect.*;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.config.hibernate.IntactHibernatePersistence;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.IntactInitializer;

import javax.persistence.spi.PersistenceUnitInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        Configuration cfg = createConfiguration(props);

        String[] sqls = cfg.generateSchemaCreationScript(Dialect.getDialect(props));
        addDelimiters(sqls);

        return sqls;
    }

    public static String[] generateUpdateSchemaDDL(String dialect, Connection connection) throws SQLException {
        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        Configuration cfg = createConfiguration(props);

        final Dialect dialectObj = Dialect.getDialect(props);

        String[] sqls = cfg.generateSchemaUpdateScript(dialectObj, new DatabaseMetadata(connection, dialectObj) );
        addDelimiters(sqls);

        return sqls;
    }

    private static void addDelimiters(String[] sqls) {
        for (int i=0; i<sqls.length; i++) {
            sqls[i] = sqls[i]+";";
        }
    }

    private static Configuration createConfiguration(Properties props) {
        Ejb3Configuration ejbConfig = new IntactHibernatePersistence().getBasicConfiguration(props);

        ejbConfig.addProperties(props);

        return ejbConfig.getHibernateConfiguration();
    }

    /**
     * Generates the DDL schema for Oracle 9i.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForOracle() {
        return generateCreateSchemaDDL(Oracle9iDialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for Oracle .
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForOracle(Connection connection) throws SQLException {
        return generateUpdateSchemaDDL(Oracle10gDialect.class.getName(), connection);
    }

    /**
     * Generates the DDL schema for PostgreSQL.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForPostgreSQL() {
        return generateCreateSchemaDDL(PostgreSQLDialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for PostgreSQL .
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForPostgreSQL(Connection connection) throws SQLException {
        return generateUpdateSchemaDDL(PostgreSQLDialect.class.getName(), connection);
    }

    /**
     * Generates the DDL schema for HSQL DB.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForHSQL() {
        return generateCreateSchemaDDL(HSQLDialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for HSQL .
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForHSQL(Connection connection) throws SQLException {
        return generateUpdateSchemaDDL(HSQLDialect.class.getName(), connection);
    }


    /**
     * Generates the DDL schema for HSQL DB.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForH2() {
        return generateCreateSchemaDDL(H2Dialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for H2.
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForH2(Connection connection) throws SQLException {
        return generateUpdateSchemaDDL(H2Dialect.class.getName(), connection);
    }

    /**
     * Generates the DDL schema
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDL(String dialect) {
        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        Configuration cfg = createConfiguration(props);

        String[] sqls = cfg.generateSchemaCreationScript(Dialect.getDialect(props));
        addDelimiters(sqls);

        return sqls;
    }

    public static String[] getTableNames() {
        List<String> tableNames = new ArrayList<String>();

        Configuration cfg = createConfiguration(new Properties());

        Iterator<PersistentClass> classMappings = cfg.getClassMappings();

        while (classMappings.hasNext()) {
            PersistentClass o =  classMappings.next();
            tableNames.add(o.getTable().getName());
        }

        return tableNames.toArray(new String[tableNames.size()]);
    }

    /**
     * Generates the DDL schema for Oracle
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDLForOracle() {
        return generateDropSchemaDDL(Oracle9iDialect.class.getName());
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
    public static void createSchema() {
        createSchema(true);
    }

    /**
     * Creates a schema
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void createSchema(boolean initializeDatabase) {
        if (log.isDebugEnabled()) log.debug("Creating schema");

        SchemaExport se = newSchemaExport();
        se.create(false, true);

        if (initializeDatabase) {
            if (log.isDebugEnabled()) log.debug("Initializing database");
            IntactInitializer initializer = (IntactInitializer) IntactContext.getCurrentInstance()
                    .getSpringContext().getBean("intactInitializer");
            try {
                initializer.init();
            } catch (Exception e) {
                throw new IntactException("Problem re-initializing core", e);
            }
        } 
    }

    protected static SchemaExport newSchemaExport() {
        LocalEntityManagerFactoryBean factoryBean = (LocalEntityManagerFactoryBean) IntactContext.getCurrentInstance().getSpringContext()
                .getBean("&entityFactoryManager");

        PersistenceUnitInfo persistenceUnitInfo = factoryBean.getPersistenceUnitInfo();
        Configuration config = new Ejb3Configuration().configure(persistenceUnitInfo, null).getHibernateConfiguration();
        
        SchemaExport se =  new SchemaExport(config);
        return se;
    }

    /**
     * Drops the current schema, emptying the database
     */
    public static void dropSchema() {
        if (log.isDebugEnabled()) log.debug("Droping schema");

        SchemaExport se = newSchemaExport();
        se.drop(false, true);
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

        dropSchema();
        createSchema(initializeDatabase);
    }
}