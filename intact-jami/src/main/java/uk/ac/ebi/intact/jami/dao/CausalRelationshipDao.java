/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;

import java.util.Collection;

/**
 * DAO for causal relationships
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface CausalRelationshipDao extends IntactBaseDao<IntactCausalRelationship> {

    public Collection<IntactCausalRelationship> getByRelationType(String typeName, String typeMI);

    public Collection<IntactCausalRelationship> getByParentAc(String parentAc);

    public Collection<IntactCausalRelationship> getByTargetAc(String parentAc);

    public Collection<IntactCausalRelationship> getByParentAndTargetAc(String parentAc, String targetAc);
}
