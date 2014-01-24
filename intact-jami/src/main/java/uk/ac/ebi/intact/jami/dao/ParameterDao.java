package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;

import java.util.Collection;

/**
 * Parameter DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface ParameterDao<P extends AbstractIntactParameter> extends IntactBaseDao<P>{

    public Collection<P> getByType(String typeName, String typeMI);

    public Collection<P> getByUnit(String unitName, String unitMI);

    public Collection<P> getByTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI);

    public Collection<P> getByParentAc(String ac);

}
