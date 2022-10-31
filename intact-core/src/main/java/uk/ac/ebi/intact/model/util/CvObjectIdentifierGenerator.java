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
package uk.ac.ebi.intact.model.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.config.SequenceCreationException;
import uk.ac.ebi.intact.core.config.SequenceManager;
import uk.ac.ebi.intact.core.config.hibernate.IntactHibernatePersistenceProvider;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;

import java.util.Collection;

/**
 * Generates identifiers for the CvObject, using a sequence in the database
 * named cv_local_seq. This sequence will be created automatically if it does not
 * exist, by the method to retrieve the next value. This could be a problem if the user
 * does not have permission to create/query sequences.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @since 1.9.0
 */
public class CvObjectIdentifierGenerator {

    private static final Log log = LogFactory.getLog(CvObjectIdentifierGenerator.class);

    /**
     * Populates the identifier property of the CvObject, by getting the value from the primaryId of
     * the identity cross reference. PSI MI identity xrefs take precedence, but if it does not exist,
     * the first identity found is used. If no identity cross references are found for the object, a
     * new identifier will be created using the <code>local_cv_seq</code> sequence. If the parameter
     * <code>createXrefIfLocalCv</code> is true, a cross reference with the new identifier will be created,
     * which points to the intact database.
     * @param cvObject The CvObject to populate
     * @param createXrefIfLocalCv If true, a new xref will be created if the CvObject didn't have one already,
     * with the newly generated identifier.
     *
     * @since 1.9.0
     */
    public void populateIdentifier(CvObject cvObject, boolean createXrefIfLocalCv) {

        String identifier = cvObject.getIdentifier();

        if (identifier == null) {
            if (log.isDebugEnabled()) log.debug("Populating identifier for: " + cvObject.getShortLabel());

            Collection<CvObjectXref> miIdXrefs = AnnotatedObjectUtils.searchXrefs(cvObject, CvDatabase.PSI_MI_MI_REF, CvXrefQualifier.IDENTITY_MI_REF);

            // try to get first the mi identity. If none is found, just use the first identity
            if (!miIdXrefs.isEmpty()) {
                identifier = miIdXrefs.iterator().next().getPrimaryId();
            } else {
                Collection<CvObjectXref> idXrefs = AnnotatedObjectUtils.searchXrefsByQualifier(cvObject, CvXrefQualifier.IDENTITY_MI_REF);

                if (!idXrefs.isEmpty()) {
                    identifier = idXrefs.iterator().next().getPrimaryId();
                } else {
                    // no identities found in object. Create a local identity if createXrefIfLocalCv.

                    if (log.isDebugEnabled())
                        log.debug("New identifier will be generated for: " + cvObject.getShortLabel());
                    try {
                        identifier = nextLocalIdentifier();
                    } catch (SequenceCreationException e) {
                        throw new IntactException("Problem generating sequence for a CvObject without identifier: " + cvObject, e);
                    }

                    if (createXrefIfLocalCv) {
                        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                        CvXrefQualifier identity = daoFactory.getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);
                        CvDatabase intact = daoFactory.getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.INTACT_MI_REF);

                        if (identity == null || intact == null) {
                            throw new IllegalStateException("Cannot create a local identity xref, as the \"identity\" CvXrefQualifier (" +
                                    CvXrefQualifier.IDENTITY_MI_REF + ") and the \"intact\" CvDatabase (" + CvDatabase.INTACT_MI_REF +
                                    ") must exist in the database if a CvObject without identity is persisted. This is necessary " +
                                    "to create a new generated identity for this object: " + cvObject);
                        }

                        CvObjectXref xref = new CvObjectXref(cvObject.getOwner(), intact, identifier, identity);
                        cvObject.addXref(xref);
                    }
                }
            }

            cvObject.setIdentifier(identifier);
        }

    }

    /**
     * The local identity is created using a sequence. If the sequence does not exist, a new one is created
     * with initial value calculated using the maximum integer for the existing local CV identifiers.
     * @return The next value available
     * @throws SequenceCreationException thrown if the sequence cannot be created.
     */
    protected String nextLocalIdentifier() throws SequenceCreationException {
        final IntactContext context = IntactContext.getCurrentInstance();
        String prefix = context.getConfig().getLocalCvPrefix();
        Integer max = context.getDataContext().getDaoFactory()
                .getCvObjectDao().getLastCvIdentifierWithPrefix(prefix);

        if (max == null) max = 0;

        SequenceManager seqManager = (SequenceManager) context.getSpringContext().getBean("sequenceManager");

        seqManager.createSequenceIfNotExists(IntactHibernatePersistenceProvider.CV_LOCAL_SEQ, max+1);

        String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactHibernatePersistenceProvider.CV_LOCAL_SEQ));
        return prefix + ":" + StringUtils.leftPad(nextIntegerAsString, 4, "0");
    }

}
