package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.NucleicAcid;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Transient;
import java.util.Collection;

/**
 * Intact implementation of nucleic acid
 *
 * Only the crc64 is persisted as a checksum
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue( "nucleic_acid" )
@Where(clause = "category = 'nucleic_acid'")
public class IntactNucleicAcid extends IntactPolymer implements NucleicAcid{
    private transient Xref ddbjEmblGenbank;
    private transient Xref refseq;

    protected IntactNucleicAcid() {
        super();
    }

    public IntactNucleicAcid(String name, CvTerm type) {
        super(name, type);
    }

    public IntactNucleicAcid(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactNucleicAcid(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactNucleicAcid(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactNucleicAcid(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactNucleicAcid(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactNucleicAcid(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactNucleicAcid(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactNucleicAcid(String name) {
        super(name);
    }

    public IntactNucleicAcid(String name, String fullName) {
        super(name, fullName);
    }

    public IntactNucleicAcid(String name, Organism organism) {
        super(name, organism);
    }

    public IntactNucleicAcid(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactNucleicAcid(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactNucleicAcid(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactNucleicAcid(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactNucleicAcid(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    /**
     * The first ddbjEmblGenbank if provided, then the first refseq identifier if provided, otherwise the first identifier in the list
     * @return
     */
    @Override
    @Transient
    public Xref getPreferredIdentifier() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return ddbjEmblGenbank != null ? ddbjEmblGenbank : (refseq != null ? refseq : super.getPreferredIdentifier());
    }

    @Transient
    public String getDdbjEmblGenbank() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.ddbjEmblGenbank != null ? this.ddbjEmblGenbank.getId() : null;
    }

    public void setDdbjEmblGenbank(String id) {
        Collection<Xref> nucleicAcidIdentifiers = getIdentifiers();

        // add new ddbj/embl/genbank if not null
        if (id != null){
            CvTerm ddbjEmblGenbankDatabase = IntactUtils.createMIDatabase(Xref.DDBJ_EMBL_GENBANK, Xref.DDBJ_EMBL_GENBANK_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old ddbj/embl/genbank if not null
            if (this.ddbjEmblGenbank != null){
                nucleicAcidIdentifiers.remove(this.ddbjEmblGenbank);
            }
            this.ddbjEmblGenbank = new InteractorXref(ddbjEmblGenbankDatabase, id, identityQualifier);
            nucleicAcidIdentifiers.add(this.ddbjEmblGenbank);
        }
        // remove all ddbj/embl/genbank if the collection is not empty
        else if (!nucleicAcidIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(nucleicAcidIdentifiers, Xref.DDBJ_EMBL_GENBANK_MI, Xref.DDBJ_EMBL_GENBANK);
            this.ddbjEmblGenbank = null;
        }
    }

    @Transient
    public String getRefseq() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.refseq != null ? this.refseq.getId() : null;
    }

    public void setRefseq(String id) {
        Collection<Xref> nucleicAcidIdentifiers = getIdentifiers();

        // add new refseq if not null
        if (id != null){
            CvTerm refseqDatabase = IntactUtils.createMIDatabase(Xref.REFSEQ, Xref.REFSEQ_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove refseq if not null
            if (this.refseq!= null){
                nucleicAcidIdentifiers.remove(this.refseq);
            }
            this.refseq = new InteractorXref(refseqDatabase, id, identityQualifier);
            nucleicAcidIdentifiers.add(this.refseq);
        }
        // remove all ensembl genomes if the collection is not empty
        else if (!nucleicAcidIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(nucleicAcidIdentifiers, Xref.REFSEQ_MI, Xref.REFSEQ);
            this.refseq = null;
        }
    }

    @Override
    protected String generateObjClass() {
        return "uk.ac.ebi.intact.model.NucleicAcidImpl";
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(NucleicAcid.NULCEIC_ACID, NucleicAcid.NULCEIC_ACID_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    protected void processAddedIdentifiersEvent(Xref added) {
        // the added identifier is ddbj/embl/genbank and it is not the current ddbj/embl/genbank identifier
        if (ddbjEmblGenbank != added && XrefUtils.isXrefFromDatabase(added, Xref.DDBJ_EMBL_GENBANK_MI, Xref.DDBJ_EMBL_GENBANK)){
            // the current ddbj/embl/genbank identifier is not identity, we may want to set ddbj/embl/genbank Identifier
            if (!XrefUtils.doesXrefHaveQualifier(ddbjEmblGenbank, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the ddbj/embl/genbank identifier is not set, we can set the ddbj/embl/genbank identifier
                if (ddbjEmblGenbank == null){
                    ddbjEmblGenbank = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    ddbjEmblGenbank = added;
                }
                // the added xref is secondary object and the current ddbj/embl/genbank identifier is not a secondary object, we reset ddbj/embl/genbank identifier
                else if (!XrefUtils.doesXrefHaveQualifier(ddbjEmblGenbank, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    ddbjEmblGenbank = added;
                }
            }
        }
        // the added identifier is refseq id and it is not the current refseq id
        else if (refseq != added && XrefUtils.isXrefFromDatabase(added, Xref.REFSEQ_MI, Xref.REFSEQ)){
            // the current refseq id is not identity, we may want to set refseq id
            if (!XrefUtils.doesXrefHaveQualifier(refseq, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the refseq id is not set, we can set the refseq id
                if (refseq == null){
                    refseq = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    refseq = added;
                }
                // the added xref is secondary object and the current refseq id is not a secondary object, we reset refseq id
                else if (!XrefUtils.doesXrefHaveQualifier(refseq, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    refseq = added;
                }
            }
        }
    }

    protected void processRemovedIdentifierEvent(Xref removed) {
        if (ddbjEmblGenbank != null && ddbjEmblGenbank.equals(removed)){
            ddbjEmblGenbank = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.DDBJ_EMBL_GENBANK_MI, Xref.DDBJ_EMBL_GENBANK);
        }
        else if (refseq != null && refseq.equals(removed)){
            refseq = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.REFSEQ_MI, Xref.REFSEQ);
        }
    }

    protected void clearPropertiesLinkedToIdentifiers() {
        ddbjEmblGenbank = null;
        refseq = null;
    }
}
