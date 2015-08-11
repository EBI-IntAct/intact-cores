package uk.ac.ebi.intact.jami.model.extension.binary;

import psidev.psi.mi.jami.binary.impl.AbstractBinaryInteraction;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;

import java.util.Date;

/**
 * Abstract class for Intact BinaryInteraction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/07/13</pre>
 */

public abstract class AbstractIntactBinaryInteraction<T extends Participant> extends AbstractBinaryInteraction<T> implements IntactPrimaryObject{
    /**
     * The curator who has last edited the object.
     */
    public String updator;

    /**
     * The curator who has created the edited object
     */
    public String creator;

    /**
     * Creation date of an object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    private Date created;

    /**
     * The last update of the object. The type is java.sql.Date, not java.util.Data, for database compatibility.
     */
    private Date updated;

    private String ac;

    private Xref acRef;

    public AbstractIntactBinaryInteraction() {
        super();
    }

    public AbstractIntactBinaryInteraction(String shortName) {
        super(shortName);
    }

    public AbstractIntactBinaryInteraction(String shortName, CvTerm type) {
        super(shortName, type);
    }

    public AbstractIntactBinaryInteraction(T participantA, T participantB) {
        super(participantA, participantB);
    }

    public AbstractIntactBinaryInteraction(String shortName, T participantA, T participantB) {
        super(shortName, participantA, participantB);
    }

    public AbstractIntactBinaryInteraction(String shortName, CvTerm type, T participantA, T participantB) {
        super(shortName, type, participantA, participantB);
    }

    public AbstractIntactBinaryInteraction(CvTerm complexExpansion) {
        super(complexExpansion);
    }

    public AbstractIntactBinaryInteraction(String shortName, CvTerm type, CvTerm complexExpansion) {
        super(shortName, type, complexExpansion);
    }

    public AbstractIntactBinaryInteraction(T participantA, T participantB, CvTerm complexExpansion) {
        super(participantA, participantB, complexExpansion);
    }

    public AbstractIntactBinaryInteraction(String shortName, T participantA, T participantB, CvTerm complexExpansion) {
        super(shortName, participantA, participantB, complexExpansion);
    }

    public AbstractIntactBinaryInteraction(String shortName, CvTerm type, T participantA, T participantB, CvTerm complexExpansion) {
        super(shortName, type, participantA, participantB, complexExpansion);
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getAc() {
        return ac;
    }

    @Override
    public Date getUpdatedDate() {
        return getUpdated();
    }

    @Override
    public void setUpdatedDate(Date updated) {
        setUpdated(updated);
    }

    @Override
    public Date getCreatedDate() {
        return getCreated();
    }

    @Override
    public void setCreatedDate(Date created) {
        setCreated(created);
    }

    public void setAc(String ac) {
        this.ac = ac;
        // only if identifiers are initialised
        if (this.acRef != null && !this.acRef.getId().equals(ac)){
            // we don't want to create a persistent xref
            Xref newRef = new DefaultXref(this.acRef.getDatabase(), ac, this.acRef.getQualifier());
            getIdentifiers().remove(acRef);
            this.acRef = newRef;
            getIdentifiers().add(acRef);
        }
    }
}
