/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.BasicObjectImpl
 */
public interface BasicObject extends IntactObject {

    public void setEvidences( Collection<Evidence> someEvidence );

    public Collection<Evidence> getEvidences();

    public void addEvidence( Evidence evidence );

    public void removeEvidence( Evidence evidence );

    public Institution getOwner();

    public void setOwner( Institution institution );

    public String getOwnerAc();

    public void setOwnerAc( String ac );

    public String toString();

}
