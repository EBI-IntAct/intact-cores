/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Set of terms to describe the participant experimental treatment and status.
 * This term group in fact 4 orthologues short controlled vocabularies delivery method, expression level, molecular source, and sample process. Each participant can then be annotated with a maximum of 4 terms selected from each short list.
 *
 * @author hhe, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.3
 */
@Entity
@DiscriminatorValue("uk.ac.ebi.intact.model.CvExperimentalPreparation")
public class CvExperimentalPreparation extends CvObject {

    //////////////////////
    // Constants


    /**
     * Necessary for hibernate, yet set to private as it should not be used for any other purpose.
     */
    public CvExperimentalPreparation() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvExperimentalPreparation instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvExperimentalPreparation
     * @param owner      The Institution which owns this CvExperimentalPreparation
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvExperimentalPreparation(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

}