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
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
@Entity
@Table( name = "ia_biosource_alias" )
public class BioSourceAlias extends Alias {

    private static final Log log = LogFactory.getLog( BioSourceAlias.class );

    public BioSourceAlias() {
    }

    public BioSourceAlias( Institution anOwner, BioSource bioSource, CvAliasType cvAliasType, String name ) {
        super( anOwner, bioSource, cvAliasType, name );
    }

    @ManyToOne( targetEntity = BioSource.class )
    @JoinColumn( name = "parent_ac" )
    public AnnotatedObject getParent() {
        return super.getParent();
    }

    @Column( name = "parent_ac", insertable = false, updatable = false )
    public String getParentAc() {
        return super.getParentAc();
    }

}
