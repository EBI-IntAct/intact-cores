/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.jami.model;

import uk.ac.ebi.intact.jami.model.audit.Auditable;

/**
 * Interface for Intact objects having a generated AC for backward compatibility
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface BackwardCompatibleObjectWithAc extends Auditable {

    /**
     * Sets the primary ac of this object
     * @param ac
     */
    public void setAc(String ac);
}
