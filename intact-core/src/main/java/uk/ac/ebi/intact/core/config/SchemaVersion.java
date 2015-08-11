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
package uk.ac.ebi.intact.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tool to deal with the schema version in the database. Stored in the ia_db_info table. This class
 * specifies the schema version required
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Sep-2006</pre>
 */
public class SchemaVersion {

    private static final Log log = LogFactory.getLog( SchemaVersion.class );

    private Integer major;
    private Integer minor;
    private Integer build;

    public SchemaVersion( Integer major, Integer minor, Integer build ) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    /**
     * Creates a SchemaVersion using the String provided
     *
     * @param version the version to parse
     *
     * @return the SchemaVersion instance
     */
    public static SchemaVersion parse( String version ) {
        SchemaVersion schemaVersion = null;

        Pattern p = Pattern.compile( "(\\d+)\\.(\\d+)\\.(\\d+)" );
        Matcher m = p.matcher( version );

        if ( m.find() ) {
            int major = Integer.parseInt( m.group( 1 ) );
            int minor = Integer.parseInt( m.group( 2 ) );
            int build = Integer.parseInt( m.group( 3 ) );

            schemaVersion = new SchemaVersion( major, minor, build );
        } else {
            throw new IntactException( "A schema version must follow this pattern: \\d+\\.\\d+\\.\\d+" );

        }

        return schemaVersion;
    }

    /**
     * Checks whether the version of the instance is compatible with the provided version
     *
     * @param schemaVersion the schema version to compare with
     *
     * @return if true, the schemas are compatible
     */
    public boolean isCompatibleWith( SchemaVersion schemaVersion ) {
        if ( getMajor().intValue() != schemaVersion.getMajor().intValue() ) {
            return false;
        } else if ( getMinor().intValue() != schemaVersion.getMinor().intValue() ) {
            return false;
        } else if ( getBuild() < schemaVersion.getBuild() ) {
            return false;
        }
        return true;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor( Integer major ) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor( Integer minor ) {
        this.minor = minor;
    }

    public Integer getBuild() {
        return build;
    }

    public void setBuild( Integer build ) {
        this.build = build;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        SchemaVersion that = ( SchemaVersion ) o;

        if ( build != null ? !build.equals( that.build ) : that.build != null ) {
            return false;
        }
        if ( major != null ? !major.equals( that.major ) : that.major != null ) {
            return false;
        }
        if ( minor != null ? !minor.equals( that.minor ) : that.minor != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( major != null ? major.hashCode() : 0 );
        result = 31 * result + ( minor != null ? minor.hashCode() : 0 );
        result = 31 * result + ( build != null ? build.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }
}
