/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.jami.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

/**
 * This class is responsible of creating database sequences that are not directly
 * configured in the model mappings, because it cannot do it that way.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
@Component
public class SequenceManager {

    private static final Log log = LogFactory.getLog( SequenceManager.class );

//    @Autowired
//    private EntityManagerFactory entityManagerFactory;

    private Dialect dialect;

    @PersistenceUnit(unitName = "intact-core")
    private HibernateEntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;

    public SequenceManager() {

    }

    public SequenceManager(HibernateEntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        init();
    }

    @PostConstruct
    public void init() {
        this.dialect = ((SessionFactoryImplementor) entityManagerFactory.getSessionFactory()).getDialect();
    }

    /**
     * Checks if a sequence exists.
     * @param sequenceName The name of the sequence
     * @return True if the sequence exists
     */
    public boolean sequenceExists(String sequenceName) {
        List<String> existingSequences = getExistingSequenceNames(entityManager);

        for (String existingSequence : existingSequences) {
            if (existingSequence.equalsIgnoreCase(sequenceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a sequence in the database, if it does not exist already. Uses the default initial value, which is 1.
     * @param sequenceName The name of the new sequence
     */
    public void createSequenceIfNotExists(String sequenceName) {
        createSequenceIfNotExists(sequenceName, 1);
    }

    /**
     * Creates a sequence in the database, if it does not exist already.
     * @param sequenceName The name of the new sequence
     * @param initialValue The initial value of the sequence. This will be the first value given by the sequence
     * when the next value is invoked
     */
    @Transactional
    public void createSequenceIfNotExists(String sequenceName, int initialValue) {
        if (!sequenceExists(sequenceName)) {
            if (log.isInfoEnabled()) log.info("Sequence could not be found and it is going to be created: "+sequenceName);

            String sql = new SequenceAuxiliaryDatabaseObject(sequenceName, initialValue).sqlCreateString(dialect);

            final Query createSeqQuery = entityManager.createNativeQuery(sql);
            createSeqQuery.executeUpdate();
        }
    }

    /**
     * Gets the names of the existing sequences in the database.
     * @param entityManager The entity manager to use
     * @return the names of the sequences
     */
    @Transactional(readOnly = true)
    public List<String> getExistingSequenceNames(EntityManager entityManager) {
        Query query ;
        String sql = dialect.getQuerySequencesString();
        if (sql == null || sql.length() == 0) {
            query = entityManager.createNativeQuery(getQuerySequencesString());
        } else {
            query = entityManager.createNativeQuery(dialect.getQuerySequencesString());
        }

        return query.getResultList();
    }

    /**
     * Gets the next value for the provided sequence
     * @param sequenceName The sequence name to query
     * @return The next value for that sequence; null if the sequence does not exist;
     */
    @Transactional(readOnly = true)
    public Long getNextValueForSequence(String sequenceName ) {
        if ( !sequenceExists(sequenceName ) ) {
            log.error( "Sequence does not exist: " + sequenceName +
                                                ". Sequences found in the database: " + getExistingSequenceNames( entityManager ) );
            return null;
        }

        Query query = entityManager.createNativeQuery( dialect.getSequenceNextValString( sequenceName ) );
        Object resultObject = query.getSingleResult();

        if ( resultObject != null ) {
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

}
