/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.AnnotatedObjectImpl
 */
public interface AnnotatedObject<T extends Xref, A extends Alias> extends BasicObject, Annotated {

    /**
     * This matches with the column size for short label
     */
    static final int RECOMMENDED_SHORT_LABEL_LEN = 20;

    static final int MAX_SHORT_LABEL_LEN = 256;

    String getShortLabel();

    void setShortLabel( String shortLabel );

    String getFullName();

    void setFullName( String fullName );

    ///////////////////////////////////////
    // access methods for associations
    void setAnnotations( Collection<Annotation> someAnnotation );

    void addAnnotation( Annotation annotation );

    void removeAnnotation( Annotation annotation );

    ///////////////////
    // Xref related
    ///////////////////
    void setXrefs( Collection<T> someXrefs );

    Collection<T> getXrefs();

    void addXref( T aXref );

    void removeXref( T xref );

    ///////////////////
    // Alias related
    ///////////////////
    void setAliases( Collection<A> someAliases );

    Collection<A> getAliases();

    void addAlias( A alias );

    void removeAlias( A alias );

}
