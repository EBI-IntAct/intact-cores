/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;

import java.util.List;
import java.util.Map;

/**
 * What a Protein DAO can do.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface ProteinDao extends InteractorDao<ProteinImpl> {

    /**
     * Gets the AC of the identity Xref
     */
    String getIdentityXrefByProteinAc( String proteinAc );

    /**
     * Gets the AC of the identity Xref
     */
    String getUniprotAcByProteinAc( String proteinAc );

    /**
     * Obtains the template of the url to be used in search.
     * Uses the uniprot Xref and then get hold of its annotation 'search-url'
     */
    List<String> getUniprotUrlTemplateByProteinAc( String proteinAc );

    Map<String, Integer> getPartnersCountingInteractionsByProteinAc( String proteinAc );

    Map<String, List<String>> getPartnersWithInteractionAcsByProteinAc( String proteinAc );

    /**
     * Returns the protein id of the parners
     *
     * @param proteinAc
     *
     * @return
     */
    List<String> getPartnersUniprotIdsByProteinAc( String proteinAc );

    @Deprecated
    Integer countPartnersByProteinAc( String proteinAc );

    List<ProteinImpl> getUniprotProteins( Integer firstResult, Integer maxResults );

    List<ProteinImpl> getUniprotProteinsInvolvedInInteractions( Integer firstResult, Integer maxResults );

    Integer countUniprotProteins();

    Integer countUniprotProteinsInvolvedInInteractions();

    List<ProteinImpl> getByUniprotId( String uniprotId );

    /**
     * Searches and return all splice variant of the given protein.
     *
     * @param protein the protein of which we want the splice variants.
     *
     * @return a non null list of intact proteins.
     */
    List<ProteinImpl> getSpliceVariants( Protein protein );

    /**
     * Given a splice variant, search and return the corresponding master protein.
     *
     * @param spliceVariant the splice variant we are searching the master of.
     *
     * @return a master protein, may return null but should be considered as a database inconsistency.
     */
    ProteinImpl getSpliceVariantMasterProtein( Protein spliceVariant );
}
