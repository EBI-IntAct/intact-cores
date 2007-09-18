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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectPersister extends AbstractAnnotatedObjectPersister<CvObject> {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog( CvObjectPersister.class );

    private static ThreadLocal<CvObjectPersister> instance = new ThreadLocal<CvObjectPersister>() {
        @Override
        protected CvObjectPersister initialValue() {
            return new CvObjectPersister();
        }
    };

    public static CvObjectPersister getInstance() {
        return instance.get();
    }

    protected CvObjectPersister() {
        super();
    }

    @Override
    protected void saveOrUpdateAttributes( CvObject intactObject ) throws PersisterException {
        if ( intactObject.getXrefs().isEmpty() ) {
            log.warn( "CvObject without Xrefs: " + intactObject.getShortLabel() );
            //throw new PersisterException("Cannot save or update a CvObject without Xrefs");
        }

        super.saveOrUpdateAttributes( intactObject );

        // TODO Bruno: as it is now that method is identical to the one in AbstractAnnotatedObjectPersister
//        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();
//        institutionPersister.saveOrUpdate( intactObject.getOwner() );
//
//        for ( CvObjectXref xref : intactObject.getXrefs() ) {
//            saveOrUpdate( xref.getCvDatabase() );
//            if ( xref.getCvXrefQualifier() != null ) {
//                saveOrUpdate( xref.getCvXrefQualifier() );
//            }
//            institutionPersister.saveOrUpdate( xref.getOwner() );
//        }
//
//        for ( Alias alias : intactObject.getAliases() ) {
//            if ( alias.getCvAliasType() != null ) {
//                saveOrUpdate( alias.getCvAliasType() );
//            }
//            institutionPersister.saveOrUpdate( alias.getOwner() );
//        }
//
//        for ( Annotation annotation : intactObject.getAnnotations() ) {
//            if ( annotation.getCvTopic() != null ) {
//                saveOrUpdate( annotation.getCvTopic() );
//            }
//            institutionPersister.saveOrUpdate( annotation.getOwner() );
//        }
    }

    /**
     * TODO: attempt identityXref(mi first and ia next)-cvType and then label-cvType
     */
    @Override
    protected CvObject fetchFromDataSource( CvObject intactObject ) {

        if( intactObject.getShortLabel().equals( "purified" ) ) {
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
        }

        // First search by psi-mi identifier
        CvDatabase psimi = getIntactContext().getDataContext().getDaoFactory()
                .getCvObjectDao( CvDatabase.class ).getByPsiMiRef( CvDatabase.PSI_MI_MI_REF );

        if ( psimi != null ) {
            CvXrefQualifier identity = getIntactContext().getDataContext().getDaoFactory()
                    .getCvObjectDao( CvXrefQualifier.class ).getByPsiMiRef( CvXrefQualifier.IDENTITY_MI_REF );

            Collection<Xref> xrefs = AnnotatedObjectUtils.searchXrefs( intactObject, psimi, identity );

            if ( !xrefs.isEmpty() ) {
                if ( xrefs.size() == 1 ) {
                    String mi = xrefs.iterator().next().getPrimaryId();
                    CvObject cvObject = getIntactContext().getDataContext().getDaoFactory()
                            .getCvObjectDao( intactObject.getClass() ).getByPsiMiRef( mi );
                    if ( cvObject != null ) {
                        return cvObject;
                    }
                }
            }
        } else {
            if ( log.isDebugEnabled() ) {
                log.debug( "Failed search by PSI-MI Xref as CvDatabase( psi-mi ) isn't in the database yet, searching by shortlabel..." );
            }
        }

        // Failed by MI, try by shortlabel
        return getIntactContext().getDataContext().getDaoFactory()
                .getCvObjectDao().getByShortLabel( intactObject.getClass(), intactObject.getShortLabel() );
    }
}