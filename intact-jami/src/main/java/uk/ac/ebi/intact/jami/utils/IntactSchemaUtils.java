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
package uk.ac.ebi.intact.jami.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.*;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;

import javax.persistence.spi.PersistenceUnitInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * IntAct schema utils, that contains methods to create/drop the database schema, create DDLs...
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public class IntactSchemaUtils {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(IntactSchemaUtils.class);

    private IntactSchemaUtils(){}

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

        return sqls;
    }

    public static List<SchemaUpdateScript> generateUpdateSchemaDDL(String dialect, Connection connection) throws SQLException {
        Properties props = new Properties();
        props.put(Environment.DIALECT, dialect);

        Configuration cfg = createConfiguration(props);

        final Dialect dialectObj = Dialect.getDialect(props);

        List<SchemaUpdateScript> sqls = cfg.generateSchemaUpdateScriptList(dialectObj, new DatabaseMetadata(connection, dialectObj));

        return sqls;
    }


    private static Configuration createConfiguration(Properties props) {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("intact-jami");

        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(Dialect.getDialect(props).getClass().getName());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.afterPropertiesSet();

        Ejb3Configuration cfg = new Ejb3Configuration();
        Ejb3Configuration configured = cfg.configure(factoryBean.getPersistenceUnitInfo(), new HashMap());

        factoryBean.getNativeEntityManagerFactory().close();

        configured.addProperties(props);

        return configured.getHibernateConfiguration();
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
    public static List<SchemaUpdateScript> generateUpdateSchemaDDLForOracle(Connection connection) throws SQLException {
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
    public static List<SchemaUpdateScript> generateUpdateSchemaDDLForPostgreSQL(Connection connection) throws SQLException {
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
    public static List<SchemaUpdateScript> generateUpdateSchemaDDLForHSQL(Connection connection) throws SQLException {
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
    public static List<SchemaUpdateScript> generateUpdateSchemaDDLForH2(Connection connection) throws SQLException {
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
    }

    protected static SchemaExport newSchemaExport() {
        LocalEntityManagerFactoryBean factoryBean = ApplicationContextProvider.getBean(LocalEntityManagerFactoryBean.class);

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
    public static void resetSchema() {
        resetSchema(true);
    }

    /**
     * Drops and creates the schema. Beware that it commits transactions
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void resetSchema(boolean initializeDatabase) {
        if (log.isDebugEnabled()) log.debug("Resetting schema");

        dropSchema();
        createSchema(initializeDatabase);
    }
}