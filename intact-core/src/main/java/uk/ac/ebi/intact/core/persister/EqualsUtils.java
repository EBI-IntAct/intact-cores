/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * Utility allowing to compare attributes of AnnotatedObjects such as Xref, Alias and Annotation.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class EqualsUtils {

    private EqualsUtils() {
    }

    public static boolean sameXref( Xref x1, Xref x2 ) {
        if (x1.getAc() != null && x2.getAc() != null) {
            return x1.getAc().equals(x2.getAc());
        }

        if ( !same( x1.getPrimaryId(), x2.getPrimaryId() ) ) {
            return false;
        }

        if ( !CvObjectUtils.areEqual( x1.getCvDatabase(), x2.getCvDatabase() ) ) {
            return false;
        }

        if ( !CvObjectUtils.areEqual( x1.getCvXrefQualifier(), x2.getCvXrefQualifier() ) ) {
            return false;
        }

        if ( !same( x1.getSecondaryId(), x2.getSecondaryId() ) ) {
            return false;
        }

        if ( !same( x1.getDbRelease(), x2.getDbRelease() ) ) {
            return false;
        }

        return true;
    }

    public static boolean sameAlias( Alias a1, Alias a2 ) {
        if (a1.getAc() != null && a2.getAc() != null) {
            return a1.getAc().equals(a2.getAc());
        }

        if ( !same( a1.getName(), a2.getName() ) ) {
            return false;
        }

        if ( !CvObjectUtils.areEqual( a1.getCvAliasType(), a2.getCvAliasType() ) ) {
            return false;
        }

        return true;
    }

    public static boolean sameAnnotation( Annotation a1, Annotation a2 ) {
        if (a1.getAc() != null && a2.getAc() != null) {
            return a1.getAc().equals(a2.getAc());
        }

        if ( !same( a1.getAnnotationText(), a2.getAnnotationText() ) ) {
            return false;
        }

        if ( !CvObjectUtils.areEqual( a1.getCvTopic(), a2.getCvTopic() ) ) {
            return false;
        }

        return true;
    }

    private static boolean same( Object s1, Object s2 ) {
        if ( s1 == null && s2 == null ) {
            return true;
        }

        if ( s1 == null || s2 == null ) {
            // only one of them is null
            return false;
        }

        return s1.equals( s2 );
    }
}