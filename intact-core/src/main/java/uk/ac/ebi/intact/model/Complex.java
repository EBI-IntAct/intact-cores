/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * TODO comments
 *
 * @author hhe
 * @version $Id$
 */
public class Complex extends InteractorImpl {

    ///////////////////////////////////////
    //attributes

    /**
     * Molecular weight of the complex in kilodalton.
     */
    private float mw;

    ///////////////////////////////////////
    //access methods for attributes

    public float getMw() {
        return mw;
    }

    public void setMw( float mw ) {
        this.mw = mw;
    }
} // end Complex




