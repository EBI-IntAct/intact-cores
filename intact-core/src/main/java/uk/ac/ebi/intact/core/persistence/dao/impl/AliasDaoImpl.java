/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.AliasDao;
import uk.ac.ebi.intact.model.Alias;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@Repository
@Transactional(readOnly = true)
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
public class AliasDaoImpl<T extends Alias> extends IntactObjectDaoImpl<T> implements AliasDao<T> {

    public AliasDaoImpl() {
        super((Class<T>) Alias.class);
    }

    public AliasDaoImpl(Class<T> aliasClass, EntityManager entityManager, IntactSession intactSession) {
        super(aliasClass, entityManager, intactSession);
    }

    public Collection<T> getByNameLike(String name) {
        return getByPropertyNameLike("name", name);
    }

    public Collection<T> getByParentAc(String parentAc) {
        return getColByPropertyName("parentAc", parentAc);
    }

    public Collection<T> getByParentAc(String parentAc, boolean ignoreCase) {
        return getColByPropertyName("parentAc", parentAc, ignoreCase);
    }
}
