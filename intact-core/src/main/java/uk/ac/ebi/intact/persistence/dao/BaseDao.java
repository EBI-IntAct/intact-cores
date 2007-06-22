/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;

import java.sql.SQLException;

/**
 * Base DAO, which any DAO has to implement
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface BaseDao<T extends Object> {

    T getSession();

    void flushCurrentSession();

    String getDbName() throws SQLException;

    String getDbUserName() throws SQLException;

}
