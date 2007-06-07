/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Represents an external database and contains all the information necessary to retrieve an object from it by a given
 * primary identifier.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvDatabase" )
@EditorTopic
public class CvDatabase extends CvObject implements Editable {
    // Constants
    ////////////////////////////////
    public static final String ENTREZ_GENE = "entrez gene/locuslink";
    public static final String ENTREZ_GENE_MI_REF = "MI:0475";

    public static final String ENSEMBL = "ensembl";
    public static final String ENSEMBL_MI_REF = "MI:0476";

    public static final String DDBG = "ddbj-embl-genbank";
    public static final String DDBG_MI_REF = "MI:0475";

    public static final String CABRI = "cabri";
    public static final String CABRI_MI_REF = "MI:0246";

    public static final String CHEBI = "chebi";
    public static final String CHEBI_MI_REF = "MI:0474";

    public static final String FLYBASE = "flybase";
    public static final String FLYBASE_MI_REF = "MI:0478";

    public static final String GO = "go";
    public static final String GO_MI_REF = "MI:0448";

    public static final String HUGE = "huge";
    public static final String HUGE_MI_REF = "MI:0249";

    public static final String INTACT = "intact";
    public static final String INTACT_MI_REF = "MI:0469";

    public static final String INTERPRO = "interpro";
    public static final String INTERPRO_MI_REF = "MI:0449";

    public static final String NEWT = "newt";
    public static final String NEWT_MI_REF = "MI:0247";

    public static final String REACTOME_COMPLEX = "reactome complex";
    public static final String REACTOME_COMPLEX_PSI_REF = "MI:0244";

    public static final String REACTOME_PROTEIN = "reactome protein";
    public static final String REACTOME_PROTEIN_PSI_REF = "MI:0245";

    public static final String PUBMED = "pubmed";
    public static final String PUBMED_MI_REF = "MI:0446";

    public static final String PSI_MI = "psi-mi";
    public static final String PSI_MI_MI_REF = "MI:0488";

    public static final String RESID = "resid";
    public static final String RESID_MI_REF = "MI:0248";

    public static final String SGD = "sgd";
    public static final String SGD_MI_REF = "MI:0484";

    public static final String SO = "so";
    public static final String SO_MI_REF = "MI:0601";

    public static final String UNIPROT = "uniprotkb";
    public static final String UNIPROT_MI_REF = "MI:0486";

    public static final String UNIPARC = "uniparc";
    public static final String UNIPARC_MI_REF = "MI:0485";

    public static final String REFSEQ = "refseq";
    public static final String REFSEQ_MI_REF = "MI:0481";

    public static final String IMEX = "imex";
    public static final String IMEX_MI_REF = "MI:0670";

    public static final String MINT = "mint";
    public static final String MINT_MI_REF = "MI:0471";

    public static final String BIND = "bind";
    public static final String BIND_MI_REF = "MI:0462";

    public static final String DIP = "dip";
    public static final String DIP_MI_REF = "MI:0465";

    public static final String MIPS = "cygd";
    public static final String MIPS_MI_REF = "MI:0464";

    public static final String PDB = "pdb";
    public static final String PDB_MI_REF = "MI:0460";


    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     */
    public CvDatabase() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvDatabase instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvDatabase
     * @param owner      The Institution which owns this CvDatabase
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvDatabase( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvDatabase




