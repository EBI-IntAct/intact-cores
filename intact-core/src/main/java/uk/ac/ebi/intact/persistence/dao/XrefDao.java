/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.Xref;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface XrefDao<T extends Xref> extends IntactObjectDao<T> {

    public Collection<T> getByPrimaryId( String primaryId );

    public Collection<T> getByPrimaryId( String primaryId, boolean ignoreCase );

    public Collection<T> getByPrimaryIdLike( String primaryId );

    public Collection<T> getByParentAc( String parentAc );

    public Collection<T> getByParentAc( String parentAc, boolean ignoreCase );

}
