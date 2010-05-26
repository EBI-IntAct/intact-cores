package uk.ac.ebi.intact.model;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Deprecated
public interface BasicObject extends OwnedObject {

    @Deprecated
    Institution getOwner();

    @Deprecated
    void setOwner( Institution institution );
}
