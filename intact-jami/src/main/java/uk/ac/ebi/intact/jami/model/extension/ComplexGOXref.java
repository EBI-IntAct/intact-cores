package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.CvTermUtils;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Implementation of xref for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@DiscriminatorValue( "go" )
public class ComplexGOXref extends InteractorXref{

    private CvTerm evidenceType;
    private String pubmed;

    protected ComplexGOXref() {
    }

    public ComplexGOXref(String id, CvTerm qualifier) {
        super(IntactUtils.createMIDatabase(Xref.GO, Xref.GO_MI), id, qualifier);
    }

    public ComplexGOXref(String id, String version, CvTerm qualifier) {
        super(IntactUtils.createMIDatabase(Xref.GO, Xref.GO_MI), id, version, qualifier);
    }

    public ComplexGOXref(String id, String version) {
        super(IntactUtils.createMIDatabase(Xref.GO, Xref.GO_MI), id, version);
    }

    public ComplexGOXref(String id) {
        super(IntactUtils.createMIDatabase(Xref.GO, Xref.GO_MI), id);
    }

    @Override
    public void setDatabase(CvTerm cvDatabase) {
        if (cvDatabase != null && !CvTermUtils.isCvTerm(cvDatabase, Xref.GO_MI, Xref.GO)){
           throw new IllegalArgumentException("A Complex GO cross reference can only have GO as a database.");
        }
        super.setDatabase(cvDatabase);
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "evidencetype_ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(CvTerm evidenceType) {
        this.evidenceType = evidenceType;
    }


    @Column(name = "pubmed")
    @Size( max = IntactUtils.MAX_ID_LEN )
    public String getPubmed() {
        return pubmed;
    }

    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
    }
}
