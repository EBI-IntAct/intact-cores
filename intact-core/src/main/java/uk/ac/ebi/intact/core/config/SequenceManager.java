/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.config.hibernate.SequenceAuxiliaryDatabaseObject;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.List;

/**
 * This class is responsible for creating database sequences that are not directly
 * configured in the model mappings, because it cannot do it that way.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class SequenceManager {

    private static final Log log = LogFactory.getLog(SequenceManager.class);

    private Dialect dialect;

    @PersistenceUnit(unitName = "intact-core-default")
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager entityManager;

    public SequenceManager() {

    }

    public SequenceManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        init();
    }

    @PostConstruct
    public void init() {
        this.dialect = ((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory().getDialect();
    }

    /**
     * Checks if a sequence exists.
     * @param sequenceName The name of the sequence
     * @return True if the sequence exists
     */
    public boolean sequenceExists(String sequenceName) {
        return getExistingSequenceNames(entityManager).stream().anyMatch(s -> s.equalsIgnoreCase(sequenceName));
    }

    /**
     * Creates a sequence in the database, if it does not exist already. Uses the default initial value, which is 1.
     * @param sequenceName The name of the new sequence
     * @throws SequenceCreationException
     */
    public void createSequenceIfNotExists(String sequenceName) throws SequenceCreationException {
        createSequenceIfNotExists(sequenceName, 1);
    }

    /**
     * Creates a sequence in the database, if it does not exist already.
     * @param sequenceName The name of the new sequence
     * @param initialValue The initial value of the sequence. This will be the first value given by the sequence
     * when the next value is invoked
     * @throws SequenceCreationException Will happen if there are problems creating the sequence in the database
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSequenceIfNotExists(String sequenceName, int initialValue) throws SequenceCreationException {
        if (!sequenceExists(sequenceName)) {
            if (log.isInfoEnabled())
                log.info("Sequence could not be found and it is going to be created: " + sequenceName);

            String sql = new SequenceAuxiliaryDatabaseObject(sequenceName, initialValue).sqlCreateString(dialect);

            try {
                final Query createSeqQuery = entityManager.createNativeQuery(sql);
                createSeqQuery.executeUpdate();
            } catch (Exception e) {
                throw new SequenceCreationException("Exception creating the sequence: " + sequenceName + " (initial value: " + initialValue + ")", e);
            }
        }
    }

    /**
     * Gets the names of the existing sequences in the database.
     * @param entityManager The entity manager to use
     * @return the names of the sequences
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<String> getExistingSequenceNames(EntityManager entityManager) {
        Query query;
        String dialectQuerySequencesString = dialect.getQuerySequencesString();
        if (dialectQuerySequencesString == null || dialectQuerySequencesString.length() == 0) {
            query = entityManager.createNativeQuery(getQuerySequencesString());
        } else {
            query = entityManager.createNativeQuery(getQuerySequenceNamesFromQuerySequences(dialectQuerySequencesString));
        }

        return query.getResultList();
    }

    /**
     * Gets the next value for the provided sequence
     * @param sequenceName The sequence name to query
     * @return The next value for that sequence; null if the sequence does not exist;
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Long getNextValueForSequence(String sequenceName) {
        if (!sequenceExists(sequenceName)) {
            throw new IllegalArgumentException("Sequence does not exist: " + sequenceName +
                    ". Sequences found in the database: " + getExistingSequenceNames(entityManager));
        }

        Query query = entityManager.createNativeQuery(dialect.getSequenceNextValString(sequenceName));
        Object resultObject = query.getSingleResult();

        if (resultObject != null) {
            Number nextSeq = (Number) resultObject;
            return nextSeq.longValue();
        }

        return null;
    }

    /**
     * Returns the query sequence for oracle as 'select sequence_name  from user_sequences'
     * doesn't return any sequences unless logged as root user
     * @return the query
     */
    private String getQuerySequencesString() {
        return "select sequence_name from all_sequences";
    }

    /**
     * Oracle dialect getQuerySequencesString method queries not only the sequence names, but all the sequences'
     * fields, so we need to only get the names from it.
     * @param dialectQuerySequencesString - SQL query to get sequences provided by Oracle dialect
     * @return SQL query to fetch only the sequence names
     */
    private String getQuerySequenceNamesFromQuerySequences(String dialectQuerySequencesString) {
        return String.format("select sequence_schema || '.' || sequence_name from (%s) all_sequences", dialectQuerySequencesString);
    }

}
