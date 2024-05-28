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
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.*;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.config.hibernate.IntactHibernatePersistenceProvider;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.IntactInitializer;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

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
    private static final IntactHibernatePersistenceProvider persistence = new IntactHibernatePersistenceProvider();

    private SchemaUtils() {
    }

    /**
     * Generates the DDL schema
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDL(DataSource dataSource, String dialect) {
        return exportSchema(
                "create",
                persistence.getBasicMetaDataBuilder(dataSource, dialect).build(),
                EnumSet.of(TargetType.SCRIPT),
                SchemaExport.Action.CREATE);
    }


    public static String[] generateUpdateSchemaDDL(DataSource dataSource, String dialect) {
        return exportSchema(
                "update",
                persistence.getBasicMetaDataBuilder(dataSource, dialect).build(),
                EnumSet.allOf(TargetType.class),
                SchemaExport.Action.BOTH);
    }

    /**
     * Generates the DDL schema
     *
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDL(DataSource dataSource, String dialect) {
        return exportSchema(
                "drop",
                persistence.getBasicMetaDataBuilder(dataSource, dialect).build(),
                EnumSet.of(TargetType.SCRIPT),
                SchemaExport.Action.DROP);
    }

    private static String[] exportSchema(
            String tempFileName,
            Metadata metadata,
            EnumSet<TargetType> targetTypes,
            SchemaExport.Action action) {

        String[] sqls;
        try {
            File file = File.createTempFile(tempFileName, ".sql");
            new SchemaExport().setDelimiter(";")
                    .setOutputFile(file.getAbsolutePath())
                    .execute(targetTypes, action, metadata);
            try (Stream<String> lines = Files.lines(file.toPath())) {
                sqls = lines.toArray(String[]::new);
            }
            if (file.delete()) log.debug("Temp file deleted");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sqls;
    }


    private static Configuration createConfiguration(DataSource dataSource, Properties props) {
        return new IntactHibernatePersistenceProvider().getBasicConfiguration(dataSource, props).addProperties(props);
    }

    /**
     * Generates the DDL schema for Oracle 9i.
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForOracle(DataSource dataSource) {
        return generateCreateSchemaDDL(dataSource, Oracle12cDialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for Oracle .
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForOracle(DataSource dataSource) {
        return generateUpdateSchemaDDL(dataSource, Oracle12cDialect.class.getName());
    }

    /**
     * Generates the DDL schema for PostgreSQL.
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForPostgreSQL(DataSource dataSource) {
        return generateCreateSchemaDDL(dataSource, PostgreSQL82Dialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for PostgreSQL .
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForPostgreSQL(DataSource dataSource) {
        return generateUpdateSchemaDDL(dataSource, PostgreSQL82Dialect.class.getName());
    }

    /**
     * Generates the DDL schema for HSQL DB.
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForHSQL(DataSource dataSource) {
        return generateCreateSchemaDDL(dataSource, HSQLDialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for HSQL .
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForHSQL(DataSource dataSource) {
        return generateUpdateSchemaDDL(dataSource, HSQLDialect.class.getName());
    }


    /**
     * Generates the DDL schema for HSQL DB.
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForH2(DataSource dataSource) {
        return generateCreateSchemaDDL(dataSource, H2Dialect.class.getName());
    }

    /**
     * Generates the UPDATE DDL schema for H2.
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateUpdateSchemaDDLForH2(DataSource dataSource) {
        return generateUpdateSchemaDDL(dataSource, H2Dialect.class.getName());
    }

    public static String[] getTableNames(DataSource dataSource) {
        return persistence.getBasicMetaDataBuilder(dataSource, PostgreSQL82Dialect.class.getName()).build()
                .getEntityBindings()
                .stream()
                .map(persistentClass -> {
                    List<String> tableNames = new ArrayList<>();
                    if (!persistentClass.isAbstract()) {
                        tableNames.add(persistentClass.getTable().getName());
                    }
                    Iterator propertiesIterator = persistentClass.getPropertyIterator();
                    while (propertiesIterator.hasNext()) {
                        Property property = (Property) propertiesIterator.next();
                        if (property.getValue().getType().isCollectionType()) {
                            Table collectionTable = ((Collection) property.getValue()).getCollectionTable();
                            if (collectionTable != null) {
                                tableNames.add(collectionTable.getName());
                            }
                        }
                    }
                    return tableNames;
                })
                .flatMap(List::stream)
                .distinct()
                .toArray(String[]::new);
    }

    /**
     * Generates the DDL schema for Oracle
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDLForOracle(DataSource dataSource) {
        return generateDropSchemaDDL(dataSource, Oracle12cDialect.class.getName());
    }

    /**
     * Generates the DDL schema for PostgreSQL
     *
     * @return an array containing the SQL statements
     */
    public static String[] generateDropSchemaDDLForPostgreSQL(DataSource dataSource) {
        return generateDropSchemaDDL(dataSource, PostgreSQL82Dialect.class.getName());
    }

    /**
     * Creates a schema and initialize the database
     */
    public static void createSchema() {
        createSchema(true);
    }

    /**
     * Creates a schema
     *
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void createSchema(boolean initializeDatabase) {
        if (log.isDebugEnabled()) log.debug("Creating schema");

        SchemaExport se = new SchemaExport();
        se.create(EnumSet.noneOf(TargetType.class), newMetadata());

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

    protected static Metadata newMetadata() {
        MetadataSources metadata = new MetadataSources(new StandardServiceRegistryBuilder().build());
        return persistence.configure(metadata.getMetadataBuilder()).build();
    }

    /**
     * Drops the current schema, emptying the database
     */
    public static void dropSchema() {
        if (log.isDebugEnabled()) log.debug("Droping schema");

        SchemaExport se = new SchemaExport();
        se.drop(EnumSet.noneOf(TargetType.class), newMetadata());
    }

    /**
     * Drops and creates the schema, initializing intact. Beware that it commits transactions
     */
    public static void resetSchema() throws IntactTransactionException {
        resetSchema(true);
    }

    /**
     * Drops and creates the schema. Beware that it commits transactions
     *
     * @param initializeDatabase If false, do not initialize the database (e.g. don't create Institution)
     */
    public static void resetSchema(boolean initializeDatabase) throws IntactTransactionException {
        if (log.isDebugEnabled()) log.debug("Resetting schema");

        dropSchema();
        createSchema(initializeDatabase);
    }
}