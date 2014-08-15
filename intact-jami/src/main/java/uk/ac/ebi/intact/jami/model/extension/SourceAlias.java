package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of alias for source/institution
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_institution_alias" )
public class SourceAlias extends AbstractIntactAlias{

    protected SourceAlias() {
    }

    public SourceAlias(CvTerm type, String name) {
        super(type, name);
    }

    public SourceAlias(String name) {
        super(name);
    }
}
