/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.config.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.AbstractAuxiliaryDatabaseObject;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * AuxiliaryDatabaseObject for hibernate, used to include sequences in the DDL
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SequenceAuxiliaryDatabaseObject extends AbstractAuxiliaryDatabaseObject{

    private String sequenceName;
    private int initialValue;

    public SequenceAuxiliaryDatabaseObject(String sequenceName, int initialValue) {
        super(new HashSet<String>());

        this.initialValue = initialValue;
        this.sequenceName = sequenceName;
    }

    public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) throws HibernateException {
        return sqlCreateString(dialect);
    }

    public String sqlCreateString(Dialect dialect) {
        String sql;
        if (dialect.supportsPooledSequences()) {
            String[] createSqls = dialect.getCreateSequenceStrings(sequenceName, initialValue, 1);
            sql = StringUtils.join(createSqls, "; ");
        } else {
            // for databases like postgres, we cannot use the above method and the method we need is protected
            // in the Dialect class (public in the subclass)
            String methodName = "getCreateSequenceString";
            try {
                final Method method = dialect.getClass().getMethod(methodName, String.class);
                sql = (String) method.invoke(dialect, sequenceName);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return sql;
    }

    public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
        String[] dropSqls = dialect.getDropSequenceStrings(sequenceName);
        return StringUtils.join(dropSqls, "; ");
    }

    @Override
    public boolean appliesToDialect(Dialect dialect) {
        return dialect.supportsSequences();
    }
}
