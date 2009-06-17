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
 * Represents a protein or peptide. The object should only contain
 * the minimum information relevant for IntAct, most information
 * should only be retrieved by the xref.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.ProteinImpl" )
@EditorTopic( name = "Protein" )
public class ProteinImpl extends PolymerImpl implements Protein, Editable {

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public ProteinImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Protein instance. A valid Protein must have at least an onwer, a
     * short label to refer to it and a biological source specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     *
     * @param owner      The 'owner' of the Protein (what does this mean in real terms??)
     * @param source     The biological source of the Protein observation
     * @param shortLabel A memorable label used to refer to the Protein instance
     *
     * @deprecated Please use {@link #ProteinImpl(Institution,BioSource,String,CvInteractorType)}
     *             instead.
     */
    @Deprecated
    public ProteinImpl( Institution owner, BioSource source, String shortLabel ) {
        //TODO Q: what about crc64, fullName, formOf - they are all indexed...
        //ALSO..A Protein can have an interaction type IF it is an Interactor,
        //but if it isn't then it doesn't need an interaction type. This does not
        //match with the classes - Interaction has a type, not Interactor...

        //super call sets up a valid AnnotatedObject (should an Interactor be better defined?)
        this( owner, source, shortLabel, null );
    }

    /**
     * A valid ProteinImpl must have at least an onwer, a biological source, a
     * short label to refer to it and an interactor type specified.
     *
     * @param owner      The 'owner' of this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel A memorable label used to refer to this instance
     * @param type       The interactor type
     */
    public ProteinImpl( Institution owner, BioSource source, String shortLabel, CvInteractorType type ) {
        super( owner, source, shortLabel, type );
    }

}
