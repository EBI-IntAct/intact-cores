package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import uk.ac.ebi.intact.jami.model.SequenceChunk;
import uk.ac.ebi.intact.jami.model.listener.PolymerSequenceListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * IntAct implementation of polymer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "polymer" )
@EntityListeners(value = {PolymerSequenceListener.class})
public class IntactPolymer extends IntactMolecule implements Polymer{

    private String sequence;

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private List<SequenceChunk> sequenceChunks;

    protected IntactPolymer(){
        super();
    }

    public IntactPolymer(String name, CvTerm type) {
        super(name, type);
    }

    public IntactPolymer(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactPolymer(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactPolymer(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactPolymer(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactPolymer(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactPolymer(String name) {
        super(name);
    }

    public IntactPolymer(String name, String fullName) {
        super(name, fullName);
    }

    public IntactPolymer(String name, Organism organism) {
        super(name, organism);
    }

    public IntactPolymer(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactPolymer(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactPolymer(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactPolymer(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactPolymer(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    @Lob
    @Column(name = "sequence")
    public String getSequence() {
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Deprecated
    /**
     * @deprecated look at checksums instead
     */
    public String getCrc64() {
        Checksum checksum = ChecksumUtils.collectFirstChecksumWithMethod(getPersistentChecksums(), null, "crc64");
        return checksum != null ? checksum.getValue() : null;
    }

    @Deprecated
    /**
     * @deprecated look at checksums instead. Only kept for bacward compatibility with intact-core
     */
    public void setCrc64( String crc64 ) {
        if (crc64 == null){
           ChecksumUtils.removeAllChecksumWithMethod(getPersistentChecksums(), null, "crc64");
        }
        else{
            Checksum checksum = ChecksumUtils.collectFirstChecksumWithMethod(getPersistentChecksums(), null, "crc64");
            if (checksum == null){
                InteractorChecksum crc64Checksum = new InteractorChecksum(IntactUtils.createMITopic("crc64", null), crc64);
                getPersistentChecksums().add(crc64Checksum);
            }
            else if (checksum instanceof InteractorChecksum){
                ((InteractorChecksum)checksum).setValue(crc64);
            }
        }
    }

    @OneToMany( orphanRemoval = true, cascade = {CascadeType.ALL})
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @IndexColumn( name = "sequence_index" )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Deprecated
    /**
     * @deprecated only for backward compatibility with intact-core
     */
    public List<SequenceChunk> getSequenceChunks() {
        return sequenceChunks;
    }

    @Override
    @Column(name = "objclass", nullable = false, insertable = false, updatable = false)
    @NotNull
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.PolymerImpl";
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Polymer.POLYMER, Polymer.POLYMER_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    protected void setSequenceChunks( List<SequenceChunk> sequenceChunks ) {
        this.sequenceChunks = sequenceChunks;
    }
}
