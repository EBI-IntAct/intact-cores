/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.LifeCycleEvent;

import java.util.Collection;
import java.util.Date;

/**
 * DAO for lifecycle events
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface LifeCycleEventDao<L extends LifeCycleEvent> extends IntactBaseDao<L> {

    public Collection<L> getByEvent(String eventName, int first, int max);
    public Collection<L> getByDate(Date date, int first, int max);
    public Collection<L> getByUser(String user, int first, int max);
    public Collection<L> getByNote(String note, int first, int max);
    public Collection<L> getByNoteLike(String note, int first, int max);
    public Collection<L> getByParentAc(String parentAc);
}
