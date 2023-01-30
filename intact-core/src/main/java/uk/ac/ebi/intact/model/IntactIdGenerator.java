/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import uk.ac.ebi.intact.core.context.IntactContext;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Generates identifiers for IntAct objects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jul-2006</pre>
 */
public class IntactIdGenerator extends SequenceStyleGenerator {

    private static final Log log = LogFactory.getLog(IntactIdGenerator.class);

    public static final String INTACT_AC_SEQUENCE_NAME = "intact_ac";
    private String sequenceCallSyntax;


    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        final Dialect dialect = jdbcEnvironment.getDialect();

        String sequenceName = ConfigurationHelper.getString(SEQUENCE_PARAM, properties, DEF_SEQUENCE_NAME);

        // use "intact_ac" only if the default sequence name is provided
        if (sequenceName.equals(DEF_SEQUENCE_NAME)) {
            sequenceName = INTACT_AC_SEQUENCE_NAME;
            properties.put(SEQUENCE_PARAM, sequenceName);
        }

        final String sequencePerEntitySuffix = ConfigurationHelper.getString(CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, properties, DEF_SEQUENCE_SUFFIX);

        final String defaultSequenceName = ConfigurationHelper.getBoolean(CONFIG_PREFER_SEQUENCE_PER_ENTITY, properties, false)
                ? properties.getProperty(JPA_ENTITY_NAME) + sequencePerEntitySuffix
                : DEF_SEQUENCE_NAME;

        sequenceCallSyntax = dialect.getSequenceNextValString(ConfigurationHelper.getString(SEQUENCE_PARAM, properties, defaultSequenceName));
        super.configure(type, properties, serviceRegistry);
    }

    /**
     * The ID is the concatenation of the prefix and a sequence provided by the database, separated
     * by a dash.
     *
     * @param sessionImplementor a hibernate session implementor
     * @param object             the object being persisted
     * @return the new generated ID
     * @throws HibernateException if something goes wrong
     */
    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object object) throws HibernateException {
        String prefix;
        if (IntactContext.currentInstanceExists()) {
            prefix = IntactContext.getCurrentInstance().getConfig().getAcPrefix();
        } else {
            prefix = "UNK";
        }
        String stringId = null;

        Connection connection = sessionImplementor.connection();
        try {
            try (PreparedStatement ps = connection.prepareStatement(sequenceCallSyntax)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        stringId = prefix + "-" + id;
                    }
                    log.trace("Assigning Id: " + stringId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stringId;
    }

    public String getSequenceName() {
        return INTACT_AC_SEQUENCE_NAME;
    }
}
