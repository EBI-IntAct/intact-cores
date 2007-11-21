/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface CvObjectDao<T extends CvObject> extends AnnotatedObjectDao<T> {

    List<T> getByPsiMiRefCollection( Collection<String> psiMis );

    /**
     * Returns a list of controlled vocabulary terms having the given MI reference as identity.
     *
     * @param psiMiRef MI identifier we are looking after.
     *
     * @return a controlled vocabulary term of type T.
     */
    T getByPsiMiRef( String psiMiRef );

    List<T> getByObjClass( Class[] objClasses );

    <T extends CvObject> T getByShortLabel( Class<T> cvType, String label );

    <T extends CvObject> T getByPrimaryId( Class<T> cvType, String miRef );

    /**
     * Collects all MIs identifiers of CvInteractorType being NucleicAcid or children term.
     * @return a non null collection of MI identifiers.
     */
    public Collection<String> getNucleicAcidMIs();
}
