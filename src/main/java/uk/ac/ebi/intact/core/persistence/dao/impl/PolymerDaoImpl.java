/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.PolymerDao;
import uk.ac.ebi.intact.model.PolymerImpl;
import uk.ac.ebi.intact.model.SequenceChunk;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
@Repository
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
@Transactional
@SuppressWarnings( {"unchecked"} )
public class PolymerDaoImpl<T extends PolymerImpl> extends InteractorDaoImpl<T> implements PolymerDao<T> {

    public PolymerDaoImpl() {
        super((Class<T>) PolymerImpl.class);
    }

    public PolymerDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager);
    }

    public PolymerDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    public String getSequenceByPolymerAc( String polymerAc ) {
        List<String> seqChunks = getSession().createCriteria( SequenceChunk.class )
                .createAlias( "parent", "p" )
                .add( Restrictions.eq( "p.ac", polymerAc ) )
                .addOrder( Order.asc( "sequenceIndex" ) )
                .setProjection( Property.forName( "sequenceChunk" ) )
                .list();

        StringBuffer sb = new StringBuffer( seqChunks.size() * PolymerImpl.MAX_SEQ_LENGTH_PER_CHUNK );

        for ( String seqChunk : seqChunks ) {
            sb.append( seqChunk );
        }

        return sb.toString();
    }

    public List<T> getByCrcAndTaxId(String crc, String taxId) {
        if (crc == null) throw new NullPointerException("crc is null");
        if (taxId == null) throw new NullPointerException("taxId is null");

        Query query = getEntityManager().createQuery("select p from PolymerImpl p " +
                                                     "where p.crc64 = :crc64 " +
                                                     "and p.bioSource.taxId = :taxId");
        query.setParameter("crc64", crc);
        query.setParameter("taxId", taxId);

        return query.getResultList();
    }
}
