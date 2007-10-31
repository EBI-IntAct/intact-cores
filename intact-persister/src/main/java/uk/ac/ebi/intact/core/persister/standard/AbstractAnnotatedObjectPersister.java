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
package uk.ac.ebi.intact.core.persister.standard;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.core.persister.AbstractPersister;
import uk.ac.ebi.intact.core.persister.BehaviourType;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractAnnotatedObjectPersister<T extends AnnotatedObject<?,?>> extends AbstractPersister<T> {

    protected AbstractAnnotatedObjectPersister() {
        super();
    }

    @Override
    protected void saveOrUpdateAttributes( T intactObject ) throws PersisterException {
        if ( intactObject == null ) {
            throw new NullPointerException( "intactObject" );
        }

        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();
        institutionPersister.saveOrUpdate( intactObject.getOwner() );

        CvObjectPersister cvPersister = CvObjectPersister.getInstance();

        for ( Xref xref : intactObject.getXrefs() ) {
            cvPersister.saveOrUpdate( xref.getCvDatabase() );
            if ( xref.getCvXrefQualifier() != null ) {
                cvPersister.saveOrUpdate( xref.getCvXrefQualifier() );
            }
            institutionPersister.saveOrUpdate( xref.getOwner() );
        }

        for ( Alias alias : intactObject.getAliases() ) {
            if ( alias.getCvAliasType() != null ) {
                cvPersister.saveOrUpdate( alias.getCvAliasType() );
            }
            institutionPersister.saveOrUpdate( alias.getOwner() );
        }
        
        for ( Annotation annotation : intactObject.getAnnotations() ) {
            if ( annotation.getCvTopic() != null ) {
                cvPersister.saveOrUpdate( annotation.getCvTopic() );
            }
            institutionPersister.saveOrUpdate( annotation.getOwner() );
        }
    }

    @Override
    protected T syncAttributes(T intactObject) {
        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();

        if (!(intactObject instanceof Institution)) {
            // breaks infinite loop as an Institution has also an owner, that is an institution that ....
            Institution owner = intactObject.getOwner();
            intactObject.setOwner(institutionPersister.syncIfTransient(owner));
        }

        CvObjectPersister cvPersister = CvObjectPersister.getInstance();

        try {
            for (Xref xref : intactObject.getXrefs()) {
                CvDatabase cvDb = (CvDatabase) cvPersister.syncIfTransient(xref.getCvDatabase());
                xref.setCvDatabase(cvDb);

                if (xref.getCvXrefQualifier() != null) {
                    CvXrefQualifier cvXrefQual = (CvXrefQualifier) cvPersister.syncIfTransient(xref.getCvXrefQualifier());
                    xref.setCvXrefQualifier(cvXrefQual);
                }
                xref.setParent(intactObject);
                xref.setOwner(institutionPersister.syncIfTransient(xref.getOwner()));
            }
            for (Alias alias :  intactObject.getAliases()) {
                CvAliasType cvAliasType = alias.getCvAliasType();

                if (cvAliasType != null) {
                    cvAliasType = (CvAliasType) cvPersister.syncIfTransient(alias.getCvAliasType());
                    alias.setCvAliasType(cvAliasType);
                }
                alias.setParent(intactObject);
                alias.setOwner(institutionPersister.syncIfTransient(alias.getOwner()));
            }
            for (Annotation annotation : intactObject.getAnnotations()) {
                CvTopic cvTopic = annotation.getCvTopic();

                if (cvTopic != null) {
                    cvTopic = (CvTopic) cvPersister.syncIfTransient(annotation.getCvTopic());
                    annotation.setCvTopic(cvTopic);
                }
                annotation.setOwner(institutionPersister.syncIfTransient(annotation.getOwner()));
            }
        } catch (Throwable t) {
            throw new IntactException("Exception syncing attributes of: "+intactObject.getShortLabel()+" ("+intactObject.getAc()+")", t);
        }

        return intactObject;
    }

    @Override
    protected BehaviourType syncedAndCandidateAreEqual(T synced, T candidate) {
        if (synced == null) {
            return BehaviourType.NEW;
        }
        
        return BehaviourType.IGNORE;
    }

    @Override
    protected boolean update(T candidateObject, T objectToUpdate) throws PersisterException {
        throw new UnsupportedOperationException();
    }

    protected boolean updateCommonAttributes(T candidateObject, T objectToBeUpdated) throws PersisterException {

        for (Annotation annotation : candidateObject.getAnnotations()) {
            objectToBeUpdated.addAnnotation(annotation);
        }

        saveOrUpdateAttributes(objectToBeUpdated);

        return true;
    }

    /**
     * Returns true if the annotated objects provided have the same annotations
     */
    protected static boolean haveSameAnnotations(AnnotatedObject<?,?> ao1, AnnotatedObject<?,?> ao2) {
        for (Annotation a1 : ao1.getAnnotations()) {
            boolean found = false;

            for (Annotation a2 : ao2.getAnnotations()) {
                if (a1.getAnnotationText().equals(a2.getAnnotationText())) {

                    CvObjectXref topicXref1 = CvObjectUtils.getPsiMiIdentityXref(a1.getCvTopic());
                    CvObjectXref topicXref2 = CvObjectUtils.getPsiMiIdentityXref(a2.getCvTopic());

                    if (topicXref1 == null && topicXref2 == null) {
                        if (a1.getCvTopic().getShortLabel().equals(a2.getCvTopic().getShortLabel())) {
                            found = true;
                            break;
                        }
                    } else if (topicXref1 == null || topicXref2 == null) {
                        // ignore
                    } else if (topicXref1.getPrimaryId().equals(topicXref2.getPrimaryId())){
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) return false;
        }
         return true;
    }
}