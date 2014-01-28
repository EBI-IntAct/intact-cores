package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.model.listener.BioactiveEntityAnnotationListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intact implementation of bioactive entity
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/14</pre>
 */
@Entity
@DiscriminatorValue( "bioactive_entity" )
@EntityListeners(value = {BioactiveEntityAnnotationListener.class})
public class IntactBioactiveEntity extends IntactMolecule implements BioactiveEntity{

    private Xref chebi;
    private Checksum smile;
    private Checksum standardInchi;
    private Checksum standardInchiKey;

    protected IntactBioactiveEntity(){
        super();
    }

    public IntactBioactiveEntity(String name, CvTerm type) {
        super(name, type);
    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactBioactiveEntity(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactBioactiveEntity(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactBioactiveEntity(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);

    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type, String uniqueChebi) {
        super(name, fullName, type);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    public IntactBioactiveEntity(String name, CvTerm type, Organism organism, String uniqueChebi) {
        super(name, type, organism);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    public IntactBioactiveEntity(String name, String fullName, CvTerm type, Organism organism, String uniqueChebi) {
        super(name, fullName, type, organism);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    public IntactBioactiveEntity(String name) {
        super(name);
    }

    public IntactBioactiveEntity(String name, String fullName) {
        super(name, fullName);
    }

    public IntactBioactiveEntity(String name, Organism organism) {
        super(name, organism);
    }

    public IntactBioactiveEntity(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactBioactiveEntity(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactBioactiveEntity(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactBioactiveEntity(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactBioactiveEntity(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);

    }

    public IntactBioactiveEntity(String name, String fullName, String uniqueChebi) {
        super(name, fullName);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    public IntactBioactiveEntity(String name, Organism organism, String uniqueChebi) {
        super(name, organism);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    public IntactBioactiveEntity(String name, String fullName, Organism organism, String uniqueChebi) {
        super(name, fullName, organism);
        if (uniqueChebi != null){
            setChebi(uniqueChebi);
        }
    }

    @Override
    /**
     * Return the first chebi identifier if provided, otherwise the first identifier in the list of identifiers
     */
    @Transient
    public Xref getPreferredIdentifier() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return chebi != null ? chebi : super.getPreferredIdentifier();
    }

    @Transient
    public String getChebi() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return chebi != null ? chebi.getId() : null;
    }

    //TODO fetch proper cv term
    public void setChebi(String id) {
        Collection<Xref> bioactiveEntityIdentifiers = getIdentifiers();

        // add new chebi if not null
        if (id != null){
            CvTerm chebiDatabase = IntactUtils.createMIDatabase(Xref.CHEBI, Xref.CHEBI_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old chebi if not null
            if (this.chebi != null){
                bioactiveEntityIdentifiers.remove(this.chebi);
            }
            this.chebi = new InteractorXref(chebiDatabase, id, identityQualifier);
            bioactiveEntityIdentifiers.add(this.chebi);
        }
        // remove all chebi if the collection is not empty
        else if (!bioactiveEntityIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(bioactiveEntityIdentifiers, Xref.CHEBI_MI, Xref.CHEBI);
            this.chebi = null;
        }
    }

    @Transient
    public String getSmile() {
        // initialise checksum if not done yet
        getChecksums();
        return smile != null ? smile.getValue() : null;
    }

    //TODO fetch proper cv term
    public void setSmile(String smile) {
        Collection<Checksum> bioactiveEntityChecksums = getChecksums();

        if (smile != null){
            CvTerm smileMethod = IntactUtils.createMITopic(Checksum.SMILE, Checksum.SMILE_MI);
            // first remove old smile
            if (this.smile != null){
                bioactiveEntityChecksums.remove(this.smile);
            }
            this.smile = new InteractorChecksum(smileMethod, smile);
            bioactiveEntityChecksums.add(this.smile);
        }
        // remove all smiles if the collection is not empty
        else if (!bioactiveEntityChecksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(bioactiveEntityChecksums, Checksum.SMILE_MI, Checksum.SMILE);
            this.smile = null;
        }
    }

    @Transient
    public String getStandardInchiKey() {
        return standardInchiKey != null ? standardInchiKey.getValue() : null;
    }

    //TODO fetch proper cv term
    public void setStandardInchiKey(String key) {
        Collection<Checksum> bioactiveEntityChecksums = getChecksums();

        if (key != null){
            CvTerm inchiKeyMethod = IntactUtils.createMITopic(Checksum.STANDARD_INCHI_KEY, Checksum.STANDARD_INCHI_KEY_MI);
            // first remove old standard inchi key
            if (this.standardInchiKey != null){
                bioactiveEntityChecksums.remove(this.standardInchiKey);
            }
            this.standardInchiKey = new InteractorChecksum(inchiKeyMethod, key);
            bioactiveEntityChecksums.add(this.standardInchiKey);
        }
        // remove all standard inchi keys if the collection is not empty
        else if (!bioactiveEntityChecksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(bioactiveEntityChecksums, Checksum.STANDARD_INCHI_KEY_MI, Checksum.STANDARD_INCHI_KEY);
            this.standardInchiKey = null;
        }
    }

    @Transient
    public String getStandardInchi() {
        return standardInchi != null ? standardInchi.getValue() : null;
    }

    //TODO fetch proper cv term
    public void setStandardInchi(String inchi) {
        Collection<Checksum> bioactiveEntityChecksums = getChecksums();

        if (inchi != null){
            CvTerm inchiMethod = IntactUtils.createMITopic(Checksum.INCHI, Checksum.INCHI_MI);;
            // first remove standard inchi
            if (this.standardInchi != null){
                bioactiveEntityChecksums.remove(this.standardInchi);
            }
            this.standardInchi = new InteractorChecksum(inchiMethod, inchi);
            bioactiveEntityChecksums.add(this.standardInchi);
        }
        // remove all standard inchi if the collection is not empty
        else if (!bioactiveEntityChecksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(bioactiveEntityChecksums, Checksum.INCHI_MI, Checksum.INCHI);
            this.standardInchi = null;
        }
    }

    @Override
    public String toString() {
        return chebi != null ? chebi.getId() : (standardInchiKey != null ? standardInchiKey.getValue() : (smile != null ? smile.getValue() : (standardInchi != null ? standardInchi.getValue() : super.toString())));
    }

    private void processAddedChecksumEvent(Checksum added) {
        // the added checksum is standard inchi key and it is not the current standard inchi key
        if (standardInchiKey == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.STANDARD_INCHI_KEY_MI, Checksum.STANDARD_INCHI_KEY)){
            // the standard inchi key is not set, we can set the standard inchi key
            standardInchiKey = added;
        }
        else if (smile == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.SMILE_MI, Checksum.SMILE)){
            // the smile is not set, we can set the smile
            smile = added;
        }
        else if (standardInchi == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.INCHI_MI, Checksum.INCHI)){
            // the standard inchi is not set, we can set the standard inchi
            standardInchi = added;
        }
    }

    private void processRemovedChecksumEvent(Checksum removed) {
        // the removed identifier is standard inchi key
        if (standardInchiKey != null && standardInchiKey.equals(removed)){
            standardInchiKey = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.STANDARD_INCHI_KEY_MI, Checksum.STANDARD_INCHI_KEY);
        }
        else if (smile != null && smile.equals(removed)){
            smile = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.SMILE_MI, Checksum.SMILE);
        }
        else if (standardInchi != null && standardInchi.equals(removed)){
            standardInchi = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.INCHI_MI, Checksum.INCHI);
        }
    }

    private void clearPropertiesLinkedToChecksums() {
        standardInchiKey = null;
        standardInchi = null;
        smile = null;
    }

    @Override
    protected void processAddedIdentifierEvent(Xref added) {
        // the added identifier is chebi and it is not the current chebi identifier
        if (chebi != added && XrefUtils.isXrefFromDatabase(added, Xref.CHEBI_MI, Xref.CHEBI)){
            // the current chebi identifier is not identity, we may want to set chebiIdentifier
            if (!XrefUtils.doesXrefHaveQualifier(chebi, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the chebi identifier is not set, we can set the chebi identifier
                if (chebi == null){
                    chebi = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    chebi = added;
                }
                // the added xref is secondary object and the current chebi is not a secondary object, we reset chebi identifier
                else if (!XrefUtils.doesXrefHaveQualifier(chebi, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    chebi = added;
                }
            }
        }
    }

    @Override
    protected void processRemovedIdentifierEvent(Xref removed) {
        // the removed identifier is chebi
        if (chebi != null && chebi.equals(removed)){
            chebi = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.CHEBI_MI, Xref.CHEBI);
        }
    }

    @Override
    protected void clearPropertiesLinkedToIdentifiers() {
        chebi = null;
    }

    @Override
    protected void initialiseChecksums() {
        super.setPersistentChecksums(new BioactiveEntityChecksumList(null));
        for (Checksum check : super.getChecksums()){
            processAddedChecksumEvent(check);
        }
    }

    @Override
    protected void setPersistentChecksums(Collection<Checksum> checksums) {
        super.setPersistentChecksums(new BioactiveEntityChecksumList(checksums));
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(BioactiveEntity.BIOACTIVE_ENTITY, BioactiveEntity.BIOACTIVE_ENTITY_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    @Override
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.SmallMoleculeImpl";
    }

    protected class BioactiveEntityChecksumList extends PersistentChecksumList {
        public BioactiveEntityChecksumList(Collection<Checksum> checksums){
            super(checksums);
        }

        @Override
        public boolean add(Checksum xref) {
            if(super.add(xref)){
                processAddedChecksumEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedChecksumEvent((Checksum) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Checksum> existingObject = new ArrayList<Checksum>(this);

            boolean removed = false;
            for (Checksum o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToChecksums();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Checksum added) {
            return false;
        }

        @Override
        protected Checksum processOrWrapElementToAdd(Checksum added) {
            return added;
        }
    }
}
