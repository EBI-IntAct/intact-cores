/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;

import java.util.Collection;

/**
 * Xref DAO
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface XrefDao<X extends AbstractIntactXref> extends IntactBaseDao<X> {

    public Collection<X> getByPrimaryId(String primaryId, String version);

    public Collection<X> getByPrimaryIdLike(String primaryId, String version);

    public Collection<X> getByDatabase(String dbName, String dbMI);

    public Collection<X> getByQualifier(String qualifierName, String qualifierMI);

    public Collection<X> getByDatabaseAndQualifier(String dbName, String dbMI, String qualifierName, String qualifierMI);

    public Collection<X> getByDatabaseAndPrimaryId(String dbName, String dbMI, String id, String version);

    public Collection<X> getByDatabaseAndPrimaryIdLike(String dbName, String dbMI, String id, String version);

    public Collection<X> getByQualifierAndPrimaryId(String qualifierName, String qualifierMI, String id, String version);

    public Collection<X> getByQualifierAndPrimaryIdLike(String qualifierName, String qualifierMI, String id, String version);

    public Collection<X> getByDatabasePrimaryIdAndQualifier(String dbName, String dbMI, String id, String version, String qualifierName, String qualifierMI);

    public Collection<X> getByDatabasePrimaryIdLikeAndQualifier(String dbName, String dbMI, String id, String version, String qualifierName, String qualifierMI);

}
