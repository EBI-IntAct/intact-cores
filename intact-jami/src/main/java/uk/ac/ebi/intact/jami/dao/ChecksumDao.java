package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;

import java.util.Collection;

/**
 * Checksum DAO
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
public interface ChecksumDao<C extends AbstractIntactChecksum> extends IntactBaseDao<C> {

    public Collection<C> getByValue(String value);

    public Collection<C> getByMethod(String methodName, String methodMI);

    public Collection<C> getByMethodAndValue(String methodName, String methodMI, String value);
}
